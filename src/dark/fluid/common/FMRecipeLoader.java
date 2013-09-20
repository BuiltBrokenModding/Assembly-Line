package dark.fluid.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.api.ColorCode;
import dark.core.common.CoreRecipeLoader;
import dark.core.common.RecipeLoader;
import dark.core.common.items.ItemParts.Parts;
import dark.fluid.common.pipes.BlockPipe.PipeData;

public class FMRecipeLoader extends RecipeLoader
{

    public static Block blockPipe;
    public static Block blockTank;
    public static Block blockMachine;
    public static Block blockRod;
    public static Block blockGenerator;
    public static Block blockReleaseValve;
    public static Block blockSink;
    public static Block blockDrain;
    public static Block blockConPump;
    public static Block blockHeater;
    public static Block blockPiston;
    public static Block blockBoiler;

    public static Block blockWasteLiquid;
    public static Block blockOilLiquid;
    public static Block blockFuelLiquid;

    @Override
    public void loadRecipes()
    {
        for (PipeData data : PipeData.values())
        {
            data.itemStack = new ItemStack(blockPipe.blockID, 1, data.ordinal());
        }
        this.registerPipes();
        this.registerTanks();

        // generator
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(FMRecipeLoader.blockGenerator, 1), new Object[] { "@T@", "OVO", "@T@", 'T', new ItemStack(blockRod, 1), '@', "plateSteel", 'O', "basicCircuit", 'V', "motor" }));
        // mechanical rod
        GameRegistry.addRecipe(new ItemStack(blockRod, 1), new Object[] { "I@I", 'I', Item.ingotIron, '@', new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Iron.ordinal()) });

        // pump
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 0), new Object[] { "C@C", "BMB", "@X@", '@', "plateSteel", 'X', new ItemStack(blockPipe, 1), 'B', new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Valve.ordinal()), 'C', "basicCircuit", 'M', "motor" }));
        // construction pump
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConPump, 1, 0), new Object[] { "@C@", "BMB", "@@@", '@', "plateSteel", 'B', new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Valve.ordinal()), 'C', "advancedCircuit", 'M', "motor" }));
        // Drain
        GameRegistry.addRecipe(new ItemStack(blockDrain, 1, 0), new Object[] { "IGI", "SVS", " P ", 'I', Item.ingotIron, 'G', Block.dispenser, 'S', Block.stone, 'P', new ItemStack(blockPipe, 1), 'V', new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Valve.ordinal()) });

        // release valve
        GameRegistry.addRecipe(new ItemStack(blockReleaseValve, 1), new Object[] { "RPR", "PVP", "RPR", 'P', new ItemStack(blockPipe, 1), 'V', new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Valve.ordinal()), 'R', Item.redstone });
        // sink
        GameRegistry.addRecipe(new ItemStack(blockSink, 1), new Object[] { "I I", "SIS", "SPS", 'P', new ItemStack(blockPipe, 1), 'I', Item.ingotIron, 'S', Block.stone });
    }

    public void registerTanks()
    {
        // lava tank
        new RecipeGrid(new ItemStack(blockTank, 4, ColorCode.RED.ordinal())).setRowOne(Block.netherrack, Block.obsidian, Block.netherrack).setRowTwo(Block.obsidian, null, Block.obsidian).setRowThree(Block.netherrack, Block.obsidian, Block.netherrack).RegisterRecipe();
        // water tank
        GameRegistry.addShapelessRecipe(new ItemStack(blockTank, 1, ColorCode.BLUE.ordinal()), new Object[] { new ItemStack(blockTank, 4, 15), new ItemStack(Item.dyePowder, 1, ColorCode.BLUE.ordinal()) });
        // milk tank
        new RecipeGrid(new ItemStack(blockTank, 4, ColorCode.WHITE.ordinal())).setRowOne(Block.planks, Block.glass, Block.planks).setRowTwo(Block.glass, null, Block.glass).setRowThree(Block.planks, Block.glass, Block.planks).RegisterRecipe();
        // generic Tank
        new RecipeGrid(new ItemStack(blockTank, 4, 15)).setRowOne(Item.ingotIron, Block.glass, Item.ingotIron).setRowTwo(Block.glass, null, Block.glass).setRowThree(Item.ingotIron, Block.glass, Item.ingotIron).RegisterRecipe();
    }

    public void registerPipes()
    {
        // Iron Pipe
        GameRegistry.addShapelessRecipe(PipeData.IRON_PIPE.itemStack, new Object[] { CoreRecipeLoader.ironTube, CoreRecipeLoader.leatherSeal });
        // Lava Tube
        new RecipeGrid(new ItemStack(blockPipe, 1, ColorCode.RED.ordinal()), 3, 1).setRowOne(CoreRecipeLoader.netherTube, CoreRecipeLoader.obbyTube, CoreRecipeLoader.netherTube).RegisterRecipe();
        // fuel pipe
        GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.YELLOW.ordinal()), new Object[] { PipeData.YELLOW_PIPE.itemStack, PipeData.YELLOW_PIPE.itemStack, PipeData.YELLOW_PIPE.itemStack, PipeData.YELLOW_PIPE.itemStack, new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.SlimeSeal.ordinal()) });

        // oil pipe
        GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.BLACK.ordinal()), new Object[] { PipeData.BLACK_PIPE.itemStack, PipeData.BLACK_PIPE.itemStack, PipeData.BLACK_PIPE.itemStack, PipeData.BLACK_PIPE.itemStack, new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.SlimeSeal.ordinal()) });

        // water pipe
        GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.BLUE.ordinal()), new Object[] { PipeData.BLUE_PIPE.itemStack, PipeData.BLUE_PIPE.itemStack, PipeData.BLUE_PIPE.itemStack, PipeData.BLUE_PIPE.itemStack, new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.SlimeSeal.ordinal()) });

        // bronze pipes
        //GameRegistry.addShapelessRecipe(PipeData., new Object[] { CoreRecipeLoader.bronzeTube, CoreRecipeLoader.slimeSeal });
        // generic pipe crafting
        for (int pipeMeta = 0; pipeMeta < 15; pipeMeta++)
        {
            new RecipeGrid(new ItemStack(blockPipe, 4, pipeMeta)).setRowOne(null, blockPipe, null).setRowTwo(blockPipe, new ItemStack(Item.dyePowder, 1, pipeMeta), blockPipe).setRowOne(null, blockPipe, null).RegisterRecipe();

        }
        new RecipeGrid(PipeData.IRON_PIPE.itemStack, 1, 1).setRowOne(blockPipe).RegisterRecipe();

    }
}
