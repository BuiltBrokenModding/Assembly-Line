package com.builtbroken.assemblyline;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.builtbroken.assemblyline.blocks.BlockBasalt;
import com.builtbroken.assemblyline.fluid.pipes.FluidPartsMaterial;
import com.builtbroken.assemblyline.generators.BlockSolarPanel;
import com.builtbroken.assemblyline.item.EnumTool;
import com.builtbroken.assemblyline.item.ItemCommonTool;
import com.builtbroken.assemblyline.item.ItemOreDirv;
import com.builtbroken.assemblyline.item.ItemParts;
import com.builtbroken.assemblyline.item.ItemParts.Parts;
import com.builtbroken.assemblyline.item.ItemReadoutTools;
import com.builtbroken.assemblyline.item.ItemWrench;
import com.builtbroken.assemblyline.machine.BlockCrate;
import com.builtbroken.assemblyline.machine.processor.BlockProcessor;
import com.builtbroken.assemblyline.transmit.BlockWire;
import com.builtbroken.minecraft.EnumMaterial;
import com.builtbroken.minecraft.EnumOrePart;
import com.builtbroken.minecraft.RecipeNames;
import com.builtbroken.minecraft.helpers.ColorCode;
import com.builtbroken.minecraft.interfaces.IToolReadOut.EnumTools;
import com.builtbroken.minecraft.recipes.MachineRecipeHandler;
import com.builtbroken.minecraft.recipes.ProcessorType;

import cpw.mods.fml.common.registry.GameRegistry;

public class ALRecipeLoader
{

    public static Block blockConveyorBelt;
    public static Block blockManipulator;
    public static BlockCrate blockCrate;
    public static Block blockImprinter;
    public static Block blockEncoder;
    public static Block blockDetector;
    public static Block blockRejector;
    public static Block blockArmbot;
    public static Block blockTurntable;
    public static Block processorMachine;
    public static Block blockAdvancedHopper;
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
    public static Block blockOre, blockDebug, blockWire;
    public static Block blockStainGlass;
    public static Block blockColorSand;
    public static Block blockBasalt;
    public static Block blockGlowGlass;
    public static Block blockSteamGen, blockSolar, blockBatBox;
    public static Block blockGas;

    public static Item itemImprint;
    public static Item itemDisk;
    public static Item itemFluidCan;
    public static Item itemTool;
    public static Item itemParts;
    public static Item itemMetals;
    public static Item battery;
    public static Item wrench;
    public static Item itemGlowingSand;
    public static Item itemDiggingTool;
    public static Item itemVehicleTest;

    private static ALRecipeLoader instance;

    public static ALRecipeLoader instance()
    {
        if (instance == null)
        {
            instance = new ALRecipeLoader();
        }
        return instance;
    }

