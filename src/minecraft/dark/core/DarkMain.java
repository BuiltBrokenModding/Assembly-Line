package dark.core;

import java.io.File;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
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
}
