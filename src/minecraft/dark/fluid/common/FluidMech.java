package dark.fluid.common;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import org.modstats.ModstatInfo;

import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.PacketManager;
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
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.core.DarkMain;
import dark.core.ModPrefab;
import dark.core.items.ItemBlockHolder;
import dark.fluid.common.item.ItemParts;
import dark.fluid.common.item.ItemParts.Parts;
import dark.fluid.common.item.ItemTools;
import dark.fluid.common.machines.BlockFluid;
import dark.fluid.common.machines.BlockReleaseValve;
import dark.fluid.common.machines.BlockSink;
import dark.fluid.common.machines.BlockTank;
import dark.fluid.common.machines.TileEntityReleaseValve;
import dark.fluid.common.machines.TileEntitySink;
import dark.fluid.common.machines.TileEntityTank;
import dark.fluid.common.pipes.BlockPipe;
import dark.fluid.common.pipes.ItemBlockPipe;
import dark.fluid.common.pipes.TileEntityGenericPipe;
import dark.fluid.common.pipes.TileEntityPipe;
import dark.fluid.common.pipes.addon.TileEntityPipeWindow;
import dark.fluid.common.pump.BlockConstructionPump;
import dark.fluid.common.pump.BlockDrain;
import dark.fluid.common.pump.BlockPumpMachine;
import dark.fluid.common.pump.TileEntityConstructionPump;
import dark.fluid.common.pump.TileEntityDrain;
import dark.fluid.common.pump.TileEntityStarterPump;
import dark.mech.common.machines.BlockGenerator;
import dark.mech.common.machines.BlockRod;

