package dark.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import dark.core.items.EnumMeterials;
import dark.core.items.EnumOreParts;

public class CoreRecipeLoader extends RecipeLoader
{

	/* BLOCKS */
	public static Block blockOre;

	/* ITEMS */
	public static Item itemMetals;

	@Override
	public void loadRecipes()
	{
		super.loadRecipes();
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
