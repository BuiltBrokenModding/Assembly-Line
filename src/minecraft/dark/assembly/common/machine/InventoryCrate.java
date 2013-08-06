package dark.assembly.common.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import dark.core.blocks.InvChest;

public class InventoryCrate extends InvChest
{
    /** slots that can be accessed from the side */
    private int[] slots;
    /** Contained items */
    private ItemStack[] items = new ItemStack[1028];
    TileEntityCrate crate = null;

    public InventoryCrate(TileEntityCrate crate)
    {
        super(crate, 1028);
        this.crate = crate;

    }

    /** Clones the single stack into an inventory format for automation interaction */
    public void buildInventory(ItemStack sampleStack)
    {
        ItemStack baseStack = sampleStack.copy();

        this.items = new ItemStack[crate.getSlotCount()];

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
            PacketManager.sendPacketToClients(crate.getDescriptionPacket(), crate.worldObj);
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.crate.worldObj.getBlockTileEntity(this.crate.xCoord, this.crate.yCoord, this.crate.zCoord) != this.crate ? false : par1EntityPlayer.getDistanceSq(crate.xCoord + 0.5D, crate.yCoord + 0.5D, crate.zCoord + 0.5D) <= 64.0D;
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
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public int getSizeInventory()
    {
        if (crate != null)
        {
            return crate.getSlotCount();
        }
        return 1028;
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
        if (slot >= crate.getSlotCount())
        {
            return false;
        }
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if (slots == null || slots != null && slots.length != crate.getSlotCount())
        {
            slots = new int[crate.getSlotCount()];
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
        return this.isItemValidForSlot(slot, itemstack) && this.crate.canStore(itemstack, slot, ForgeDirection.getOrientation(side));
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int side)
    {
        return crate.canRemove(itemstack, slot, ForgeDirection.getOrientation(side));
    }

    @Override
    public void onInventoryChanged()
    {
        crate.onInventoryChanged();
    }

    @Override
    public ItemStack[] getContainedItems()
    {
        if (this.items == null)
        {
            this.items = new ItemStack[this.getSizeInventory()];
        }
        return this.items;
    }

    @Override
    public void saveInv(NBTTagCompound nbt)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadInv(NBTTagCompound nbt)
    {
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

    }
}