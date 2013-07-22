package dark.fluid.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.api.ColorCode;
import dark.core.RecipeLoader;
import dark.fluid.common.item.ItemParts.Parts;

public class FMRecipeLoader extends RecipeLoader
{

	public static Block blockPipe;
	public static Block blockGenPipe;
	public static Block blockTank;
	public static Block blockMachine;
	public static Block blockRod;
	public static Block blockGenerator;
	public static Block blockReleaseValve;
	public static Block blockSink;
	public static Block blockDrain;
	public static Block blockConPump;
	public static Block blockWasteLiquid;

	/* ITEMS */
	public static Item itemParts;
	public static Item itemGauge;

	@Override
	public void loadRecipes()
	{
		// generator
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(this.blockGenerator, 1), new Object[] { "@T@", "OVO", "@T@", 'T', new ItemStack(blockRod, 1), '@', "plateSteel", 'O', "basicCircuit", 'V', "motor" }));
		// pipe gauge
		GameRegistry.addRecipe(new ItemStack(itemGauge, 1, 0), new Object[] { "TVT", " T ", 'V', new ItemStack(itemParts, 1, Parts.Valve.ordinal()), 'T', new ItemStack(itemParts, 1, Parts.Iron.ordinal()) });
		// iron tube
		GameRegistry.addRecipe(new ItemStack(itemParts, 4, Parts.Iron.ordinal()), new Object[] { "@@@", '@', Item.ingotIron });
		// bronze tube
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(itemParts, 4, Parts.Bronze.ordinal()), new Object[] { "@@@", '@', "ingotBronze" }));
		// obby tube
		GameRegistry.addRecipe(new ItemStack(itemParts, 4, Parts.Obby.ordinal()), new Object[] { "@@@", '@', Block.obsidian });
		// nether tube
		GameRegistry.addRecipe(new ItemStack(itemParts, 4, Parts.Nether.ordinal()), new Object[] { "NNN", 'N', Block.netherrack });
		// seal
		GameRegistry.addRecipe(new ItemStack(itemParts, 4, Parts.Seal.ordinal()), new Object[] { "@@", "@@", '@', Item.leather });
		// slime steal
		GameRegistry.addShapelessRecipe(new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal()), new Object[] { new ItemStack(itemParts, 1, Parts.Seal.ordinal()), new ItemStack(Item.slimeBall, 1) });
		// part valve
		GameRegistry.addRecipe(new ItemStack(itemParts, 1, Parts.Valve.ordinal()), new Object[] { "T@T", 'T', new ItemStack(itemParts, 1, Parts.Iron.ordinal()), '@', Block.lever });

		// unfinished tank
		GameRegistry.addRecipe(new ItemStack(itemParts, 1, Parts.Tank.ordinal()), new Object[] { " @ ", "@ @", " @ ", '@', Item.ingotIron });
		// mechanical rod
		GameRegistry.addRecipe(new ItemStack(blockRod, 1), new Object[] { "I@I", 'I', Item.ingotIron, '@', new ItemStack(itemParts, 1, Parts.Iron.ordinal()) });

		// Iron Pipe
		GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 1, 15), new Object[] { new ItemStack(itemParts, 1, Parts.Iron.ordinal()), new ItemStack(itemParts, 1, Parts.Seal.ordinal()) });
		// Lava Tube
		GameRegistry.addRecipe(new ItemStack(blockPipe, 1, ColorCode.RED.ordinal()), new Object[] { "N@N", 'N', new ItemStack(itemParts, 1, Parts.Nether.ordinal()), '@', new ItemStack(itemParts, 1, Parts.Obby.ordinal()) });
		// fuel pipe
		GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.YELLOW.ordinal()), new Object[] { new ItemStack(blockGenPipe, 1, ColorCode.YELLOW.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.YELLOW.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.YELLOW.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.YELLOW.ordinal()), new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal()) });

		// oil pipe
		GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.BLACK.ordinal()), new Object[] { new ItemStack(blockGenPipe, 1, ColorCode.BLACK.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLACK.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLACK.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLACK.ordinal()), new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal()) });

		// water pipe
		GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.BLUE.ordinal()), new Object[] { new ItemStack(blockGenPipe, 1, ColorCode.BLUE.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLUE.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLUE.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLUE.ordinal()), new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal()) });

		// steam pipes
		GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.ORANGE.ordinal()), new Object[] { new ItemStack(itemParts, 1, Parts.Bronze.ordinal()), new ItemStack(itemParts, 1, Parts.Seal.ordinal()) });
		// generic pipe crafting
		for (int pipeMeta = 0; pipeMeta < 15; pipeMeta++)
		{
			if (pipeMeta != ColorCode.WHITE.ordinal() && pipeMeta != ColorCode.NONE.ordinal())
			{
				GameRegistry.addRecipe(new ItemStack(blockGenPipe, 4, pipeMeta), new Object[] { " P ", "PCP", " P ", 'P', blockGenPipe, 'C', new ItemStack(Item.dyePowder, 1, pipeMeta) });
			}
		}
		GameRegistry.addRecipe(new ItemStack(blockGenPipe, 1, 15), new Object[] { "P", 'P', blockGenPipe });
		GameRegistry.addRecipe(new ItemStack(blockGenPipe, 1, 15), new Object[] { "P", 'P', blockPipe });

		// white pipe crafting -- has to be separate since iron pipe is #15 instead of white
		GameRegistry.addRecipe(new ItemStack(blockGenPipe, 4, ColorCode.WHITE.ordinal()), new Object[] { " P ", "PCP", " P ", 'P', blockGenPipe, 'C', new ItemStack(Item.dyePowder, 1, 15) });

		// lava tank
		GameRegistry.addRecipe(new ItemStack(blockTank, 1, ColorCode.RED.ordinal()), new Object[] { "N@N", "@ @", "N@N", 'T', new ItemStack(itemParts, 1, Parts.Tank.ordinal()), '@', Block.obsidian, 'N', Block.netherrack });
		// water tank
		GameRegistry.addRecipe(new ItemStack(blockTank, 1, ColorCode.BLUE.ordinal()), new Object[] { "@G@", "STS", "@G@", 'T', new ItemStack(itemParts, 1, Parts.Tank.ordinal()), '@', Block.planks, 'G', Block.glass, 'S', new ItemStack(itemParts, 1, Parts.Seal.ordinal()) });
		// milk tank
		GameRegistry.addRecipe(new ItemStack(blockTank, 1, ColorCode.WHITE.ordinal()), new Object[] { "W@W", "WTW", "W@W", 'T', new ItemStack(itemParts, 1, Parts.Tank.ordinal()), '@', Block.stone, 'W', Block.planks });
		// generic Tank
		GameRegistry.addRecipe(new ItemStack(blockTank, 1, ColorCode.NONE.ordinal()), new Object[] { "@@@", "@T@", "@@@", 'T', new ItemStack(itemParts, 1, Parts.Tank.ordinal()), '@', Block.stone });

		// pump
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 0), new Object[] { "C@C", "BMB", "@X@", '@', "plateSteel", 'X', new ItemStack(blockPipe, 1), 'B', new ItemStack(itemParts, 1, Parts.Valve.ordinal()), 'C', "basicCircuit", 'M', "motor" }));
		// construction pump
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(blockConPump, 1, 0), new Object[] { "@C@", "BMB", "@@@", '@', "plateSteel", 'B', new ItemStack(itemParts, 1, Parts.Valve.ordinal()), 'C', "advancedCircuit", 'M', "motor" }));
		// Drain
		GameRegistry.addRecipe(new ItemStack(blockDrain, 1, 0), new Object[] { "IGI", "SVS", " P ", 'I', Item.ingotIron, 'G', Block.dispenser, 'S', Block.stone, 'P', new ItemStack(blockPipe, 1), 'V', new ItemStack(itemParts, 1, Parts.Valve.ordinal()) });

		// release valve
		GameRegistry.addRecipe(new ItemStack(blockReleaseValve, 1), new Object[] { "RPR", "PVP", "RPR", 'P', new ItemStack(blockPipe, 1), 'V', new ItemStack(itemParts, 1, Parts.Valve.ordinal()), 'R', Item.redstone });
		// sink
		GameRegistry.addRecipe(new ItemStack(blockSink, 1), new Object[] { "I I", "SIS", "SPS", 'P', new ItemStack(blockPipe, 1), 'I', Item.ingotIron, 'S', Block.stone });
		GameRegistry.addRecipe(new ItemStack(blockSink, 1), new Object[] { "I I", "SIS", "SPS", 'P', new ItemStack(blockGenPipe, 1), 'I', Item.ingotIron, 'S', Block.stone });
	}
}
