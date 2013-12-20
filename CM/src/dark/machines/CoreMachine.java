package dark.machines;

import java.io.File;
import java.util.Arrays;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.FluidRegistry;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import universalelectricity.prefab.ore.OreGenerator;

import com.dark.CoreRegistry;
import com.dark.DarkCore;
import com.dark.fluid.EnumGas;
import com.dark.network.PacketDataWatcher;
import com.dark.network.PacketHandler;
import com.dark.prefab.ItemBlockHolder;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import dark.machines.blocks.BlockGasOre;
import dark.machines.blocks.BlockOre;
import dark.machines.blocks.BlockOre.OreData;
import dark.machines.blocks.GasOreGenerator;
import dark.machines.deco.BlockBasalt;
import dark.machines.deco.BlockColorGlass;
import dark.machines.deco.BlockColorGlowGlass;
import dark.machines.deco.BlockColorSand;
import dark.machines.deco.ItemBlockColored;
import dark.machines.generators.BlockSmallSteamGen;
import dark.machines.generators.BlockSolarPanel;
import dark.machines.items.ItemBattery;
import dark.machines.items.ItemBlockOre;
import dark.machines.items.ItemColoredDust;
import dark.machines.items.ItemCommonTool;
import dark.machines.items.ItemReadoutTools;
import dark.machines.items.ItemWrench;
import dark.machines.machines.BlockDebug;
import dark.machines.machines.BlockEnergyStorage;
import dark.machines.machines.ItemBlockEnergyStorage;
import dark.machines.prefab.ModPrefab;
import dark.machines.prefab.entities.EntityTestCar;
import dark.machines.prefab.entities.ItemVehicleSpawn;
import dark.machines.transmit.BlockWire;
import dark.machines.transmit.ItemBlockWire;

