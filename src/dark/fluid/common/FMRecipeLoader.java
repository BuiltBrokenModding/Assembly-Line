package dark.fluid.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.api.ColorCode;
import dark.core.common.CoreRecipeLoader;
import dark.core.common.RecipeLoader;
import dark.core.common.items.ItemParts.Parts;
import dark.fluid.common.pipes.PipeMaterial;

public class FMRecipeLoader extends RecipeLoader
{

    public static Block blockPipe;
    public static Block blockTank;
    public static Block blockPumpMachine;
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
        super.loadRecipes();
        this.registerPipes();
        this.registerTanks();

        // pump
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockPumpMachine, 1, 0), new Object[] { "C@C", "BMB", "@X@", '@', steelPlate, 'X', new ItemStack(blockPipe, 1), 'B', new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Valve.ordinal()), 'C', circuit, 'M', "motor" }));
        // construction pump
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConPump, 1, 0), new Object[] { "@C@", "BMB", "@@@", '@', steelPlate, 'B', new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Valve.ordinal()), 'C', "advancedCircuit", 'M', "motor" }));
        // Drain
        GameRegistry.addRecipe(new ItemStack(blockDrain, 1, 0), new Object[] { "IGI", "SVS", " P ", 'I', Item.ingotIron, 'G', Block.dispenser, 'S', Block.stone, 'P', new ItemStack(blockPipe, 1), 'V', new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Valve.ordinal()) });

        // release valve
        GameRegistry.addRecipe(new ItemStack(blockReleaseValve, 1), new Object[] { "RPR", "PVP", "RPR", 'P', new ItemStack(blockPipe, 1), 'V', new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Valve.ordinal()), 'R', Item.redstone });
        // sink
        GameRegistry.addRecipe(new ItemStack(blockSink, 1), new Object[] { "I I", "SIS", "SPS", 'P', new ItemStack(blockPipe, 1), 'I', Item.ingotIron, 'S', Block.stone });
    }

    public void registerTanks()
    {
        GameRegistry.addRecipe(new ItemStack(blockPumpMachine, 1, 0), new Object[] { "IXI", "X X", "IXI", 'I', Item.ingotIron, 'X', Block.glass });
    }

    @SuppressWarnings("deprecation")
    public void registerPipes()
    {
        for (PipeMaterial mat : PipeMaterial.values())
        {
            if (mat.canSupportFluids && !mat.canSupportGas && !mat.canSupportMoltenFluids)
            {
                GameRegistry.addRecipe(new ShapelessOreRecipe(mat.getStack(2), new Object[] { mat.matName + "tube", "LetherSeal" }));
            }
            else if (mat.canSupportGas)
            {
                GameRegistry.addRecipe(new ShapelessOreRecipe(mat.getStack(2), new Object[] { mat.matName + "tube", "GasSeal" }));
            }
            else if (mat.canSupportMoltenFluids)
            {
                if (mat == PipeMaterial.OBBY)
                {
                    GameRegistry.addRecipe(new ShapelessOreRecipe(mat.getStack(2), new Object[] { mat.matName + "tube", Block.netherBrick, Block.netherBrick }));
                }
                if (mat == PipeMaterial.HELL)
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(4), new Object[] { "OOO", "BNB", "OOO", 'N', Block.netherBrick, 'B', Item.blazeRod, 'O', Block.obsidian }));
                }
            }
            for (ColorCode color : ColorCode.values())
            {
                GameRegistry.addRecipe(mat.getStack(color), new Object[] { " X ", "XIX", " X ", 'I', new ItemStack(Item.dyePowder, 1, color.ordinal()), 'X', blockPipe });
                new RecipeGrid(mat.getStack(), 1, 1).setRowOne(mat.getStack(color)).RegisterRecipe();
            }

        }

    }
}
