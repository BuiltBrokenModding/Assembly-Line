package assemblyline.common.machine.imprinter;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import assemblyline.api.IFilterable;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.TileEntityAssemblyNetwork;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public abstract class TileEntityImprintable extends TileEntityAssemblyNetwork implements IRotatable, IFilterable, IPacketReceiver, IInventory
{
	private ItemStack filterItem;
	private boolean inverted;

	/**
	 * Looks through the things in the filter and finds out which item is being filtered.
	 * 
	 * @return Is this filterable block filtering this specific ItemStack?
	 */
	public boolean isFiltering(ItemStack itemStack)
	{
		if (this.getFilter() != null && itemStack != null)
		{
			ArrayList<ItemStack> checkStacks = ItemImprinter.getFilters(getFilter());

			if (checkStacks != null)
			{
				for (int i = 0; i < checkStacks.size(); i++)
				{
					if (checkStacks.get(i) != null)
					{
						if (checkStacks.get(i).isItemEqual(itemStack)) { return !inverted; }
					}
				}
			}
		}

		return inverted;
	}

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

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket());
		}
	}

	@Override
	public ItemStack getFilter()
	{
		return this.getStackInSlot(0);
	}

	public void setInverted(boolean inverted)
	{
		this.inverted = inverted;
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket());
		}
	}
	
	public boolean isInverted()
	{
		return this.inverted;
	}
	
	public void toggleInversion()
	{
		setInverted(!isInverted());
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
	public int getInventoryStackLimit()
	{
		return 1;
	}

	/**
	 * Don't override this! Override getPackData() instead!
	 */
	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.getPacketData().toArray());
	}

	public ArrayList getPacketData()
	{
		ArrayList array = new ArrayList();
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		array.add(tag);
		return array;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (worldObj.isRemote)
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
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		NBTTagCompound filter = new NBTTagCompound();
		if (getFilter() != null)
			getFilter().writeToNBT(filter);
		nbt.setTag("filter", filter);
		nbt.setBoolean("inverted", inverted);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		inverted = nbt.getBoolean("inverted");
		NBTTagCompound filter = nbt.getCompoundTag("filter");
		setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(filter));
	}

}
