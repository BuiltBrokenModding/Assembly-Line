package dark.library.locking.prefab;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.PacketDispatcher;
import dark.library.locking.AccessLevel;
import dark.library.locking.ISpecialAccess;
import dark.library.locking.UserAccess;

public abstract class TileEntityElectricLockable extends TileEntityElectricityRunnable implements ISpecialAccess, IPacketReceiver
{
	public enum PacketType
	{
		DESCR_DATA, LIST_EDIT, SETTING_EDIT, OTHER;
	}

	/**
	 * A list of user access data.
	 */
	private final List<UserAccess> users = new ArrayList<UserAccess>();

	/**
	 * The amount of players using the console.
	 */
	public int playersUsing = 0;
	/**
	 * was the access list changed, used to trigger a packet update early
	 */
	public boolean listUpdate = false;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			// // update lit when changes are made or every 2 seconds if a player is near
			if (listUpdate || (this.worldObj.getClosestPlayer(xCoord, yCoord, zCoord, 20) != null && this.ticks % 40 == 0))
			{
				listUpdate = false;
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 20);
			}
		}
	}

	/**
	 * Channel to be used to send packets on
	 */
	public abstract String getChannel();

	/**
	 * Sends all NBT data. Server -> Client
	 */
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return PacketManager.getPacket(this.getChannel(), this, PacketType.DESCR_DATA.ordinal(), nbt);
	}

	/**
	 * send a packet the server with info on an access list change
	 * 
	 * @param player - player's access
	 * @param remove - is the change a remove order
	 */
	public void sendEditToServer(UserAccess player, boolean remove)
	{
		if (this.worldObj.isRemote && player != null)
		{
			Packet packet = PacketManager.getPacket(this.getChannel(), this, PacketType.LIST_EDIT.ordinal(), player.username, player.level.ordinal(), player.shouldSave, remove);
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetID, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			PacketType packetType = PacketType.values()[dataStream.readInt()];

			switch (packetType)
			{
				case DESCR_DATA:
				{
					if (this.worldObj.isRemote)
					{
						short size = dataStream.readShort();

						if (size > 0)
						{
							byte[] byteCode = new byte[size];
							dataStream.readFully(byteCode);
							this.readFromNBT(CompressedStreamTools.decompress(byteCode));
						}
					}

					break;
				}
				case LIST_EDIT:
				{
					if (!this.worldObj.isRemote)
					{
						String name = dataStream.readUTF();
						AccessLevel level = AccessLevel.get(dataStream.readInt());
						Boolean shouldSave = dataStream.readBoolean();
						Boolean remove = dataStream.readBoolean();
						if (remove)
						{
							this.removeUserAccess(name, true);
						}
						else
						{
							this.addUserAccess(new UserAccess(name, level, shouldSave), true);
						}

					}
					break;
				}
				case SETTING_EDIT:
				{
					// TODO add settings packet handler when settings are added
					break;
				}
				// // PacketType.Other is treated as a default call //
				default:
					break;
			}

		}
		catch (Exception e)
		{
			FMLLog.severe("GS: Failed to handle packet for locked door.");
			e.printStackTrace();
		}
	}

	@Override
	public AccessLevel getUserAccess(String username)
	{
		for (int i = 0; i < this.users.size(); i++)
		{
			if (this.users.get(i).username.equalsIgnoreCase(username))
			{
				return this.users.get(i).level;
			}
		}
		return AccessLevel.NONE;
	}

	@Override
	public List<UserAccess> getUsers()
	{
		return this.users;
	}

	@Override
	public List<UserAccess> getUsersWithAcess(AccessLevel level)
	{
		List<UserAccess> players = new ArrayList<UserAccess>();

		for (int i = 0; i < this.users.size(); i++)
		{
			UserAccess ref = this.users.get(i);

			if (ref.level == level)
			{
				players.add(ref);
			}
		}
		return players;

	}

	/**
	 * checks to see if a user is on the access list regardless of access
	 */
	public boolean isOnList(String username)
	{
		for (UserAccess user : this.getUsers())
		{
			if (user.username.equalsIgnoreCase(username))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addUserAccess(UserAccess user, boolean isServer)
	{
		if (!isServer)
		{
			this.sendEditToServer(user, false);
		}
		else
		{
			this.removeUserAccess(user.username, isServer);
			this.listUpdate = true;
			return this.users.add(user);
		}
		return false;

	}

	@Override
	public boolean removeUserAccess(String player, boolean isServer)
	{

		if (!isServer)
		{
			UserAccess access = new UserAccess(player, AccessLevel.BASIC, false);
			this.sendEditToServer(access, true);
		}
		else
		{

			List<UserAccess> list = UserAccess.removeUserAccess(player, this.users);
			if (list.size() < this.users.size())
			{
				this.users.clear();
				this.users.addAll(list);
				this.listUpdate = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * can the player access this tileEntity in any way
	 */
	public boolean canAccess(EntityPlayer player)
	{
		if (this.users.size() <= 0)
		{
			return true;
		}
		return this.getUserAccess(player.username).ordinal() >= AccessLevel.USER.ordinal();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		// Read user list
		this.users.clear();
		this.users.addAll(UserAccess.readListFromNBT(nbt, "Users"));
		this.listUpdate = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		// Write user list
		UserAccess.writeListToNBT(nbt, this.users);
	}
}
