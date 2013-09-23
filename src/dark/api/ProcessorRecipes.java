package dark.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.builtbroken.common.Pair;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dark.core.prefab.helpers.AutoCraftingManager;

/** Recipes for ore processor machines
 * 
 * @author DarkGuardsman */
public class ProcessorRecipes
{
    private static Random random = new Random();

    public static enum ProcessorType
    {
        CRUSHER(),
        GRINDER(),
        PRESS();
        public HashMap<Pair<Integer, Integer>, ItemStack> recipes = new HashMap();
        public HashMap<Pair<Integer, Integer>, Pair<ItemStack, Float>> recipesChance = new HashMap();
        public HashMap<Pair<Integer, Integer>, Float> recipesChanceSalvage = new HashMap();
        public HashMap<Pair<Integer, Integer>, ItemStack> damagedOutput = new HashMap();
        public List<Pair<Integer, Integer>> canSalvage = new ArrayList();

    }

    static
    {
        createRecipe(ProcessorType.CRUSHER, Block.stone, Block.cobblestone);
        createRecipe(ProcessorType.CRUSHER, Block.oreDiamond, Item.diamond);
        createRecipe(ProcessorType.CRUSHER, Block.oreLapis, new ItemStack(Item.dyePowder.itemID, 4, ColorCode.BLUE.ordinal()));
        createRecipe(ProcessorType.CRUSHER, Block.oreRedstone, new ItemStack(Item.redstone.itemID, 4, 0));
        createRecipe(ProcessorType.CRUSHER, Block.oreEmerald, new ItemStack(Item.redstone.itemID, 4, 0));

        createRecipe(ProcessorType.GRINDER, new ItemStack(Block.cobblestone.blockID, 1, 0), new ItemStack(Block.sand.blockID, 1, 0));
        createRecipe(ProcessorType.GRINDER, Block.glass, Block.sand);

        ProcessorRecipes.createSalvageDamageOutput(ProcessorType.GRINDER, Block.stone, Block.cobblestone);
        ProcessorRecipes.createSalvageDamageOutput(ProcessorType.GRINDER, Block.cobblestoneMossy, Block.cobblestone);
        ProcessorRecipes.createSalvageDamageOutput(ProcessorType.GRINDER, Block.glass, Block.sand);
        ProcessorRecipes.createSalvageDamageOutput(ProcessorType.CRUSHER, Item.stick, null);

        ProcessorRecipes.createSalvageDamageOutput(ProcessorType.CRUSHER, Block.stone, Block.cobblestone);
        ProcessorRecipes.createSalvageDamageOutput(ProcessorType.CRUSHER, Block.cobblestoneMossy, Block.cobblestone);
        ProcessorRecipes.createSalvageDamageOutput(ProcessorType.CRUSHER, Item.stick, null);

        //TODO replace these with ItemOreDirv
        ProcessorRecipes.createSalvageDamageOutput(ProcessorType.CRUSHER, Block.glass, Block.sand);

        markOutputSalavageWithChance(ProcessorType.CRUSHER, new ItemStack(Block.chest, 1), .8f);
        markOutputSalavageWithChance(ProcessorType.CRUSHER, new ItemStack(Block.brick, 1), .7f);
    }

