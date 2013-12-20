package dark.machines;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.dark.EnumMaterial;
import com.dark.EnumOrePart;
import com.dark.helpers.ColorCode;
import com.dark.interfaces.IToolReadOut.EnumTools;
import com.dark.prefab.RecipeLoader;

import cpw.mods.fml.common.registry.GameRegistry;
import dark.api.reciepes.MachineRecipeHandler;
import dark.api.reciepes.ProcessorType;
import dark.machines.deco.BlockBasalt;
import dark.machines.generators.BlockSolarPanel;
import dark.machines.items.EnumTool;
import dark.machines.items.ItemCommonTool;
import dark.machines.items.ItemReadoutTools;
import dark.machines.items.ItemWrench;
import dark.machines.transmit.BlockWire;

public class CoreRecipeLoader extends RecipeLoader
{

    /* BLOCKS */
    public static Block blockOre, blockDebug, blockWire;
    public static Block blockStainGlass;
    public static Block blockColorSand;
    public static Block blockBasalt;
    public static Block blockGlowGlass;
    public static Block blockSteamGen, blockSolar, blockBatBox;
    public static Block blockGas;

    /* ITEMS */
    public static Item battery;
    public static Item wrench;

    public static Item itemGlowingSand;
    public static Item itemDiggingTool;
    public static Item itemVehicleTest;
    public static boolean debugOreItems = false;
    public static Item itemTool;

