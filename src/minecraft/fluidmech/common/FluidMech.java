package fluidmech.common;

import hydraulic.core.implement.ColorCode;
import hydraulic.core.liquids.LiquidHandler;

import java.io.File;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import fluidmech.common.block.BlockPumpMachine;
import fluidmech.common.block.BlockReleaseValve;
import fluidmech.common.block.BlockSink;
import fluidmech.common.block.BlockTank;
import fluidmech.common.block.liquids.BlockWasteLiquid;
import fluidmech.common.item.ItemGuage;
import fluidmech.common.item.ItemLiquidMachine;
import fluidmech.common.item.ItemParts;
import fluidmech.common.item.ItemPipe;
import fluidmech.common.item.ItemReleaseValve;
import fluidmech.common.item.ItemTank;
import fluidmech.common.item.ItemParts.Parts;
import fluidmech.common.machines.TileEntityPump;
import fluidmech.common.machines.TileEntityReleaseValve;
import fluidmech.common.machines.TileEntitySink;
import fluidmech.common.machines.TileEntityTank;
import fluidmech.common.machines.mech.BlockGenerator;
import fluidmech.common.machines.mech.BlockRod;
import fluidmech.common.machines.mech.TileEntityGenerator;
import fluidmech.common.machines.mech.TileEntityRod;
import fluidmech.common.machines.pipes.BlockPipe;
import fluidmech.common.machines.pipes.TileEntityPipe;

/**
 * Used in the creation of a new mod class
 * 
 * @author Rseifert
 */
