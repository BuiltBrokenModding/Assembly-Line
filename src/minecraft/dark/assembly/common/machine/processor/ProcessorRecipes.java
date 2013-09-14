package dark.assembly.common.machine.processor;

import java.util.HashMap;

import dark.core.prefab.helpers.Pair;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ProcessorRecipes
{
    public static HashMap<Pair<Integer, Integer>, ItemStack> crusherRecipes = new HashMap();
    public static HashMap<Pair<Integer, Integer>, ItemStack> grinderrRecipes = new HashMap();
    public static HashMap<Pair<Integer, Integer>, ItemStack> pressRecipes = new HashMap();

    static
    {
        createABRecipe(ProcessorType.CRUSHER, new ItemStack(Block.stone.blockID, 1, 0), new ItemStack(Block.cobblestone.blockID, 1, 0));
    }

    public static void createABRecipe(ProcessorType type, Object in, Object out)
    {
        if (in != null && out != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null)
            {
                HashMap<Pair<Integer, Integer>, ItemStack> map = null;
                switch (type)
                {
                    case CRUSHER:
                        map = crusherRecipes;
                        break;
                    case GRINDER:
                        map = grinderrRecipes;
                        break;
                    case PRESS:
                        map = pressRecipes;
                        break;
                }
                if (map != null && !crusherRecipes.containsKey(input))
                {
                    crusherRecipes.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), output);
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
        HashMap<Pair<Integer, Integer>, ItemStack> map = null;
        ItemStack testStack = stack.copy();
        testStack.stackSize = 1;
        switch (type)
        {
            case CRUSHER:
                map = crusherRecipes;
                break;
            case GRINDER:
                map = grinderrRecipes;
                break;
            case PRESS:
                map = pressRecipes;
                break;
        }
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

    public static class oreReceipe
    {
        ItemStack input, output;

    }

    public static enum ProcessorType
    {
        CRUSHER(),
        GRINDER(),
        PRESS();
    }
}
