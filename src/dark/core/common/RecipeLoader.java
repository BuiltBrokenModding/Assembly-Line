package dark.core.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.builtbroken.common.Pair;
import com.builtbroken.common.Triple;

import cpw.mods.fml.common.registry.GameRegistry;
import dark.core.helpers.AutoCraftingManager;

/** Recipe system to make it easier to load recipes for a mod
 * 
 * @author DarkGuardsman */
public abstract class RecipeLoader
{
    protected static Object circuit;
    protected static Object circuit2;
    protected static Object circuit3;
    protected static Object steel;
    protected static Object steelPlate;
    protected static Object motor;
    protected static Object bronze;
    protected static Object bronzePlate;
    protected static Object copper;
    protected static Object copperPlate;

    static boolean loaded = false;

    /** Should be called to load recipes. The main class only loads ore name items to decrease
     * chances of missing items in recipes */
    public void loadRecipes()
    {
        if (!loaded)
        {
            /* Vinalla items load first */
            circuit = Item.redstoneRepeater;
            circuit2 = Item.comparator;
            steel = Item.ingotIron;
            steelPlate = Item.ingotGold;
            copper = Item.ingotIron;
            copperPlate = Item.ingotGold;
            motor = Block.pistonBase;
            bronze = Item.ingotIron;
            bronzePlate = Item.ingotGold;
            /* Ore directory items load over the vinalla ones if they are present */
            if (OreDictionary.getOres("basicCircuit").size() > 0)
            {
                circuit = "basicCircuit";
            }
            if (OreDictionary.getOres("advancedCircuit").size() > 0)
            {
                circuit = "advancedCircuit";
            }
            if (OreDictionary.getOres("ingotSteel").size() > 0)
            {
                steel = "ingotSteel";
            }
            if (OreDictionary.getOres("plateSteel").size() > 0)
            {
                steelPlate = "plateSteel";
            }
            if (OreDictionary.getOres("motor").size() > 0)
            {
                motor = "motor";
            }
            if (OreDictionary.getOres("ingotBronze").size() > 0)
            {
                bronze = "ingotBronze";
            }
            if (OreDictionary.getOres("plateBronze").size() > 0)
            {
                bronzePlate = "plateBronze";
            }
            if (OreDictionary.getOres("copperBronze").size() > 0)
            {
                bronze = "copperBronze";
            }
            if (OreDictionary.getOres("copperBronze").size() > 0)
            {
                bronzePlate = "copperBronze";
            }
        }
    }

    public ItemStack setStackSize(ItemStack stack, int amount)
    {
        if (stack != null)
        {
            ItemStack itemStack = stack.copy();
            itemStack.stackSize = amount;
            return itemStack;
        }
        return stack;
    }

    /** An easier to read recipe system for the basic minecraft recipes
     * 
     * @author DarkGaurdsman */
    @Deprecated
    public static class RecipeGrid
    {
        Object[] items = new Object[9];
        Object output;
        int width = 3;
        int hight = 3;

        /** @param stack - output item */
        public RecipeGrid(Object stack)
        {
            output = stack;
        }

        /** @param stack - output item
         * @param w - width of grid
         * @param h - height of grid */
        public RecipeGrid(Object stack, int w, int h)
        {
            this(stack);
            this.setSize(w, h);
        }

        /** 3x3 Crafting grid. Each Triple is a row. Input for the triples should be any of { Item,
         * Block, ItemStack, String}
         * 
         * @param one - top row
         * @param two - middle row
         * @param three - bottom row */
        public RecipeGrid(Object stack, Triple one, Triple two, Triple three)
        {
            this(stack);
            this.setRowOne(one.getA(), one.getB(), one.getC());
            this.setRowTwo(two.getA(), two.getB(), two.getC());
            this.setRowThree(three.getA(), three.getB(), three.getC());
        }

        /** 2x2 Crafting grid. Each Pair is a row. Input for the pairs should be any of { Item,
         * Block, ItemStack, String}
         * 
         * @param one - top row
         * @param two - middle row */
        public RecipeGrid(Object stack, Pair one, Pair two)
        {
            this(stack);
            this.setRowOne(one.left(), one.right());
            this.setRowTwo(two.left(), two.right());
            this.hight = 2;
            this.width = 2;
        }

        /** Sets the grid size */
        public RecipeGrid setSize(int w, int h)
        {
            this.width = Math.max(Math.min(w, 3), 1);
            this.hight = Math.max(Math.min(h, 3), 1);
            return this;
        }

        /** Sets the grid recipe output */
        public RecipeGrid setOutput(Object stack)
        {
            output = stack;
            return this;
        }

        public RecipeGrid setRowOne(Object... objects)
        {
            if (objects != null)
            {
                this.items[0] = objects[0];
                if (objects.length > 1)
                {
                    this.items[1] = objects[1];
                }
                if (objects.length > 2)
                {
                    this.items[2] = objects[2];
                }
            }
            return this;
        }

