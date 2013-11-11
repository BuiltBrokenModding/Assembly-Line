package dark.core.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.api.ColorCode;
import dark.api.IToolReadOut.EnumTools;
import dark.api.reciepes.MachineRecipeHandler;
import dark.api.reciepes.ProcessorType;
import dark.core.common.blocks.BlockBasalt;
import dark.core.common.blocks.BlockOre;
import dark.core.common.blocks.BlockOre.OreData;
import dark.core.common.items.EnumMaterial;
import dark.core.common.items.EnumOrePart;
import dark.core.common.items.EnumTool;
import dark.core.common.items.ItemCommonTool;
import dark.core.common.items.ItemOreDirv;
import dark.core.common.items.ItemParts;
import dark.core.common.items.ItemParts.Parts;
import dark.core.common.items.ItemReadoutTools;
import dark.core.common.items.ItemWrench;
import dark.core.common.machines.BlockSolarPanel;
import dark.core.common.transmit.BlockWire;

public class CoreRecipeLoader extends RecipeLoader
{

    /* BLOCKS */
    public static Block blockOre, blockDebug, blockWire;
    public static Block blockStainGlass;
    public static Block blockColorSand;
    public static Block blockBasalt;
    public static Block blockGlowGlass;
    public static Block basicMachine, blockSolar;
    public static Block blockGas;

    /* ITEMS */
    public static Item itemMetals, battery, itemTool, itemParts;
    public static Item wrench;

