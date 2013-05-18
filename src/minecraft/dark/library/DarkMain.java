package dark.library;

import java.io.File;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import basiccomponents.common.BasicComponents;
import cpw.mods.fml.common.Loader;

public class DarkMain
{
	private static boolean loadedItems = false;

	/* RESOURCE FILE PATHS */
	public static final String RESOURCE_PATH = "/mods/dark/";
	public static final String TEXTURE_DIRECTORY = RESOURCE_PATH + "textures/";
	public static final String GUI_DIRECTORY = TEXTURE_DIRECTORY + "gui/";
	public static final String BLOCK_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
	public static final String ITEM_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "items/";
	public static final String MODEL_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "models/";
	public static final String TEXTURE_NAME_PREFIX = "dark:";

	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/General.cfg"));

	public static final Logger LOGGER = Logger.getLogger("DarkLib");

	public void init()
	{

	}

	public static void registerMod(Object mod)
	{

	}

	/**
	 * Loads most of the items from basic components to be used
	 */
	public static void forceLoadBCItems(Object mod, String channel)
	{
		if (!loadedItems)
		{
			LOGGER.fine("Loaded Basic Components Items");

			// UniversalElectricity.CONFIGURATION.load();
			BasicComponents.requestItem("ingotCopper", 0);
			BasicComponents.requestItem("ingotTin", 0);

			BasicComponents.requestBlock("oreCopper", 0);
			BasicComponents.requestBlock("oreTin", 0);

			BasicComponents.requestBlock("copperWire", 0);

			BasicComponents.requestItem("ingotSteel", 0);
			BasicComponents.requestItem("dustSteel", 0);
			BasicComponents.requestItem("plateSteel", 0);

			BasicComponents.requestItem("ingotBronze", 0);
			BasicComponents.requestItem("dustBronze", 0);
			BasicComponents.requestItem("plateBronze", 0);

			BasicComponents.requestItem("circuitBasic", 0);
			BasicComponents.requestItem("circuitAdvanced", 0);
			BasicComponents.requestItem("circuitElite", 0);

			BasicComponents.requestItem("motor", 0);
			BasicComponents.requestItem("battery", 0);
			BasicComponents.requestItem("infiniteBattery", 0);

			loadedItems = true;
		}
		BasicComponents.register(mod, channel);
	}
}
