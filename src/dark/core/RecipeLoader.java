package dark.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public abstract class RecipeLoader
{
	protected static Object circuit;
	protected static Object circuit2;
	protected static Object circuit3;
	protected static Object steel;
	protected static Object steelPlate;
	protected static Object motor;

	/** Should be called to load recipes. The main class only loads ore name items to decrease
	 * chances of missing items in recipes */
	public void loadRecipes()
	{
		/* Vinalla items load first */
		circuit = Item.redstoneRepeater;
		circuit2 = Item.comparator;
		steel = Item.ingotIron;
		steelPlate = Item.ingotGold;
		motor = Block.pistonBase;
		/* Ore directory items load over teh vinalla ones if they are present */
		if (OreDictionary.getOres("basicCircuit").size() > 0)
		{
			circuit = "basicCircuit";
		}
		if (OreDictionary.getOres("advancedCircuit").size() > 0)
		{
			circuit = "advancedCircuit";
		}
		if (OreDictionary.getOres("ingotSteel").size() > 0)
		{
			steel = "ingotSteel";
		}
		if (OreDictionary.getOres("plateSteel").size() > 0)
		{
			steelPlate = "plateSteel";
		}
		if (OreDictionary.getOres("motor").size() > 0)
		{
			motor = "motor";
		}
	}
}
