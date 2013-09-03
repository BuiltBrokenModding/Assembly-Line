package dark.assembly.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.assembly.common.machine.BlockCrate;
import dark.core.common.RecipeLoader;

public class ALRecipeLoader extends RecipeLoader
{

    public Block blockConveyorBelt;
    public Block blockManipulator;
    public BlockCrate blockCrate;
    public Block blockImprinter;
    public Block blockEncoder;
    public Block blockDetector;
    public Block blockRejector;
    public Block blockArmbot;
    public Block blockCraneController;
    public Block blockCraneFrame;
    public Block blockTurntable;

    public Item itemImprint;
    public Item itemDisk;

    @Override
    public void loadRecipes()
    {
        super.loadRecipes();
        this.createStandardRecipes();
        this.createUERecipes();
    }

    private void createUERecipes()
    {
        // Armbot
        GameRegistry.addRecipe(new ShapedOreRecipe(blockArmbot, new Object[] { "II ", "SIS", "MCM", 'S', ALRecipeLoader.steelPlate, 'C', ALRecipeLoader.circuit2, 'I', ALRecipeLoader.steel, 'M', ALRecipeLoader.motor }));
        // Disk
        GameRegistry.addRecipe(new ShapedOreRecipe(itemDisk, new Object[] { "III", "ICI", "III", 'I', itemImprint, 'C', ALRecipeLoader.circuit2 }));
        // Encoder
        GameRegistry.addRecipe(new ShapedOreRecipe(blockEncoder, new Object[] { "SIS", "SCS", "SSS", 'I', itemImprint, 'S', ALRecipeLoader.steel, 'C', ALRecipeLoader.circuit2 }));
        // Detector
        GameRegistry.addRecipe(new ShapedOreRecipe(blockDetector, new Object[] { "SES", "SCS", "S S", 'S', ALRecipeLoader.steel, 'C', ALRecipeLoader.circuit, 'E', Item.eyeOfEnder }));
        // Conveyor Belt
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 10), new Object[] { "III", "WMW", 'I', ALRecipeLoader.steel, 'W', Block.planks, 'M', ALRecipeLoader.motor }));
        // Rejector
        GameRegistry.addRecipe(new ShapedOreRecipe(blockRejector, new Object[] { "CPC", "@R@", '@', steel, 'R', Item.redstone, 'P', Block.pistonBase, 'C', ALRecipeLoader.circuit }));
        // Turntable
        GameRegistry.addRecipe(new ShapedOreRecipe(blockTurntable, new Object[] { "IMI", " P ", 'M', ALRecipeLoader.motor, 'P', Block.pistonBase, 'I', ALRecipeLoader.steel }));
        // Manipulator
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockManipulator, 2), new Object[] { Block.dispenser, ALRecipeLoader.circuit }));
    }

    private void createStandardRecipes()
    {
        // Imprint
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemImprint, 2), new Object[] { "R", "P", "I", 'P', Item.paper, 'R', Item.redstone, 'I', new ItemStack(Item.dyePowder, 1, 0) }));
        // Imprinter
        GameRegistry.addRecipe(new ShapedOreRecipe(blockImprinter, new Object[] { "SIS", "SPS", "WCW", 'S', Item.ingotIron, 'C', Block.chest, 'W', Block.workbench, 'P', Block.pistonBase, 'I', new ItemStack(Item.dyePowder, 1, 0) }));
        // Crate
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 0), new Object[] { "TST", "S S", "TST", 'S', Item.ingotIron, 'T', Block.planks }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 1), new Object[] { "TST", "SCS", "TST", 'C', new ItemStack(blockCrate, 1, 0), 'S', ALRecipeLoader.steel, 'T', Block.wood }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 2), new Object[] { "TST", "SCS", "TST", 'C', new ItemStack(blockCrate, 1, 1), 'S', ALRecipeLoader.steelPlate, 'T', Block.wood }));
    }
}
