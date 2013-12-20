package dark.fluid.common;

import com.dark.prefab.RecipeLoader;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.api.ColorCode;
import dark.core.basics.EnumMaterial;
import dark.core.basics.EnumOrePart;
import dark.core.basics.ItemParts;
import dark.core.basics.ItemParts.Parts;
import dark.machines.CoreRecipeLoader;

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
        //GameRegistry.addRecipe(new ItemStack(blockSink, 1), new Object[] { "I I", "SIS", "SPS", 'P', new ItemStack(blockPipe, 1), 'I', Item.ingotIron, 'S', Block.stone });
    }

    public void registerTanks()
    {
        GameRegistry.addRecipe(new ItemStack(blockPumpMachine, 1, 0), new Object[] { "IXI", "X X", "IXI", 'I', Item.ingotIron, 'X', Block.glass });
    }

    public void registerPipes()
    {
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.IRON.getStack(2), new Object[] { EnumMaterial.IRON.getOreName(EnumOrePart.TUBE), ItemParts.Parts.Seal.name }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.GOLD.getStack(2), new Object[] { EnumMaterial.GOLD.getOreName(EnumOrePart.TUBE), ItemParts.Parts.Seal.name }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.TIN.getStack(2), new Object[] { EnumMaterial.TIN.getOreName(EnumOrePart.TUBE), ItemParts.Parts.Seal.name }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.COPPER.getStack(2), new Object[] { EnumMaterial.COPPER.getOreName(EnumOrePart.TUBE), ItemParts.Parts.Seal.name }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.BRONZE.getStack(2), new Object[] { EnumMaterial.BRONZE.getOreName(EnumOrePart.TUBE), ItemParts.Parts.GasSeal.name }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.STEEL.getStack(2), new Object[] { EnumMaterial.STEEL.getOreName(EnumOrePart.TUBE), ItemParts.Parts.GasSeal.name }));

        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.OBBY.getStack(2), new Object[] { EnumMaterial.OBBY.getOreName(EnumOrePart.TUBE), Block.netherBrick, Block.netherBrick }));
        GameRegistry.addRecipe(new ShapedOreRecipe(FluidPartsMaterial.HELL.getStack(4), new Object[] { "OOO", "BNB", "OOO", 'N', Block.netherBrick, 'B', Item.blazeRod, 'O', Block.obsidian }));

        for (FluidPartsMaterial mat : FluidPartsMaterial.values())
        {
            for (ColorCode color : ColorCode.values())
            {
                GameRegistry.addRecipe(mat.getStack(color), new Object[] { " X ", "XIX", " X ", 'I', new ItemStack(Item.dyePowder, 1, color.ordinal()), 'X', blockPipe });
                GameRegistry.addShapelessRecipe(mat.getStack(), new Object[] { mat.getStack(color) });
            }

        }

    }
}
