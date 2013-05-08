package dark.library;

import java.awt.Color;
import java.io.File;
import java.util.logging.Logger;

import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import universalelectricity.core.vector.Vector3;
import basiccomponents.common.BasicComponents;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Loader;
import dark.library.effects.FXBeam;

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

	public static final Logger LOGGER = Logger.getLogger("Dark-Lib");

	

	/**
	 * Renders a laser beam from one power to another by a set color for a set time
	 * 
	 * @param world - world this laser is to be rendered in
	 * @param position - start vector3
	 * @param target - end vector3
	 * @param color - color of the beam
	 * @param age - life of the beam in 1/20 secs
	 */
	public static void renderBeam(World world, Vector3 position, Vector3 target, Color color, int age)
	{
		if (world.isRemote)
		{
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXBeam(world, position, target, color, DarkMain.TEXTURE_DIRECTORY + "", age));
		}
	}

	/**
	 * Renders a bullet tracer from one spot to another will later be replaced with start and degree
	 */
	public static void renderTracer(World world, Vector3 position, Vector3 target)
	{
		if (world.isRemote)
		{
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXBeam(world, position, target, Color.DARK_GRAY, DarkMain.TEXTURE_DIRECTORY + "traceStream.png", 5, true));
		}
	}

	/**
	 * Loads most of the items from basic components to be used
	 */
	public static void forceLoadBCItems(Object mod, String channel)
	{
		BasicComponents.register(mod, channel);

		if (!loadedItems)
		{
			// UniversalElectricity.CONFIGURATION.load();
			BasicComponents.requestItem("ingotCopper", 0);
			BasicComponents.requestItem("ingotTin", 0);

			BasicComponents.requestBlock("oreCopper", 0);
			BasicComponents.requestBlock("oreTin", 0);

			BasicComponents.requestItem("ingotSteel", 0);
			BasicComponents.requestItem("dustSteel", 0);
			BasicComponents.requestItem("plateSteel", 0);

			BasicComponents.requestItem("ingotBronze", 0);
			BasicComponents.requestItem("dustBronze", 0);
			BasicComponents.requestItem("plateBronze", 0);

			BasicComponents.requestBlock("copperWire", 0);

			BasicComponents.requestItem("circuitBasic", 0);
			BasicComponents.requestItem("circuitAdvanced", 0);
			BasicComponents.requestItem("circuitElite", 0);

			BasicComponents.requestItem("motor", 0);
			BasicComponents.registerInfiniteBattery(0);
			BasicComponents.registerBattery(0);

			loadedItems = true;
		}
	}
}
