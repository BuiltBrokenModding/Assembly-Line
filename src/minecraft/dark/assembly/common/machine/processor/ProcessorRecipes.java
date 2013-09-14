package dark.assembly.common.machine.processor;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ProcessorRecipes
{
    public static HashMap<ItemStack, ItemStack> crusherRecipes = new HashMap();
    public static HashMap<ItemStack, ItemStack> grinderrRecipes = new HashMap();
    public static HashMap<ItemStack, ItemStack> pressRecipes = new HashMap();

    static
    {
        createABRecipe(ProcessorType.CRUSHER, Block.stone, Block.cobblestone);
    }

    public static void createABRecipe(ProcessorType type, Object in, Object out)
    {
        if (in != null && out != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null)
            {
                HashMap<ItemStack, ItemStack> map = null;
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
                    crusherRecipes.put(input, output);
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
            return new ItemStack((Block) object, 1);
        }
        if (object instanceof Item)
        {
            return new ItemStack((Item) object, 1);
        }
        return null;
    }

    public static ItemStack getOuput(ProcessorType type, ItemStack stack)
    {
        if (stack == null || type == null)
        {
            return null;
        }
        HashMap<ItemStack, ItemStack> map = null;
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
        return map == null ? null : map.get(stack);
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
