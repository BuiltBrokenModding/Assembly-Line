package assemblyline.machines;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.Vector3;
import universalelectricity.implement.IElectricityReceiver;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.AssemblyLine;
import assemblyline.TileEntityBase;
import assemblyline.belts.TileEntityConveyorBelt;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;

public class TileEntitySorter extends TileEntityBase implements IElectricityReceiver, IPacketReceiver
{
	/**
	 * Used to id the packet types
	 */
	private enum tPacketID
	{
		ANIMATION, GUI, SETTINGON
	}

	/**
	 * Joules required per tick.
	 */
	public static final int WATTS_REQUIRED = 10;
	/**
	 * Stored energy
	 */
	public double wattsReceived = 0;
	/**
	 * should the piston fire, or be extended
	 */
	public boolean firePiston = false;
	/**
	 * on/off value for the GUI buttons
	 */
	public boolean[] onOff = new boolean[]
	{ true, true, true, true, true };
	/**
	 * the belt found in the search area
	 */
	public TileEntityConveyorBelt beltSide = null;

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		// has to update a bit faster than a
		// conveyer belt
		if (this.ticks % 5 == 0)
		{

			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			this.firePiston = false;

			// area to search for items
			ForgeDirection searchPosition = Vector3.getOrientationFromSide(ForgeDirection.getOrientation(getDirection(meta)), ForgeDirection.SOUTH);
			TileEntity tileEntity = worldObj.getBlockTileEntity(xCoord + searchPosition.offsetX, yCoord + searchPosition.offsetY, zCoord + searchPosition.offsetZ);

			// find the belt in that search area
			if (tileEntity instanceof TileEntityConveyorBelt)
			{
				this.beltSide = (TileEntityConveyorBelt) tileEntity;
			}
			else
			{
				this.beltSide = null;
			}

			try
			{
				// search area bound box
				AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(xCoord + searchPosition.offsetX, yCoord + searchPosition.offsetY, zCoord + searchPosition.offsetZ, xCoord + searchPosition.offsetX + 1, yCoord + searchPosition.offsetY + 1, zCoord + searchPosition.offsetZ + 1);
				// EntityItem list
				List<EntityItem> itemsBehind = worldObj.getEntitiesWithinAABB(EntityItem.class, bounds);

				boolean flag = false;

				if (itemsBehind.size() > 0 && this.wattsReceived > this.WATTS_REQUIRED)
				{
					// for every item found check
					// if can be thrown then throw
					// item off belt if it can
					for (EntityItem entity : itemsBehind)
					{
						if (this.canItemBeThrow(entity))
						{
							this.throwItem(searchPosition, entity);
							flag = true;
						}
					}
				}
				// send packet with animation data
				// if an item was rejected from
				// the area
				if (!worldObj.isRemote && flag)
				{
					Packet packet = PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.data(tPacketID.ANIMATION));
					PacketManager.sendPacketToClients(packet, worldObj, Vector3.get(this), 30);
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			if (!worldObj.isRemote && this.playerUsing > 0)
			{
				Packet packet = PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.data(tPacketID.GUI));
				PacketManager.sendPacketToClients(packet, worldObj, Vector3.get(this), 10);
			}
		}
	}

	/**
	 * Used to move after it has been rejected
	 * 
	 * @param side
	 *            - used to do the offset
	 * @param entity
	 *            - Entity being thrown
	 */
	public void throwItem(ForgeDirection side, Entity entity)
	{
		this.firePiston = true;
		if (this.beltSide != null)
		{
			this.beltSide.ignoreEntity(entity);

		}
		System.out.print(" \n fire ");
		entity.motionX = (double) side.offsetX * 0.1;
		entity.motionY += 0.10000000298023224D;
		entity.motionZ = (double) side.offsetZ * 0.1;
		this.wattsReceived -= this.WATTS_REQUIRED;
	}

	public boolean canItemBeThrow(Entity entity)
	{
		// TODO add other things than items
		if (entity instanceof EntityItem)
		{
			EntityItem itemE = (EntityItem) entity;
			ItemStack item = itemE.item;

			if (this.onOff[4])
			{

				// reject matching items
				for (int i = 0; i < this.containingItems.length; i++)
				{
					if (containingItems[i] != null && onOff[i])
					{
						if (containingItems[i].itemID == item.itemID && containingItems[i].getItemDamage() == item.getItemDamage()) { return true; }
					}
				}
				return false;

			}
			else if (!this.onOff[4])
			{
				// reject all but matching items
				for (int i = 0; i < this.containingItems.length; i++)
				{
					if (containingItems[i] != null && onOff[i])
					{
						if (containingItems[i].itemID == item.itemID && containingItems[i].getItemDamage() == item.getItemDamage()) { return false; }
					}
				}
				return true;
			}
		}
		return false;
	}

	public byte getDirection(int meta)
	{

		switch (meta)
		{
			case 0:
				return 2;
			case 1:
				return 5;
			case 2:
				return 3;
			case 3:
				return 4;
		}
		return 0;
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return side == ForgeDirection.DOWN;
	}

	/**
	 * Used to change any one of the boolean value
	 * of on/off array After changing the value if
	 * it was changed client side it will send a
	 * packet server side with the changes
	 * 
	 * @param i
	 */
	public void changeOnOff(int i)
	{
		if (i >= this.onOff.length) { return; }
		if (this.onOff[i])
		{
			this.onOff[i] = false;
		}
		else
		{
			this.onOff[i] = true;
		}
		if (worldObj.isRemote)
		{
			Packet packet = PacketManager.getPacket("asmLine", this, tPacketID.SETTINGON.ordinal(), i);
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	/**
	 * Data methods
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		for (int i = 0; i < this.onOff.length; i++)
		{
			this.onOff[i] = nbt.getBoolean("onOff" + i);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		for (int i = 0; i < this.onOff.length; i++)
		{
			nbt.setBoolean("onOff" + i, this.onOff[i]);
		}
	}

	public Object[] data(tPacketID id)
	{
		if (id == tPacketID.ANIMATION) { return new Object[]
		{ id.ordinal(), this.firePiston }; }
		if (id == tPacketID.GUI)
		{
			Object[] da = new Object[this.onOff.length];
			da[0] = id.ordinal();
			for (int i = 0; i < this.onOff.length; i++)
			{
				da[i + 1] = onOff[i];
			}
			return da;
		}
		return new Object[]
		{ id.ordinal() };
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			int id = dataStream.readInt();
			tPacketID pID = tPacketID.values()[id];
			System.out.print("\n id:" + id + " ");
			if (pID == tPacketID.ANIMATION)
			{
				this.firePiston = dataStream.readBoolean();
			}
			else if (pID == tPacketID.GUI)
			{
				for (int i = 0; i < this.onOff.length; i++)
				{
					this.onOff[i] = dataStream.readBoolean();
				}
			}
			else if (pID == tPacketID.SETTINGON)
			{
				int num = dataStream.readInt();
				this.changeOnOff(num);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * inventory methods
	 */
	@Override
	public String getInvName()
	{
		return "Rejector";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public int getSizeInventory()
	{
		return 4;
	}

	/**
	 * disabling methods
	 */
	@Override
	public void onDisable(int duration)
	{

	}

	@Override
	public boolean isDisabled()
	{
		return false;
	}

	@Override
	public boolean canConnect(ForgeDirection side)
	{
		return true;
	}

	/**
	 * UE methods
	 */
	@Override
	public double getVoltage()
	{
		return 120;
	}

	@Override
	public double wattRequest()
	{
		return WATTS_REQUIRED;
	}

	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{
		this.wattsReceived += (amps * voltage);

	}

}
