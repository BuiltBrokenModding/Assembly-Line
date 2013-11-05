package dark.api.reciepes;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import com.builtbroken.common.Pair;

import cpw.mods.fml.common.registry.GameRegistry;
import dark.api.ColorCode;
import dark.core.common.CoreRecipeLoader;
import dark.core.common.items.EnumMaterial;
import dark.core.common.items.EnumOrePart;
import dark.core.common.items.ItemOreDirv;
import dark.core.prefab.helpers.AutoCraftingManager;

/** Recipes for ore processor machines
 *
 * @author DarkGuardsman */
public class ProcessorRecipes
{
    private static Random random = new Random();
    private static boolean loadedOres = false;

    static
    {
        createRecipe(ProcessorType.CRUSHER, Block.stone, Block.cobblestone);
        createRecipe(ProcessorType.CRUSHER, Block.oreDiamond, Item.diamond);
        createRecipe(ProcessorType.CRUSHER, Block.oreLapis, new ItemStack(Item.dyePowder.itemID, 4, ColorCode.BLUE.ordinal()));
        createRecipe(ProcessorType.CRUSHER, Block.oreRedstone, new ItemStack(Item.redstone.itemID, 4, 0));
        createRecipe(ProcessorType.CRUSHER, Block.oreEmerald, new ItemStack(Item.redstone.itemID, 4, 0));

        createRecipe(ProcessorType.GRINDER, new ItemStack(Block.cobblestone.blockID, 1, 0), new ItemStack(Block.sand.blockID, 1, 0));
        createRecipe(ProcessorType.GRINDER, Block.glass, Block.sand);

        ProcessorRecipes.setAltOutput(ProcessorType.GRINDER, Block.stone, Block.cobblestone);
        ProcessorRecipes.setAltOutput(ProcessorType.GRINDER, Block.cobblestoneMossy, Block.cobblestone);
        ProcessorRecipes.setAltOutput(ProcessorType.GRINDER, Block.glass, Block.sand);
        ProcessorRecipes.setAltOutput(ProcessorType.CRUSHER, Item.stick, null);

        ProcessorRecipes.setAltOutput(ProcessorType.CRUSHER, Block.stone, Block.cobblestone);
        ProcessorRecipes.setAltOutput(ProcessorType.CRUSHER, Block.cobblestoneMossy, Block.cobblestone);
        ProcessorRecipes.setAltOutput(ProcessorType.CRUSHER, Item.stick, null);

        //TODO replace these with ItemOreDirv
        ProcessorRecipes.setAltOutput(ProcessorType.CRUSHER, Block.glass, Block.sand);
    }

    /** Creates a simple one itemStack in one ItemStack out. Itemstack output can actual have a stack
     * size larger than one
     *
     * @param type - processor type
     * @param in - input item, stacksize is ignored
     * @param out - ouput item */
    public static void createRecipe(ProcessorType type, Object in, Object out)
    {
        createRecipe(type, in, out, -1, -1);
    }