    public static ItemStack leatherSeal, slimeSeal;
    public static ItemStack valvePart;
    public static ItemStack unfinishedTank;
    public static Item itemGlowingSand;
    public static Item itemDiggingTool;
    public static Item itemVehicleTest;
    public static boolean debugOreItems = false;

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
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getTool(EnumTool.PICKAX), "III", " S ", " S ", 'I', mat.getStack(EnumOrePart.INGOTS, 1), 'S', Item.stick));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getTool(EnumTool.HOE), "II ", " S ", " S ", 'I', mat.getStack(EnumOrePart.INGOTS, 1), 'S', Item.stick));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getTool(EnumTool.SPADE), " I ", " S ", " S ", 'I', mat.getStack(EnumOrePart.INGOTS, 1), 'S', Item.stick));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getTool(EnumTool.AX), "II ", "IS ", " S ", 'I', mat.getStack(EnumOrePart.INGOTS, 1), 'S', Item.stick));
                    //GameRegistry.addRecipe(new ShapedOreRecipe(mat.getTool(EnumTool.SHEAR), "III", " S ", 'I', mat.getStack(EnumOrePart.INGOTS, 1)));
                }
            }
        }
        this.loadParts();
    }

    public void loadParts()
    {
        if (itemParts instanceof ItemParts)
        {
            leatherSeal = new ItemStack(itemParts, 1, Parts.Seal.ordinal());
            slimeSeal = new ItemStack(itemParts, 1, Parts.GasSeal.ordinal());
            valvePart = new ItemStack(itemParts, 1, Parts.Tank.ordinal());
            unfinishedTank = new ItemStack(itemParts, 1, Parts.Tank.ordinal());

            // seal
            GameRegistry.addRecipe(this.setStackSize(leatherSeal, 16), new Object[] { "LL", "LL", 'L', Item.leather });
            // slime steal
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 4, Parts.GasSeal.ordinal()), " # ", "#S#", " # ", '#', "leatherSeal", 'S', Item.slimeBall));
            // part valve
            GameRegistry.addRecipe(new ShapedOreRecipe(valvePart, new Object[] { "PLP", 'P', "ironPipe", 'L', Block.lever }));
            //Basic Circuit
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.CircuitBasic.ordinal()), "!#!", "#@#", "!#!", '@', copperPlate, '#', Block.glass, '!', "copperWire"));
            //Advanced Circuit
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.CircuitAdvanced.ordinal()), "!#!", "#@#", "!#!", '@', copperPlate, '#', Item.redstone, '!', "copperWire"));
            //Elite Circuit
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.CircuitElite.ordinal()), "!#!", "#@#", "!#!", '@', "plateGold", '#', Item.redstone, '!', "copperWire"));

            // unfinished tank
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.Tank.ordinal()), " # ", "# #", " # ", '#', bronze));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.Tank.ordinal()), " # ", "# #", " # ", '#', steel));
            //Motor
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 1, Parts.Motor.ordinal()), new Object[] { "@!@", "!#!", "@!@", '!', steel, '#', Item.ingotIron, '@', new ItemStack(itemParts, 8, Parts.COIL.ordinal()) }));
            //Laser Diode
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 4, Parts.LASER.ordinal()), new Object[] { " G ", "!S!", " C ", '!', "copperWire", 'G', Block.glass, 'S', Block.sand, 'C', RecipeLoader.circuit }));
            //Coil
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemParts, 8, Parts.COIL.ordinal()), new Object[] { "WWW", "W W", "WWW", 'W', "copperWire" }));

        }

        if (itemMetals instanceof ItemOreDirv)
        {
            //Alt salvaging item list
            MachineRecipeHandler.newAltProcessorOutput(ProcessorType.GRINDER, Block.wood, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.DUST, 3));
            MachineRecipeHandler.newAltProcessorOutput(ProcessorType.GRINDER, Block.planks, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.DUST, 1));

            MachineRecipeHandler.newAltProcessorOutput(ProcessorType.CRUSHER, Block.wood, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.SCRAPS, 3));
            MachineRecipeHandler.newAltProcessorOutput(ProcessorType.CRUSHER, Block.planks, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.SCRAPS, 1));

            //Stone recipes
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, Block.stone, EnumMaterial.getStack(EnumMaterial.STONE, EnumOrePart.DUST, 1));

            //Wood recipes
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, Block.wood, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.DUST, 3));
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, Block.planks, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.DUST, 1));
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.wood, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.SCRAPS, 3));
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.planks, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.SCRAPS, 1));

            //Gold Recipes
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.blockIron, EnumMaterial.getStack(EnumMaterial.IRON, EnumOrePart.SCRAPS, 8));
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.oreIron, EnumMaterial.getStack(EnumMaterial.IRON, EnumOrePart.RUBBLE, 1));
            //Iron Recipes
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.blockGold, EnumMaterial.getStack(EnumMaterial.GOLD, EnumOrePart.SCRAPS, 8));
            MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, Block.oreGold, EnumMaterial.getStack(EnumMaterial.GOLD, EnumOrePart.RUBBLE, 1));
            //Dust recipes
            GameRegistry.addShapelessRecipe(EnumMaterial.getStack(EnumMaterial.STEEL, EnumOrePart.DUST, 1), new Object[] { EnumMaterial.getStack(EnumMaterial.IRON, EnumOrePart.DUST, 1), new ItemStack(Item.coal, 1, 0), new ItemStack(Item.coal, 1, 0) });
            GameRegistry.addShapelessRecipe(EnumMaterial.getStack(EnumMaterial.STEEL, EnumOrePart.DUST, 1), new Object[] { EnumMaterial.getStack(EnumMaterial.IRON, EnumOrePart.DUST, 1), new ItemStack(Item.coal, 1, 1), new ItemStack(Item.coal, 1, 1) });
            GameRegistry.addShapelessRecipe(EnumMaterial.getStack(EnumMaterial.STEEL, EnumOrePart.DUST, 1), new Object[] { EnumMaterial.getStack(EnumMaterial.IRON, EnumOrePart.DUST, 1), new ItemStack(Item.coal, 1, 1), new ItemStack(Item.coal, 1, 0) });
            GameRegistry.addShapelessRecipe(EnumMaterial.getStack(EnumMaterial.BRONZE, EnumOrePart.DUST, 4), new Object[] { EnumMaterial.getStack(EnumMaterial.COPPER, EnumOrePart.DUST, 1), EnumMaterial.getStack(EnumMaterial.COPPER, EnumOrePart.DUST, 1), EnumMaterial.getStack(EnumMaterial.COPPER, EnumOrePart.DUST, 1), EnumMaterial.getStack(EnumMaterial.TIN, EnumOrePart.DUST, 1) });

            if (debugOreItems)
            {
                System.out.println("\n\nTesting material part returns");
                for (EnumMaterial mat : EnumMaterial.values())
                {
                    System.out.println("\n-Material-> " + mat.simpleName);
                    for (EnumOrePart part : EnumOrePart.values())
                    {
                        if (mat.shouldCreateItem(part))
                        {
                            System.out.println("--Part-> " + part.simpleName);
                            System.out.println("----ItemStack-> " + mat.getStack(part, 1).toString());
                        }
                    }
                }
            }
            //Ore material recipe loop
            for (EnumMaterial mat : EnumMaterial.values())
            {
                //Dust recipes
                if (mat.shouldCreateItem(EnumOrePart.DUST))
                {
                    FurnaceRecipes.smelting().addSmelting(mat.getStack(EnumOrePart.DUST, 1).itemID, mat.getStack(EnumOrePart.DUST, 1).getItemDamage(), mat.getStack(EnumOrePart.INGOTS, 1), 0.6f);
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(EnumOrePart.RUBBLE, 1), mat.getStack(EnumOrePart.DUST, 1), 1, 4);
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(EnumOrePart.SCRAPS, 1), mat.getStack(EnumOrePart.DUST, 1));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(EnumOrePart.INGOTS, 1), mat.getStack(EnumOrePart.DUST, 1));
                    MachineRecipeHandler.newAltProcessorOutput(ProcessorType.GRINDER, mat.getStack(EnumOrePart.INGOTS, 1), mat.getStack(EnumOrePart.DUST, 1));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(EnumOrePart.PLATES, 1), mat.getStack(EnumOrePart.DUST, 1), 2, 4);
                    MachineRecipeHandler.newAltProcessorOutput(ProcessorType.GRINDER, mat.getStack(EnumOrePart.PLATES, 1), mat.getStack(EnumOrePart.DUST, 3));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(EnumOrePart.ROD, 1), mat.getStack(EnumOrePart.DUST, 1));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, mat.getStack(EnumOrePart.TUBE, 1), mat.getStack(EnumOrePart.DUST, 1));
                }

                // Salvaging recipe

                if (mat.shouldCreateItem(EnumOrePart.SCRAPS))
                {
                    FurnaceRecipes.smelting().addSmelting(mat.getStack(EnumOrePart.SCRAPS, 1).itemID, mat.getStack(EnumOrePart.SCRAPS, 1).getItemDamage(), mat.getStack(EnumOrePart.INGOTS, 1), 0.6f);
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, mat.getStack(EnumOrePart.PLATES, 1), mat.getStack(EnumOrePart.SCRAPS, 3));
                    MachineRecipeHandler.newAltProcessorOutput(ProcessorType.CRUSHER, mat.getStack(EnumOrePart.PLATES, 1), mat.getStack(EnumOrePart.SCRAPS, 3));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, mat.getStack(EnumOrePart.RUBBLE, 1), mat.getStack(EnumOrePart.SCRAPS, 1), 1, 5);
                    MachineRecipeHandler.newAltProcessorOutput(ProcessorType.CRUSHER, mat.getStack(EnumOrePart.INGOTS, 1), mat.getStack(EnumOrePart.SCRAPS, 1));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, mat.getStack(EnumOrePart.ROD, 1), mat.getStack(EnumOrePart.SCRAPS, 1));
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, mat.getStack(EnumOrePart.TUBE, 1), mat.getStack(EnumOrePart.SCRAPS, 1));
                }
                if (mat.shouldCreateItem(EnumOrePart.TUBE))
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(EnumOrePart.TUBE, 6), new Object[] { "III", 'I', mat.simpleName + "ingot" }));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(EnumOrePart.TUBE, 6), new Object[] { "III", 'I', "ingot" + mat.simpleName }));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(EnumOrePart.TUBE, 1), new Object[] { "I", 'I', "rod" + mat.simpleName }));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(EnumOrePart.TUBE, 1), new Object[] { "I", 'I', mat.simpleName + "rod" }));
                }
                if (mat.shouldCreateItem(EnumOrePart.ROD))
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(EnumOrePart.ROD, 4), new Object[] { "II", 'I', mat.simpleName + "rod" }));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(EnumOrePart.ROD, 4), new Object[] { "II", 'I', "rod" + mat.simpleName }));
                }
                if (mat.shouldCreateItem(EnumOrePart.PLATES))
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(EnumOrePart.PLATES, 1), new Object[] { "II", "II", 'I', mat.simpleName + "ingot" }));
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(EnumOrePart.PLATES, 1), new Object[] { "II", "II", 'I', "ingot" + mat.simpleName }));
                }
                if (mat.shouldCreateItem(EnumOrePart.GEARS))
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(mat.getStack(EnumOrePart.GEARS, 4), new Object[] { " I", "IRI", " I ", 'I', "ingot" + mat.simpleName, 'R', mat.shouldCreateItem(EnumOrePart.ROD) ? mat.getStack(EnumOrePart.ROD) : Item.stick }));
                }

            }
        }

        if (blockOre instanceof BlockOre)
        {
            for (OreData data : OreData.values())
            {
                if (CoreRecipeLoader.itemMetals instanceof ItemOreDirv)
                {
                    FurnaceRecipes.smelting().addSmelting(blockOre.blockID, data.ordinal(), EnumMaterial.getStack(data.mat, EnumOrePart.INGOTS, 1), 0.6f);
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.CRUSHER, new ItemStack(blockOre.blockID, 1, data.ordinal()), EnumMaterial.getStack(data.mat, EnumOrePart.RUBBLE, 1), 1, 2);
                    MachineRecipeHandler.newProcessorRecipe(ProcessorType.GRINDER, new ItemStack(blockOre.blockID, 1, data.ordinal()), EnumMaterial.getStack(data.mat, EnumOrePart.DUST, 1), 1, 3);

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