    public void loadRecipes()
    {
        this.createStandardRecipes();
        this.createUERecipes();

        this.registerPipes();
        this.registerTanks();

        // pump
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockPumpMachine, 1, 0), new Object[] { "C@C", "BMB", "@X@", '@', RecipeNames.PLATE_STEEL.name, 'X', new ItemStack(blockPipe, 1), 'B', new ItemStack(itemParts, 1, Parts.Valve.ordinal()), 'C', RecipeNames.BASIC_CIRCUIT.name, 'M', "motor" }));
        // construction pump
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConPump, 1, 0), new Object[] { "@C@", "BMB", "@@@", '@', RecipeNames.PLATE_STEEL.name, 'B', new ItemStack(itemParts, 1, Parts.Valve.ordinal()), 'C', RecipeNames.ADVANCED_CIRCUIT.name, 'M', RecipeNames.MOTOR.name }));
        // Drain
        GameRegistry.addRecipe(new ItemStack(blockDrain, 1, 0), new Object[] { "IGI", "SVS", " P ", 'I', Item.ingotIron, 'G', Block.dispenser, 'S', Block.stone, 'P', new ItemStack(blockPipe, 1), 'V', new ItemStack(itemParts, 1, Parts.Valve.ordinal()) });

        // release valve
        GameRegistry.addRecipe(new ItemStack(blockReleaseValve, 1), new Object[] { "RPR", "PVP", "RPR", 'P', new ItemStack(blockPipe, 1), 'V', new ItemStack(itemParts, 1, Parts.Valve.ordinal()), 'R', Item.redstone });
        // sink
        //GameRegistry.addRecipe(new ItemStack(blockSink, 1), new Object[] { "I I", "SIS", "SPS", 'P', new ItemStack(blockPipe, 1), 'I', Item.ingotIron, 'S', Block.stone });

        if (itemParts instanceof ItemParts)
        {
            // seal
            GameRegistry.addRecipe(new ItemStack(itemParts, 16, Parts.Seal.ordinal()), new Object[] { "LL", "LL", 'L', Item.leather });
            // slime steal
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 4, Parts.GasSeal.ordinal()), " # ", "#S#", " # ", '#', Parts.Seal.name, 'S', Item.slimeBall));
            // part valve
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.Tank.ordinal()), new Object[] { "PLP", 'P', "ironPipe", 'L', Block.lever }));
            //Basic Circuit
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.CircuitBasic.ordinal()), "!#!", "#@#", "!#!", '@', RecipeNames.PLATE_COPPER.name, '#', Block.glass, '!', RecipeNames.COPPER_WIRE.name));
            //Advanced Circuit
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.CircuitAdvanced.ordinal()), "!#!", "#@#", "!#!", '@', RecipeNames.PLATE_COPPER.name, '#', Item.redstone, '!', RecipeNames.COPPER_WIRE.name));
            //Elite Circuit
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.CircuitElite.ordinal()), "!#!", "#@#", "!#!", '@', RecipeNames.PLATE_GOLD.name, '#', Item.redstone, '!', RecipeNames.COPPER_WIRE.name));

            // unfinished tank
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.Tank.ordinal()), " # ", "# #", " # ", '#', RecipeNames.INGOT_BRONZE.name));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.Tank.ordinal()), " # ", "# #", " # ", '#', RecipeNames.INGOT_STEEL.name));
            //Motor
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.Motor.ordinal()), new Object[] { "@!@", "!#!", "@!@", '!', RecipeNames.INGOT_STEEL.name, '#', Item.ingotIron, '@', Parts.COIL.name }));
            //Laser Diode
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 4, Parts.LASER.ordinal()), new Object[] { " G ", "!S!", " C ", '!', RecipeNames.COPPER_WIRE.name, 'G', Block.glass, 'S', Block.sand, 'C', RecipeNames.BASIC_CIRCUIT.name }));
            //Coil
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 8, Parts.COIL.ordinal()), new Object[] { "WWW", "W W", "WWW", 'W', RecipeNames.COPPER_WIRE.name }));

        }
        if (itemMetals instanceof ItemOreDirv)
        {
            //Alt salvaging item list
            MachineRecipeHandler.newAltProcessorOutput(ProcessorType.GRINDER, Block.wood, EnumMaterial.getStack(itemMetals, EnumMaterial.WOOD, EnumOrePart.DUST, 3));
            MachineRecipeHandler.newAltProcessorOutput(ProcessorType.GRINDER, Block.planks, EnumMaterial.getStack(itemMetals, EnumMaterial.WOOD, EnumOrePart.DUST, 1));

            MachineRecipeHandler.newAltProcessorOutput(ProcessorType.CRUSHER, Block.wood, EnumMaterial.getStack(itemMetals, EnumMaterial.WOOD, EnumOrePart.SCRAPS, 3));
            MachineRecipeHandler.newAltProcessorOutput(ProcessorType.CRUSHER, Block.planks, EnumMaterial.getStack(itemMetals, EnumMaterial.WOOD, EnumOrePart.SCRAPS, 1));

            //Stone recipes
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, Block.stone, EnumMaterial.getStack(itemMetals, EnumMaterial.STONE, EnumOrePart.DUST, 1));

            //Wood recipes
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, Block.wood, EnumMaterial.getStack(itemMetals, EnumMaterial.WOOD, EnumOrePart.DUST, 3));
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, Block.planks, EnumMaterial.getStack(itemMetals, EnumMaterial.WOOD, EnumOrePart.DUST, 1));
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.wood, EnumMaterial.getStack(itemMetals, EnumMaterial.WOOD, EnumOrePart.SCRAPS, 3));
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.planks, EnumMaterial.getStack(itemMetals, EnumMaterial.WOOD, EnumOrePart.SCRAPS, 1));

            //Gold Recipes
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.blockIron, EnumMaterial.getStack(itemMetals, EnumMaterial.IRON, EnumOrePart.SCRAPS, 8));
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.oreIron, EnumMaterial.getStack(itemMetals, EnumMaterial.IRON, EnumOrePart.RUBBLE, 1));
            //Iron Recipes
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.blockGold, EnumMaterial.getStack(itemMetals, EnumMaterial.GOLD, EnumOrePart.SCRAPS, 8));
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.oreGold, EnumMaterial.getStack(itemMetals, EnumMaterial.GOLD, EnumOrePart.RUBBLE, 1));
            //Dust recipes
            GameRegistry.addShapelessRecipe(EnumMaterial.getStack(itemMetals, EnumMaterial.STEEL, EnumOrePart.DUST, 1), new Object[] { EnumMaterial.getStack(itemMetals, EnumMaterial.IRON, EnumOrePart.DUST, 1), new ItemStack(Item.coal, 1, 0), new ItemStack(Item.coal, 1, 0) });
            GameRegistry.addShapelessRecipe(EnumMaterial.getStack(itemMetals, EnumMaterial.STEEL, EnumOrePart.DUST, 1), new Object[] { EnumMaterial.getStack(itemMetals, EnumMaterial.IRON, EnumOrePart.DUST, 1), new ItemStack(Item.coal, 1, 1), new ItemStack(Item.coal, 1, 1) });
            GameRegistry.addShapelessRecipe(EnumMaterial.getStack(itemMetals, EnumMaterial.STEEL, EnumOrePart.DUST, 1), new Object[] { EnumMaterial.getStack(itemMetals, EnumMaterial.IRON, EnumOrePart.DUST, 1), new ItemStack(Item.coal, 1, 1), new ItemStack(Item.coal, 1, 0) });
            GameRegistry.addShapelessRecipe(EnumMaterial.getStack(itemMetals, EnumMaterial.BRONZE, EnumOrePart.DUST, 4), new Object[] { EnumMaterial.getStack(itemMetals, EnumMaterial.COPPER, EnumOrePart.DUST, 1), EnumMaterial.getStack(itemMetals, EnumMaterial.COPPER, EnumOrePart.DUST, 1), EnumMaterial.getStack(itemMetals, EnumMaterial.COPPER, EnumOrePart.DUST, 1), EnumMaterial.getStack(itemMetals, EnumMaterial.TIN, EnumOrePart.DUST, 1) });

            //Ore material recipe loop
            for (EnumMaterial mat : EnumMaterial.values())
            {
                //Dust recipes
                if (mat.shouldCreateItem(EnumOrePart.DUST))
                {
                    FurnaceRecipes.smelting().addSmelting(mat.getStack(itemMetals, EnumOrePart.DUST, 1).itemID, mat.getStack(itemMetals, EnumOrePart.DUST, 1).getItemDamage(), mat.getStack(itemMetals, EnumOrePart.INGOTS, 1), 0.6f);
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(itemMetals, EnumOrePart.RUBBLE, 1), mat.getStack(itemMetals, EnumOrePart.DUST, 1), 1, 4);
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1), mat.getStack(itemMetals, EnumOrePart.DUST, 1));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(itemMetals, EnumOrePart.INGOTS, 1), mat.getStack(itemMetals, EnumOrePart.DUST, 1));
                    MachineRecipeHandler.newAltProcessorOutput(ProcessorType.GRINDER, mat.getStack(itemMetals, EnumOrePart.INGOTS, 1), mat.getStack(itemMetals, EnumOrePart.DUST, 1));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(itemMetals, EnumOrePart.PLATES, 1), mat.getStack(itemMetals, EnumOrePart.DUST, 1), 2, 4);
                    MachineRecipeHandler.newAltProcessorOutput(ProcessorType.GRINDER, mat.getStack(itemMetals, EnumOrePart.PLATES, 1), mat.getStack(itemMetals, EnumOrePart.DUST, 3));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(itemMetals, EnumOrePart.ROD, 1), mat.getStack(itemMetals, EnumOrePart.DUST, 1));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(itemMetals, EnumOrePart.TUBE, 1), mat.getStack(itemMetals, EnumOrePart.DUST, 1));
                }

                // Salvaging recipe

                if (mat.shouldCreateItem(EnumOrePart.SCRAPS))
                {
                    FurnaceRecipes.smelting().addSmelting(mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1).itemID, mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1).getItemDamage(), mat.getStack(itemMetals, EnumOrePart.INGOTS, 1), 0.6f);
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, mat.getStack(itemMetals, EnumOrePart.PLATES, 1), mat.getStack(itemMetals, EnumOrePart.SCRAPS, 3));
                    MachineRecipeHandler.newAltProcessorOutput(ProcessorType.CRUSHER, mat.getStack(itemMetals, EnumOrePart.PLATES, 1), mat.getStack(itemMetals, EnumOrePart.SCRAPS, 3));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, mat.getStack(itemMetals, EnumOrePart.RUBBLE, 1), mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1), 1, 5);
                    MachineRecipeHandler.newAltProcessorOutput(ProcessorType.CRUSHER, mat.getStack(itemMetals, EnumOrePart.INGOTS, 1), mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, mat.getStack(itemMetals, EnumOrePart.ROD, 1), mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, mat.getStack(itemMetals, EnumOrePart.TUBE, 1), mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1));
                }
                if (mat.shouldCreateItem(EnumOrePart.TUBE))
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(itemMetals, EnumOrePart.TUBE, 6), new Object[] { "I", "I", "I", 'I', mat.getOreName(EnumOrePart.INGOTS) }));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(itemMetals, EnumOrePart.TUBE, 6), new Object[] { "I", "I", "I", 'I', mat.getOreNameReverse(EnumOrePart.INGOTS) }));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(itemMetals, EnumOrePart.TUBE, 1), new Object[] { "I", 'I', mat.getOreName(EnumOrePart.ROD) }));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(itemMetals, EnumOrePart.TUBE, 1), new Object[] { "I", 'I', mat.getOreNameReverse(EnumOrePart.ROD) }));
                }
                if (mat.shouldCreateItem(EnumOrePart.ROD))
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(itemMetals, EnumOrePart.ROD, 4), new Object[] { "I", "I", 'I', mat.getOreName(EnumOrePart.ROD) }));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(itemMetals, EnumOrePart.ROD, 4), new Object[] { "I", "I", 'I', mat.getOreNameReverse(EnumOrePart.ROD) }));
                }
                if (mat.shouldCreateItem(EnumOrePart.PLATES))
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(itemMetals, EnumOrePart.PLATES, 1), new Object[] { "II", "II", 'I', mat.getOreName(EnumOrePart.INGOTS) }));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(itemMetals, EnumOrePart.PLATES, 1), new Object[] { "II", "II", 'I', mat.getOreNameReverse(EnumOrePart.INGOTS) }));
                }
                if (mat.shouldCreateItem(EnumOrePart.GEARS))
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(itemMetals, EnumOrePart.GEARS, 4), new Object[] { " I ", "IRI", " I ", 'I', mat.getOreName(EnumOrePart.INGOTS), 'R', mat.shouldCreateItem(EnumOrePart.ROD) ? mat.getOreName(EnumOrePart.ROD) : Item.stick }));
                }

            }
        }
        if (itemTool instanceof ItemReadoutTools)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTool, 1, EnumTools.PIPE_GUAGE.ordinal()), "TVT", " T ", 'T', "ironTube", 'V', "valvePart"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTool, 1, EnumTools.MULTI_METER.ordinal()), "PGP", "WCW", "PRP", 'P', Block.planks, 'G', Block.glass, 'C', RecipeNames.BASIC_CIRCUIT.name, 'W', RecipeNames.COPPER_WIRE.name, 'R', RecipeNames.COIL_COPPER.name));
        }
        if (wrench instanceof ItemWrench)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(wrench, 1), "S S", " S ", " S ", 'S', RecipeNames.INGOT_STEEL.name));
        }
        if (blockSolar instanceof BlockSolarPanel)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSolar, 1), "GGG", "SCS", "SWS", 'G', Block.glass, 'W', "copperWire", 'C', RecipeNames.BASIC_CIRCUIT.name, 'S', RecipeNames.INGOT_STEEL.name));
        }
        if (blockWire != null)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockWire, 16, 1), "III", "WWW", "III", 'I', Block.cloth, 'W', RecipeNames.INGOT_COPPER.name));
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

    private void createUERecipes()
    {
        // Armbot
        GameRegistry.addRecipe(new ShapedOreRecipe(blockArmbot, new Object[] { "II ", "SIS", "MCM", 'S', RecipeNames.PLATE_STEEL.name, 'C', RecipeNames.ADVANCED_CIRCUIT.name, 'I', RecipeNames.INGOT_STEEL.name, 'M', RecipeNames.MOTOR.name }));
        // Disk
        GameRegistry.addRecipe(new ShapedOreRecipe(itemDisk, new Object[] { "III", "ICI", "III", 'I', itemImprint, 'C', RecipeNames.ADVANCED_CIRCUIT.name }));
        // Encoder
        GameRegistry.addRecipe(new ShapedOreRecipe(blockEncoder, new Object[] { "SIS", "SCS", "SSS", 'I', itemImprint, 'S', RecipeNames.INGOT_STEEL.name, 'C', RecipeNames.ADVANCED_CIRCUIT.name }));
        // Detector
        GameRegistry.addRecipe(new ShapedOreRecipe(blockDetector, new Object[] { "SES", "SCS", "S S", 'S', RecipeNames.INGOT_STEEL.name, 'C', RecipeNames.BASIC_CIRCUIT.name, 'E', Item.eyeOfEnder }));
        // Conveyor Belt
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConveyorBelt, 10), new Object[] { "III", "WMW", 'I', RecipeNames.INGOT_STEEL.name, 'W', Block.planks, 'M', RecipeNames.MOTOR.name }));
        // Rejector
        GameRegistry.addRecipe(new ShapedOreRecipe(blockRejector, new Object[] { "CPC", "@R@", '@', RecipeNames.INGOT_STEEL.name, 'R', Item.redstone, 'P', Block.pistonBase, 'C', RecipeNames.BASIC_CIRCUIT.name }));
        // Turntable
        GameRegistry.addRecipe(new ShapedOreRecipe(blockTurntable, new Object[] { "IMI", " P ", 'M', RecipeNames.MOTOR.name, 'P', Block.pistonBase, 'I', RecipeNames.INGOT_STEEL.name }));
        // Manipulator
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockManipulator, 2), new Object[] { Block.dispenser, RecipeNames.BASIC_CIRCUIT.name }));
        if (processorMachine instanceof BlockProcessor)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(processorMachine, 1, BlockProcessor.ProcessorData.GRINDER.startMeta), new Object[] { "SSS", "PMP", "SCS", 'M', RecipeNames.MOTOR.name, 'P', RecipeNames.PLATE_STEEL.name, 'S', RecipeNames.INGOT_STEEL.name, 'C', RecipeNames.BASIC_CIRCUIT.name }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(processorMachine, 1, BlockProcessor.ProcessorData.CRUSHER.startMeta), new Object[] { "SPS", "RPR", "SCS", 'R', Block.pistonBase, 'P', RecipeNames.PLATE_STEEL.name, 'S', RecipeNames.INGOT_STEEL.name, 'C', RecipeNames.BASIC_CIRCUIT.name }));
        }
    }

    private void createStandardRecipes()
    {
        // Imprint
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemImprint, 2), new Object[] { "RPI", 'P', Item.paper, 'R', Item.redstone, 'I', new ItemStack(Item.dyePowder, 1, 0) }));
        // Imprinter
        GameRegistry.addRecipe(new ShapedOreRecipe(blockImprinter, new Object[] { "SIS", "SPS", "WCW", 'S', Item.ingotIron, 'C', Block.chest, 'W', Block.workbench, 'P', Block.pistonBase, 'I', new ItemStack(Item.dyePowder, 1, 0) }));
        // Crate
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 0), new Object[] { "TST", "S S", "TST", 'S', Item.ingotIron, 'T', Block.planks }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 1), new Object[] { "TST", "SCS", "TST", 'C', new ItemStack(blockCrate, 1, 0), 'S', RecipeNames.INGOT_STEEL.name, 'T', Block.wood }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 2), new Object[] { "TST", "SCS", "TST", 'C', new ItemStack(blockCrate, 1, 1), 'S', RecipeNames.PLATE_STEEL.name, 'T', Block.wood }));
    }

    public void registerTanks()
    {
        GameRegistry.addRecipe(new ItemStack(blockPumpMachine, 1, 0), new Object[] { "IXI", "X X", "IXI", 'I', Item.ingotIron, 'X', Block.glass });
    }

    public void registerPipes()
    {
        //TODO re-add leather seal recipes
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.IRON.getStack(2), new Object[] { EnumMaterial.IRON.getOreName(EnumOrePart.TUBE), Item.leather }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.GOLD.getStack(2), new Object[] { EnumMaterial.GOLD.getOreName(EnumOrePart.TUBE), Item.leather }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.TIN.getStack(2), new Object[] { EnumMaterial.TIN.getOreName(EnumOrePart.TUBE), Item.leather }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.COPPER.getStack(2), new Object[] { EnumMaterial.COPPER.getOreName(EnumOrePart.TUBE), Item.leather }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.BRONZE.getStack(2), new Object[] { EnumMaterial.BRONZE.getOreName(EnumOrePart.TUBE), Item.leather }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(FluidPartsMaterial.STEEL.getStack(2), new Object[] { EnumMaterial.STEEL.getOreName(EnumOrePart.TUBE), Item.leather }));

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

    public static void parseOreNames(Configuration config)
    {
        if (config.get("Ore", "processOreDictionary", true, "Scans the ore dictionary and adds recipes for other mod's items").getBoolean(true))
        {
            for (EnumMaterial mat : EnumMaterial.values())
            {
                List<ItemStack> ingots = OreDictionary.getOres(mat.getOreName(EnumOrePart.INGOTS));
                List<ItemStack> plates = OreDictionary.getOres(mat.getOreName(EnumOrePart.PLATES));
                List<ItemStack> dusts = OreDictionary.getOres(mat.getOreName(EnumOrePart.DUST));
                List<ItemStack> ores = OreDictionary.getOres("ore" + mat.simpleName);
                if (config.get("Ore", "OverrideDustSmelthing", true, "Forces all mods to use the same ingot for dust smelting").getBoolean(true))
                {
                    for (ItemStack du : dusts)
                    {
                        if (mat.shouldCreateItem(EnumOrePart.INGOTS))
                        {
                            FurnaceRecipes.smelting().addSmelting(du.itemID, du.getItemDamage(), mat.getStack(itemMetals, EnumOrePart.INGOTS, 1), 0.6f);
                        }
                    }
                }

                for (ItemStack ing : ingots)
                {
                    if (mat.shouldCreateItem(EnumOrePart.DUST))
                    {
                        MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, ing, mat.getStack(itemMetals, EnumOrePart.DUST, 1));
                        MachineRecipeHandler.newAltProcessorOutput(ProcessorType.GRINDER, ing, mat.getStack(itemMetals, EnumOrePart.DUST, 1));
                    }
                    if (mat.shouldCreateItem(EnumOrePart.SCRAPS))
                    {
                        MachineRecipeHandler.newAltProcessorOutput(ProcessorType.CRUSHER, ing, mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1));
                        MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, ing, mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1));
                    }
                    if (mat.shouldCreateItem(EnumOrePart.INGOTS) && config.get("Ore", "AddIngotConversionRecipe", true, "Creates a recipe to convert other mods ingots into AL ingots").getBoolean(true))
                    {
                        GameRegistry.addShapelessRecipe(mat.getStack(itemMetals, EnumOrePart.INGOTS, 1), new Object[] { ing });
                    }
                }
                for (ItemStack pla : plates)
                {
                    if (mat.shouldCreateItem(EnumOrePart.DUST))
                    {

                        MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, pla, mat.getStack(itemMetals, EnumOrePart.DUST, 1));
                        MachineRecipeHandler.newAltProcessorOutput(ProcessorType.GRINDER, pla, mat.getStack(itemMetals, EnumOrePart.DUST, 1));

                    }
                    if (mat.shouldCreateItem(EnumOrePart.SCRAPS))
                    {

                        MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, pla, mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1));
                        MachineRecipeHandler.newAltProcessorOutput(ProcessorType.CRUSHER, pla, mat.getStack(itemMetals, EnumOrePart.SCRAPS, 1));

                    }
                }
                for (ItemStack ore : ores)
                {
                    if (mat.shouldCreateItem(EnumOrePart.RUBBLE))
                    {
                        MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, ore, mat.getStack(itemMetals, EnumOrePart.RUBBLE, 1), 1, 2);
                    }
                    if (mat.shouldCreateItem(EnumOrePart.DUST))
                    {
                        MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, ore, mat.getStack(itemMetals, EnumOrePart.DUST, 1), 1, 3);
                    }
                    if (mat.shouldCreateItem(EnumOrePart.INGOTS) && config.get("Ore", "OverrideOreSmelthing", true, "Forces other mods ores to smelt as one ingot").getBoolean(true))
                    {
                        FurnaceRecipes.smelting().addSmelting(ore.itemID, ore.getItemDamage(), mat.getStack(itemMetals, EnumOrePart.INGOTS, 1), 0.6f);
                    }
                }

            }
        }
    }
}
