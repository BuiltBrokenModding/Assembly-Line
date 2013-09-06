package dark.api.farm;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import dark.core.prefab.helpers.Pair;

public class DecayMatterList
{
    //TODO handle special cases like single stack items that have non-meta damage values, or zombie flesh that needs to poison the compost.
    public static HashMap<Pair<Integer, Integer>, Pair<Float, Integer>> compostList = new HashMap<Pair<Integer, Integer>, Pair<Float, Integer>>();
    static
    {
        //TODO add some items here but leave most of the work to some of the sub methods that will ID and upload all crop like blocks
    }

    /** Used to flag an itemStack as decayable matter for the compost box and later real time world
     * decay
     * 
     * @param stack - itemID and meta to check against
     * @param output - how many buckets of compost are created. Accepts part buckets
     * @param time - time in which to decay the matter */
    public static void addDecayMatter(ItemStack stack, float output, int time)
    {
        if (stack != null)
        {
            Pair<Integer, Integer> par = new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage());
            Pair<Float, Integer> par2 = new Pair<Float, Integer>(output, time);
            if (!compostList.containsKey(par))
            {
                compostList.put(par, par2);
            }
        }
    }

    /** Gets the time in ticks that the item will decay into compost matter
     * 
     * @param stack
     * @return -1 if the list doesn't contain the ID */
    public static int getDecayTime(ItemStack stack)
    {
        if (stack != null && compostList.containsKey(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage())))
        {
            return compostList.get(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage())).getValue();
        }
        return -1;
    }

    /** Gets the amount of compost matter the itemStack creates on decay
     * 
     * @param stack
     * @return -1 if the list doesn't contain the ID */
    public static float getDecayOuput(ItemStack stack)
    {
        if (stack != null && compostList.containsKey(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage())))
        {
            return compostList.get(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage())).getKey();
        }
        return -1;
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

    /** Loads user settings for items that validate for decay matter */
    public static void parseConfigString(String string)
    {
        if (string != null && !string.isEmpty())
        {
            try
            {
                String[] subStrings = string.split(",");
                for (String str : subStrings)
                {
                    try
                    {
                        String[] split = str.split(":");
                        String ID = split[0];
                        String meta = split[1];
                        String decayT = split[2];
                        String decayO = split[3];
                        try
                        {
                            int blockID = Integer.parseInt(ID);
                            int metaID = Integer.parseInt(meta);
                            int decayTime = Integer.parseInt(decayT);
                            int decayV = Integer.parseInt(decayO);
                            DecayMatterList.addDecayMatter(new ItemStack(blockID, 1, metaID), decayV, decayTime);
                        }
                        catch (Exception e)
                        {
                            //TODO add a string based system that will allow for full item or block names
                            //eg tile.stone:0, tile.wood:2,
                            System.out.println("[FarmTech] Entries for compost list must be Integers");
                            e.printStackTrace();
                        }

                    }
                    catch (Exception e)
                    {
                        System.out.println("[FarmTech] Failed to read entry in compost ID config. Plz check your config for errors");
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("[FarmTech] Failed to parse compost ID list from config. Plz check your config for errors");
                e.printStackTrace();
            }
        }
    }
}
