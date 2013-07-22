package dark.core;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import universalelectricity.prefab.ore.OreGenerator;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.core.blocks.BlockMulti;
import dark.core.blocks.BlockOre;
import dark.core.blocks.TileEntityMulti;
import dark.core.helpers.FluidRestrictionHandler;
import dark.core.items.EnumMeterials;
import dark.core.items.ItemOre;
import dark.core.items.ItemOreDirv;

/** @author HangCow, DarkGuardsman */
@Mod(modid = DarkMain.MOD_ID, name = DarkMain.MOD_NAME, version = DarkMain.VERSION, dependencies = "after:IC2,after:BuildCraft|Energy", useMetadata = true)
@NetworkMod(channels = { DarkMain.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class DarkMain extends ModPrefab
{
	// @Mod Prerequisites
	public static final String MAJOR_VERSION = "@MAJOR@";
	public static final String MINOR_VERSION = "@MINOR@";
	public static final String REVIS_VERSION = "@REVIS@";
	public static final String BUILD_VERSION = "@BUILD@";

	// @Mod
	public static final String MOD_ID = "DarkCore";
	public static final String MOD_NAME = "Dark Heart";
	public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVIS_VERSION + "." + BUILD_VERSION;

	@SidedProxy(clientSide = "dark.core.ClientProxy", serverSide = "dark.core.CommonProxy")
	public static CommonProxy proxy;

	public static final String CHANNEL = "DarkPackets";

	@Metadata(DarkMain.MOD_ID)
	public static ModMetadata meta;

	/** Main config file */
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/General.cfg"));
	private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };
	/** Can over pressure of devices do area damage */
	public static boolean overPressureDamage;
	/** Main mod output to console */
	public static final Logger LOGGER = Logger.getLogger("DarkCore");

	public static BlockMulti blockMulti;

	public static DarkMain instance;
	public static CoreRecipeLoader recipeLoader;

	public DarkMain()
	{
		super("dark");
	}

	@EventHandler
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		LOGGER.setParent(FMLLog.getLogger());
		LOGGER.info("Initializing...");
		recipeLoader = new CoreRecipeLoader();
		instance = this;

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new FluidRestrictionHandler());

		proxy.preInit();

	}

	@EventHandler
	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);

		GameRegistry.registerBlock(recipeLoader.blockOre, ItemOre.class, "DMOre");
		GameRegistry.registerBlock(blockMulti, "multiBlock");

		GameRegistry.registerTileEntity(TileEntityMulti.class, "ALMulti");

		BlockOre.regiserOreNames();

		for (int i = 0; i < EnumMeterials.values().length; i++)
		{
			if (EnumMeterials.values()[i].doWorldGen)
			{
				OreGenReplaceStone gen = EnumMeterials.values()[i].getGeneratorSettings();
				if (gen != null && gen.shouldGenerate)
				{
					OreGenerator.addOre(gen);
				}
			}
		}
		proxy.init();
	}

	@EventHandler
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);

		recipeLoader.loadRecipes();

		proxy.postInit();

	}

	@Override
	public void loadConfig()
	{
		/* CONFIGS */
		CONFIGURATION.load();
		/* BLOCKS */
		DarkMain.blockMulti = new BlockMulti(DarkMain.CONFIGURATION.getBlock("RestrictedPipes", BLOCK_ID_PREFIX++).getInt());
		if (CONFIGURATION.get("general", "LoadOre", true).getBoolean(true))
		{
			recipeLoader.blockOre = new BlockOre(BLOCK_ID_PREFIX++, CONFIGURATION);
		}
		/* ITEMS */
		if (CONFIGURATION.get("general", "LoadOreItems", true).getBoolean(true))
		{
			recipeLoader.itemMetals = new ItemOreDirv(ITEM_ID_PREFIX++, CONFIGURATION);
		}
		if (CONFIGURATION.hasChanged())
		{
			CONFIGURATION.save();
		}
		/* CONFIG END */

	}

	@Override
	public void loadModMeta()
	{
		/* MCMOD.INFO FILE BUILDER? */
		meta.modId = MOD_ID;
		meta.name = MOD_NAME;
		meta.description = "Main mod for several of the mods created by DarkGuardsman and his team. Adds basic features, functions, ores, items, and blocks";
		meta.url = "www.BuiltBroken.com";

		meta.logoFile = TEXTURE_DIRECTORY + "GP_Banner.png";
		meta.version = VERSION;
		meta.authorList = Arrays.asList(new String[] { "DarkGuardsman", "HangCow" });
		meta.credits = "Please see the website.";
		meta.autogenerated = false;
	}

	@ForgeSubscribe
	public void onWorldSave(WorldEvent.Save event)
	{
		SaveManager.save(!event.world.isRemote);
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event)
	{
		SaveManager.save(true);
	}

}
