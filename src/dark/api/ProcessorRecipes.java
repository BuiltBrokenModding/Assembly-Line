package dark.api;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import dark.core.prefab.helpers.AutoCraftingManager;
import dark.core.prefab.helpers.Pair;

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

    }

    static
    {
        createRecipe(ProcessorType.CRUSHER, new ItemStack(Block.stone.blockID, 1, 0), new ItemStack(Block.cobblestone.blockID, 1, 0));
        createRecipe(ProcessorType.GRINDER, new ItemStack(Block.cobblestone.blockID, 1, 0), new ItemStack(Block.sand.blockID, 1, 0));
        createSalvageRecipe(ProcessorType.CRUSHER, new ItemStack(Block.chest, 1), .8f);

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

    public static void createRecipeWithChance(ProcessorType type, Object in, Object out, float chance)
    {
        if (in != null && out != null && type != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null)
            {
                HashMap<Pair<Integer, Integer>, Pair<ItemStack, Float>> map = type.recipesChance;
                if (map != null && !map.containsKey(input))
                {
                    map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), new Pair<ItemStack, Float>(output, chance));
                }
            }
        }
    }

    public static void createSalvageRecipe(ProcessorType type, Object in, float chance)
    {
        if (in != null && type != null)
        {
            ItemStack input = convert(in);
            if (input != null && input != null)
            {
                HashMap<Pair<Integer, Integer>, Float> map = type.recipesChanceSalvage;
                if (map != null && !map.containsKey(input))
                {
                    map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), chance);
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

    public static ItemStack[] getOuput(ProcessorType type, ItemStack stack)
    {
        if (stack == null || type == null || stack.getItem() == null)
        {
            return null;
        }
        HashMap<Pair<Integer, Integer>, ItemStack> map = type.recipes;
        HashMap<Pair<Integer, Integer>, Pair<ItemStack, Float>> mapChance = type.recipesChance;
        HashMap<Pair<Integer, Integer>, Float> mapSalvage = type.recipesChanceSalvage;
        Pair<Integer, Integer> blockSet = new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage());
        if (map == null)
        {
            return null;
        }
        ItemStack re = map.get(new Pair<Integer, Integer>(stack.itemID, -1));
        if (re != null)
        {
            return new ItemStack[] { re };
        }
        re = map.get(blockSet);
        if (re != null)
        {
            return new ItemStack[] { re };
        }
        Pair<ItemStack, Float> ree = mapChance.get(blockSet);
        if (ree != null && random.nextFloat() >= ree.getValue())
        {
            return new ItemStack[] { ree.getKey() };
        }
        float chance = 0;
        try
        {
            chance = mapSalvage != null ? mapSalvage.get(blockSet) : 0;
        }
        catch (Exception e)
        {
        }
        if (chance == 0)
        {
            chance = .1f;
        }
        ItemStack[] recipeList = AutoCraftingManager.getReverseRecipe(stack.copy());
        ItemStack[] reList = null;
        if (recipeList != null)
        {
            reList = new ItemStack[recipeList.length];
            for (int i = 0; i < recipeList.length; i++)
            {
                if (recipeList[i] != null && random.nextFloat() >= chance)
                {
                    int meta = recipeList[i].getItemDamage();
                    NBTTagCompound tag = recipeList[i].getTagCompound();
                    if (recipeList[i].itemID < Block.blocksList.length && Block.blocksList[recipeList[i].itemID] != null && recipeList[i].getItemDamage() > 16)
                    {
                        meta = 0;

                    }
                    reList[i] = new ItemStack(recipeList[i].itemID, recipeList[i].stackSize, meta);
                    reList[i].setTagCompound(tag);
                }
            }
        }
        return reList;
    }

    public static void parseOreNames()
    {

    }
}
