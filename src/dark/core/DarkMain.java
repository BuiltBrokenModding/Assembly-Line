package dark.core;

import java.io.File;
import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import universalelectricity.prefab.ore.OreGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.common.debug.BlockDebug;
import dark.common.debug.BlockDebug.debugBlocks;
import dark.common.transmit.BlockWire;
import dark.common.transmit.TileEntityWire;
import dark.core.blocks.BlockMulti;
import dark.core.blocks.BlockOre;
import dark.core.blocks.TileEntityMulti;
import dark.core.helpers.FluidRestrictionHandler;
import dark.core.items.EnumMeterials;
import dark.core.items.ItemBattery;
import dark.core.items.ItemBlockHolder;
import dark.core.items.ItemOre;
import dark.core.items.ItemOreDirv;
import dark.core.items.ItemParts;
import dark.core.items.ItemParts.Parts;
import dark.core.items.ItemTools;
import dark.core.items.ItemWrench;

/** @author HangCow, DarkGuardsman */
@Mod(modid = DarkMain.MOD_ID, name = DarkMain.MOD_NAME, version = DarkMain.VERSION, dependencies = "after:BuildCraft|Energy", useMetadata = true)
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
    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/TheDarkMachine.cfg"));
    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };
    /** Can over pressure of devices do area damage */
    public static boolean overPressureDamage;

    public static BlockMulti blockMulti;

    @Instance(MOD_ID)
    private static DarkMain instance;

    public static CoreRecipeLoader recipeLoader;

    public static DarkMain getInstance()
    {
        if (instance == null)
        {
            instance = new DarkMain();
        }
        return instance;
    }

    @EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new FluidRestrictionHandler());

        proxy.preInit();
    }

    @EventHandler
    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);

        if (CoreRecipeLoader.blockOre != null)
        {
            GameRegistry.registerBlock(CoreRecipeLoader.blockOre, ItemOre.class, "DMOre");
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
        }
        if (CoreRecipeLoader.blockWire != null)
        {
            GameRegistry.registerBlock(CoreRecipeLoader.blockWire, ItemBlockHolder.class, "DMWire");
            GameRegistry.registerTileEntity(TileEntityWire.class, "DMWire");
        }
        if (CoreRecipeLoader.blockDebug != null)
        {
            GameRegistry.registerBlock(CoreRecipeLoader.blockDebug, ItemBlockHolder.class, "DMDebug");
            for (int i = 0; i < debugBlocks.values().length; i++)
            {
                GameRegistry.registerTileEntity(debugBlocks.values()[i].clazz, "DMDebug" + i);
            }
        }
        if (CoreRecipeLoader.itemParts != null)
        {
            /* ORE DIRECTORY REGISTER */
            OreDictionary.registerOre("bronzeTube", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Bronze.ordinal()));
            OreDictionary.registerOre("ironTube", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Iron.ordinal()));
            OreDictionary.registerOre("netherTube", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Nether.ordinal()));
            OreDictionary.registerOre("obbyTube", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Obby.ordinal()));
            OreDictionary.registerOre("leatherSeal", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Seal.ordinal()));
            OreDictionary.registerOre("leatherSlimeSeal", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.SlimeSeal.ordinal()));
            OreDictionary.registerOre("valvePart", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Valve.ordinal()));
            OreDictionary.registerOre("bronzeTube", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Bronze.ordinal()));
            OreDictionary.registerOre("unfinishedTank", new ItemStack(CoreRecipeLoader.itemParts, 1, Parts.Tank.ordinal()));

        }
        //TODO look at possibility of having this only be enabled if needed but still no option to disable manually
        GameRegistry.registerBlock(blockMulti, "multiBlock");
        GameRegistry.registerTileEntity(TileEntityMulti.class, "DMMultiBlock");

        proxy.init();
    }

    @EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        //TODO load langs
        recipeLoader.loadRecipes();

        proxy.postInit();

    }

    @Override
    public void loadConfig()
    {
        if (recipeLoader == null)
        {
            recipeLoader = new CoreRecipeLoader();
        }
        /* CONFIGS */
        CONFIGURATION.load();
        /* BLOCKS */
        blockMulti = new BlockMulti(DarkMain.CONFIGURATION.getBlock("MultiBlock", getNextID()).getInt());

        if (CONFIGURATION.get("general", "LoadOre", true, "Disabling this also disabled the items that go with the ores").getBoolean(true))
        {
            CoreRecipeLoader.blockOre = new BlockOre(getNextID(), CONFIGURATION);
        }
        if (CONFIGURATION.get("general", "EnableWires", true).getBoolean(true))
        {
            CoreRecipeLoader.blockWire = new BlockWire(CONFIGURATION, getNextID());
        }
        if (CONFIGURATION.get("general", "EnableDebugBlocks", true, "Enable this to use the creative mode only infinite power, load, fluid, and void blocks").getBoolean(true))
        {
            CoreRecipeLoader.blockDebug = new BlockDebug(getNextID(), CONFIGURATION);
        }
        /* ITEMS */
        if (CONFIGURATION.get("general", "LoadOreItems", true, "Only disable ore items if you have another mod that provides metal dust, ingots, and plates").getBoolean(true))
        {
            CoreRecipeLoader.itemMetals = new ItemOreDirv(ITEM_ID_PREFIX++, CONFIGURATION);
        }
        if (CONFIGURATION.get("general", "LoadCraftingParts", true, "Only disable this if you do not plan to craft, or are not using any mods that need these parts.").getBoolean(true))
        {
            CoreRecipeLoader.itemParts = new ItemParts(ITEM_ID_PREFIX++, CONFIGURATION);
        }
        if (CONFIGURATION.get("general", "EnableBattery", true).getBoolean(true))
        {
            CoreRecipeLoader.battery = new ItemBattery("Battery", ITEM_ID_PREFIX++);
        }
        if (CONFIGURATION.get("general", "EnableWrench", true).getBoolean(true))
        {
            CoreRecipeLoader.wrench = new ItemWrench(ITEM_ID_PREFIX++, DarkMain.CONFIGURATION);
        }
        CoreRecipeLoader.itemTool = new ItemTools(ITEM_ID_PREFIX++, DarkMain.CONFIGURATION);

        CONFIGURATION.save();
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

    @Override
    public String getDomain()
    {
        return "dark";
    }

}
