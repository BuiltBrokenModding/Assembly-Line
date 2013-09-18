package dark.farmtech.machines;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import dark.api.farm.DecayMatterList;
import dark.core.interfaces.IInvBox;
import dark.core.prefab.invgui.InvChest;
import dark.core.prefab.machine.TileEntityEnergyMachine;

/** Simple box that turns matter into compost to grow plants with
 *
 * 6 slot input stores output as an float that then need to be converted to a bucket of compost
 *
 * @author DarkGuardsman */
public class TileEntityCompBox extends TileEntityEnergyMachine
{
    /** Allow undead parts to be compost */
    public static final boolean undeadCompost = false;

    /** Process time left per slot before the item is processed */
    int[] processTime = new int[6];
    /** Amount of buckets worth of compost that are created */
    float compostBuckets = 0;

    @Override
    public IInvBox getInventory()
    {
        if (inventory == null)
        {
            inventory = new InvChest(this, 6);
        }
        return inventory;
    }

    /** Converts one item in the slot into compost
     *
     * @param slot 0-5 */
    public void process(int slot)
    {
        if (slot < processTime.length)
        {
            this.processTime[slot] = -1;
            float output = DecayMatterList.getDecayOuput(this.getInventory().getStackInSlot(slot));
            if (output >= 0)
            {
                this.compostBuckets += output;
            }
        }
    }

    /** Tests if the item in the slot is read to be processed
     *
     * @param slot 0-5 */
    public boolean canProcess(int slot)
    {
        if (slot < processTime.length && DecayMatterList.isDecayMatter(this.getInventory().getStackInSlot(slot)))
        {
            if (processTime[slot] == -1)
            {
                processTime[slot] = DecayMatterList.getDecayTime(this.getInventory().getStackInSlot(slot));
            }
            else
            {
                if (processTime[slot] <= 0)
                {
                    if (!outputSpace())
                    {
                        return false;
                    }
                    return true;
                }
                processTime[slot] = processTime[slot] - 1;
            }
        }
        return false;
    }

    /** Is there space to output to */
    public boolean outputSpace()
    {
        return false;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return stack != null && DecayMatterList.isDecayMatter(stack);
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        return stack != null && !DecayMatterList.isDecayMatter(stack);
    }
}
