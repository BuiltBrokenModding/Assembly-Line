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
        GameRegistry.addRecipe(new ShapedOreRecipe(blockArmbot, new Object[] { "II ", "SIS", "MCM", 'S', RecipeLoader.steelPlate, 'C', RecipeLoader.circuit2, 'I', RecipeLoader.steel, 'M', RecipeLoader.motor }));
        // Disk
        GameRegistry.addRecipe(new ShapedOreRecipe(itemDisk, new Object[] { "III", "ICI", "III", 'I', itemImprint, 'C', RecipeLoader.circuit2 }));
        // Encoder
        GameRegistry.addRecipe(new ShapedOreRecipe(blockEncoder, new Object[] { "SIS", "SCS", "SSS", 'I', itemImprint, 'S', RecipeLoader.steel, 'C', RecipeLoader.circuit2 }));
        // Detector
        GameRegistry.addRecipe(new ShapedOreRecipe(blockDetector, new Object[] { "SES", "SCS", "S S", 'S', RecipeLoader.steel, 'C', RecipeLoader.circuit, 'E', Item.eyeOfEnder }));
        // Conveyor Belt
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 10), new Object[] { "III", "WMW", 'I', RecipeLoader.steel, 'W', Block.planks, 'M', RecipeLoader.motor }));
        // Rejector
        GameRegistry.addRecipe(new ShapedOreRecipe(blockRejector, new Object[] { "CPC", "@R@", '@', steel, 'R', Item.redstone, 'P', Block.pistonBase, 'C', RecipeLoader.circuit }));
        // Turntable
        GameRegistry.addRecipe(new ShapedOreRecipe(blockTurntable, new Object[] { "IMI", " P ", 'M', RecipeLoader.motor, 'P', Block.pistonBase, 'I', RecipeLoader.steel }));
        // Manipulator
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockManipulator, 2), new Object[] { Block.dispenser, RecipeLoader.circuit }));
    }

    private void createStandardRecipes()
    {
        // Imprint
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemImprint, 2), new Object[] { "R", "P", "I", 'P', Item.paper, 'R', Item.redstone, 'I', new ItemStack(Item.dyePowder, 1, 0) }));
        // Imprinter
        GameRegistry.addRecipe(new ShapedOreRecipe(blockImprinter, new Object[] { "SIS", "SPS", "WCW", 'S', Item.ingotIron, 'C', Block.chest, 'W', Block.workbench, 'P', Block.pistonBase, 'I', new ItemStack(Item.dyePowder, 1, 0) }));
        // Crate
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 0), new Object[] { "TST", "S S", "TST", 'S', Item.ingotIron, 'T', Block.planks }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 1), new Object[] { "TST", "SCS", "TST", 'C', new ItemStack(blockCrate, 1, 0), 'S', RecipeLoader.steel, 'T', Block.wood }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 2), new Object[] { "TST", "SCS", "TST", 'C', new ItemStack(blockCrate, 1, 1), 'S', RecipeLoader.steelPlate, 'T', Block.wood }));
    }
}