    @Override
    public void loadRecipes()
    {
        super.loadRecipes();
        if (itemTool instanceof ItemReadoutTools)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTool, 1, EnumTools.PIPE_GUAGE.ordinal()), "TVT", " T ", 'T', "ironTube", 'V', "valvePart"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTool, 1, EnumTools.MULTI_METER.ordinal()), "PGP", "WCW", "PRP", 'P', Block.planks, 'G', Block.glass, 'C', circuit, 'W', "copperWire", 'R', "CopperCoil"));
        }
        if (wrench instanceof ItemWrench)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(wrench, 1), "S S", " S ", " S ", 'S', steel));
        }
        if (blockSolar instanceof BlockSolarPanel)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSolar, 1), "GGG", "SCS", "SWS", 'G', Block.glass, 'W', "copperWire", 'C', circuit, 'S', steel));
        }
        if (blockWire instanceof BlockWire)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockWire, 16, 1), "III", "WWW", "III", 'I', Block.cloth, 'W', copper));
        }
        if (itemDiggingTool instanceof ItemCommonTool)
        {

            for (EnumMaterial mat : EnumMaterial.values())
            {
                if (mat.shouldCreateTool())
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(EnumTool.PICKAX.getTool(mat), "III", " S ", " S ", 'I', mat.getOreName(EnumOrePart.INGOTS), 'S', Item.stick));
                    GameRegistry.addRecipe(new ShapedOreRecipe(EnumTool.HOE.getTool(mat), "II ", " S ", " S ", 'I', mat.getOreName(EnumOrePart.INGOTS), 'S', Item.stick));
                    GameRegistry.addRecipe(new ShapedOreRecipe(EnumTool.SPADE.getTool(mat), " I ", " S ", " S ", 'I', mat.getOreName(EnumOrePart.INGOTS), 'S', Item.stick));
                    GameRegistry.addRecipe(new ShapedOreRecipe(EnumTool.AX.getTool(mat), "II ", "IS ", " S ", 'I', mat.getOreName(EnumOrePart.INGOTS), 'S', Item.stick));
                    //GameRegistry.addRecipe(new ShapedOreRecipe(mat.getTool(EnumTool.SHEAR), "III", " S ", 'I', mat.getStack(EnumOrePart.INGOTS, 1)));
                }
            }
        }
    }

    public void loadStainGlass()
    {
        for (ColorCode code : ColorCode.values())
        {
            // Stained Glass //
            if (blockColorSand != null)
            {

                if (blockStainGlass != null)
                {
                    FurnaceRecipes.smelting().addSmelting(blockColorSand.blockID, code.ordinal(), new ItemStack(blockStainGlass, 1, code.ordinal()), 10F);
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, new ItemStack(blockStainGlass, 1, code.ordinal()), new ItemStack(blockColorSand.blockID, 1, code.ordinal()));
                }
                GameRegistry.addShapelessRecipe(new ItemStack(blockColorSand.blockID, 1, code.ordinal()), new Object[] { new ItemStack(blockColorSand, 1, code.ordinal()) });
                MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, new ItemStack(blockColorSand, 1, code.ordinal()), new ItemStack(itemGlowingSand, 1, code.ordinal()));

            }

            // Glowing Glass //
            if (itemGlowingSand != null)
            {
                if (blockGlowGlass != null)
                {
                    FurnaceRecipes.smelting().addSmelting(itemGlowingSand.itemID, code.ordinal(), new ItemStack(blockGlowGlass, 1, code.ordinal()), 10F);
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, new ItemStack(blockGlowGlass, 1, code.ordinal()), new ItemStack(itemGlowingSand, 1, code.ordinal()));
                }
                if (blockColorSand != null)
                {
                    GameRegistry.addShapelessRecipe(new ItemStack(itemGlowingSand, 1, code.ordinal()), new Object[] { new ItemStack(blockColorSand.blockID, 1, code.ordinal()), Item.redstone });
                    GameRegistry.addShapelessRecipe(new ItemStack(itemGlowingSand, 1, code.ordinal()), new Object[] { new ItemStack(blockColorSand.blockID, 1, code.ordinal()), Item.glowstone });
                    MachineRecipeHandler.banProcessingOfItem(new ItemStack(itemGlowingSand, 1, code.ordinal()));
                }
            }

            // Colored Sand //
            if (blockColorSand != null)
            {
                GameRegistry.addRecipe(new ItemStack(blockColorSand, 8, code.ordinal()), new Object[] { "SSS", "SDS", "SSS", 'S', Block.sand, 'D', new ItemStack(Item.dyePowder, 1, code.ordinal()) });

            }
        }
        // Extra Block //
        if (blockBasalt != null)
        {
            GameRegistry.addShapelessRecipe(new ItemStack(blockBasalt, 1, BlockBasalt.block.COBBLE.ordinal()), new Object[] { new ItemStack(blockBasalt, 1, BlockBasalt.block.STONE.ordinal()) });
            GameRegistry.addShapelessRecipe(new ItemStack(blockBasalt, 1, BlockBasalt.block.MOSSY.ordinal()), new Object[] { new ItemStack(blockBasalt, 1, BlockBasalt.block.BRICK.ordinal()), Block.vine });
            GameRegistry.addShapelessRecipe(new ItemStack(blockBasalt, 2, BlockBasalt.block.CRACKED.ordinal()), new Object[] { new ItemStack(blockBasalt, 1, BlockBasalt.block.BRICK.ordinal()), new ItemStack(blockBasalt, 1, BlockBasalt.block.BRICK.ordinal()) });
            GameRegistry.addRecipe(new ItemStack(blockBasalt, 4, BlockBasalt.block.BRICK.ordinal()), new Object[] { "SS", "SS", 'S', new ItemStack(blockBasalt, 1, BlockBasalt.block.STONE.ordinal()) });
            GameRegistry.addRecipe(new ItemStack(blockBasalt, 8, BlockBasalt.block.CHISILED.ordinal()), new Object[] { "SSS", "S S", "SSS", 'S', new ItemStack(blockBasalt, 1, BlockBasalt.block.STONE.ordinal()) });

            GameRegistry.addShapelessRecipe(new ItemStack(blockBasalt, 2, BlockBasalt.block.COBBLE.ordinal()), new Object[] { Block.cobblestone, new ItemStack(blockBasalt, 1, BlockBasalt.block.STONE.ordinal()) });
            GameRegistry.addSmelting(Block.stone.blockID, new ItemStack(blockBasalt, 1, BlockBasalt.block.STONE.ordinal()), 1f);
        }

    }

   
}