        public RecipeGrid setRowTwo(Object... objects)
        {
            if (objects != null)
            {
                this.items[3] = objects[0];
                if (objects.length > 1)
                {
                    this.items[4] = objects[1];
                }
                if (objects.length > 2)
                {
                    this.items[5] = objects[2];
                }
            }
            return this;
        }

        public RecipeGrid setRowThree(Object... objects)
        {
            if (objects != null)
            {
                this.items[6] = objects[0];
                if (objects.length > 1)
                {
                    this.items[7] = objects[1];
                }
                if (objects.length > 2)
                {
                    this.items[8] = objects[2];
                }
            }
            return this;
        }

        public String getOreName(ItemStack stack)
        {
            if (stack != null)
            {
                return OreDictionary.getOreName(OreDictionary.getOreID(stack));
            }
            return null;
        }

        public void RegisterRecipe()
        {
            ShapedOreRecipe shapedOreRecipe = null;
            Object[] recipe = null;
            String[] slots = { "A", "B", "C", "D", "E", "F", "G", "H", "I" };

            for (int i = 0; i < slots.length; i++)
            {
                if (this.items[i] == null || this.items[i] instanceof String && ((String) this.items[i]).isEmpty())
                {
                    slots[i] = " ";
                    this.items[i] = "";
                }//TODO include normal items and blocks
                else if (this.items[i] instanceof ItemStack)
                {
                    //Automatically converts an itemstack to its orename so that recipes are more mod compatible
                    String s = OreDictionary.getOreName(OreDictionary.getOreID((ItemStack) this.items[i]));
                    if (s != null && !s.equals("Unknown"))
                    {
                        this.items[i] = s;
                    }
                }
            }

            if (width == 3 && hight == 3)
            {
                recipe = new Object[] { slots[0] + slots[1] + slots[2], slots[3] + slots[4] + slots[5], slots[6] + slots[7] + slots[8], 'A', items[0], 'B', items[1], 'C', items[2], 'D', items[3], 'E', items[4], 'F', items[5], 'G', items[6], 'H', items[7], 'I', items[8] };
            }
            else if (width == 2 && hight == 3)
            {
                recipe = new Object[] { slots[0] + slots[1], slots[3] + slots[4], slots[6] + slots[7], 'A', items[0], 'B', items[1], 'D', items[3], 'E', items[4], 'G', items[6], 'H', items[7] };
            }
            else if (width == 3 && hight == 2)
            {
                recipe = new Object[] { slots[0] + slots[1] + slots[2], slots[3] + slots[4] + slots[5], 'A', items[0], 'B', items[1], 'C', items[2], 'D', items[3], 'E', items[4], 'F', items[5] };
            }
            else if (width == 1 && hight == 3)
            {
                recipe = new Object[] { slots[0], slots[3], slots[6], 'A', items[0], 'D', items[3], 'G', items[6] };
            }
            else if (width == 3 && hight == 1)
            {
                recipe = new Object[] { slots[0] + slots[1] + slots[2], 'A', items[0], 'B', items[1], 'C', items[2] };
            }
            else if (width == 2 && hight == 2)
            {
                recipe = new Object[] { slots[0] + slots[1], slots[3] + slots[4], 'A', items[0], 'B', items[1], 'D', items[2], 'E', items[4] };
            }
            else if (width == 1 && hight == 2)
            {
                recipe = new Object[] { slots[0], slots[3], 'A', items[0], 'D', items[3], };
            }
            else if (width == 2 && hight == 1)
            {
                recipe = new Object[] { slots[0] + slots[1], 'A', items[0], 'B', items[1], };
            }
            else if (width == 1 && hight == 1)
            {
                recipe = new Object[] { slots[0], 'A', items[0] };
            }
            else
            {
                System.out.println("Recipe was created with an out of bounds size");
                System.out.println(this.toString());
            }
            if (recipe != null)
            {
                if (output instanceof Block)
                {
                    shapedOreRecipe = new ShapedOreRecipe(((Block) output), recipe);
                }
                else if (output instanceof Item)
                {
                    shapedOreRecipe = new ShapedOreRecipe(((Item) output), recipe);
                }
                else if (output instanceof ItemStack)
                {
                    shapedOreRecipe = new ShapedOreRecipe(((ItemStack) output), recipe);
                }
                else
                {
                    System.out.println("Recipe was created with a null output");
                    System.out.println(this.toString());
                }
            }
            if (CoreRecipeLoader.debugOreItems)
            {
                System.out.println("\nRecipe created");
                System.out.println(this.toString());
                AutoCraftingManager.printRecipes(shapedOreRecipe.getRecipeOutput());
            }
            if (shapedOreRecipe != null)
            {
                GameRegistry.addRecipe(shapedOreRecipe);
            }
        }

        @Override
        public String toString()
        {
            return "RecipeGrid[Out: " + (output != null ? output.toString() : "null") + "\n In: " + " " + items[0] + "|  " + items[1] + "|  " + items[2] + "\n    " + items[3] + "|  " + items[4] + "|  " + items[5] + "\n    " + items[6] + "|  " + items[7] + "|  " + items[8];
        }
    }
}
