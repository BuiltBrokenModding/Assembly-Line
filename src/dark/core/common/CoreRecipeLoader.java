package dark.core.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.core.common.blocks.BlockBasalt;
import dark.core.common.blocks.BlockOre.OreData;
import dark.core.common.items.EnumMaterial;
import dark.core.common.items.EnumOrePart;
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
    public static Item itemRefinedSand;
    public static Item itemGlowingSand;

    @Override
    public void loadRecipes()
    {
        super.loadRecipes();
        new RecipeGrid(new ItemStack(itemTool, 1, 0), 3, 2).setRowOne("ironTube", "valvePart", "ironTube").setRowTwo(null, "ironTube", null).RegisterRecipe();

        this.loadSmeltingRecipes();
        this.loadParts();
    }

    public void loadParts()
    {
        if (itemParts != null)
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
    }

    public void loadSmeltingRecipes()
    {
        if (blockOre != null && itemMetals != null)
        {

            for (int i = 0; i < EnumMaterial.values().length; i++)
            {
                if (EnumMaterial.values()[i].shouldCreateItem(EnumOrePart.DUST) && EnumMaterial.values()[i] != EnumMaterial.WOOD && EnumMaterial.values()[i] != EnumMaterial.COAL)
                {
                    FurnaceRecipes.smelting().addSmelting(itemMetals.itemID, i + 20, new ItemStack(itemMetals.itemID, 1, 40 + i), 0.6f);
                }
            }
        }
    }

    public void loadStainGlass()
    {
        // Stained Glass //
        if (itemRefinedSand != null)
        {
            for (int i = 0; i < DarkMain.dyeColorNames.length; i++)
            {
                FurnaceRecipes.smelting().addSmelting(itemRefinedSand.itemID, i, new ItemStack(blockStainGlass, 1, i), 10F);
            }

            for (int j = 0; j < DarkMain.dyeColorNames.length; j++)
            {
                GameRegistry.addShapelessRecipe(new ItemStack(itemRefinedSand, 1, j), new Object[] { new ItemStack(blockColorSand, 1, j) });
            }
        }

        // Glowing Glass //
        if (itemGlowingSand != null)
        {
            for (int i = 0; i < DarkMain.dyeColorNames.length; i++)
            {
                FurnaceRecipes.smelting().addSmelting(itemGlowingSand.itemID, i, new ItemStack(blockGlowGlass, 1, i), 10F);
            }
            for (int j = 0; j < DarkMain.dyeColorNames.length; j++)
            {
                GameRegistry.addShapelessRecipe(new ItemStack(itemGlowingSand, 1, j), new Object[] { new ItemStack(itemRefinedSand, 1, j), Item.redstone });
                GameRegistry.addShapelessRecipe(new ItemStack(itemGlowingSand, 1, j), new Object[] { new ItemStack(itemRefinedSand, 1, j), Item.glowstone });

            }
        }

        // Colored Sand //
        if (blockColorSand != null)
        {
            for (int j = 0; j < DarkMain.dyeColorNames.length; j++)
            {
                GameRegistry.addRecipe(new ItemStack(blockColorSand, 8, j), new Object[] { "SSS", "SDS", "SSS", 'S', Block.sand, 'D', new ItemStack(Item.dyePowder, 1, j) });
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