@ModstatInfo(prefix = "fluidmech")
@Mod(modid = FluidMech.MOD_ID, name = FluidMech.MOD_NAME, version = DarkMain.VERSION, dependencies = "after:DarkCore", useMetadata = true)
@NetworkMod(channels = { FluidMech.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class FluidMech extends ModPrefab
{

    // @Mod
    public static final String MOD_ID = "FluidMech";
    public static final String MOD_NAME = "Fluid_Mechanics";

    // @NetworkMod
    public static final String CHANNEL = "FluidMech";

    @Metadata(FluidMech.MOD_ID)
    public static ModMetadata meta;

    /* SUPPORTED LANGS */
    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US", "de_DE" };

    /* CONFIG FILE */
    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir() + "/Dark/", FluidMech.MOD_NAME + ".cfg"));

    /* BLOCKS */

    @SidedProxy(clientSide = "dark.fluid.client.ClientProxy", serverSide = "dark.fluid.common.CommonProxy")
    public static CommonProxy proxy;

    @Instance(FluidMech.MOD_NAME)
    public static FluidMech instance;

    public static FMRecipeLoader recipeLoader;

    /* LOGGER - EXTENDS FORGE'S LOG SYSTEM */
    public static Logger FMLog = Logger.getLogger(FluidMech.MOD_NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        super.preInit(event);

        /* BLOCK REGISTER CALLS */
        GameRegistry.registerBlock(FMRecipeLoader.blockPipe, ItemBlockPipe.class, "lmPipe");
        GameRegistry.registerBlock(FMRecipeLoader.blockGenPipe, ItemBlockPipe.class, "lmGenPipe");
        GameRegistry.registerBlock(FMRecipeLoader.blockReleaseValve, ItemBlockHolder.class, "eValve");
        GameRegistry.registerBlock(FMRecipeLoader.blockRod, "mechRod");
        GameRegistry.registerBlock(FMRecipeLoader.blockGenerator, "lmGen");
        GameRegistry.registerBlock(FMRecipeLoader.blockMachine, ItemBlockHolder.class, "lmMachines");
        GameRegistry.registerBlock(FMRecipeLoader.blockTank, ItemBlockHolder.class, "lmTank");
        GameRegistry.registerBlock(FMRecipeLoader.blockSink, "lmSink");
        GameRegistry.registerBlock(FMRecipeLoader.blockDrain, "lmDrain");
        GameRegistry.registerBlock(FMRecipeLoader.blockConPump, "lmConPump");

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        /* LOGGER */
        FMLog.info("Loading...");
        proxy.Init();

        /* TILE ENTITY REGISTER CALLS */
        GameRegistry.registerTileEntity(TileEntityPipe.class, "FluidPipe");
        GameRegistry.registerTileEntity(TileEntityGenericPipe.class, "ColoredPipe");
        GameRegistry.registerTileEntity(TileEntityStarterPump.class, "starterPump");
        GameRegistry.registerTileEntity(TileEntityReleaseValve.class, "ReleaseValve");
        GameRegistry.registerTileEntity(TileEntityTank.class, "FluidTank");
        GameRegistry.registerTileEntity(TileEntitySink.class, "FluidSink");
        GameRegistry.registerTileEntity(TileEntityDrain.class, "FluidDrain");
        GameRegistry.registerTileEntity(TileEntityConstructionPump.class, "ConstructionPump");

        /* LANG LOADING */
        FMLog.info(" Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " Languages.");
        /* ORE DIRECTORY REGISTER */
        OreDictionary.registerOre("bronzeTube", new ItemStack(FMRecipeLoader.itemParts, 1, Parts.Bronze.ordinal()));
        OreDictionary.registerOre("ironTube", new ItemStack(FMRecipeLoader.itemParts, 1, Parts.Iron.ordinal()));
        OreDictionary.registerOre("netherTube", new ItemStack(FMRecipeLoader.itemParts, 1, Parts.Nether.ordinal()));
        OreDictionary.registerOre("obbyTube", new ItemStack(FMRecipeLoader.itemParts, 1, Parts.Obby.ordinal()));
        OreDictionary.registerOre("leatherSeal", new ItemStack(FMRecipeLoader.itemParts, 1, Parts.Seal.ordinal()));
        OreDictionary.registerOre("leatherSlimeSeal", new ItemStack(FMRecipeLoader.itemParts, 1, Parts.SlimeSeal.ordinal()));
        OreDictionary.registerOre("valvePart", new ItemStack(FMRecipeLoader.itemParts, 1, Parts.Valve.ordinal()));
        OreDictionary.registerOre("bronzeTube", new ItemStack(FMRecipeLoader.itemParts, 1, Parts.Bronze.ordinal()));
        OreDictionary.registerOre("unfinishedTank", new ItemStack(FMRecipeLoader.itemParts, 1, Parts.Tank.ordinal()));

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        /* LOGGER */
        FMLog.info("Finalizing...");
        proxy.postInit();

        /* /******** RECIPES ************* */
        recipeLoader.loadRecipes();

        FMLog.info("Done Loading");
    }

    @Override
    public void loadConfig()
    {
        if (recipeLoader == null)
        {
            recipeLoader = new FMRecipeLoader();
        }
        /* CONFIGS */
        CONFIGURATION.load();
        if (FluidMech.CONFIGURATION.get("general", "EnableWasteFluid", true).getBoolean(true))
        {
            Fluid waste = new Fluid("waste");
            FMRecipeLoader.blockWasteLiquid = new BlockFluid(waste, getNextID());
            FluidRegistry.registerFluid(waste);
        }
        if (FluidMech.CONFIGURATION.get("general", "EnableOilFluid", true).getBoolean(true))
        {
            Fluid oil = new Fluid("oil");
            FMRecipeLoader.blockWasteLiquid = new BlockFluid(oil, getNextID());
            FluidRegistry.registerFluid(oil);
        }
        /* BLOCK DECLARATION -- CONFIG LOADER */
        FMRecipeLoader.blockGenPipe = new BlockPipe(getNextID(), "GenericPipe");
        FMRecipeLoader.blockMachine = new BlockPumpMachine(getNextID());
        FMRecipeLoader.blockRod = new BlockRod(getNextID());
        FMRecipeLoader.blockGenerator = new BlockGenerator(getNextID());
        FMRecipeLoader.blockReleaseValve = new BlockReleaseValve(getNextID());
        FMRecipeLoader.blockTank = new BlockTank(getNextID());
        FMRecipeLoader.blockSink = new BlockSink(getNextID());
        FMRecipeLoader.blockDrain = new BlockDrain(getNextID());
        FMRecipeLoader.blockConPump = new BlockConstructionPump(getNextID());
        FMRecipeLoader.blockPipe = new BlockPipe(getNextID(), "RestrictedPipe");

        /* ITEM DECLARATION */
        FMRecipeLoader.itemParts = new ItemParts(FluidMech.CONFIGURATION.getItem("Parts", ITEM_ID_PREFIX++).getInt());
        FMRecipeLoader.itemGauge = new ItemTools(FluidMech.CONFIGURATION.getItem("PipeGuage", ITEM_ID_PREFIX++).getInt());
        CONFIGURATION.save();

    }

    @Override
    public void loadModMeta()
    {
        /* MCMOD.INFO FILE BUILDER? */
        meta.modId = FluidMech.MOD_ID;
        meta.name = FluidMech.MOD_NAME;
        meta.description = "Fluid Mechanics is a combination between supporting fluid handling and mechanical energy handling system. " + "Its designed to help other mods move there liquids using a universal liquid system managed by forge. As a bonus it also " + "comes with suppot to help mods move energy by means of mechanics motion along rods. This mod by itself doesn't offer much more " + "than basic liquid storage, placement, and removel in the world. Its suggest to download other mods that supports the Forge's " + "Fluid System. " + "\n\n" + "Suported Power systems: Universal Electric, BuildCraft, IndustrialCraft ";

        meta.url = "http://www.universalelectricity.com/fluidmechanics";

        meta.logoFile = FluidMech.TEXTURE_DIRECTORY + "FM_Banner.png";
        meta.version = DarkMain.VERSION;
        meta.authorList = Arrays.asList(new String[] { "DarkGuardsman AKA DarkCow" });
        meta.credits = "Please see the website.";
        meta.autogenerated = false;

    }

    public static final CreativeTabs TabFluidMech = new CreativeTabs("Fluid Mechanics")
    {
        public ItemStack getIconItemStack()
        {
            return new ItemStack(FMRecipeLoader.blockPipe, 1, 4);
        }
    };

    @Override
    public String getDomain()
    {
        return "fm";
    }
}
