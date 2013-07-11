package assemblyline.common.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
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

public class TileEntityCrate extends TileEntityAdvanced implements IInventory, IPacketReceiver, ISidedInventory
{
	/* Collective total stack of all inv slots */
	private ItemStack sampleStack;
	/* Slots that can be accesed threw ISidedInv */
	private int[] slots;
	/* Sudo inv for the crate to interact with other things */
	private ItemStack[] items = new ItemStack[1028];

	public long prevClickTime = -1000;

	/** Clones the single stack into an inventory format for automation interaction */
	public void buildInventory()
	{
		ItemStack baseStack = this.sampleStack.copy();

		this.items = new ItemStack[this.getSlotCount()];

		for (int slot = 0; slot < this.items.length; slot++)
		{
			int stackL = Math.min(Math.min(baseStack.stackSize, baseStack.getMaxStackSize()), this.getInventoryStackLimit());
			this.items[slot] = new ItemStack(baseStack.itemID, stackL, baseStack.getItemDamage());
			baseStack.stackSize -= stackL;
			if (baseStack.stackSize <= 0)
			{
				baseStack = null;
				break;
			}
		}
	}

	/** Turns the inventory array into a single stack of matching items. This assumes that all items
	 * in the crate are the same TODO eject minority items and only keep the majority that are the
	 * same to prevent duplication issues
	 * 
	 * @param force - force a rebuild of the inventory from the single stack created */
	public void buildSampleStack(boolean force)
	{
		int count = 0;
		int id = 0;
		int meta = 0;

		boolean rebuildBase = false;

		/* Creates the sample stack that is used as a collective itemstack */
		for (int i = 0; i < this.items.length; i++)
		{
			ItemStack stack = this.items[i];
			if (stack != null && stack.itemID > 0 && stack.stackSize > 0)
			{
				id = this.items[i].itemID;
				meta = this.items[i].getItemDamage();
				int ss = this.items[i].stackSize;

				count += ss;

				if (ss > this.items[i].getMaxStackSize())
				{
					rebuildBase = true;
				}
			}
		}
		if (id == 0 || count == 0)
		{
			this.sampleStack = null;
		}
		else
		{
			this.sampleStack = new ItemStack(id, count, meta);
		}
		/* if one stack is over sized this rebuilds the inv to redistribute the items in the slots */
		if ((rebuildBase || force || this.items.length > this.getSlotCount()) && this.sampleStack != null)
		{
			this.buildInventory();
		}
	}

	public ItemStack getSampleStack()
	{
		if (this.sampleStack == null)
		{
			this.buildSampleStack(false);
		}
		return this.sampleStack;
	}

	public void addToStack(ItemStack stack, int amount)
	{
		if (stack != null)
		{
			this.addToStack(new ItemStack(stack.stackSize, amount, stack.getItemDamage()));
		}
	}

	public void addToStack(ItemStack stack)
	{
		if (stack != null)
		{
			this.buildSampleStack(false);
			boolean flag = false;
			if (this.sampleStack == null)
			{
				this.sampleStack = stack;
				flag = true;
			}
			else if (this.sampleStack.isItemEqual(stack))
			{
				this.sampleStack.stackSize += stack.stackSize;
				flag = true;
			}
			if (flag)
			{
				this.buildInventory();
				if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
				{
					PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj);
				}
			}
		}
	}

	public int getSlotCount()
	{
		return TileEntityCrate.getSlotCount(this.getBlockMetadata());
	}

	public static int getSlotCount(int metadata)
	{
		if (metadata >= 2)
		{
			return 256;
		}
		else if (metadata >= 1)
		{
			return 64;
		}

		return 32;
	}

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
					if (this.sampleStack == null)
					{
						this.sampleStack = new ItemStack(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
					}
					else
					{
						this.sampleStack.itemID = dataStream.readInt();
						this.sampleStack.stackSize = dataStream.readInt();
						this.sampleStack.setItemDamage(dataStream.readInt());
					}
				}
				else
				{
					this.sampleStack = null;
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
		this.buildSampleStack(false);
		ItemStack stack = this.getSampleStack();
		if (stack != null)
		{
			return PacketManager.getPacket(AssemblyLine.CHANNEL, this, true, stack.itemID, stack.stackSize, stack.getItemDamage());
		}
		else
		{
			return PacketManager.getPacket(AssemblyLine.CHANNEL, this, false);
		}
	}

	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.items[par1];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.items[par1] != null)
		{
			ItemStack itemstack;

			if (this.items[par1].stackSize <= par2)
			{
				itemstack = this.items[par1];
				this.items[par1] = null;
				this.onInventoryChanged();
				return itemstack;
			}
			else
			{
				itemstack = this.items[par1].splitStack(par2);

				if (this.items[par1].stackSize == 0)
				{
					this.items[par1] = null;
				}

				this.onInventoryChanged();
				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public void onInventoryChanged()
	{
		super.onInventoryChanged();

		if (this.worldObj != null)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj);
			}
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (this.items[par1] != null)
		{
			ItemStack itemstack = this.items[par1];
			this.items[par1] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.items[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
		{
			par2ItemStack.stackSize = this.getInventoryStackLimit();
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

	/** NBT Data */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		this.items = new ItemStack[this.getSizeInventory()];

		if (nbt.hasKey("Items"))
		{
			NBTTagList var2 = nbt.getTagList("Items");

			for (int var3 = 0; var3 < var2.tagCount(); ++var3)
			{
				NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
				byte var5 = var4.getByte("Slot");

				if (var5 >= 0 && var5 < this.items.length)
				{
					this.items[var5] = ItemStack.loadItemStackFromNBT(var4);
				}
			}
			if (nbt.hasKey("Count") && this.items[0] != null)
			{
				this.items[0].stackSize = nbt.getInteger("Count");
			}
		}
		else
		{
			ItemStack stack = new ItemStack(nbt.getInteger("itemID"), nbt.getInteger("Count"), nbt.getInteger("itemMeta"));
			if (stack != null && stack.itemID != 0 && stack.stackSize > 0)
			{
				this.sampleStack = stack;
				this.buildInventory();
			}
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		this.buildSampleStack(false);
		ItemStack stack = this.getSampleStack();
		if (stack != null)
		{
			nbt.setInteger("itemID", stack.itemID);
			nbt.setInteger("itemMeta", stack.getItemDamage());
			nbt.setInteger("Count", stack.stackSize);
		}
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public int getSizeInventory()
	{
		if (worldObj != null)
		{
			return this.getSlotCount();
		}
		return this.items.length;
	}

	@Override
	public String getInvName()
	{
		return "inv.Crate";
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		if (slot >= this.getSlotCount())
		{
			return false;
		}
		if (this.sampleStack == null || itemstack != null && itemstack.isItemEqual(this.sampleStack))
		{
			return true;
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		if (slots == null || slots != null && slots.length != this.getSlotCount())
		{
			slots = new int[this.getSlotCount()];
			for (int i = 0; i < slots.length; i++)
			{
				slots[i] = i;
			}
		}
		return this.slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return this.isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return true;
	}
}
