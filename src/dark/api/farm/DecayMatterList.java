package dark.api.farm;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import dark.prefab.helpers.Pair;

public class DecayMatterList
{
    //TODO handle special cases like single stack items that have non-meta damage values, or zombie flesh that needs to poison the compost.
    public static HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> compostList = new HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>>();
    static
    {
        //TODO add some items here but leave most of the work to some of the sub methods that will ID and upload all crop like blocks
    }

    /** Used to flag an itemStack as decayable matter for the compost box and later real time world
     * decay
     *
     * @param stack - itemID and meta to check against
     * @param output - how many decayed matter to output
     * @param time - time in which to decay the matter */
    public void addDecayMatter(ItemStack stack, int output, int time)
    {
        if (stack != null)
        {
            Pair<Integer, Integer> par = new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage());
            Pair<Integer, Integer> par2 = new Pair<Integer, Integer>(output, time);
            if (!compostList.containsKey(par))
            {
                compostList.put(par, par2);
            }
        }
    }

    public static boolean isDecayMatter(ItemStack stack)
    {
        return stack != null && compostList.containsKey(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage()));
    }

    /** Called after all mods/blocks have loaded to auto sort threw blocks looking for anything that
     * might be close to decayable matter */
    public static void triggerPostBlockAddition()
    {
        //TODO parse the list of blocks and auto add all crop blocks.
    }
}
