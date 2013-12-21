package com.builtbroken.assemblyline;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import universalelectricity.prefab.ore.OreGenerator;

import com.builtbroken.assemblyline.api.coding.TaskRegistry;
import com.builtbroken.assemblyline.armbot.BlockArmbot;
import com.builtbroken.assemblyline.armbot.command.TaskBreak;
import com.builtbroken.assemblyline.armbot.command.TaskDrop;
import com.builtbroken.assemblyline.armbot.command.TaskEnd;
import com.builtbroken.assemblyline.armbot.command.TaskFire;
import com.builtbroken.assemblyline.armbot.command.TaskGOTO;
import com.builtbroken.assemblyline.armbot.command.TaskGive;
import com.builtbroken.assemblyline.armbot.command.TaskGrabEntity;
import com.builtbroken.assemblyline.armbot.command.TaskGrabItem;
import com.builtbroken.assemblyline.armbot.command.TaskHarvest;
import com.builtbroken.assemblyline.armbot.command.TaskIF;
import com.builtbroken.assemblyline.armbot.command.TaskIdle;
import com.builtbroken.assemblyline.armbot.command.TaskPlace;
import com.builtbroken.assemblyline.armbot.command.TaskReturn;
import com.builtbroken.assemblyline.armbot.command.TaskRotateBy;
import com.builtbroken.assemblyline.armbot.command.TaskRotateTo;
import com.builtbroken.assemblyline.armbot.command.TaskStart;
import com.builtbroken.assemblyline.armbot.command.TaskTake;
import com.builtbroken.assemblyline.armbot.command.TaskUse;
import com.builtbroken.assemblyline.blocks.BlockBasalt;
import com.builtbroken.assemblyline.blocks.BlockColorGlass;
import com.builtbroken.assemblyline.blocks.BlockColorGlowGlass;
import com.builtbroken.assemblyline.blocks.BlockColorSand;
import com.builtbroken.assemblyline.blocks.BlockGasOre;
import com.builtbroken.assemblyline.blocks.BlockOre;
import com.builtbroken.assemblyline.blocks.GasOreGenerator;
import com.builtbroken.assemblyline.blocks.ItemBlockColored;
import com.builtbroken.assemblyline.blocks.BlockOre.OreData;
import com.builtbroken.assemblyline.entities.EntityFarmEgg;
import com.builtbroken.assemblyline.entities.EnumBird;
import com.builtbroken.assemblyline.entities.prefab.EntityTestCar;
import com.builtbroken.assemblyline.entities.prefab.ItemVehicleSpawn;
import com.builtbroken.assemblyline.fluid.pipes.BlockPipe;
import com.builtbroken.assemblyline.fluid.pipes.FluidPartsMaterial;
import com.builtbroken.assemblyline.fluid.pipes.ItemBlockPipe;
import com.builtbroken.assemblyline.fluid.pump.BlockConstructionPump;
import com.builtbroken.assemblyline.fluid.pump.BlockDrain;
import com.builtbroken.assemblyline.fluid.pump.BlockPumpMachine;
import com.builtbroken.assemblyline.generators.BlockSmallSteamGen;
import com.builtbroken.assemblyline.generators.BlockSolarPanel;
import com.builtbroken.assemblyline.imprinter.BlockImprinter;
import com.builtbroken.assemblyline.imprinter.ItemImprinter;
import com.builtbroken.assemblyline.item.BehaviorDispenseEgg;
import com.builtbroken.assemblyline.item.ItemBattery;
import com.builtbroken.assemblyline.item.ItemBlockOre;
import com.builtbroken.assemblyline.item.ItemColoredDust;
import com.builtbroken.assemblyline.item.ItemCommonTool;
import com.builtbroken.assemblyline.item.ItemFarmEgg;
import com.builtbroken.assemblyline.item.ItemOreDirv;
import com.builtbroken.assemblyline.item.ItemParts;
import com.builtbroken.assemblyline.item.ItemReadoutTools;
import com.builtbroken.assemblyline.item.ItemWrench;
import com.builtbroken.assemblyline.item.ItemParts.Parts;
import com.builtbroken.assemblyline.machine.BlockCrate;
import com.builtbroken.assemblyline.machine.BlockDebug;
import com.builtbroken.assemblyline.machine.BlockDetector;
import com.builtbroken.assemblyline.machine.BlockEnergyStorage;
import com.builtbroken.assemblyline.machine.BlockManipulator;
import com.builtbroken.assemblyline.machine.BlockRejector;
import com.builtbroken.assemblyline.machine.BlockReleaseValve;
import com.builtbroken.assemblyline.machine.BlockSink;
import com.builtbroken.assemblyline.machine.BlockTank;
import com.builtbroken.assemblyline.machine.BlockTurntable;
import com.builtbroken.assemblyline.machine.ItemBlockCrate;
import com.builtbroken.assemblyline.machine.ItemBlockEnergyStorage;
import com.builtbroken.assemblyline.machine.ItemFluidCan;
import com.builtbroken.assemblyline.machine.TileEntityAssembly;
import com.builtbroken.assemblyline.machine.belt.BlockConveyorBelt;
import com.builtbroken.assemblyline.machine.encoder.BlockEncoder;
import com.builtbroken.assemblyline.machine.encoder.ItemDisk;
import com.builtbroken.assemblyline.machine.processor.BlockProcessor;
import com.builtbroken.assemblyline.machine.red.BlockAdvancedHopper;
import com.builtbroken.assemblyline.transmit.BlockWire;
import com.builtbroken.assemblyline.transmit.ItemBlockWire;
import com.builtbroken.minecraft.CoreRegistry;
import com.builtbroken.minecraft.DarkCore;
import com.builtbroken.minecraft.EnumMaterial;
import com.builtbroken.minecraft.EnumOrePart;
import com.builtbroken.minecraft.IndustryTabs;
import com.builtbroken.minecraft.fluid.EnumGas;
import com.builtbroken.minecraft.prefab.ItemBlockHolder;

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
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@ModstatInfo(prefix = "asmline")
@Mod(modid = AssemblyLine.MOD_ID, name = AssemblyLine.MOD_NAME, version = AssemblyLine.VERSION, useMetadata = true)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class AssemblyLine
{

    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVIS_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVIS_VERSION + "." + BUILD_VERSION;
    // @Mod
    public static final String MOD_ID = "AssemblyLine";
    public static final String MOD_NAME = "Assembly Line";

    public static final String DOMAIN = "al";
    public static final String PREFIX = DOMAIN + ":";

    public static String DIRECTORY_NO_SLASH = "assets/" + DOMAIN + "/";
    public static String DIRECTORY = "/" + DIRECTORY_NO_SLASH;
    public static String LANGUAGE_PATH = DIRECTORY + "languages/";
    public static String SOUND_PATH = DIRECTORY + "audio/";

    @SidedProxy(clientSide = "com.builtbroken.assemblyline.client.ClientProxy", serverSide = "com.builtbroken.assemblyline.CommonProxy")
    public static CommonProxy proxy;

    @Instance(AssemblyLine.MOD_ID)
    public static AssemblyLine instance;

    public static ALRecipeLoader recipeLoader;

    @Metadata(AssemblyLine.MOD_ID)
    public static ModMetadata meta;

    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US", "de_DE" };

    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/AssemblyLine.cfg"));

    public static Logger FMLog = Logger.getLogger(AssemblyLine.MOD_NAME);

    public static boolean VINALLA_RECIPES = false;

    public static int entitiesIds = 60;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        FMLog.setParent(FMLLog.getLogger());

        DarkCore.instance().preLoad();
        Modstats.instance().getReporter().registerMod(this);
        MinecraftForge.EVENT_BUS.register(this);
        NetworkRegistry.instance().registerGuiHandler(this, proxy);

        proxy.preInit();

        TaskRegistry.registerCommand(new TaskDrop());
        TaskRegistry.registerCommand(new TaskGive());
        TaskRegistry.registerCommand(new TaskTake());
        TaskRegistry.registerCommand(new TaskGrabItem());
        TaskRegistry.registerCommand(new TaskGrabEntity());
        TaskRegistry.registerCommand(new TaskRotateBy());
        TaskRegistry.registerCommand(new TaskRotateTo());
        TaskRegistry.registerCommand(new TaskUse());
        TaskRegistry.registerCommand(new TaskIF());
        TaskRegistry.registerCommand(new TaskGOTO());
        TaskRegistry.registerCommand(new TaskReturn());
        TaskRegistry.registerCommand(new TaskEnd());
        TaskRegistry.registerCommand(new TaskFire());
        TaskRegistry.registerCommand(new TaskHarvest());
        TaskRegistry.registerCommand(new TaskPlace());
        TaskRegistry.registerCommand(new TaskBreak());
        TaskRegistry.registerCommand(new TaskStart());
        TaskRegistry.registerCommand(new TaskIdle());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        DarkCore.instance().Load();
        this.registerObjects();
        proxy.init();

        FMLog.info("Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " languages.");
        IndustryTabs.tabAutomation().setIconItemStack(new ItemStack(ALRecipeLoader.blockConveyorBelt));
        EntityRegistry.registerGlobalEntityID(EntityTestCar.class, "TestCar", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityTestCar.class, "TestCar", 60, this, 64, 1, true);

        for (EnumGas gas : EnumGas.values())
        {
            FluidRegistry.registerFluid(gas.getGas());
        }
        if (ALRecipeLoader.blockGas != null)
        {
            EnumGas.NATURAL_GAS.getGas().setBlockID(ALRecipeLoader.blockGas);
        }
        if (ALRecipeLoader.blockGas != null)
        {
            GameRegistry.registerWorldGenerator(new GasOreGenerator());
        }
        if (ALRecipeLoader.blockOre != null)
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
        if (ALRecipeLoader.itemParts != null)
        {
            for (Parts part : Parts.values())
            {
                OreDictionary.registerOre(part.name, new ItemStack(ALRecipeLoader.itemParts, 1, part.ordinal()));
            }
        }
        if (ALRecipeLoader.itemMetals != null)
        {
            MinecraftForge.EVENT_BUS.register(ALRecipeLoader.itemMetals);
        }
        if (ALRecipeLoader.itemParts != null)
        {
            IndustryTabs.tabMining().itemStack = new ItemStack(ALRecipeLoader.itemParts.itemID, 1, ItemParts.Parts.MiningIcon.ordinal());
        }
        if (ALRecipeLoader.itemMetals != null)
        {
            IndustryTabs.tabIndustrial().itemStack = EnumMaterial.getStack(ALRecipeLoader.itemMetals, EnumMaterial.IRON, EnumOrePart.GEARS, 1);
            ALRecipeLoader.parseOreNames(CONFIGURATION);
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        DarkCore.instance().postLoad();
        proxy.postInit();
        this.loadRecipes();
    }

    public void registerObjects()
    {

        if (recipeLoader == null)
        {
            recipeLoader = new ALRecipeLoader();
        }
        CONFIGURATION.load();
        /* BLOCKS */
        ALRecipeLoader.blockConveyorBelt = CoreRegistry.createNewBlock("ALBlockConveyor", AssemblyLine.MOD_ID, BlockConveyorBelt.class);
        ALRecipeLoader.blockManipulator = CoreRegistry.createNewBlock("Manipulator", AssemblyLine.MOD_ID, BlockManipulator.class);
        ALRecipeLoader.blockCrate = (BlockCrate) CoreRegistry.createNewBlock("Crate", AssemblyLine.MOD_ID, BlockCrate.class, ItemBlockCrate.class);
        ALRecipeLoader.blockImprinter = CoreRegistry.createNewBlock("Imprinter", AssemblyLine.MOD_ID, BlockImprinter.class);
        ALRecipeLoader.blockDetector = CoreRegistry.createNewBlock("Detector", AssemblyLine.MOD_ID, BlockDetector.class);
        ALRecipeLoader.blockRejector = CoreRegistry.createNewBlock("Rejector", AssemblyLine.MOD_ID, BlockRejector.class);
        ALRecipeLoader.blockEncoder = CoreRegistry.createNewBlock("Encoder", AssemblyLine.MOD_ID, BlockEncoder.class);
        ALRecipeLoader.blockArmbot = CoreRegistry.createNewBlock("Armbot", AssemblyLine.MOD_ID, BlockArmbot.class);
        ALRecipeLoader.blockTurntable = CoreRegistry.createNewBlock("Turntable", AssemblyLine.MOD_ID, BlockTurntable.class);
        ALRecipeLoader.processorMachine = CoreRegistry.createNewBlock("ALBlockProcessor", AssemblyLine.MOD_ID, BlockProcessor.class, ItemBlockHolder.class);
        ALRecipeLoader.blockAdvancedHopper = CoreRegistry.createNewBlock("ALBlockHopper", AssemblyLine.MOD_ID, BlockAdvancedHopper.class, ItemBlockHolder.class);
        ALRecipeLoader.blockPipe = CoreRegistry.createNewBlock("FMBlockPipe", AssemblyLine.MOD_ID, BlockPipe.class, ItemBlockPipe.class);
        ALRecipeLoader.blockPumpMachine = CoreRegistry.createNewBlock("FMBlockPump", AssemblyLine.MOD_ID, BlockPumpMachine.class, ItemBlockHolder.class);
        ALRecipeLoader.blockReleaseValve = CoreRegistry.createNewBlock("FMBlockReleaseValve", AssemblyLine.MOD_ID, BlockReleaseValve.class, ItemBlockHolder.class);
        ALRecipeLoader.blockTank = CoreRegistry.createNewBlock("FMBlockTank", AssemblyLine.MOD_ID, BlockTank.class, ItemBlockPipe.class);
        ALRecipeLoader.blockSink = CoreRegistry.createNewBlock("FMBlockSink", AssemblyLine.MOD_ID, BlockSink.class, ItemBlockHolder.class);
        ALRecipeLoader.blockDrain = CoreRegistry.createNewBlock("FMBlockDrain", AssemblyLine.MOD_ID, BlockDrain.class, ItemBlockHolder.class);
        ALRecipeLoader.blockConPump = CoreRegistry.createNewBlock("FMBlockConstructionPump", AssemblyLine.MOD_ID, BlockConstructionPump.class, ItemBlockHolder.class);

        ALRecipeLoader.blockSteamGen = CoreRegistry.createNewBlock("DMBlockSteamMachine", AssemblyLine.MOD_ID, BlockSmallSteamGen.class, ItemBlockHolder.class);
        ALRecipeLoader.blockOre = CoreRegistry.createNewBlock("DMBlockOre", AssemblyLine.MOD_ID, BlockOre.class, ItemBlockOre.class);
        ALRecipeLoader.blockWire = CoreRegistry.createNewBlock("DMBlockWire", AssemblyLine.MOD_ID, BlockWire.class, ItemBlockWire.class);
        ALRecipeLoader.blockDebug = CoreRegistry.createNewBlock("DMBlockDebug", AssemblyLine.MOD_ID, BlockDebug.class, ItemBlockHolder.class);
        ALRecipeLoader.blockStainGlass = CoreRegistry.createNewBlock("DMBlockStainedGlass", AssemblyLine.MOD_ID, BlockColorGlass.class, ItemBlockColored.class);
        ALRecipeLoader.blockColorSand = CoreRegistry.createNewBlock("DMBlockColorSand", AssemblyLine.MOD_ID, BlockColorSand.class, ItemBlockColored.class);
        ALRecipeLoader.blockBasalt = CoreRegistry.createNewBlock("DMBlockBasalt", AssemblyLine.MOD_ID, BlockBasalt.class, ItemBlockColored.class);
        ALRecipeLoader.blockGlowGlass = CoreRegistry.createNewBlock("DMBlockGlowGlass", AssemblyLine.MOD_ID, BlockColorGlowGlass.class, ItemBlockColored.class);
        ALRecipeLoader.blockSolar = CoreRegistry.createNewBlock("DMBlockSolar", AssemblyLine.MOD_ID, BlockSolarPanel.class, ItemBlockHolder.class);
        ALRecipeLoader.blockGas = CoreRegistry.createNewBlock("DMBlockGas", AssemblyLine.MOD_ID, BlockGasOre.class, ItemBlockHolder.class);
        ALRecipeLoader.blockBatBox = CoreRegistry.createNewBlock("DMBlockBatBox", AssemblyLine.MOD_ID, BlockEnergyStorage.class, ItemBlockEnergyStorage.class);

        /* ITEMS */
        ALRecipeLoader.itemTool = CoreRegistry.createNewItem("DMReadoutTools", AssemblyLine.MOD_ID, ItemReadoutTools.class, true);
        ALRecipeLoader.battery = CoreRegistry.createNewItem("DMItemBattery", AssemblyLine.MOD_ID, ItemBattery.class, true);
        ALRecipeLoader.wrench = CoreRegistry.createNewItem("DMWrench", AssemblyLine.MOD_ID, ItemWrench.class, true);
        ALRecipeLoader.itemGlowingSand = CoreRegistry.createNewItem("DMItemGlowingSand", AssemblyLine.MOD_ID, ItemColoredDust.class, true);
        ALRecipeLoader.itemDiggingTool = CoreRegistry.createNewItem("ItemDiggingTools", AssemblyLine.MOD_ID, ItemCommonTool.class, true);
        ALRecipeLoader.itemVehicleTest = CoreRegistry.createNewItem("ItemVehicleTest", AssemblyLine.MOD_ID, ItemVehicleSpawn.class, true);

        ALRecipeLoader.itemImprint = new ItemImprinter(CONFIGURATION.getItem("Imprint", DarkCore.getNextItemId()).getInt());
        ALRecipeLoader.itemDisk = new ItemDisk(CONFIGURATION.getItem("Disk", DarkCore.getNextItemId()).getInt());
        ALRecipeLoader.itemFluidCan = CoreRegistry.createNewItem("ItemFluidCan", AssemblyLine.MOD_ID, ItemFluidCan.class, true);
        ALRecipeLoader.itemParts = CoreRegistry.createNewItem("DMCraftingParts", AssemblyLine.MOD_ID, ItemParts.class, true);
        ALRecipeLoader.itemMetals = CoreRegistry.createNewItem("DMOreDirvParts", AssemblyLine.MOD_ID, ItemOreDirv.class, true);

        TileEntityAssembly.refresh_diff = CONFIGURATION.get("TileSettings", "RefreshRandomRange", 9, "n = value of config, 1 + n, random number range from 1 to n that will be added to the lowest refresh value").getInt();
        TileEntityAssembly.refresh_min_rate = CONFIGURATION.get("TileSettings", "RefreshLowestValue", 20, "Lowest value the refresh rate of the tile network will be").getInt();

        if (AssemblyLine.CONFIGURATION.get("Override", "Eggs", true).getBoolean(true))
        {
            Item.itemsList[Item.egg.itemID] = null;
            Item.egg = null;
            Item.egg = new ItemFarmEgg(88);
            GameRegistry.registerItem(Item.egg, "FTEgg", MOD_ID);
            EntityRegistry.registerGlobalEntityID(EntityFarmEgg.class, "FarmEgg", EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerModEntity(EntityFarmEgg.class, "FarmEgg", entitiesIds++, this, 64, 1, true);
            BlockDispenser.dispenseBehaviorRegistry.putObject(Item.egg, new BehaviorDispenseEgg());
        }

        for (EnumBird bird : EnumBird.values())
        {
            if (bird != EnumBird.VANILLA_CHICKEN && CONFIGURATION.get("Entities", "Enable_" + bird.name(), true).getBoolean(true))
            {
                bird.register();
            }
        }

        if (ALRecipeLoader.blockPipe != null)
        {
            IndustryTabs.tabHydraulic().setIconItemStack(FluidPartsMaterial.IRON.getStack());
        }

        CONFIGURATION.save();
    }

    public void loadModMeta()
    {
        meta.modId = AssemblyLine.MOD_ID;
        meta.name = AssemblyLine.MOD_NAME;
        meta.description = "Simi Realistic factory system for minecraft bring in conveyor belts, robotic arms, and simple machines";

        meta.url = "http://www.universalelectricity.com/coremachine";

        meta.logoFile = "/al_logo.png";
        meta.version = VERSION;
        meta.authorList = Arrays.asList(new String[] { "DarkGuardsman", "Briman", "Calclavia" });
        meta.credits = "Please see the website.";
        meta.autogenerated = false;

    }

    public void loadRecipes()
    {
        recipeLoader.loadRecipes();
    }
}
