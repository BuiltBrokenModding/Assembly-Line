package assemblyline.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import assemblyline.common.AssemblyLine;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileEntityCrate extends TileEntityAdvanced implements IInventory, IPacketReceiver
{
	public static final int MAX_LIMIT = 2880;
	private ItemStack[] containingItems = new ItemStack[1];
	public long prevClickTime = -1000;

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (this.worldObj.isRemote)
		{
			try
			{
				if (dataStream.readBoolean())
				{
					if (this.containingItems[0] == null)
					{
						this.containingItems[0] = new ItemStack(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
					}
					else
					{
						this.containingItems[0].itemID = dataStream.readInt();
						this.containingItems[0].stackSize = dataStream.readInt();
						this.containingItems[0].setItemDamage(dataStream.readInt());
					}
				}
				else
				{
					this.containingItems[0] = null;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		if (this.containingItems[0] != null)
		{
			return PacketManager.getPacket(AssemblyLine.CHANNEL, this, true, this.containingItems[0].itemID, this.containingItems[0].stackSize, this.containingItems[0].getItemDamage());
		}
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, false);
	}

	/**
	 * Inventory functions.
	 */
	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.containingItems[par1];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var3;

			if (this.containingItems[par1].stackSize <= par2)
			{
				var3 = this.containingItems[par1];
				this.setInventorySlotContents(par1, null);
				return var3;
			}
			else
			{
				var3 = this.containingItems[par1].splitStack(par2);

				if (this.containingItems[par1].stackSize == 0)
				{
					this.containingItems[par1] = null;
				}

				this.setInventorySlotContents(par1, this.containingItems[par1]);

				return var3;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var2 = this.containingItems[par1];
			this.containingItems[par1] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		if (stack != null)
		{
			if (stack.isStackable())
			{
				this.containingItems[slot] = stack;

				if (stack != null && stack.stackSize > this.getInventoryStackLimit())
				{
					stack.stackSize = this.getInventoryStackLimit();
				}
			}
		}
		else
		{
			this.containingItems[slot] = null;
		}

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj);
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

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		NBTTagList var2 = nbt.getTagList("Items");

		this.containingItems = new ItemStack[this.getSizeInventory()];

		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
			byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < this.containingItems.length)
			{
				this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}

		if (this.containingItems[0] != null)
		{
			this.containingItems[0].stackSize = nbt.getInteger("Count");
		}

	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		NBTTagList var2 = new NBTTagList();

		for (int var3 = 0; var3 < this.containingItems.length; ++var3)
		{
			if (this.containingItems[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.containingItems[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		nbt.setTag("Items", var2);

		if (this.containingItems[0] != null)
		{
			nbt.setInteger("Count", this.containingItems[0].stackSize);
		}
	}

	@Override
	public int getInventoryStackLimit()
	{
		return MAX_LIMIT;
	}

	@Override
	public int getSizeInventory()
	{
		return this.containingItems.length;
	}

	@Override
	public String getInvName()
	{
		return "Crate";
	}
}
