package assemblyline.common.machine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.network.PacketManager.PacketType;
import assemblyline.api.IFilterable;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.filter.ItemFilter;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

/**
 * 
 * @author Darkguardsman
 * 
 */
public class TileEntityRejector extends TileEntityFilterable
{

	/**
	 * Used to id the packet types
	 */
	private enum PacketTypes
	{
		ANIMATION, INVENTORY, SETTINGON
	}

	/**
	 * should the piston fire, or be extended
	 */
	public boolean firePiston = false;
	/**
	 * on/off value for the GUI buttons
	 */
	public boolean[] guiButtons = new boolean[] { true, true, true, true, true };

	private int playerUsing = 0;

	@Override
	public void onUpdate()
	{
		/**
		 * Has to update a bit faster than a conveyer belt
		 */
		if (this.ticks % 5 == 0 && !this.isDisabled())
		{
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			this.firePiston = false;

			// area to search for items
			Vector3 searchPosition = new Vector3(this);
			searchPosition.modifyPositionFromSide(this.getDirection());
			TileEntity tileEntity = searchPosition.getTileEntity(this.worldObj);

			try
			{
				boolean flag = false;

				if (this.isRunning())
				{
					/**
					 * Find all entities in the position in which this block is facing and attempt
					 * to push it out of the way.
					 */
					AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(searchPosition.x, searchPosition.y, searchPosition.z, searchPosition.x + 1, searchPosition.y + 1, searchPosition.z + 1);
					List<Entity> entitiesInFront = this.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

					for (Entity entity : entitiesInFront)
					{
						if (this.canEntityBeThrow(entity))
						{
							this.throwItem(this.getDirection(), entity);
							flag = true;
						}
					}
				}

				/**
				 * If a push happened, send a packet to the client to notify it for an animation.
				 */
				if (!this.worldObj.isRemote && flag)
				{
					// Packet packet = PacketManager.getPacket(AssemblyLine.CHANNEL, this,
					// this.getPacketData(PacketTypes.ANIMATION));
					// PacketManager.sendPacketToClients(packet, this.worldObj, new Vector3(this),
					// 30);
					PacketManager.sendPacketToClients(getDescriptionPacket());
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && this.playerUsing > 0)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 10);
			}
		}
	}

	/*
	 * @Override public Packet getDescriptionPacket() { return
	 * PacketManager.getPacket(AssemblyLine.CHANNEL, this,
	 * this.getPacketData(PacketTypes.INVENTORY)); }
	 */

	/**
	 * Used to move after it has been rejected
	 * 
	 * @param side - used to do the offset
	 * @param entity - Entity being thrown
	 */
	public void throwItem(ForgeDirection side, Entity entity)
	{
		this.firePiston = true;

		entity.motionX = (double) side.offsetX * 0.1;
		entity.motionY += 0.10000000298023224D;
		entity.motionZ = (double) side.offsetZ * 0.1;
		
		PacketManager.sendPacketToClients(getDescriptionPacket());
	}

	public boolean canEntityBeThrow(Entity entity)
	{
		// TODO Add other things than items
		if (entity instanceof EntityItem)
		{
			EntityItem entityItem = (EntityItem) entity;
			ItemStack itemStack = entityItem.func_92014_d();

			if (getFilter() != null)
			{
				ArrayList<ItemStack> checkStacks = ItemFilter.getFilters(getFilter());

				// Reject matching items
				for (int i = 0; i < checkStacks.size(); i++)
				{
					if (checkStacks.get(i) != null)
					{
						if (checkStacks.get(i).isItemEqual(itemStack)) { return true; }
					}
				}
			}
		}

		return false;
	}

	/**
	 * Used to change any one of the boolean value of on/off array After changing the value if it
	 * was changed client side it will send a packet server side with the changes
	 * 
	 * @param i
	 */
	public void changeOnOff(int i)
	{
		if (i >= this.guiButtons.length) { return; }
		if (this.guiButtons[i])
		{
			this.guiButtons[i] = false;
		}
		else
		{
			this.guiButtons[i] = true;
		}
		Packet packet = PacketManager.getPacket("asmLine", this, new Object[] { PacketTypes.SETTINGON.ordinal(), i });
		if (worldObj.isRemote)
		{
			PacketDispatcher.sendPacketToServer(packet);
		}
		else
		{
			PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 10);
		}
	}

	/*
	 * public Object[] getPacketData(PacketTypes id) { if (id == PacketTypes.ANIMATION) { return new
	 * Object[] { id.ordinal(), this.firePiston }; } if (id == PacketTypes.INVENTORY) { Object[] da
	 * = new Object[this.guiButtons.length + 1]; da[0] = id.ordinal();
	 * 
	 * for (int i = 0; i < this.guiButtons.length; i++) { da[i + 1] = guiButtons[i]; } return da; }
	 * return new Object[] { id.ordinal() }; }
	 */

	/*
	 * @Override public void handlePacketData(INetworkManager network, int packetType,
	 * Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream) { try {
	 * int id = dataStream.readInt(); PacketTypes pID = PacketTypes.values()[id]; DataInputStream
	 * inputStream = new DataInputStream((InputStream) dataStream);
	 * 
	 * if (pID == PacketTypes.ANIMATION) { this.firePiston = dataStream.readBoolean(); } else if
	 * (pID == PacketTypes.INVENTORY) { for (int i = 0; i < this.guiButtons.length; i++) {
	 * this.guiButtons[i] = dataStream.readBoolean(); } } else if (pID == PacketTypes.SETTINGON) {
	 * int num = dataStream.readInt(); this.changeOnOff(num); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */

	/**
	 * inventory methods
	 */
	@Override
	public String getInvName()
	{
		return TranslationHelper.getLocal("tile.rejector.name");
	}

	/**
	 * UE methods
	 */
	@Override
	public double getVoltage()
	{
		return 120;
	}

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		
		firePiston = nbt.getBoolean("piston");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		nbt.setBoolean("piston", firePiston);
	}
}