    /** Creates a simple one itemStack in one ItemStack out. Itemstack output can actual have a stack
     * size larger than one
     * 
     * @param type - processor type
     * @param in - input item, stacksize is ignored
     * @param out - ouput item */
    public static void createRecipe(ProcessorType type, Object in, Object out)
    {
        if (in != null && out != null && type != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null)
            {
                HashMap<Pair<Integer, Integer>, ItemStack> map = type.recipes;
                if (map != null && !map.containsKey(new Pair<Integer, Integer>(input.itemID, input.getItemDamage())))
                {
                    map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), output);
                }
            }
        }
    }

    /** Creates a recipe that has a chance of failing
     * 
     * @param type - processor type
     * @param in - input item stack, stack size is ignored
     * @param out - output item stack, stack size is used
     * @param chance - chance to fail with 1 being zero chance and zero being 100% chance */
    public static void createRecipeWithChance(ProcessorType type, Object in, Object out, float chance)
    {
        if (in != null && out != null && type != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null)
            {
                HashMap<Pair<Integer, Integer>, Pair<ItemStack, Float>> map = type.recipesChance;
                if (map != null && !map.containsKey(new Pair<Integer, Integer>(input.itemID, input.getItemDamage())))
                {
                    map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), new Pair<ItemStack, Float>(output, chance));
                }
            }
        }
    }

    /** Not so much of a recipe but it applies a change on the item. TODO improve and control actual
     * output of the recipe */
    public static void markOutputSalavageWithChance(ProcessorType type, Object in, float chance)
    {
        if (in != null && type != null)
        {
            ItemStack input = convert(in);
            if (input != null && input != null)
            {
                HashMap<Pair<Integer, Integer>, Float> map = type.recipesChanceSalvage;
                if (map != null && !map.containsKey(new Pair<Integer, Integer>(input.itemID, input.getItemDamage())))
                {
                    map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), chance);
                }
            }
        }
    }

    /** Used to track items that should be converted to different items during salvaging. */
    public static void createSalvageDamageOutput(ProcessorType type, Object in, Object out)
    {
        if (in != null && out != null && type != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null)
            {
                HashMap<Pair<Integer, Integer>, ItemStack> map = type.damagedOutput;
                if (map != null)
                {
                    if (!map.containsKey(new Pair<Integer, Integer>(input.itemID, input.getItemDamage())))
                    {
                        map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), output);
                    }
                    else if (map.get(new Pair<Integer, Integer>(input.itemID, input.getItemDamage())) == null)
                    {
                        map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), output);
                    }
                }
            }
        }
    }

    /** Marks an itemstack as unsalvagable by all processors */
    public static void markUnsalvagable(ItemStack stack)
    {
        if (stack != null)
        {
            for (ProcessorType type : ProcessorType.values())
            {
                markUnsalvagable(type, stack);
            }
        }
    }

    /** Marks an itemstack as unsalvagable by processors */
    public static void markUnsalvagable(ProcessorType type, ItemStack stack)
    {
        if (type != null && stack != null)
        {
            type.canSalvage.add(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage()));
        }
    }

    /** Converts an object input into an itemstack for use */
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

    /** Gets the lit of items that are created from the input item stack. General this will be an
     * array of one item. However, in salavaging cases it can be up to 8 items.
     * 
     * @param type - Processor type
     * @param stack - item stack input ignores stacksize
     * @return array of itemStacks */
    public static ItemStack[] getOuput(ProcessorType type, ItemStack stack, boolean damageSalvage)
    {
        if (stack == null || type == null || stack.getItem() == null)
        {
            return null;
        }
        HashMap<Pair<Integer, Integer>, ItemStack> map = type.recipes;
        HashMap<Pair<Integer, Integer>, Pair<ItemStack, Float>> mapChance = type.recipesChance;
        HashMap<Pair<Integer, Integer>, Float> mapSalvage = type.recipesChanceSalvage;
        HashMap<Pair<Integer, Integer>, ItemStack> altSalvageMap = type.damagedOutput;
        Pair<Integer, Integer> blockSet = new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage());

        //Read normal recipe map for outputs
        if (map != null)
        {
            ItemStack re = map.get(new Pair<Integer, Integer>(stack.itemID, -1));
            if (re != null)
            {
                return new ItemStack[] { convert(re) };
            }
            re = map.get(blockSet);
            if (re != null)
            {
                return new ItemStack[] { convert(re) };
            }
        }

        //Read chance output map
        Pair<ItemStack, Float> ree = mapChance.get(blockSet);
        if (ree != null && random.nextFloat() >= ree.getValue())
        {
            return new ItemStack[] { convert(ree.getKey()) };
        }

        //Start salvaging items
        ItemStack[] recipeList = AutoCraftingManager.getReverseRecipe(stack.copy());
        ItemStack[] reList = null;
        if (recipeList != null)
        {
            reList = new ItemStack[recipeList.length];
            for (int i = 0; i < recipeList.length; i++)
            {
                if (recipeList[i] != null && random.nextFloat() >= .3f)
                {
                    int meta = recipeList[i].getItemDamage();
                    reList[i] = recipeList[i];
                    if (recipeList[i].itemID < Block.blocksList.length && Block.blocksList[recipeList[i].itemID] != null && recipeList[i].getItemDamage() > 16)
                    {
                        reList[i].setItemDamage(0);
                    }
                    if (damageSalvage && altSalvageMap != null && altSalvageMap.containsKey(new Pair<Integer, Integer>(reList[i].itemID, reList[i].getItemDamage())))
                    {
                        reList[i] = convert(altSalvageMap.get(new Pair<Integer, Integer>(reList[i].itemID, reList[i].getItemDamage())));
                    }
                }
            }
        }
        return reList;
    }

    public static void parseOreNames()
    {

    }
}
