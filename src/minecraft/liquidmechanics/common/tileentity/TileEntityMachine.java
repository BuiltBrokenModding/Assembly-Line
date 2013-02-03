package liquidmechanics.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;

public abstract class TileEntityMachine extends TileEntity implements IInventory

{
	/* 15 */private int tickCounts = 0;
	/* 16 */private int userCount = 0;
	/* 17 */public ItemStack[] storedItems = new ItemStack[getSizeInventory()];

	public void writeToNBT(NBTTagCompound par1NBTTagCompound)

	{
		/* 22 */super.writeToNBT(par1NBTTagCompound);

		/* 24 */NBTTagList var2 = new NBTTagList();

		/* 26 */for (int var3 = 0; var3 < this.storedItems.length; var3++)

		{
			/* 28 */if (this.storedItems[var3] != null)

			{
				/* 30 */NBTTagCompound var4 = new NBTTagCompound();
				/* 31 */var4.setByte("Slot", (byte) var3);
				/* 32 */this.storedItems[var3].writeToNBT(var4);
				/* 33 */var2.appendTag(var4);
			}
		}

		/* 37 */par1NBTTagCompound.setTag("Items", var2);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound)

	{
		/* 43 */super.readFromNBT(par1NBTTagCompound);

		/* 45 */NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
		/* 46 */this.storedItems = new ItemStack[getSizeInventory()];

		/* 48 */for (int var3 = 0; var3 < var2.tagCount(); var3++)

		{
			/* 50 */NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
			/* 51 */byte var5 = var4.getByte("Slot");

			/* 53 */if ((var5 >= 0) && (var5 < this.storedItems.length))

			{
				/* 55 */this.storedItems[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
	}

	public boolean canUpdate()

	{
		/* 63 */return true;
	}

	public abstract Object[] getSendData();

	public abstract boolean needUpdate();

	public void updateEntity()

	{
		/* 77 */super.updateEntity();

		/* 79 */if ((this.tickCounts++ >= 10) && (!this.worldObj.isRemote) && (needUpdate()))

		{
			/* 81 */this.tickCounts = 0;
			/* 82 */Packet packet = PacketManager.getPacket(getChannel(), this, getSendData());
			/* 83 */PacketManager.sendPacketToClients(packet, this.worldObj, new Vector3(this), 40.0D);
		}
	}

	public abstract String getChannel();

	public abstract int getSizeInventory();

	public int getInventoryStackLimit()

	{
		/* 102 */return 64;
	}

	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)

	{
		/* 111 */return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this;
	}

	public ItemStack getStackInSlot(int par1)

	{
		/* 119 */return this.storedItems[par1];
	}

	public ItemStack decrStackSize(int par1, int par2)

	{
		/* 124 */if (this.storedItems[par1] != null)

		{
			/* 128 */if (this.storedItems[par1].stackSize <= par2)

			{
				/* 130 */ItemStack var3 = this.storedItems[par1];
				/* 131 */this.storedItems[par1] = null;
				/* 132 */return var3;
			}

			/* 136 */ItemStack var3 = this.storedItems[par1].splitStack(par2);

			/* 138 */if (this.storedItems[par1].stackSize == 0)

			{
				/* 140 */this.storedItems[par1] = null;
			}

			/* 143 */return var3;
		}

		/* 148 */return null;
	}

	public ItemStack getStackInSlotOnClosing(int par1)

	{
		/* 159 */if (this.storedItems[par1] != null)

		{
			/* 161 */ItemStack var2 = this.storedItems[par1];
			/* 162 */this.storedItems[par1] = null;
			/* 163 */return var2;
		}

		/* 167 */return null;
	}

	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)

	{
		/* 177 */this.storedItems[par1] = par2ItemStack;

		/* 179 */if ((par2ItemStack != null) && (par2ItemStack.stackSize > getInventoryStackLimit()))

		{
			/* 181 */par2ItemStack.stackSize = getInventoryStackLimit();
		}
	}

	public String getInvName()

	{
		/* 187 */return "SteamMachine";
	}

	public void openChest()

	{
		/* 193 */this.userCount += 1;
	}

	public void closeChest()

	{
		/* 199 */this.userCount -= 1;
	}

}