@Mod(modid = FluidMech.NAME, name = FluidMech.NAME, version = FluidMech.VERSION, dependencies = "after:BasicComponents")
@NetworkMod(channels = { FluidMech.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class FluidMech extends DummyModContainer
{
    // TODO Change in Version Release
    public static final String VERSION = "0.2.7";

    // Constants
    public static final String NAME = "Fluid_Mechanics";
    public static final String CHANNEL = "FluidMech";

    public static final String PATH = "/fluidmech/";
    public static final String RESOURCE_PATH = PATH + "resource/";
    public static final String BLOCK_TEXTURE_FILE = RESOURCE_PATH + "blocks.png";
    public static final String ITEM_TEXTURE_FILE = RESOURCE_PATH + "items.png";
    public static final String LANGUAGE_PATH = RESOURCE_PATH + "lang/";

    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };

    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir() + "/UniversalElectricity/", NAME + ".cfg"));

    public final static int BLOCK_ID_PREFIX = 3100;
    public final static int ITEM_ID_PREFIX = 13200;

    public static Block blockPipe;
    public static Block blockTank;
    public static Block blockMachine;
    public static Block blockRod;
    public static Block blockGenerator;
    public static Block blockReleaseValve;
    public static Block blockSink;

    public static Block blockWasteLiquid;

    public static LiquidStack liquidSteam;

    public static Item itemParts;
    // public static Item itemPipes;
    public static Item itemGauge;

    @SidedProxy(clientSide = "fluidmech.client.ClientProxy", serverSide = "fluidmech.common.CommonProxy")
    public static CommonProxy proxy;

    @Instance(NAME)
    public static FluidMech instance;

    public static Logger FMLog = Logger.getLogger(NAME);

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        FMLog.setParent(FMLLog.getLogger());
        FMLog.info("Initializing...");
        MinecraftForge.EVENT_BUS.register(new LiquidHandler());

        instance = this;
        CONFIGURATION.load();

        // Blocks
        blockPipe = new BlockPipe(this.CONFIGURATION.getBlock("Pipes", BLOCK_ID_PREFIX).getInt());
        blockMachine = new BlockPumpMachine(this.CONFIGURATION.getBlock("Machines", BLOCK_ID_PREFIX + 1).getInt());
        blockRod = new BlockRod(this.CONFIGURATION.getBlock("Mechanical Rod", BLOCK_ID_PREFIX + 3).getInt());
        blockGenerator = new BlockGenerator((this.CONFIGURATION.getBlock("Generator", BLOCK_ID_PREFIX + 4).getInt()));
        blockReleaseValve = new BlockReleaseValve((this.CONFIGURATION.getBlock("Release Valve", BLOCK_ID_PREFIX + 5).getInt()));
        blockTank = new BlockTank(this.CONFIGURATION.getBlock("Tank", BLOCK_ID_PREFIX + 6).getInt());
        blockWasteLiquid = new BlockWasteLiquid(this.CONFIGURATION.getBlock("WasteLiquid", BLOCK_ID_PREFIX + 7).getInt());
        blockSink = new BlockSink(this.CONFIGURATION.getBlock("Sink", BLOCK_ID_PREFIX + 8).getInt());

        // Items
        itemParts = new ItemParts(this.CONFIGURATION.getItem("Parts", ITEM_ID_PREFIX).getInt());

        // Valve item
        itemGauge = new ItemGuage(this.CONFIGURATION.getItem("PipeGuage", ITEM_ID_PREFIX + 3).getInt());

        CONFIGURATION.save();

        proxy.preInit();

        // block registry
        GameRegistry.registerBlock(blockPipe, ItemPipe.class, "lmPipe");
        GameRegistry.registerBlock(blockReleaseValve, ItemReleaseValve.class, "eValve");
        GameRegistry.registerBlock(blockRod, "mechRod");
        GameRegistry.registerBlock(blockGenerator, "lmGen");
        GameRegistry.registerBlock(blockMachine, ItemLiquidMachine.class, "lmMachines");
        GameRegistry.registerBlock(blockTank, ItemTank.class, "lmTank");
        GameRegistry.registerBlock(blockSink, "lmSink");

    }

    @Init
    public void Init(FMLInitializationEvent event)
    {
        FMLog.info("Loading...");
        proxy.Init();
        // TileEntities
        GameRegistry.registerTileEntity(TileEntityPipe.class, "lmPipeTile");
        GameRegistry.registerTileEntity(TileEntityPump.class, "lmPumpTile");
        GameRegistry.registerTileEntity(TileEntityRod.class, "lmRodTile");
        GameRegistry.registerTileEntity(TileEntityReleaseValve.class, "lmeValve");
        GameRegistry.registerTileEntity(TileEntityTank.class, "lmTank");
        GameRegistry.registerTileEntity(TileEntityGenerator.class, "lmGen");
        GameRegistry.registerTileEntity(TileEntitySink.class, "lmSink");
        FMLog.info(" Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " Languages.");

    }

    @PostInit
    public void PostInit(FMLPostInitializationEvent event)
    {
        FMLog.info("Finalizing...");
        proxy.postInit();
        TabFluidMech.setItemStack(new ItemStack(blockPipe, 1, 4));
        // generator
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(this.blockGenerator, 1), new Object[] {
                "@T@", "OVO", "@T@",
                'T', new ItemStack(FluidMech.blockRod, 1),
                '@', "plateSteel",
                'O', "basicCircuit",
                'V', "motor" }));
        // pipe gauge
        GameRegistry.addRecipe(new ItemStack(this.itemGauge, 1, 0), new Object[] {
                "TVT", " T ",
                'V', new ItemStack(itemParts, 1, Parts.Valve.ordinal()),
                'T', new ItemStack(itemParts, 1, Parts.Iron.ordinal()) });
        // iron tube
        GameRegistry.addRecipe(new ItemStack(itemParts, 4, Parts.Iron.ordinal()), new Object[] {
                "@@@",
                '@', Item.ingotIron });
        // bronze tube
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(itemParts, 4, Parts.Bronze.ordinal()), new Object[] {
                "@@@",
                '@', "ingotBronze" }));
        // obby tube
        GameRegistry.addRecipe(new ItemStack(itemParts, 4, Parts.Obby.ordinal()), new Object[] {
                "@@@",
                '@', Block.obsidian });
        // nether tube
        GameRegistry.addRecipe(new ItemStack(itemParts, 4, Parts.Nether.ordinal()), new Object[] {
                "N@N",
                'N', Block.netherrack,
                '@', new ItemStack(itemParts, 2, Parts.Obby.ordinal()) });
        // seal
        GameRegistry.addRecipe(new ItemStack(itemParts, 4, Parts.Seal.ordinal()), new Object[] {
                "@@", "@@",
                '@', Item.leather });
        // slime steal
        GameRegistry.addShapelessRecipe(new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal()), new Object[] {
                new ItemStack(itemParts, 1, Parts.Seal.ordinal()),
                new ItemStack(Item.slimeBall, 1) });
        // part valve
        GameRegistry.addRecipe(new ItemStack(itemParts, 1, Parts.Valve.ordinal()), new Object[] {
                "T@T",
                'T', new ItemStack(itemParts, 1, Parts.Iron.ordinal()),
                '@', Block.lever });

        // unfinished tank
        GameRegistry.addRecipe(new ItemStack(itemParts, 1, Parts.Tank.ordinal()), new Object[] {
                " @ ", "@ @", " @ ",
                '@', Item.ingotIron });
        // mechanical rod
        GameRegistry.addRecipe(new ItemStack(blockRod, 1), new Object[] {
                "I@I",
                'I', Item.ingotIron,
                '@', new ItemStack(itemParts, 1, Parts.Iron.ordinal()) });

        // Iron Pipe
        GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 1, 15), new Object[] {
                new ItemStack(itemParts, 1, Parts.Iron.ordinal()),
                new ItemStack(itemParts, 1, Parts.Seal.ordinal()) });
        for (int it = 0; it < 15; it++)
        {
            if (it != ColorCode.WHITE.ordinal() && it != ColorCode.ORANGE.ordinal())
            {
                GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, it), new Object[] {
                        new ItemStack(blockPipe, 1, 15),
                        new ItemStack(blockPipe, 1, 15),
                        new ItemStack(blockPipe, 1, 15),
                        new ItemStack(blockPipe, 1, 15),
                        new ItemStack(Item.dyePowder, 1, it) });
            }
        }
        // steam pipes
        GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 1, ColorCode.ORANGE.ordinal()), new Object[] {
                new ItemStack(itemParts, 1, Parts.Bronze.ordinal()),
                new ItemStack(itemParts, 1, Parts.Seal.ordinal()) });
        // milk pipes
        GameRegistry.addShapelessRecipe(new ItemStack(blockPipe, 4, ColorCode.WHITE.ordinal()), new Object[] {
                new ItemStack(blockPipe, 1, 15),
                new ItemStack(blockPipe, 1, 15),
                new ItemStack(blockPipe, 1, 15),
                new ItemStack(blockPipe, 1, 15),
                new ItemStack(Item.dyePowder, 1, 15) });
        // steam tank
        GameRegistry.addShapelessRecipe(new ItemStack(blockTank, 1, ColorCode.ORANGE.ordinal()), new Object[] {
                new ItemStack(itemParts, 1, Parts.Tank.ordinal()),
                new ItemStack(itemParts, 1, Parts.Seal.ordinal()),
                new ItemStack(itemParts, 1, Parts.Bronze.ordinal()),
                new ItemStack(itemParts, 1, Parts.Bronze.ordinal()) });
        // lava tank
        GameRegistry.addRecipe(new ItemStack(blockTank, 1, ColorCode.RED.ordinal()), new Object[] {
                "N@N", "@ @", "N@N",
                'T', new ItemStack(itemParts, 1, Parts.Tank.ordinal()),
                '@', Block.obsidian,
                'N', Block.netherrack });
        // water tank
        GameRegistry.addRecipe(new ItemStack(blockTank, 1, ColorCode.BLUE.ordinal()), new Object[] {
                "@G@", "STS", "@G@",
                'T', new ItemStack(itemParts, 1, Parts.Tank.ordinal()),
                '@', Block.planks,
                'G', Block.glass,
                'S', new ItemStack(itemParts, 1, Parts.Seal.ordinal()) });
        // milk tank
        GameRegistry.addRecipe(new ItemStack(blockTank, 1, ColorCode.WHITE.ordinal()), new Object[] {
                "W@W", "WTW", "W@W",
                'T', new ItemStack(itemParts, 1, Parts.Tank.ordinal()),
                '@', Block.stone,
                'W', Block.planks });
        // generic Tank
        GameRegistry.addRecipe(new ItemStack(blockTank, 1, ColorCode.NONE.ordinal()), new Object[] {
                "@@@", "@T@", "@@@",
                'T', new ItemStack(itemParts, 1, Parts.Tank.ordinal()),
                '@', Block.stone });

        // pump
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 0), new Object[] {
                "C@C", "BMB", "@X@",
                '@', "plateSteel",
                'X', new ItemStack(blockPipe, 1, ColorCode.NONE.ordinal()),
                'B', new ItemStack(itemParts, 1, Parts.Valve.ordinal()),
                'C', "basicCircuit",
                'M', "motor" }));

        // release valve
        GameRegistry.addRecipe(new ItemStack(blockReleaseValve, 1), new Object[] {
                "RPR", "PVP", "RPR",
                'P', new ItemStack(blockPipe, 1, 15),
                'V', new ItemStack(itemParts, 1, Parts.Valve.ordinal()),
                'R', Item.redstone });
        // sink
        GameRegistry.addRecipe(new ItemStack(blockSink, 1), new Object[] {
                "I I", "SIS", "SPS",
                'P', new ItemStack(blockPipe, 1, 15),
                'I', Item.ingotIron,
                'S', Block.stone });

        // reg ore directory for parts
        OreDictionary.registerOre("bronzeTube", new ItemStack(itemParts, 1, Parts.Bronze.ordinal()));
        OreDictionary.registerOre("ironTube", new ItemStack(itemParts, 1, Parts.Iron.ordinal()));
        OreDictionary.registerOre("netherTube", new ItemStack(itemParts, 1, Parts.Nether.ordinal()));
        OreDictionary.registerOre("obbyTube", new ItemStack(itemParts, 1, Parts.Obby.ordinal()));
        OreDictionary.registerOre("leatherSeal", new ItemStack(itemParts, 1, Parts.Seal.ordinal()));
        OreDictionary.registerOre("leatherSlimeSeal", new ItemStack(itemParts, 1, Parts.SlimeSeal.ordinal()));
        OreDictionary.registerOre("valvePart", new ItemStack(itemParts, 1, Parts.Valve.ordinal()));
        OreDictionary.registerOre("bronzeTube", new ItemStack(itemParts, 1, Parts.Bronze.ordinal()));
        OreDictionary.registerOre("unfinishedTank", new ItemStack(itemParts, 1, Parts.Tank.ordinal()));
        // add Default Liquids to current list, done last to let other mods use
        // there liquid data first if used
        LiquidStack waste = LiquidDictionary.getOrCreateLiquid("Waste", new LiquidStack(FluidMech.blockWasteLiquid, 1));
        LiquidHandler.addDefaultLiquids();
        FMLog.info("Done Loading");
    }
}
