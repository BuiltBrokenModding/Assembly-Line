package dark.assembly.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class Recipes
{
	static Object circuit;
	static Object circuit2;
	static Object circuit3;
	static Object steel;
	static Object steelPlate;
	static Object motor;

	public static void loadRecipes()
	{
		circuit = Item.redstoneRepeater;
		circuit2 = Item.comparator;
		steel = Item.ingotIron;
		steelPlate = Item.ingotGold;
		motor = Block.pistonBase;

		if (!AssemblyLine.VINALLA_RECIPES)
		{
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
		Recipes.createStandardRecipes();
		Recipes.createUERecipes();
	}

	private static void createUERecipes()
	{
		// Armbot
		GameRegistry.addRecipe(new ShapedOreRecipe(AssemblyLine.blockArmbot, new Object[] { "II ", "SIS", "MCM", 'S', Recipes.steelPlate, 'C', Recipes.circuit2, 'I', Recipes.steel, 'M', Recipes.motor }));
		// Disk
		GameRegistry.addRecipe(new ShapedOreRecipe(AssemblyLine.itemDisk, new Object[] { "III", "ICI", "III", 'I', AssemblyLine.itemImprint, 'C', Recipes.circuit2 }));
		// Encoder
		GameRegistry.addRecipe(new ShapedOreRecipe(AssemblyLine.blockEncoder, new Object[] { "SIS", "SCS", "SSS", 'I', AssemblyLine.itemImprint, 'S', Recipes.steel, 'C', Recipes.circuit2 }));
		// Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(AssemblyLine.blockDetector, new Object[] { "SES", "SCS", "S S", 'S', Recipes.steel, 'C', Recipes.circuit, 'E', Item.eyeOfEnder }));
		// Conveyor Belt
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AssemblyLine.blockConveyorBelt, 10), new Object[] { "III", "WMW", 'I', Recipes.steel, 'W', Block.planks, 'M', Recipes.motor }));
		// Rejector
		GameRegistry.addRecipe(new ShapedOreRecipe(AssemblyLine.blockRejector, new Object[] { "CPC", "@R@", '@', Recipes.steel, 'R', Item.redstone, 'P', Block.pistonBase, 'C', Recipes.circuit }));
		// Turntable
		GameRegistry.addRecipe(new ShapedOreRecipe(AssemblyLine.blockTurntable, new Object[] { "IMI", " P ", 'M', Recipes.motor, 'P', Block.pistonBase, 'I', Recipes.steel }));
		// Manipulator
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(AssemblyLine.blockManipulator, 2), new Object[] { Block.dispenser, Recipes.circuit }));
	}

	private static void createStandardRecipes()
	{
		// Imprint
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AssemblyLine.itemImprint, 2), new Object[] { "R", "P", "I", 'P', Item.paper, 'R', Item.redstone, 'I', new ItemStack(Item.dyePowder, 1, 0) }));
		// Imprinter
		GameRegistry.addRecipe(new ShapedOreRecipe(AssemblyLine.blockImprinter, new Object[] { "SIS", "SPS", "WCW", 'S', Item.ingotIron, 'C', Block.chest, 'W', Block.workbench, 'P', Block.pistonBase, 'I', new ItemStack(Item.dyePowder, 1, 0) }));
		// Crate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AssemblyLine.blockCrate, 1, 0), new Object[] { "TST", "S S", "TST", 'S', Item.ingotIron, 'T', Block.planks }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AssemblyLine.blockCrate, 1, 1), new Object[] { "TST", "SCS", "TST", 'C', new ItemStack(AssemblyLine.blockCrate, 1, 0), 'S', Recipes.steel, 'T', Block.wood }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AssemblyLine.blockCrate, 1, 2), new Object[] { "TST", "SCS", "TST", 'C', new ItemStack(AssemblyLine.blockCrate, 1, 1), 'S', Recipes.steelPlate, 'T', Block.wood }));
	}
}
