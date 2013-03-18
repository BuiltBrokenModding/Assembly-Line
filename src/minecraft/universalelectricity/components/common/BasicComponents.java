package universalelectricity.components.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.components.common.item.ItemIngot;
import universalelectricity.components.common.item.ItemPlate;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.item.ItemElectric;
import universalelectricity.prefab.ore.OreGenBase;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * The main class for managing Basic Component items and blocks. Reference objects from this class
 * to add them to your recipes and such.
 * 
 * @author Calclavia
 */

public class BasicComponents
{
	public static final String NAME = "Basic Components";
	public static final String CHANNEL = "BasicComponents";

	public static final String RESOURCE_PATH = "/mods/basiccomponents/";

	public static final CreativeTabs TAB = new TabBC(CreativeTabs.getNextID(), CHANNEL);

	public static final String TEXTURE_DIRECTORY = RESOURCE_PATH + "textures/";
	public static final String GUI_DIRECTORY = TEXTURE_DIRECTORY + "gui/";
	public static final String BLOCK_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
	public static final String ITEM_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "items/";
	public static final String MODEL_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "models/";

	public static final String TEXTURE_NAME_PREFIX = "basiccomponents:";

	public static final String LANGUAGE_PATH = RESOURCE_PATH + "languages/";

	public static int BLOCK_ID_PREFIX = 3970;

	public static Block blockBasicOre;
	public static Block blockCopperWire;
	public static Block blockMachine;

	public static final int ITEM_ID_PREFIX = 13970;
	public static ItemElectric itemBattery;
	public static Item itemInfiniteBattery;
	public static Item itemWrench;
	public static Item itemCircuit;
	public static Item itemBronzeDust;
	public static Item itemMotor;
	public static Item itemPlate;
	public static Item itemIngot;
	public static Item itemSteelDust;

	public static OreGenBase copperOreGeneration;
	public static OreGenBase tinOreGeneration;

	/**
	 * 
	 * @param itemName: Steel, Bronze Copper, Tin
	 * @return
	 */
	public static void requestPlates(int id)
	{
		if (BasicComponents.itemPlate == null)
		{
			UniversalElectricity.CONFIGURATION.load();
			itemPlate = new ItemPlate(UniversalElectricity.CONFIGURATION.getItem("Plates", BasicComponents.ITEM_ID_PREFIX + 13).getInt());
			OreDictionary.registerOre("ingotIron", Item.ingotIron);
			OreDictionary.registerOre("ingotGold", Item.ingotGold);

			for (int i = 0; i < ItemPlate.TYPES.length; i++)
			{
				String plateName = ItemPlate.TYPES[i];
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPlate, 1, i), new Object[] { "!!", "!!", '!', plateName.replaceAll("plate", "ingot") }));

				if (plateName.equals("ingotIron"))
				{
					GameRegistry.addRecipe(new ShapelessOreRecipe(Item.ingotIron, new Object[] { new ItemStack(itemPlate, 1, i) }));
				}
				else if (plateName.equals("ingotGold"))
				{
					GameRegistry.addRecipe(new ShapelessOreRecipe(Item.ingotGold, new Object[] { new ItemStack(itemPlate, 1, i) }));
				}

				OreDictionary.registerOre(plateName, new ItemStack(itemPlate, 1, i));
			}

			UniversalElectricity.CONFIGURATION.save();
		}
	}

	public static void requestIngots(int id)
	{
		if (BasicComponents.itemIngot == null)
		{
			UniversalElectricity.CONFIGURATION.load();
			BasicComponents.itemIngot = new ItemIngot(UniversalElectricity.CONFIGURATION.getItem("Ingots", BasicComponents.ITEM_ID_PREFIX + 4).getInt());

			for (int i = 0; i < ItemIngot.TYPES.length; i++)
			{
				String itemName = ItemIngot.TYPES[i];
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemIngot, 1, i), new Object[] { itemName.replaceAll("ingot", "plate") }));
				OreDictionary.registerOre(itemName, new ItemStack(itemIngot, 1, i));
			}

			UniversalElectricity.CONFIGURATION.save();

		}
	}
}
