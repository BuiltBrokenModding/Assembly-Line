package dark.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import dark.common.BlockRegistry.BlockData;
import dark.common.blocks.BlockOre;
import dark.common.debug.BlockDebug;
import dark.common.items.EnumMeterials;
import dark.common.items.ItemBattery;
import dark.common.items.ItemBlockOre;
import dark.common.items.ItemOreDirv;
import dark.common.items.ItemParts;
import dark.common.items.ItemParts.Parts;
import dark.common.items.ItemTools;
import dark.common.items.ItemWrench;
import dark.common.transmit.BlockWire;
import dark.prefab.BlockMulti;
import dark.prefab.ModPrefab;
import dark.prefab.TileEntityMulti;
import dark.prefab.helpers.FluidHelper;

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
    public static final String MOD_NAME = "Darks CoreMachine";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVIS_VERSION + "." + BUILD_VERSION;

    @SidedProxy(clientSide = "dark.client.ClientProxy", serverSide = "dark.common.CommonProxy")
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
        MinecraftForge.EVENT_BUS.register(new FluidHelper());

        proxy.preInit();
    }

    @EventHandler
    @Override
    public void init(FMLInitializationEvent event)
    {
        BlockRegistry.registerAllBlocks();
        super.init(event);

        if (CoreRecipeLoader.blockOre != null)
        {
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
    public List<BlockData> getBlocks()
    {
        List<BlockData> dataList = new ArrayList<BlockData>();
        if (recipeLoader == null)
        {
            recipeLoader = new CoreRecipeLoader();
        }
        /* CONFIGS */
        CONFIGURATION.load();
        /* BLOCKS */
        blockMulti = new BlockMulti(DarkMain.CONFIGURATION.getBlock("MultiBlock", getNextID()).getInt());
        CoreRecipeLoader.blockOre = new BlockOre(getNextID(), CONFIGURATION);
        CoreRecipeLoader.blockWire = new BlockWire(CONFIGURATION, getNextID());
        CoreRecipeLoader.blockDebug = new BlockDebug(getNextID(), CONFIGURATION);

        dataList.add(new BlockData(CoreRecipeLoader.blockOre, ItemBlockOre.class, "DMOre"));
        dataList.add(new BlockData(CoreRecipeLoader.blockWire, "DMWire"));
        dataList.add(new BlockData(CoreRecipeLoader.blockDebug, "DMDebug"));
        dataList.add(new BlockData(blockMulti, "DMDMultiBlock").addTileEntity("DMMultiBlock", TileEntityMulti.class).canDisable(false));
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
        return dataList;
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
        //SaveManager.save(!event.world.isRemote);
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        //SaveManager.save(true);
    }

    @Override
    public String getDomain()
    {
        return "dark";
    }

}
