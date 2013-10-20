package dark.fluid.common;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.fluids.Fluid;

import org.modstats.ModstatInfo;

import universalelectricity.prefab.TranslationHelper;
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
import dark.core.common.DMCreativeTab;
import dark.core.common.DarkMain;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.items.ItemBlockHolder;
import dark.core.registration.ModObjectRegistry;
import dark.fluid.common.machines.BlockReleaseValve;
import dark.fluid.common.machines.BlockSink;
import dark.fluid.common.machines.BlockTank;
import dark.fluid.common.pipes.BlockPipe;
import dark.fluid.common.pipes.BlockPipe.PipeData;
import dark.fluid.common.pipes.ItemBlockPipe;
import dark.fluid.common.pump.BlockConstructionPump;
import dark.fluid.common.pump.BlockDrain;
import dark.fluid.common.pump.BlockPumpMachine;

@ModstatInfo(prefix = "fluidmech")
@Mod(modid = FluidMech.MOD_ID, name = FluidMech.MOD_NAME, version = FluidMech.VERSION, dependencies = "after:DarkCore", useMetadata = true)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class FluidMech extends ModPrefab
{

    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVIS_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVIS_VERSION + "." + BUILD_VERSION;
    // @Mod
    public static final String MOD_ID = "FluidMech";
    public static final String MOD_NAME = "Fluid_Mechanics";

    public static final String WASTE_FLUID_NAME = "mixedWaste";
    public static final String OIL_FLUID_NAME = "oil";
    public static final String FUEL_FLUID_NAME = "fuel";
    public static final String BIO_FUEL_Name = "";

    public static Fluid fmWaste, fmOil, fmFuel, fmBio;
    public static Fluid waste, oil, fuel, bio;

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

    @Override
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        super.preInit(event);

        /* BLOCK REGISTER CALLS */

        proxy.preInit();
    }

    @Override
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        proxy.Init();

        /* LANG LOADING */
        FMLog.info(" Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " Languages.");
        DMCreativeTab.tabHydrualic.setIconItemStack(new ItemStack(FMRecipeLoader.blockPipe, 1, PipeData.IRON_PIPE.ordinal()));
    }

    @Override
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        proxy.postInit();

        /* /******** RECIPES ************* */
        recipeLoader.loadRecipes();

        FMLog.info("Done Loading");
    }

    @Override
    public void registerObjects()
    {
        if (recipeLoader == null)
        {
            recipeLoader = new FMRecipeLoader();
        }
        CONFIGURATION.load();

        FMRecipeLoader.blockWasteLiquid = ModObjectRegistry.createNewFluidBlock(FluidMech.MOD_ID, FluidMech.CONFIGURATION, new Fluid(WASTE_FLUID_NAME).setUnlocalizedName("fluid.waste.name").setDensity(1300).setViscosity(1800));
        FMRecipeLoader.blockOilLiquid = ModObjectRegistry.createNewFluidBlock(FluidMech.MOD_ID, FluidMech.CONFIGURATION, new Fluid(OIL_FLUID_NAME).setUnlocalizedName("fluid.oil.name").setDensity(1500).setViscosity(4700));

        FMRecipeLoader.blockPipe = ModObjectRegistry.createNewBlock("FMBlockPipe", FluidMech.MOD_ID, BlockPipe.class, ItemBlockPipe.class);
        FMRecipeLoader.blockPumpMachine = ModObjectRegistry.createNewBlock("FMBlockPump", FluidMech.MOD_ID, BlockPumpMachine.class, ItemBlockHolder.class);
        FMRecipeLoader.blockReleaseValve = ModObjectRegistry.createNewBlock("FMBlockReleaseValve", FluidMech.MOD_ID, BlockReleaseValve.class, ItemBlockHolder.class);
        FMRecipeLoader.blockTank = ModObjectRegistry.createNewBlock("FMBlockTank", FluidMech.MOD_ID, BlockTank.class, ItemBlockPipe.class);
        FMRecipeLoader.blockSink = ModObjectRegistry.createNewBlock("FMBlockSink", FluidMech.MOD_ID, BlockSink.class, ItemBlockHolder.class);
        FMRecipeLoader.blockDrain = ModObjectRegistry.createNewBlock("FMBlockDrain", FluidMech.MOD_ID, BlockDrain.class, ItemBlockHolder.class);
        FMRecipeLoader.blockConPump = ModObjectRegistry.createNewBlock("FMBlockConstructionPump", FluidMech.MOD_ID, BlockConstructionPump.class, ItemBlockHolder.class);

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

        meta.logoFile = ModPrefab.TEXTURE_DIRECTORY + "FM_Banner.png";
        meta.version = DarkMain.VERSION;
        meta.authorList = Arrays.asList(new String[] { "DarkGuardsman AKA DarkCow" });
        meta.credits = "Please see the website.";
        meta.autogenerated = false;

    }

    @Override
    public String getDomain()
    {
        return "fm";
    }

    @Override
    public void loadRecipes()
    {
        // TODO Auto-generated method stub

    }
}
