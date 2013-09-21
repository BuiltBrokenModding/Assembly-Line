package dark.core.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.api.ColorCode;
import dark.api.ProcessorRecipes;
import dark.api.ProcessorRecipes.ProcessorType;
import dark.core.common.blocks.BlockBasalt;
import dark.core.common.blocks.BlockOre;
import dark.core.common.blocks.BlockOre.OreData;
import dark.core.common.items.EnumMaterial;
import dark.core.common.items.EnumOrePart;
import dark.core.common.items.ItemOreDirv;
import dark.core.common.items.ItemParts;
import dark.core.common.items.ItemParts.Parts;
import dark.core.common.items.ItemWrench;

public class CoreRecipeLoader extends RecipeLoader
{

    /* BLOCKS */
    public static Block blockOre, blockDebug, blockWire;
    public static Block blockStainGlass;
    public static Block blockColorSand;
    public static Block blockBasalt;
    public static Block blockGlowGlass;

    /* ITEMS */
    public static Item itemMetals, battery, itemTool, itemParts;
    public static ItemWrench wrench;

    public static ItemStack ironTube, bronzeTube, obbyTube, netherTube;
    public static ItemStack leatherSeal, slimeSeal;
    public static ItemStack valvePart;
    public static ItemStack unfinishedTank;
    public static Item itemGlowingSand;

    @Override
    public void loadRecipes()
    {
        super.loadRecipes();
        new RecipeGrid(new ItemStack(itemTool, 1, 0), 3, 2).setRowOne("ironTube", "valvePart", "ironTube").setRowTwo(null, "ironTube", null).RegisterRecipe();

        this.loadParts();
    }

    public void loadParts()
    {
        if (itemParts instanceof ItemParts)
        {
            ironTube = new ItemStack(itemParts, 1, Parts.Iron.ordinal());
            bronzeTube = new ItemStack(itemParts, 1, Parts.Bronze.ordinal());
            obbyTube = new ItemStack(itemParts, 1, Parts.Obby.ordinal());
            netherTube = new ItemStack(itemParts, 1, Parts.Nether.ordinal());
            leatherSeal = new ItemStack(itemParts, 1, Parts.Seal.ordinal());
            slimeSeal = new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal());
            valvePart = new ItemStack(itemParts, 1, Parts.Tank.ordinal());
            unfinishedTank = new ItemStack(itemParts, 1, Parts.Tank.ordinal());

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
            new RecipeGrid(unfinishedTank).setRowOne(null, Item.ingotIron, null).setRowTwo(Item.ingotIron, null, Item.ingotIron).setRowThree(null, Item.ingotIron, null).RegisterRecipe();
            new RecipeGrid(unfinishedTank).setRowOne(null, bronze, null).setRowTwo(bronze, null, bronze).setRowThree(null, bronze, null).RegisterRecipe();
        }

