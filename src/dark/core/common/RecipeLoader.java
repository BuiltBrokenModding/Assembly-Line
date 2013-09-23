package dark.core.common;

import com.builtbroken.common.Pair;
import com.builtbroken.common.Triple;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

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
            motor = Block.pistonBase;
            bronze = Item.ingotIron;
            bronzePlate = Item.ingotGold;
            /* Ore directory items load over teh vinalla ones if they are present */
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
        }
    }

    public ItemStack setStackSize(ItemStack stack, int amount)
    {
        if (stack != null)
        {
            return new ItemStack(stack.itemID, amount, stack.getItemDamage());
        }
        return stack;
    }

    public static class RecipeGrid
    {
        Object[] rl = new Object[9];
        Object out;
        int width = 3;
        int hight = 3;

        public RecipeGrid(Object stack)
        {
            out = stack;
        }

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
            out = stack;
            return this;
        }

        public RecipeGrid setRowOne(Object... objects)
        {
            if (objects != null)
            {
                this.rl[0] = objects[0];
                if (objects.length > 1)
                {
                    this.rl[1] = objects[1];
                }
                if (objects.length > 2)
                {
                    this.rl[2] = objects[2];
                }
            }
            return this;
        }

        public RecipeGrid setRowTwo(Object... objects)
        {
            if (objects != null)
            {
                this.rl[3] = objects[0];
                if (objects.length > 1)
                {
                    this.rl[4] = objects[1];
                }
                if (objects.length > 2)
                {
                    this.rl[5] = objects[2];
                }
            }
            return this;
        }

        public RecipeGrid setRowThree(Object... objects)
        {
            if (objects != null)
            {
                this.rl[6] = objects[0];
                if (objects.length > 1)
                {
                    this.rl[7] = objects[1];
                }
                if (objects.length > 2)
                {
                    this.rl[8] = objects[2];
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
            ShapedOreRecipe receipe = this.getShapedRecipe();
            if (receipe != null)
            {
                GameRegistry.addRecipe(receipe);
            }
        }

        public ShapedOreRecipe getShapedRecipe()
        {
            //TODO convert items to their ore names
            ShapedOreRecipe re = null;
            Object[] recipe = null;
            String[] r = { "A", "B", "C", "D", "E", "F", "G", "H", "I" };

            for (int i = 0; i < r.length; i++)
            {
                if (this.rl[i] == null)
                {
                    r[i] = " ";
                    this.rl[i] = "";
                }
                else if (this.rl[i] instanceof ItemStack)
                {
                    String s = OreDictionary.getOreName(OreDictionary.getOreID((ItemStack) this.rl[i]));
                    if (s != null)
                    {
                        this.rl[i] = s;
                    }
                }
            }

            if (width == 3 && hight == 3)
            {
                recipe = new Object[] { r[0] + r[1] + r[2], r[3] + r[4] + r[5], r[6] + r[7] + r[8], 'A', rl[0], 'B', rl[1], 'C', rl[2], 'D', rl[3], 'E', rl[4], 'F', rl[5], 'G', rl[6], 'H', rl[7], 'I', rl[8] };
            }
            else if (width == 2 && hight == 3)
            {
                recipe = new Object[] { r[0] + r[1], r[3] + r[4], r[6] + r[7], 'A', rl[0], 'B', rl[1], 'D', rl[3], 'E', rl[4], 'G', rl[6], 'H', rl[7] };
            }
            else if (width == 3 && hight == 2)
            {
                recipe = new Object[] { r[0] + r[1] + r[2], r[3] + r[4] + r[5], 'A', rl[0], 'B', rl[1], 'C', rl[2], 'D', rl[3], 'E', rl[4], 'F', rl[5] };
            }
            else if (width == 1 && hight == 3)
            {
                recipe = new Object[] { r[0], r[3], r[6], 'A', rl[0], 'D', rl[3], 'G', rl[6] };
            }
            else if (width == 3 && hight == 1)
            {
                recipe = new Object[] { r[0] + r[1] + r[2], 'A', rl[0], 'B', rl[1], 'C', rl[2] };
            }
            else if (width == 2 && hight == 2)
            {
                recipe = new Object[] { r[0] + r[1], r[3] + r[4], 'A', rl[0], 'B', rl[1], 'D', rl[2], 'E', rl[4] };
            }
            else if (width == 1 && hight == 2)
            {
                recipe = new Object[] { r[0], r[3], 'A', rl[0], 'D', rl[3], };
            }
            else if (width == 2 && hight == 1)
            {
                recipe = new Object[] { r[0] + r[1], 'A', rl[0], 'B', rl[1], };
            }
            else if (width == 1 && hight == 1)
            {
                recipe = new Object[] { r[0], 'A', rl[0] };
            }
            if (recipe != null)
            {
                if (out instanceof Block)
                {
                    re = new ShapedOreRecipe(((Block) out), recipe);
                }
                else if (out instanceof Item)
                {
                    re = new ShapedOreRecipe(((Item) out), recipe);
                }
                else if (out instanceof ItemStack)
                {
                    re = new ShapedOreRecipe(((ItemStack) out), recipe);
                }
            }
            return re;
        }
    }
}
