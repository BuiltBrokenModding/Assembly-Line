package dark.core.prefab.machine;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import dark.api.AccessLevel;
import dark.api.ISpecialAccess;
import dark.core.interfaces.IExternalInv;
import dark.core.interfaces.IInvBox;
import dark.core.prefab.access.UserAccess;
import dark.core.prefab.invgui.InvChest;

/** Prefab for simple object who only need basic inv support and nothing more
 * 
 * @author Darkguardsman */
public class TileEntityInv extends TileEntityAdvanced implements IExternalInv, ISidedInventory, ISpecialAccess
{
    protected IInvBox inventory;
    protected boolean lockInv;
    protected int invSlots = 1;
    /** A list of user access data. */
    protected final List<UserAccess> users = new ArrayList<UserAccess>();

    @Override
    public IInvBox getInventory()
    {
        if (inventory == null)
        {
            inventory = new InvChest(this, this.invSlots);
        }
        return inventory;
    }

    /** Gets the container class that goes with this tileEntity when creating a gui */
    public Class<? extends Container> getContainer()
    {
        return null;
    }

    @Override
    public int getSizeInventory()
    {
        return this.getInventory().getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return this.getInventory().getStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        return this.getInventory().decrStackSize(i, j);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        return this.getInventory().getStackInSlotOnClosing(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        this.getInventory().setInventorySlotContents(i, itemstack);

    }

    @Override
    public String getInvName()
    {
        return this.getInventory().getInvName();
    }

    @Override
    public boolean isInvNameLocalized()
    {
        return this.getInventory().isInvNameLocalized();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return this.getInventory().getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return this.getInventory().isUseableByPlayer(entityplayer);
    }

    @Override
    public void openChest()
    {
        this.getInventory().openChest();

    }

    @Override
    public void closeChest()
    {
        this.getInventory().closeChest();

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return this.getInventory().isItemValidForSlot(i, itemstack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1)
    {
        return this.getInventory().getAccessibleSlotsFromSide(var1);
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j)
    {
        return this.getInventory().canInsertItem(i, itemstack, j);
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j)
    {
        return this.getInventory().canExtractItem(i, itemstack, j);
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        if (slot >= this.getSizeInventory())
        {
            return false;
        }
        return true;
    }

    /*
     * User access
     */

    @Override
    public AccessLevel getUserAccess(String username)
    {
        for (int i = 0; i < this.users.size(); i++)
        {
            if (this.users.get(i).username.equalsIgnoreCase(username))
            {
                return this.users.get(i).level;
            }
        }
        return AccessLevel.NONE;
    }

    public boolean canUserAccess(String username)
    {
        return (this.getUserAccess(username).ordinal() > AccessLevel.BASIC.ordinal());
    }

    @Override
    public List<UserAccess> getUsers()
    {
        return this.users;
    }

    @Override
    public List<UserAccess> getUsersWithAcess(AccessLevel level)
    {
        List<UserAccess> players = new ArrayList<UserAccess>();

        for (int i = 0; i < this.users.size(); i++)
        {
            UserAccess ref = this.users.get(i);

            if (ref.level == level)
            {
                players.add(ref);
            }
        }
        return players;

    }

    @Override
    public boolean addUserAccess(String player, AccessLevel lvl, boolean save)
    {
        this.removeUserAccess(player);
        boolean bool = this.users.add(new UserAccess(player, lvl, save));
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        return bool;
    }

    @Override
    public boolean removeUserAccess(String player)
    {
        List<UserAccess> removeList = new ArrayList<UserAccess>();
        for (int i = 0; i < this.users.size(); i++)
        {
            UserAccess ref = this.users.get(i);
            if (ref.username.equalsIgnoreCase(player))
            {
                removeList.add(ref);
            }
        }
        if (removeList != null && removeList.size() > 0)
        {

            boolean bool = this.users.removeAll(removeList);
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            return bool;
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.getInventory().loadInv(nbt);
        // Read user list
        this.users.clear();

        NBTTagList userList = nbt.getTagList("Users");

        for (int i = 0; i < userList.tagCount(); ++i)
        {
            NBTTagCompound var4 = (NBTTagCompound) userList.tagAt(i);
            this.users.add(UserAccess.loadFromNBT(var4));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.getInventory().saveInv(nbt);
        // Write user list
        NBTTagList usersTag = new NBTTagList();
        for (int player = 0; player < this.users.size(); ++player)
        {
            UserAccess access = this.users.get(player);
            if (access != null && access.shouldSave)
            {
                NBTTagCompound accessData = new NBTTagCompound();
                access.writeToNBT(accessData);
                usersTag.appendTag(accessData);
            }
        }

        nbt.setTag("Users", usersTag);
    }
}
