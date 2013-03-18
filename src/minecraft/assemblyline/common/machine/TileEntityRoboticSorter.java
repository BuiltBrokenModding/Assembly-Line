package assemblyline.common.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityRoboticSorter extends TileEntityElectricityRunnable implements IPacketReceiver, IInventory
{
	/**
	 * The items this container contains.
	 */
	protected ItemStack[] containingItems = new ItemStack[this.getSizeInventory()];

	/**
	 * Used to id the packet types
	 */
	private enum PacketTypes
	{
		ANIMATION, GUI, SETTINGON
	}

	/**
	 * Joules required per tick.
	 */
	public static final int WATT_REQUEST = 10;

	/**
	 * Stored energy
	 */
	public double wattsReceived = 0;
	/**
	 * on/off value for the GUI buttons
	 */
	public boolean[] guiButtons = new boolean[] { true, true, true, true, true };
	/**
	 * the belt found in the search area
	 */
	public TileEntityConveyorBelt[] beltSide = new TileEntityConveyorBelt[6];

	private int playerUsing = 0;

	@Override
	public String getInvName()
	{
		return "Sorter";
	}

	@Override
	public int getSizeInventory()
	{
		return 4;
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
				this.containingItems[par1] = null;
				return var3;
			}
			else
			{
				var3 = this.containingItems[par1].splitStack(par2);

				if (this.containingItems[par1].stackSize == 0)
				{
					this.containingItems[par1] = null;
				}

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
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.containingItems[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
		{
			par2ItemStack.stackSize = this.getInventoryStackLimit();
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
		this.playerUsing++;
	}

	@Override
	public void closeChest()
	{
		this.playerUsing--;
	}

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		for (int i = 0; i < this.guiButtons.length; i++)
		{
			this.guiButtons[i] = nbt.getBoolean("guiButton" + i);
		}

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
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		for (int i = 0; i < this.guiButtons.length; i++)
		{
			nbt.setBoolean("guiButton" + i, this.guiButtons[i]);
		}

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
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public boolean canConnect(ForgeDirection dir)
	{
		return dir == ForgeDirection.DOWN;
	}

	@Override
	public boolean func_94042_c()
	{
		return false;
	}

	@Override
	public boolean func_94041_b(int i, ItemStack itemstack)
	{
		return false;
	}
}