    public static void createRecipe(ProcessorType type, Object in, Object out, int min, int max)
    {
        if (in != null && out != null && type != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null && type.recipes != null)
            {
                if (min == -1)
                {
                    min = output.stackSize;
                }
                if (max == -1 || max < min)
                {
                    max = output.stackSize;
                }
                type.recipes.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), new ProcessorRecipe(output, min, max));
            }
        }
    }

    /** Used to track items that should be converted to different items during salvaging. */
    public static void setAltOutput(ProcessorType type, Object in, Object out)
    {
        if (in != null && out != null && type != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null && type.altOutput != null)
            {
                type.altOutput.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), output);
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
            type.banList.add(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage()));
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
        if (stack != null && type != null)
        {
            ItemStack[] reList = getOuputNormal(type, stack);
            if (reList == null)
            {
                reList = salvageItem(type, stack);
            }
            return reList;
        }
        return null;
    }

    public static ItemStack[] salvageItem(ProcessorType type, ItemStack stack)
    {
        //TODO find a way around having to force single output size salvaging
        ItemStack[] recipeList = AutoCraftingManager.getReverseRecipe(stack.copy(), 1);
        ItemStack[] reList = new ItemStack[recipeList.length];
        for (int i = 0; i < recipeList.length; i++)
        {
            if (recipeList[i] != null && random.nextFloat() >= .3f)
            {
                reList[i] = recipeList[i];
                if (recipeList[i].itemID < Block.blocksList.length && Block.blocksList[recipeList[i].itemID] != null && recipeList[i].getItemDamage() > 16)
                {
                    reList[i].setItemDamage(0);
                }
                if (type.altOutput != null && type.altOutput.containsKey(new Pair<Integer, Integer>(reList[i].itemID, reList[i].getItemDamage())))
                {
                    reList[i] = convert(type.altOutput.get(new Pair<Integer, Integer>(reList[i].itemID, reList[i].getItemDamage())));
                }
            }
        }
        return reList;
    }

    public static ItemStack[] getOuputNormal(ProcessorType type, ItemStack stack)
    {
        if (type.recipes != null)
        {
            ProcessorRecipe re = type.recipes.get(new Pair<Integer, Integer>(stack.itemID, -1));
            if (re == null || re.output == null)
            {
                re = type.recipes.get(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage()));
            }
            if (type.altOutput != null && (re == null || re.output == null))
            {
                return new ItemStack[] { type.altOutput.get(new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage())) };
            }
            if (re != null && re.output != null)
            {
                ItemStack output = re.output.copy();
                output.stackSize = Math.min(re.maxItemsOut, re.minItemsOut + random.nextInt(re.minItemsOut));
                if (re.chancePerItem < 1.0f)
                {

                }
                return new ItemStack[] { output };
            }
        }

        return null;
    }

    public static void parseOreNames(Configuration config)
    {
        if (!loadedOres && CoreRecipeLoader.itemMetals instanceof ItemOreDirv)
        {
            for (EnumMaterial mat : EnumMaterial.values())
            {                //Ingots
                List<ItemStack> ingots = OreDictionary.getOres("ingot" + mat.simpleName);
                ingots.addAll(OreDictionary.getOres(mat.simpleName + "ingot"));
                //plate
                List<ItemStack> plates = OreDictionary.getOres("plate" + mat.simpleName);
                plates.addAll(OreDictionary.getOres(mat.simpleName + "plate"));
                //ore
                List<ItemStack> ores = OreDictionary.getOres("ore" + mat.simpleName);
                ores.addAll(OreDictionary.getOres(mat.simpleName + "ore"));
                //dust
                List<ItemStack> dusts = OreDictionary.getOres("dust" + mat.simpleName);
                dusts.addAll(OreDictionary.getOres(mat.simpleName + "dust"));
                for (ItemStack du : dusts)
                {
                    if (mat.shouldCreateItem(EnumOrePart.INGOTS) && config.get("OreParser", "OverrideDustSmelthing", true).getBoolean(true))
                    {
                        FurnaceRecipes.smelting().addSmelting(du.itemID, du.getItemDamage(), mat.getStack(EnumOrePart.INGOTS, 1), 0.6f);
                    }
                }

                for (ItemStack ing : ingots)
                {
                    if (mat.shouldCreateItem(EnumOrePart.DUST))
                    {
                        ProcessorRecipes.createRecipe(ProcessorType.GRINDER, ing, mat.getStack(EnumOrePart.DUST, 1));
                        ProcessorRecipes.setAltOutput(ProcessorType.GRINDER, ing, mat.getStack(EnumOrePart.DUST, 1));
                    }
                    if (mat.shouldCreateItem(EnumOrePart.SCRAPS))
                    {
                        ProcessorRecipes.setAltOutput(ProcessorType.CRUSHER, ing, mat.getStack(EnumOrePart.SCRAPS, 1));
                        ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, ing, mat.getStack(EnumOrePart.SCRAPS, 1));
                    }
                    if (mat.shouldCreateItem(EnumOrePart.INGOTS))
                    {
                        GameRegistry.addShapelessRecipe(mat.getStack(EnumOrePart.INGOTS, 1), new Object[] { ing });
                    }
                }
                for (ItemStack pla : plates)
                {
                    if (mat.shouldCreateItem(EnumOrePart.DUST))
                    {

                        ProcessorRecipes.createRecipe(ProcessorType.GRINDER, pla, mat.getStack(EnumOrePart.DUST, 1));
                        ProcessorRecipes.setAltOutput(ProcessorType.GRINDER, pla, mat.getStack(EnumOrePart.DUST, 1));

                    }
                    if (mat.shouldCreateItem(EnumOrePart.SCRAPS))
                    {

                        ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, pla, mat.getStack(EnumOrePart.SCRAPS, 1));
                        ProcessorRecipes.setAltOutput(ProcessorType.CRUSHER, pla, mat.getStack(EnumOrePart.SCRAPS, 1));

                    }
                    if (mat.shouldCreateItem(EnumOrePart.PLATES))
                    {
                        GameRegistry.addShapelessRecipe(mat.getStack(EnumOrePart.PLATES, 1), new Object[] { pla });
                        if (config.get("OreParser", "ForcePlateToIngotDM", true).getBoolean(true))
                        {
                            GameRegistry.addShapelessRecipe(mat.getStack(EnumOrePart.INGOTS, 4), new Object[] { pla });
                        }
                    }
                }
                for (ItemStack ore : ores)
                {
                    if (mat.shouldCreateItem(EnumOrePart.RUBBLE))
                    {
                        ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, ore, mat.getStack(EnumOrePart.RUBBLE, 1), 1, 2);
                    }
                    if (mat.shouldCreateItem(EnumOrePart.DUST))
                    {
                        ProcessorRecipes.createRecipe(ProcessorType.GRINDER, ore, mat.getStack(EnumOrePart.DUST, 1), 1, 3);
                    }
                    if (mat.shouldCreateItem(EnumOrePart.INGOTS) && config.get("OreParser", "OverrideOreSmelthing", true).getBoolean(true))
                    {
                        FurnaceRecipes.smelting().addSmelting(ore.itemID, ore.getItemDamage(), mat.getStack(EnumOrePart.INGOTS, 1), 0.6f);
                    }
                }

            }
            loadedOres = true;
        }
    }
}
