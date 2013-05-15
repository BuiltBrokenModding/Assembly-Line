package assemblyline.common.block;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import universalelectricity.prefab.implement.ITier;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import assemblyline.common.AssemblyLine;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileEntityCrate extends TileEntityAdvanced implements ITier, IInventory, IPacketReceiver, ISidedInventory
{
	/* Collective total stack of all inv slots */
	private ItemStack sampleStack;
	/* Slots that can be accesed threw ISidedInv */
	private int[] slots;
	/* Sudo inv for the crate to interact with other things */
	private ItemStack[] items = new ItemStack[1028];

	public long prevClickTime = -1000;

	@Override
	public void initiate()
	{
		slots = new int[this.getSlotCount()];
		for (int i = 0; i < slots.length; i++)
		{
			slots[i] = i;
		}
		ItemStack[] itemSet = this.items.clone();
		this.items = new ItemStack[this.getSlotCount()];
		for (int i = 0; i < itemSet.length; i++)
		{
			if (i < this.items.length)
			{
				this.items[i] = itemSet[i];
			}
		}
		this.buildSampleStack();
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj);
		}
	}

	public void buildSampleStack()
	{
		int count = 0;
		int id = 0;
		int meta = 0;

		boolean rebuildBase = false;

		for (int i = 0; i < this.items.length; i++)
		{
			ItemStack stack = this.items[i];
			if (stack != null && stack.itemID != 0)
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

		if (rebuildBase && this.getSampleStack() != null)
		{
			ItemStack baseStack = this.getSampleStack().copy();
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
	}

	public ItemStack getSampleStack()
	{
		if (this.sampleStack == null)
		{
			this.buildSampleStack();
		}
		return this.sampleStack;
	}

	public int getSlotCount()
	{
		return TileEntityCrate.getSlotCount(this.getTier());
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
		return this.ticks <= 1;
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

	/**
	 * Inventory functions.
	 */
	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.items[par1];
	}

	@Override
	public ItemStack decrStackSize(int slot, int ammount)
	{
		if (this.items[slot] != null)
		{
			ItemStack var3;

			if (this.items[slot].stackSize <= ammount)
			{
				var3 = this.items[slot];
				this.setInventorySlotContents(slot, null);
				return var3;
			}
			else
			{
				var3 = this.items[slot].splitStack(ammount);

				if (this.items[slot].stackSize == 0)
				{
					this.items[slot] = null;
				}

				this.setInventorySlotContents(slot, this.items[slot]);

				return var3;
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
		if (this.items[slot] != null)
		{
			ItemStack var2 = this.items[slot];
			this.items[slot] = null;
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
			this.items[slot] = stack;

			if (stack.stackSize > this.getInventoryStackLimit())
			{
				stack.stackSize = this.getInventoryStackLimit();
			}
		}
		else
		{
			this.items[slot] = null;
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
				this.items[0] = stack;
			}
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		this.buildSampleStack();
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
	public int getTier()
	{
		return this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public void setTier(int tier)
	{
		this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, tier, 3);
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack)
	{
		if (this.sampleStack == null)
		{
			return true;
		}
		else if (itemstack != null && itemstack.equals(this.sampleStack))
		{
			return true;
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1)
	{
		return this.slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		if (this.getSampleStack() == null)
		{
			return true;
		}
		else if (itemstack != null && itemstack.isItemEqual(this.getSampleStack()))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return true;
	}
}