        if (itemMetals instanceof ItemOreDirv)
        {
            //Alt salvaging item list
            ProcessorRecipes.createSalvageDamageOutput(ProcessorType.GRINDER, Block.wood, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.DUST, 3));
            ProcessorRecipes.createSalvageDamageOutput(ProcessorType.GRINDER, Block.planks, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.DUST, 1));

            ProcessorRecipes.createSalvageDamageOutput(ProcessorType.CRUSHER, Block.wood, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.SCRAPS, 3));
            ProcessorRecipes.createSalvageDamageOutput(ProcessorType.CRUSHER, Block.planks, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.SCRAPS, 1));

            //Stone recipes
            ProcessorRecipes.createRecipe(ProcessorType.GRINDER, Block.stone, EnumMaterial.getStack(EnumMaterial.STONE, EnumOrePart.DUST, 1));

            //Wood recipes
            ProcessorRecipes.createRecipe(ProcessorType.GRINDER, Block.wood, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.DUST, 3));
            ProcessorRecipes.createRecipe(ProcessorType.GRINDER, Block.planks, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.DUST, 1));
            ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, Block.wood, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.SCRAPS, 3));
            ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, Block.planks, EnumMaterial.getStack(EnumMaterial.WOOD, EnumOrePart.SCRAPS, 1));

            //Gold Recipes
            ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, Block.blockIron, EnumMaterial.getStack(EnumMaterial.IRON, EnumOrePart.SCRAPS, 8));
            ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, Block.oreIron, EnumMaterial.getStack(EnumMaterial.IRON, EnumOrePart.RUBBLE, 1));
            //Iron Recipes
            ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, Block.blockGold, EnumMaterial.getStack(EnumMaterial.GOLD, EnumOrePart.SCRAPS, 8));
            ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, Block.oreGold, EnumMaterial.getStack(EnumMaterial.GOLD, EnumOrePart.RUBBLE, 1));

            //Ore material recipe loop
            for (EnumMaterial mat : EnumMaterial.values())
            {
                ItemStack dust = EnumMaterial.getStack(mat, EnumOrePart.DUST, 2);
                ItemStack ingot = EnumMaterial.getStack(mat, EnumOrePart.INGOTS, 1);
                ItemStack scraps = EnumMaterial.getStack(mat, EnumOrePart.SCRAPS, 1);
                ItemStack plates = EnumMaterial.getStack(mat, EnumOrePart.PLATES, 1);
                ItemStack rubble = EnumMaterial.getStack(mat, EnumOrePart.RUBBLE, 1);

                //Smelting recipes
                FurnaceRecipes.smelting().addSmelting(dust.itemID, dust.getItemDamage(), ingot, 0.6f);
                FurnaceRecipes.smelting().addSmelting(scraps.itemID, scraps.getItemDamage(), ingot, 0.6f);

                //Dust recipes
                ProcessorRecipes.createRecipe(ProcessorType.GRINDER, rubble, dust);
                dust.stackSize = 1;
                ProcessorRecipes.createRecipe(ProcessorType.GRINDER, scraps, dust);
                ProcessorRecipes.createRecipe(ProcessorType.GRINDER, ingot, dust);
                ProcessorRecipes.createSalvageDamageOutput(ProcessorType.GRINDER, ingot, dust);

                // Salvaging recipe
                scraps.stackSize = 3;
                dust.stackSize = 2;
                ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, plates, scraps);
                ProcessorRecipes.createSalvageDamageOutput(ProcessorType.CRUSHER, plates, scraps);
                ProcessorRecipes.createRecipe(ProcessorType.GRINDER, plates, dust);
                ProcessorRecipes.createSalvageDamageOutput(ProcessorType.GRINDER, plates, dust);
                scraps.stackSize = 2;
                ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, rubble, scraps);
                scraps.stackSize = 1;
                ProcessorRecipes.createSalvageDamageOutput(ProcessorType.CRUSHER, ingot, scraps);

                //Press recipes TODO set this up another way since input.stackSize can only equal 1
                //ingot.stackSize = 3;
                //ProcessorRecipes.createRecipe(ProcessorType.PRESS, ingot, plates);

            }
        }

        if (blockOre instanceof BlockOre)
        {
            for (OreData data : OreData.values())
            {
                if (CoreRecipeLoader.itemMetals instanceof ItemOreDirv)
                {
                    FurnaceRecipes.smelting().addSmelting(blockOre.blockID, data.ordinal(), EnumMaterial.getStack(data.mat, EnumOrePart.INGOTS, 1), 0.6f);
                    ProcessorRecipes.createRecipe(ProcessorType.CRUSHER, new ItemStack(blockOre.blockID, 1, data.ordinal()), EnumMaterial.getStack(data.mat, EnumOrePart.RUBBLE, 1));
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
                    ProcessorRecipes.createRecipe(ProcessorType.GRINDER, new ItemStack(blockStainGlass, 1, code.ordinal()), new ItemStack(blockColorSand.blockID, 1, code.ordinal()));
                }
                GameRegistry.addShapelessRecipe(new ItemStack(blockColorSand.blockID, 1, code.ordinal()), new Object[] { new ItemStack(blockColorSand, 1, code.ordinal()) });
                ProcessorRecipes.createRecipe(ProcessorType.GRINDER, new ItemStack(blockColorSand, 1, code.ordinal()), new ItemStack(itemGlowingSand, 1, code.ordinal()));

            }

            // Glowing Glass //
            if (itemGlowingSand != null)
            {
                if (blockGlowGlass != null)
                {
                    FurnaceRecipes.smelting().addSmelting(itemGlowingSand.itemID, code.ordinal(), new ItemStack(blockGlowGlass, 1, code.ordinal()), 10F);
                    ProcessorRecipes.createRecipe(ProcessorType.GRINDER, new ItemStack(blockGlowGlass, 1, code.ordinal()), new ItemStack(itemGlowingSand, 1, code.ordinal()));
                }
                if (blockColorSand != null)
                {
                    GameRegistry.addShapelessRecipe(new ItemStack(itemGlowingSand, 1, code.ordinal()), new Object[] { new ItemStack(blockColorSand.blockID, 1, code.ordinal()), Item.redstone });
                    GameRegistry.addShapelessRecipe(new ItemStack(itemGlowingSand, 1, code.ordinal()), new Object[] { new ItemStack(blockColorSand.blockID, 1, code.ordinal()), Item.glowstone });
                    ProcessorRecipes.markUnsalvagable(new ItemStack(itemGlowingSand, 1, code.ordinal()));
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
