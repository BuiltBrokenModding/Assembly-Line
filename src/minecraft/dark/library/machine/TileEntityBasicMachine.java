package dark.library.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;

public abstract class TileEntityBasicMachine extends TileEntityElectricityRunnable implements ISidedInventory, net.minecraftforge.common.ISidedInventory
{
	int players = 0;
	int invSize = 1;
	ItemStack[] containingItems = new ItemStack[invSize];

	@Override
	public int getSizeInventory()
	{
		return this.containingItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return this.containingItems[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int ammount)
	{
		if (this.containingItems[slot] != null)
		{
			ItemStack itemstack;

			if (this.containingItems[slot].stackSize <= ammount)
			{
				itemstack = this.containingItems[slot];
				this.containingItems[slot] = null;
				return itemstack;
			}
			else
			{
				itemstack = this.containingItems[slot].splitStack(ammount);

				if (this.containingItems[slot].stackSize == 0)
				{
					this.containingItems[slot] = null;
				}

				return itemstack;
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
		if (this.containingItems[slot] != null)
		{
			ItemStack itemstack = this.containingItems[slot];
			this.containingItems[slot] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		this.containingItems[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
		{
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName()
	{
		return "container.machine";
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items");
		this.containingItems = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.containingItems.length)
			{
				this.containingItems[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.containingItems.length; ++i)
		{
			if (this.containingItems[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.containingItems[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbt.setTag("Items", nbttaglist);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}

	public void openChest()
	{
		this.players++;
	}

	public void closeChest()
	{
		this.players--;
	}
}
