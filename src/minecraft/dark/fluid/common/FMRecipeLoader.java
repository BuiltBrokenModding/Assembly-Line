package dark.fluid.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

	public static ItemStack ironTube;
	public static ItemStack bronzeTube;
	public static ItemStack obbyTube;
	public static ItemStack netherTube;
	public static ItemStack leatherSeal;
	public static ItemStack slimeSeal;
	public static ItemStack valvePart;
	public static ItemStack unfinishedTank;

	public static ItemStack ironPipe;
	public static ItemStack bronzePipe;

	public static ItemStack fuelPipe;
	public static ItemStack lavePipe;
	public static ItemStack oilPipe;
	public static ItemStack bioPipe;
	public static ItemStack wastePipe;

	@Override
	public void loadRecipes()
	{
		ironTube = new ItemStack(itemParts, 1, Parts.Iron.ordinal());
		bronzeTube = new ItemStack(itemParts, 1, Parts.Bronze.ordinal());
		obbyTube = new ItemStack(itemParts, 1, Parts.Obby.ordinal());
		netherTube = new ItemStack(itemParts, 1, Parts.Nether.ordinal());
		leatherSeal = new ItemStack(itemParts, 1, Parts.Seal.ordinal());
		slimeSeal = new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal());
		valvePart = new ItemStack(itemParts, 1, Parts.Tank.ordinal());
		unfinishedTank = new ItemStack(itemParts, 1, Parts.Tank.ordinal());

		ironPipe = new ItemStack(blockGenPipe, 1, 15);
		bronzePipe = new ItemStack(blockPipe, 4, ColorCode.ORANGE.ordinal());

		this.registerItems();
		this.registerPipes();
		this.registerTanks();

		// generator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this.blockGenerator, 1), new Object[] { "@T@", "OVO", "@T@", 'T', new ItemStack(blockRod, 1), '@', "plateSteel", 'O', "basicCircuit", 'V', "motor" }));
		// mechanical rod
		GameRegistry.addRecipe(new ItemStack(blockRod, 1), new Object[] { "I@I", 'I', Item.ingotIron, '@', new ItemStack(itemParts, 1, Parts.Iron.ordinal()) });
		// pipe gauge
		new RecipeGrid(itemGauge, 3, 2).setRowOne("ironTube", "valvePart", "ironTube").setRowTwo(null, "ironTube", null).RegisterRecipe();

		// white pipe crafting -- has to be separate since iron pipe is #15 instead of white
		GameRegistry.addRecipe(new ItemStack(blockGenPipe, 4, ColorCode.WHITE.ordinal()), new Object[] { " P ", "PCP", " P ", 'P', blockGenPipe, 'C', new ItemStack(Item.dyePowder, 1, 15) });

		// pump
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 0), new Object[] { "C@C", "BMB", "@X@", '@', "plateSteel", 'X', new ItemStack(blockPipe, 1), 'B', new ItemStack(itemParts, 1, Parts.Valve.ordinal()), 'C', "basicCircuit", 'M', "motor" }));
		// construction pump
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConPump, 1, 0), new Object[] { "@C@", "BMB", "@@@", '@', "plateSteel", 'B', new ItemStack(itemParts, 1, Parts.Valve.ordinal()), 'C', "advancedCircuit", 'M', "motor" }));
		// Drain
		GameRegistry.addRecipe(new ItemStack(blockDrain, 1, 0), new Object[] { "IGI", "SVS", " P ", 'I', Item.ingotIron, 'G', Block.dispenser, 'S', Block.stone, 'P', new ItemStack(blockPipe, 1), 'V', new ItemStack(itemParts, 1, Parts.Valve.ordinal()) });

		// release valve
		GameRegistry.addRecipe(new ItemStack(blockReleaseValve, 1), new Object[] { "RPR", "PVP", "RPR", 'P', new ItemStack(blockPipe, 1), 'V', new ItemStack(itemParts, 1, Parts.Valve.ordinal()), 'R', Item.redstone });
		// sink
		GameRegistry.addRecipe(new ItemStack(blockSink, 1), new Object[] { "I I", "SIS", "SPS", 'P', new ItemStack(blockPipe, 1), 'I', Item.ingotIron, 'S', Block.stone });
		GameRegistry.addRecipe(new ItemStack(blockSink, 1), new Object[] { "I I", "SIS", "SPS", 'P', new ItemStack(blockGenPipe, 1), 'I', Item.ingotIron, 'S', Block.stone });
	}

	public void registerItems()
	{
		// iron tube
		new RecipeGrid(this.setStackSize(ironTube, 4), 3, 1).setRowOne(Item.ingotIron, Item.ingotIron, Item.ingotIron).RegisterRecipe();
		// bronze tube
		new RecipeGrid(this.setStackSize(bronzeTube, 4), 3, 1).setRowOne("ingotBronze", "ingotBronze", "ingotBronze").RegisterRecipe();
		// obby tube
		new RecipeGrid(this.setStackSize(obbyTube, 4), 3, 1).setRowOne(Block.obsidian, Block.obsidian, Block.obsidian).RegisterRecipe();
		// nether tube
		new RecipeGrid(this.setStackSize(netherTube, 4), 3, 1).setRowOne(Block.netherrack, Block.netherrack, Block.netherrack).RegisterRecipe();
		// seal
		new RecipeGrid(this.setStackSize(leatherSeal, 16), 2, 2).setRowOne(Item.leather, Item.leather).setRowTwo(Item.leather, Item.leather).RegisterRecipe();
		// slime steal
		new RecipeGrid(this.setStackSize(slimeSeal, 4)).setRowOne(null, leatherSeal, null).setRowTwo(leatherSeal, Item.slimeBall, leatherSeal).setRowThree(null, leatherSeal, null).RegisterRecipe();
		// part valve
		new RecipeGrid(valvePart, 3, 1).setRowOne(ironTube, Block.lever, ironTube).RegisterRecipe();
		// unfinished tank
		new RecipeGrid(unfinishedTank).setRowOne(null, Item.ingotIron, null).setRowTwo(Item.ingotIron, null, Item.ingotIron).setRowThree(null, Item.ingotIron, null);
		new RecipeGrid(unfinishedTank).setRowOne(null, bronze, null).setRowTwo(bronze, null, bronze).setRowThree(null, bronze, null);

	}

	public void registerTanks()
	{
		// lava tank
		new RecipeGrid(new ItemStack(blockTank, 4, ColorCode.RED.ordinal())).setRowOne(Block.netherrack, Block.obsidian, Block.netherrack).setRowTwo(Block.obsidian, null, Block.obsidian).setRowThree(Block.netherrack, Block.obsidian, Block.netherrack);
		// water tank
		GameRegistry.addShapelessRecipe(new ItemStack(blockTank, 1, ColorCode.BLUE.ordinal()), new Object[] { new ItemStack(blockTank, 4, ColorCode.NONE.ordinal()), new ItemStack(Item.dyePowder, 1, ColorCode.BLUE.ordinal()) });
		// milk tank
		new RecipeGrid(new ItemStack(blockTank, 4, ColorCode.WHITE.ordinal())).setRowOne(Block.planks, Block.glass, Block.planks).setRowTwo(Block.glass, null, Block.glass).setRowThree(Block.planks, Block.glass, Block.planks);
		// generic Tank
		new RecipeGrid(new ItemStack(blockTank, 4, ColorCode.NONE.ordinal())).setRowOne(Item.ingotIron, Block.glass, Item.ingotIron).setRowTwo(Block.glass, null, Block.glass).setRowThree(Item.ingotIron, Block.glass, Item.ingotIron);
	}

	public void registerPipes()
	{
		// Iron Pipe
		GameRegistry.addShapelessRecipe(ironPipe, new Object[] { ironTube, leatherSeal });
		// Lava Tube
		new RecipeGrid(new ItemStack(blockPipe, 1, ColorCode.RED.ordinal()), 3, 1).setRowOne(netherTube, obbyTube, netherTube).RegisterRecipe();
		// fuel pipe
		GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.YELLOW.ordinal()), new Object[] { new ItemStack(blockGenPipe, 1, ColorCode.YELLOW.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.YELLOW.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.YELLOW.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.YELLOW.ordinal()), new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal()) });

		// oil pipe
		GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.BLACK.ordinal()), new Object[] { new ItemStack(blockGenPipe, 1, ColorCode.BLACK.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLACK.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLACK.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLACK.ordinal()), new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal()) });

		// water pipe
		GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.BLUE.ordinal()), new Object[] { new ItemStack(blockGenPipe, 1, ColorCode.BLUE.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLUE.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLUE.ordinal()), new ItemStack(blockGenPipe, 1, ColorCode.BLUE.ordinal()), new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal()) });

		// bronze pipes
		GameRegistry.addShapelessRecipe(bronzePipe, new Object[] { bronzeTube, slimeSeal });
		// generic pipe crafting
		for (int pipeMeta = 0; pipeMeta < 15; pipeMeta++)
		{
			if (pipeMeta != ColorCode.WHITE.ordinal() && pipeMeta != ColorCode.NONE.ordinal())
			{
				new RecipeGrid(new ItemStack(blockGenPipe, 4, pipeMeta)).setRowOne(null, blockGenPipe, null).setRowTwo(blockGenPipe, new ItemStack(Item.dyePowder, 1, pipeMeta), blockGenPipe).setRowOne(null, blockGenPipe, null);
			}
		}
		new RecipeGrid(ironPipe, 1, 1).setRowOne(blockGenPipe).RegisterRecipe();
		new RecipeGrid(ironPipe, 1, 1).setRowOne(blockPipe).RegisterRecipe();

	}
}
