package dark.api;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public interface IInvBox extends ISidedInventory
{
    /** Gets the inventory array. ForgeDirection.UNKOWN must return all sides */
    public ItemStack[] getContainedItems();

    /** Called to save the inventory array */
    public void saveInv(NBTTagCompound tag);

    /** Called to load the inventory array */
    public void loadInv(NBTTagCompound tag);
}
