package dark.api;

import java.util.HashMap;

import dark.core.prefab.helpers.Pair;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** Recipes for ore processor machines
 *
 * @author DarkGuardsman */
public class ProcessorRecipes
{
    public static enum ProcessorType
    {
        CRUSHER("crusher"),
        GRINDER("grinder"),
        PRESS("press");
        public String unlocalizedContainerName;
        public HashMap<Pair<Integer, Integer>, ItemStack> recipes = new HashMap();

        private ProcessorType(String name)
        {
            this.unlocalizedContainerName = "tile." + name + ".name";
        }
    }

    static
    {
        createRecipe(ProcessorType.CRUSHER, new ItemStack(Block.stone.blockID, 1, 0), new ItemStack(Block.cobblestone.blockID, 1, 0));
        createRecipe(ProcessorType.GRINDER, new ItemStack(Block.cobblestone.blockID, 1, 0), new ItemStack(Block.sand.blockID, 1, 0));

    }

    public static void createRecipe(ProcessorType type, Object in, Object out)
    {
        if (in != null && out != null && type != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null)
            {
                HashMap<Pair<Integer, Integer>, ItemStack> map = type.recipes;
                if (map != null && !map.containsKey(input))
                {
                    map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), output);
                }
            }
        }
    }

    private static ItemStack convert(Object object)
    {
        if (object instanceof ItemStack)
        {
            return (ItemStack) object;
        }
        if (object instanceof Block)
        {
            return new ItemStack(((Block) object).blockID, 1, -1);
        }
        if (object instanceof Item)
        {
            return new ItemStack(((Item) object).itemID, 1, -1);
        }
        return null;
    }

    public static ItemStack getOuput(ProcessorType type, ItemStack stack)
    {
        if (stack == null || type == null)
        {
            return null;
        }
        HashMap<Pair<Integer, Integer>, ItemStack> map = type.recipes;
        ItemStack testStack = stack.copy();
        testStack.stackSize = 1;
        if (map == null)
        {
            return null;
        }
        ItemStack re = map.get(new Pair<Integer, Integer>(stack.itemID, -1));
        if (re != null)
        {
            return re;
        }
        return map.get(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage()));
    }
}
