package assemblyline.common.machine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.network.PacketManager.PacketType;
import assemblyline.api.IFilterable;
import assemblyline.common.AssemblyLine;

import com.google.common.io.ByteArrayDataInput;

public abstract class TileEntityFilterable extends TileEntityAssemblyNetwork implements IRotatable, IFilterable, IPacketReceiver, IInventory
{
	private ItemStack filterItem;

	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if (slot == 0)
			return this.filterItem;
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (this.filterItem != null)
		{
			ItemStack stack;

			if (this.filterItem.stackSize <= amount)
			{
				stack = this.filterItem;
				filterItem = null;
				return stack;
			}
			else
			{
				stack = this.filterItem.splitStack(amount);

				if (this.filterItem.stackSize == 0)
				{
					this.filterItem = null;
				}

				return stack;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (this.filterItem != null)
		{
			ItemStack stack = this.filterItem;
			filterItem = null;
			return stack;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		this.filterItem = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest()
	{

	}

	@Override
	public void closeChest()
	{

	}

	@Override
	public void setFilter(ItemStack filter)
	{
		this.setInventorySlotContents(0, filter);
		PacketManager.sendPacketToClients(getDescriptionPacket());
	}

	@Override
	public ItemStack getFilter()
	{
		return this.getStackInSlot(0);
	}

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	@Override
	public void setDirection(ForgeDirection facingDirection)
	{
		this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, facingDirection.ordinal());
	}

	@Override
	public abstract String getInvName();

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = AssemblyLine.CHANNEL;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try
		{
			dos.writeInt(PacketType.TILEENTITY.ordinal());
			dos.writeInt(this.xCoord);
			dos.writeInt(this.yCoord);
			dos.writeInt(this.zCoord);
			NBTTagCompound tag = new NBTTagCompound();
			writeToNBT(tag);
			PacketManager.writeNBTTagCompound(tag, dos);
			packet.data = bos.toByteArray();
			packet.length = bos.size();
			return packet;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
		DataInputStream dis = new DataInputStream(bis);
		int id, x, y, z;
		try
		{
			id = dis.readInt();
			x = dis.readInt();
			y = dis.readInt();
			z = dis.readInt();
			NBTTagCompound tag = Packet.readNBTTagCompound(dis);
			readFromNBT(tag);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		NBTTagCompound filter = new NBTTagCompound();
		if (getFilter() != null)
			getFilter().writeToNBT(filter);
		nbt.setTag("filter", filter);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		NBTTagCompound filter = nbt.getCompoundTag("filter");
		setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(filter));
	}

}
