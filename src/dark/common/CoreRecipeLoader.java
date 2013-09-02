package dark.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import dark.common.items.EnumMeterials;
import dark.common.items.EnumOreParts;
import dark.common.items.ItemWrench;
import dark.common.items.ItemParts.Parts;

public class CoreRecipeLoader extends RecipeLoader
{

    /* BLOCKS */
    public static Block blockOre, blockDebug, blockWire;

    /* ITEMS */
    public static Item itemMetals, battery, itemTool, itemParts;
    public static ItemWrench wrench;

    public static ItemStack ironTube, bronzeTube, obbyTube, netherTube;
    public static ItemStack leatherSeal, slimeSeal;
    public static ItemStack valvePart;
    public static ItemStack unfinishedTank;

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
            for (int i = 0; i < EnumMeterials.values().length; i++)
            {
                if (EnumMeterials.values()[i].doWorldGen)
                {
                    FurnaceRecipes.smelting().addSmelting(blockOre.blockID, i, new ItemStack(itemMetals.itemID, 1, 40 + i), 0.6f);
                }
                if (EnumMeterials.values()[i].shouldCreateItem(EnumOreParts.DUST) && EnumMeterials.values()[i] != EnumMeterials.WOOD && EnumMeterials.values()[i] != EnumMeterials.COAL)
                {
                    FurnaceRecipes.smelting().addSmelting(itemMetals.itemID, i + 20, new ItemStack(itemMetals.itemID, 1, 40 + i), 0.6f);
                }
            }
        }
    }
}
