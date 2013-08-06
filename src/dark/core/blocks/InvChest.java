package dark.core.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import dark.api.IExternalInv;
import dark.api.IInvBox;

public class InvChest implements IInvBox
{
    /** Access able slots side all */
    private int[] chestSlots;
    /** Items contained in this inv */
    private ItemStack[] items;
    /** Host tileEntity */
    private TileEntity hostChest;
    /** Host tileEntity as external inv */
    private IExternalInv inv;
    /** Default slot max count */
    private final int slots;

    public InvChest(TileEntity chest, IExternalInv inv, int slots)
    {
        this.hostChest = chest;
        this.slots = slots;
        this.inv = inv;
    }

    public InvChest(TileEntity chest, int slots)
    {
        this(chest, ((IExternalInv) chest), slots);
    }

    @Override
    public int getSizeInventory()
    {
        return slots;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.items[slot];
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
                this.items[slot] = null;
                this.onInventoryChanged();
                return var3;
            }
            else
            {
                var3 = this.items[slot].splitStack(ammount);

                if (this.items[slot].stackSize == 0)
                {
                    this.items[slot] = null;
                }

                this.onInventoryChanged();
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
        if (this.items[par1] != null)
        {
            ItemStack var2 = this.items[par1];
            this.items[par1] = null;
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
        this.items[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }

        this.onInventoryChanged();
    }

    @Override
    public String getInvName()
    {
        return "container.chest";
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
    public boolean isInvNameLocalized()
    {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1)
    {
        if (chestSlots == null)
        {
            this.chestSlots = new int[this.getSizeInventory()];
            for (int i = 0; i < this.chestSlots.length; i++)
            {
                chestSlots[i] = i;
            }
        }
        return this.chestSlots;
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j)
    {
        return this.isItemValidForSlot(i, itemstack) && this.inv.canStore(itemstack, i, ForgeDirection.getOrientation(j));
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j)
    {
        return this.inv.canRemove(itemstack, i, ForgeDirection.getOrientation(j));
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void onInventoryChanged()
    {
        this.hostChest.onInventoryChanged();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.hostChest.worldObj.getBlockTileEntity(this.hostChest.xCoord, this.hostChest.yCoord, this.hostChest.zCoord) != this.hostChest ? false : par1EntityPlayer.getDistanceSq((double) this.hostChest.xCoord + 0.5D, (double) this.hostChest.yCoord + 0.5D, (double) this.hostChest.zCoord + 0.5D) <= 64.0D;
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
        NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < this.items.length; ++var3)
        {
            if (this.items[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                this.items[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }
        nbt.setTag("Items", var2);
    }

    @Override
    public void loadInv(NBTTagCompound nbt)
    {
        // chest inv reading
        NBTTagList var2 = nbt.getTagList("Items");
        this.items = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
            int var5 = var4.getByte("Slot") & 255;

            if (var5 >= 0 && var5 < this.items.length)
            {
                this.items[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

    }

}