/** @author HangCow, DarkGuardsman */
@Mod(modid = CoreMachine.MOD_ID, name = CoreMachine.MOD_NAME, version = CoreMachine.VERSION, dependencies = "after:BuildCraft|Energy", useMetadata = true)
@NetworkMod(channels = { CoreMachine.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class CoreMachine extends ModPrefab
{
    // @Mod Prerequisites
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVIS_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVIS_VERSION + "." + BUILD_VERSION;

    // @Mod
    public static final String MOD_ID = "DarkCore";
    public static final String MOD_NAME = "CoreMachine";

    @SidedProxy(clientSide = "dark.machines.client.ClientProxy", serverSide = "dark.machines.CommonProxy")
    public static CommonProxy proxy;

    public static final String CHANNEL = DarkCore.CHANNEL;

    @Metadata(CoreMachine.MOD_ID)
    public static ModMetadata meta;

    /** Main config file */
    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/TheDarkMachine.cfg"));
    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };
    /** Can over pressure of devices do area damage */
    public static boolean overPressureDamage, zeroRendering, zeroAnimation, zeroGraphics;

    @Instance(MOD_ID)
    private static CoreMachine instance;

    public static CoreRecipeLoader recipeLoader;

    public static CoreMachine getInstance()
    {
        if (instance == null)
        {
            instance = new CoreMachine();
        }
        return instance;
    }

    @EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        super.preInit(event);
        NetworkRegistry.instance().registerGuiHandler(this, proxy);
        MinecraftForge.EVENT_BUS.register(PacketDataWatcher.instance);
        proxy.preInit();
    }

    @EventHandler
    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        EntityRegistry.registerGlobalEntityID(EntityTestCar.class, "TestCar", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityTestCar.class, "TestCar", 60, this, 64, 1, true);

        for (EnumGas gas : EnumGas.values())
        {
            FluidRegistry.registerFluid(gas.getGas());
        }
        if (CoreRecipeLoader.blockGas != null)
        {
            EnumGas.NATURAL_GAS.getGas().setBlockID(CoreRecipeLoader.blockGas);
        }
        if (CoreRecipeLoader.blockGas != null)
        {
            GameRegistry.registerWorldGenerator(new GasOreGenerator());
        }
        if (CoreRecipeLoader.blockOre != null)
        {
            for (OreData data : OreData.values())
            {
                if (data.doWorldGen)
                {
                    OreGenReplaceStone gen = data.getGeneratorSettings();
                    if (gen != null)
                    {
                        OreGenerator.addOre(gen);
                    }
                }
            }
        }

        FMLLog.info(" Loaded: " + TranslationHelper.loadLanguages(DarkCore.LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " Languages.");
        proxy.init();
    }

    @EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        proxy.postInit();
    }

    @Override
    public void registerObjects()
    {
        if (recipeLoader == null)
        {
            recipeLoader = new CoreRecipeLoader();
        }
        /* CONFIGS */
        CONFIGURATION.load();

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            CoreMachine.zeroAnimation = CONFIGURATION.get("Graphics", "DisableAllAnimation", false, "Disables active animations by any non-active models").getBoolean(false);
            CoreMachine.zeroRendering = CONFIGURATION.get("Graphics", "DisableAllRendering", false, "Replaces all model renderers with single block forms").getBoolean(false);
            CoreMachine.zeroGraphics = CONFIGURATION.get("Graphics", "DisableAllGraphics", false, "Disables extra effects that models and renders have. Such as particles, and text").getBoolean(false);
        }
        /* BLOCKS */
        CoreRecipeLoader.blockSteamGen = CoreRegistry.createNewBlock("DMBlockSteamMachine", CoreMachine.MOD_ID, BlockSmallSteamGen.class, ItemBlockHolder.class);
        CoreRecipeLoader.blockOre = CoreRegistry.createNewBlock("DMBlockOre", CoreMachine.MOD_ID, BlockOre.class, ItemBlockOre.class);
        CoreRecipeLoader.blockWire = CoreRegistry.createNewBlock("DMBlockWire", CoreMachine.MOD_ID, BlockWire.class, ItemBlockWire.class);
        CoreRecipeLoader.blockDebug = CoreRegistry.createNewBlock("DMBlockDebug", CoreMachine.MOD_ID, BlockDebug.class, ItemBlockHolder.class);
        CoreRecipeLoader.blockStainGlass = CoreRegistry.createNewBlock("DMBlockStainedGlass", CoreMachine.MOD_ID, BlockColorGlass.class, ItemBlockColored.class);
        CoreRecipeLoader.blockColorSand = CoreRegistry.createNewBlock("DMBlockColorSand", CoreMachine.MOD_ID, BlockColorSand.class, ItemBlockColored.class);
        CoreRecipeLoader.blockBasalt = CoreRegistry.createNewBlock("DMBlockBasalt", CoreMachine.MOD_ID, BlockBasalt.class, ItemBlockColored.class);
        CoreRecipeLoader.blockGlowGlass = CoreRegistry.createNewBlock("DMBlockGlowGlass", CoreMachine.MOD_ID, BlockColorGlowGlass.class, ItemBlockColored.class);
        CoreRecipeLoader.blockSolar = CoreRegistry.createNewBlock("DMBlockSolar", CoreMachine.MOD_ID, BlockSolarPanel.class, ItemBlockHolder.class);
        CoreRecipeLoader.blockGas = CoreRegistry.createNewBlock("DMBlockGas", CoreMachine.MOD_ID, BlockGasOre.class, ItemBlockHolder.class);
        CoreRecipeLoader.blockBatBox = CoreRegistry.createNewBlock("DMBlockBatBox", CoreMachine.MOD_ID, BlockEnergyStorage.class, ItemBlockEnergyStorage.class);

        /* ITEMS */
        CoreRecipeLoader.itemTool = CoreRegistry.createNewItem("DMReadoutTools", CoreMachine.MOD_ID, ItemReadoutTools.class, true);
        CoreRecipeLoader.battery = CoreRegistry.createNewItem("DMItemBattery", CoreMachine.MOD_ID, ItemBattery.class, true);
        CoreRecipeLoader.wrench = CoreRegistry.createNewItem("DMWrench", CoreMachine.MOD_ID, ItemWrench.class, true);
        CoreRecipeLoader.itemGlowingSand = CoreRegistry.createNewItem("DMItemGlowingSand", CoreMachine.MOD_ID, ItemColoredDust.class, true);
        CoreRecipeLoader.itemDiggingTool = CoreRegistry.createNewItem("ItemDiggingTools", CoreMachine.MOD_ID, ItemCommonTool.class, true);
        CoreRecipeLoader.itemVehicleTest = CoreRegistry.createNewItem("ItemVehicleTest", CoreMachine.MOD_ID, ItemVehicleSpawn.class, true);
        CONFIGURATION.save();

    }

    @Override
    public void loadModMeta()
    {
        /* MCMOD.INFO FILE BUILDER? */
        meta.modId = MOD_ID;
        meta.name = MOD_NAME;
        meta.description = "Main mod for several of the mods created by DarkGuardsman and his team. Adds basic features, functions, ores, items, and blocks";
        meta.url = "http://www.universalelectricity.com/coremachine";

        meta.logoFile = DarkCore.TEXTURE_DIRECTORY + "GP_Banner.png";
        meta.version = VERSION;
        meta.authorList = Arrays.asList(new String[] { "DarkGuardsman", "HangCow", "Elrath18", "Archadia" });
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

    @Override
    public void loadRecipes()
    {
        if (recipeLoader == null)
        {
            recipeLoader = new CoreRecipeLoader();
        }
        recipeLoader.loadRecipes();
    }

}
