package assemblyline.interaction;

import java.util.List;

import com.google.common.io.ByteArrayDataInput;

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
import assemblyline.TileEntityBase;
import assemblyline.belts.TileEntityConveyorBelt;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileEntityEjector extends TileEntityBase implements IElectricityReceiver, IPacketReceiver
{
	/**
	 * Joules required per tick.
	 */
	public static final int WATTS_REQUIRED = 10;

	public double wattsReceived = 0;

	public boolean firePiston = false;
	public boolean pFirePiston = false;
	public boolean rejectItems = true;
	public boolean[] onOff = new boolean[]
	{ true, true, true, true };
	public TileEntityConveyorBelt beltSide = null;

	@Override
	public double wattRequest()
	{
		return WATTS_REQUIRED;
	}

	// TODO add computer craft support to change
	// onOff values, or even select what can be
	// rejected.
	// If option two add a rejector item to tell
	// the computer what item is in front of the
	// ejector
	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.ticks % 10 == 0)
		{
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			ForgeDirection searchPosition = Vector3.getOrientationFromSide(ForgeDirection.getOrientation(getDirection(meta)), ForgeDirection.SOUTH);
			TileEntity tileEntity = worldObj.getBlockTileEntity(xCoord + searchPosition.offsetX, yCoord + searchPosition.offsetY, zCoord + searchPosition.offsetZ);
			if (tileEntity instanceof TileEntityConveyorBelt)
			{
				this.beltSide = (TileEntityConveyorBelt) tileEntity;
			}
			else
			{
				this.beltSide = null;
			}
			this.firePiston = false;
			try
			{
				AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(xCoord + searchPosition.offsetX, yCoord + searchPosition.offsetY, zCoord + searchPosition.offsetZ, xCoord + searchPosition.offsetX + 1, yCoord + searchPosition.offsetY + 1, zCoord + searchPosition.offsetZ + 1);
				List<EntityItem> itemsBehind = worldObj.getEntitiesWithinAABB(EntityItem.class, bounds);

				if (itemsBehind.size() > 0 && this.wattsReceived > this.WATTS_REQUIRED)
				{
					for (EntityItem entity : itemsBehind)
					{
						if (this.canItemBeThrow(entity))
						{
							this.throwItem(searchPosition, entity);
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Used to do the actual throwing of the item
	 * from the piston arm
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
			this.beltSide.ignore(entity);

		}
		entity.motionX = (double) side.offsetX * 0.1;
		entity.motionY += 0.10000000298023224D;
		entity.motionZ = (double) side.offsetZ * 0.1;
		this.wattsReceived -= this.WATTS_REQUIRED;
	}

	public boolean canItemBeThrow(Entity entity)
	{
		// TODO add ability to eject Entities that
		// are not items, though i might want to
		// create a bigger ejector for this
		// TODO might also want to add damaging
		// effect to items like glass once i make
		// a sorter arm
		if (entity instanceof EntityItem)
		{
			EntityItem itemE = (EntityItem) entity;
			ItemStack item = itemE.item;

			if (this.rejectItems)
			{

				// reject the items with same IDS
				// as those placed in inventory
				for (int i = 0; i < this.containingItems.length; i++)
				{
					if (containingItems[i] != null)
					{
						if (containingItems[i].itemID == item.itemID && containingItems[i].getItemDamage() == item.getItemDamage()) { return true; }
					}
				}
				return false;

			}
			else if (!this.rejectItems)
			{
				// reject all but the items with
				// same IDS as those placed in
				// inventory
				for (int i = 0; i < this.containingItems.length; i++)
				{
					if (containingItems[i] != null)
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
		// TODO Auto-generated method stub
		return side == ForgeDirection.DOWN;
	}

	/*
	 * @Override public Object[] sendDataA() {
	 * return new Object[] { this.firePiston,
	 * this.rejectItems }; }
	 * 
	 * @Override public Object[] sendDataG() {
	 * Object[] data = new
	 * Object[this.onOff.length]; for (int i = 0;
	 * i < this.onOff.length; i++) { data[i] =
	 * onOff[i]; } return data; }
	 * 
	 * @Override public void
	 * guiPacket(NetworkManager network, int
	 * packetType, Packet250CustomPayload packet,
	 * EntityPlayer player, ByteArrayDataInput
	 * dataStream) { if (worldObj.isRemote) { try
	 * { for (int i = 0; i < this.onOff.length;
	 * i++) { this.onOff[i] =
	 * dataStream.readBoolean(); }
	 * 
	 * } catch (Exception e) {
	 * e.printStackTrace(); } }
	 * 
	 * }
	 * 
	 * @Override public void
	 * animationPacket(NetworkManager network, int
	 * packetType, Packet250CustomPayload packet,
	 * EntityPlayer player, ByteArrayDataInput
	 * dataStream) { if (worldObj.isRemote) { try
	 * { this.firePiston =
	 * dataStream.readBoolean(); this.rejectItems
	 * = dataStream.readBoolean();
	 * 
	 * } catch (Exception e) {
	 * e.printStackTrace(); } }
	 * 
	 * }
	 * 
	 * @Override public void
	 * otherPacket(NetworkManager network, int
	 * packetType, Packet250CustomPayload packet,
	 * EntityPlayer player, ByteArrayDataInput
	 * dataStream) { if (worldObj.isRemote) { try
	 * {
	 * 
	 * } catch (Exception e) {
	 * e.printStackTrace(); } } else if
	 * (!worldObj.isRemote) { try { int ID =
	 * dataStream.readInt(); if (ID == 0) {
	 * this.changeOnOff(dataStream.readInt()); }
	 * else if (ID == 1) { this.changeRejected();
	 * }
	 * 
	 * } catch (Exception e) {
	 * e.printStackTrace(); } }
	 * 
	 * }
	 */

	public void changeOnOff(int i)
	{
		if (i >= this.onOff.length) { return; }
		boolean cc = this.onOff[i];
		if (cc)
		{
			cc = false;
		}
		else
		{
			cc = true;
		}
		this.onOff[i] = cc;
		if (worldObj.isRemote)
		{
			Packet packet = PacketManager.getPacket("asmLine", this, new Object[]
			{ 2, 0, i });
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	public void changeRejected()
	{

		boolean cc = this.rejectItems;
		if (cc)
		{
			cc = false;
		}
		else
		{
			cc = true;
		}
		this.rejectItems = cc;
		if (worldObj.isRemote)
		{
			Packet packet = PacketManager.getPacket("asmLine", this, new Object[]
			{ 2, 1 });
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		for (int i = 0; i < this.onOff.length; i++)
		{
			this.onOff[i] = nbt.getBoolean("onOff" + i);
		}
		this.rejectItems = nbt.getBoolean("reject");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		for (int i = 0; i < this.onOff.length; i++)
		{
			nbt.setBoolean("onOff" + i, this.onOff[i]);
		}
		nbt.setBoolean("reject", this.rejectItems);
	}

	@Override
	public String getInvName()
	{
		return "Ejector";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{
		this.wattsReceived += (amps * voltage);

	}

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

	@Override
	public double getVoltage()
	{
		return 120;
	}

	@Override
	public int getSizeInventory()
	{
		return 10;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		
	}
}
