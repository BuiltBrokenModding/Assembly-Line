package dark.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.prefab.helpers.Pair;
import dark.prefab.helpers.Triple;

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
        Object A, B, C, D, E, F, G, H, I;
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
            this.setRowOne(one.getKey(), one.getValue());
            this.setRowTwo(two.getKey(), two.getValue());
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
                this.A = objects[0];
                if (objects.length > 1)
                {
                    this.B = objects[1];
                }
                if (objects.length > 2)
                {
                    this.C = objects[2];
                }
            }
            return this;
        }

        public RecipeGrid setRowTwo(Object... objects)
        {
            if (objects != null)
            {
                this.D = objects[0];
                if (objects.length > 1)
                {
                    this.E = objects[1];
                }
                if (objects.length > 2)
                {
                    this.F = objects[2];
                }
            }
            return this;
        }

        public RecipeGrid setRowThree(Object... objects)
        {
            if (objects != null)
            {
                this.G = objects[0];
                if (objects.length > 1)
                {
                    this.H = objects[1];
                }
                if (objects.length > 2)
                {
                    this.I = objects[2];
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
            ShapedOreRecipe re = null;
            Object[] recipe = null;
            String AA = "A";
            String BB = "B";
            String CC = "C";
            String DD = "D";
            String EE = "E";
            String FF = "F";
            String GG = "G";
            String HH = "H";
            String II = "I";
            if (A == null)
            {
                AA = " ";
                A = "";
            }
            if (B == null)
            {
                BB = " ";
                B = "";
            }
            if (C == null)
            {
                CC = " ";
                C = "";
            }
            if (D == null)
            {
                DD = " ";
                D = "";
            }
            if (E == null)
            {
                EE = " ";
                E = "";
            }
            if (F == null)
            {
                FF = " ";
                F = "";
            }
            if (G == null)
            {
                GG = " ";
                G = "";
            }
            if (H == null)
            {
                HH = " ";
                H = "";
            }
            if (I == null)
            {
                II = " ";
                I = "";
            }

            if (width == 3 && hight == 3)
            {
                recipe = new Object[] { AA + BB + CC, DD + EE + FF, GG + HH + II, 'A', A, 'B', B, 'C', C, 'D', D, 'E', E, 'F', F, 'G', G, 'H', H, 'I', I };
            }
            else if (width == 2 && hight == 3)
            {
                recipe = new Object[] { AA + BB, DD + EE, GG + HH, 'A', A, 'B', B, 'D', D, 'E', E, 'G', G, 'H', H };
            }
            else if (width == 3 && hight == 2)
            {
                recipe = new Object[] { AA + BB + CC, DD + EE + FF, 'A', A, 'B', B, 'C', C, 'D', D, 'E', E, 'F', F };
            }
            else if (width == 1 && hight == 3)
            {
                recipe = new Object[] { AA, DD, GG, 'A', A, 'D', D, 'G', G };
            }
            else if (width == 3 && hight == 1)
            {
                recipe = new Object[] { AA + BB + CC, 'A', A, 'B', B, 'C', C };
            }
            else if (width == 2 && hight == 2)
            {
                recipe = new Object[] { AA + BB, DD + EE, 'A', A, 'B', B, 'D', D, 'E', E };
            }
            else if (width == 1 && hight == 2)
            {
                recipe = new Object[] { AA, DD, 'A', A, 'D', D, };
            }
            else if (width == 2 && hight == 1)
            {
                recipe = new Object[] { AA + BB, 'A', A, 'B', B, };
            }
            else if (width == 1 && hight == 1)
            {
                recipe = new Object[] { AA, 'A', A };
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
