package dark.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import dark.core.RecipeLoader.RecipeGrid;
import dark.core.items.EnumMeterials;
import dark.core.items.EnumOreParts;
import dark.core.items.ItemTools;
import dark.core.items.ItemWrench;

public class CoreRecipeLoader extends RecipeLoader
{

    /* BLOCKS */
    public static Block blockOre, blockDebug, blockWire;

    /* ITEMS */
    public static Item itemMetals, battery, itemTool;
    public static ItemWrench wrench;

    @Override
    public void loadRecipes()
    {
        super.loadRecipes();
        new RecipeGrid(new ItemStack(itemTool, 1, 0), 3, 2).setRowOne("ironTube", "valvePart", "ironTube").setRowTwo(null, "ironTube", null).RegisterRecipe();

        loadSmeltingRecipes();
    }

    public void loadSmeltingRecipes()
    {
        if (blockOre != null && itemMetals != null)
        {
            for (int i = 0; i < EnumMeterials.values().length; i++)
            {
                if (EnumMeterials.values()[i].doWorldGen)
                {
                    FurnaceRecipes.smelting().addSmelting(blockOre.blockID, i, new ItemStack(itemMetals.itemID, 1, 40 + i), 0.6f);
                }
                if (EnumMeterials.values()[i].shouldCreateItem(EnumOreParts.DUST) && EnumMeterials.values()[i] != EnumMeterials.WOOD && EnumMeterials.values()[i] != EnumMeterials.COAL)
                {
                    FurnaceRecipes.smelting().addSmelting(itemMetals.itemID, i + 20, new ItemStack(itemMetals.itemID, 1, 40 + i), 0.6f);
                }
            }
        }
    }
}
