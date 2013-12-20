package dark.api.farm;

import java.util.HashMap;

import net.minecraft.item.ItemStack;

import com.builtbroken.common.Pair;

/** Tracks a list of all crops that can be auto farmed. Does some guessing on block to avoid having
 * each mod register all its crops
 * 
 * @author DarkGuardsman */
public class CropAutomationHandler
{
    //TODO handle special cases
    public static HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> cropList = new HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>>();
    public static HashMap<Object, Class<? extends ICropHandler>> specialCropCases = new HashMap<Object, Class<? extends ICropHandler>>();

    static
    {
        //TODO add some items here but leave most of the work to some of the sub methods that will ID and upload all crop like blocks
    }

    public void addCrop(ItemStack stack, int level, int time)
    {
        if (stack != null)
        {
            Pair<Integer, Integer> par = new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage());
            Pair<Integer, Integer> par2 = new Pair<Integer, Integer>(level, time);
            if (!cropList.containsKey(par))
            {
                cropList.put(par, par2);
            }
        }
    }

    public static boolean isAllowedCrop(ItemStack stack)
    {
        return stack != null && cropList.containsKey(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage()));
    }

    /** Called after all mods/blocks have loaded to auto sort threw blocks looking for anything that
     * might be close to decayable matter */
    public static void triggerPostBlockAddition()
    {
        //TODO parse the list of blocks and auto add all crop blocks.
    }
}
