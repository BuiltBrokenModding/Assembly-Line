package dark.core;

import cpw.mods.fml.common.registry.GameRegistry;
import dark.core.helpers.Pair;
import dark.core.helpers.Triple;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public abstract class RecipeLoader
{
	protected static Object circuit;
	protected static Object circuit2;
	protected static Object circuit3;
	protected static Object steel;
	protected static Object steelPlate;
	protected static Object motor;

	/** Should be called to load recipes. The main class only loads ore name items to decrease
	 * chances of missing items in recipes */
	public void loadRecipes()
	{
		/* Vinalla items load first */
		circuit = Item.redstoneRepeater;
		circuit2 = Item.comparator;
		steel = Item.ingotIron;
		steelPlate = Item.ingotGold;
		motor = Block.pistonBase;
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
	}

	public static class RecipeGrid
	{
		Object A, B, C, D, E, F, G, H, I;
		Object out;
		int width = 3;
		int hight = 3;

		public RecipeGrid(int width, int hight)
		{
			this.width = Math.max(Math.min(width, 3), 1);
			this.hight = Math.max(Math.min(hight, 3), 1);;
		}

		/** 3x3 Crafting grid. Each Triple is a row. Input for the triples should be any of { Item,
		 * Block, ItemStack, String}
		 *
		 * @param one - top row
		 * @param two - middle row
		 * @param three - bottom row */
		public RecipeGrid(Triple one, Triple two, Triple three)
		{
			this.setRowOne(one.getA(), one.getB(), one.getC());
			this.setRowTwo(two.getA(), two.getB(), two.getC());
			this.setRowThree(three.getA(), three.getB(), three.getC());
		}

		/** 2x2 Crafting grid. Each Pair is a row. Input for the pairs should be any of { Item,
		 * Block, ItemStack, String}
		 *
		 * @param one - top row
		 * @param two - middle row */
		public RecipeGrid(Pair one, Pair two)
		{
			this.setRowOne(one.getKey(), one.getValue());
			this.setRowTwo(two.getKey(), two.getValue());
			this.hight = 2;
			this.width = 2;
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

		public void norm()
		{
			Object[] list = new Object[] { A, B, C, D, E, F, G, H, I };
			for (int i = 0; i < list.length; i++)
			{
				if (list[i] == null || (!(list[i] instanceof Item) && !(list[i] instanceof Block) && !(list[i] instanceof ItemStack) && !(list[i] instanceof String)))
				{
					list[i] = "";
				}
				else
				{
					if (list[i] instanceof ItemStack)
					{
						String name = OreDictionary.getOreName(OreDictionary.getOreID((ItemStack) list[i]));
						if (name != null)
						{
							list[i] = name;
						}
					}
				}
			}
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

			this.norm();
			if (width == 3 && hight == 3)
			{
				recipe = new Object[] { "ABC", "DEF", "GHI", 'A', A, 'B', B, 'C', C, 'D', D, 'E', E, 'F', F, 'G', G, 'H', H, 'I', I };
			}
			else if (width == 2 && hight == 3)
			{
				recipe = new Object[] { "AB", "DE", "GH", 'A', A, 'B', B, 'D', D, 'E', E, 'G', G, 'H', H };
			}
			else if (width == 3 && hight == 2)
			{
				recipe = new Object[] { "ABC", "DEF", 'A', A, 'B', B, 'C', C, 'D', D, 'E', E, 'F', F };
			}
			else if (width == 1 && hight == 3)
			{
				recipe = new Object[] { "A", "D", "G", 'A', A, 'D', D, 'G', G };
			}
			else if (width == 3 && hight == 1)
			{
				recipe = new Object[] { "ABC", 'A', A, 'B', B, 'C', C };
			}
			else if (width == 2 && hight == 2)
			{
				recipe = new Object[] { "AB", "DE", 'A', A, 'B', B, 'D', D, 'E', E };
			}
			else if (width == 1 && hight == 2)
			{
				recipe = new Object[] { "A", "D", 'A', A, 'D', D, };
			}
			else if (width == 2 && hight == 1)
			{
				recipe = new Object[] { "AB", 'A', A, 'B', B, };
			}
			else if (width == 1 && hight == 1)
			{
				recipe = new Object[] { "A", 'A', A };
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
