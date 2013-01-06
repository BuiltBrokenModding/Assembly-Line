package liquidmechanics.common;

import java.io.File;

import liquidmechanics.api.helpers.PipeColor;
import liquidmechanics.common.block.BlockGenerator;
import liquidmechanics.common.block.BlockPumpMachine;
import liquidmechanics.common.block.BlockPipe;
import liquidmechanics.common.block.BlockReleaseValve;
import liquidmechanics.common.block.BlockRod;
import liquidmechanics.common.block.BlockTank;
import liquidmechanics.common.block.BlockWasteLiquid;
import liquidmechanics.common.handlers.LiquidHandler;
import liquidmechanics.common.item.ItemGuage;
import liquidmechanics.common.item.ItemLiquidMachine;
import liquidmechanics.common.item.ItemParts;
import liquidmechanics.common.item.ItemParts.Parts;
import liquidmechanics.common.item.ItemPipe;
import liquidmechanics.common.item.ItemTank;
import liquidmechanics.common.tileentity.TileEntityGenerator;
import liquidmechanics.common.tileentity.TileEntityPipe;
import liquidmechanics.common.tileentity.TileEntityPump;
import liquidmechanics.common.tileentity.TileEntityReleaseValve;
import liquidmechanics.common.tileentity.TileEntityRod;
import liquidmechanics.common.tileentity.TileEntityTank;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.DummyModContainer;
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

/**
 * Used in the creation of a new mod class
 * 
 * @author Rseifert
 * 
 */
@Mod(modid = LiquidMechanics.NAME, name = LiquidMechanics.NAME, version = LiquidMechanics.VERSION, dependencies = "after:BasicComponents")
@NetworkMod(channels = { LiquidMechanics.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class LiquidMechanics extends DummyModContainer
{
    // TODO Change in Version Release
    public static final String VERSION = "0.2.4";

    // Constants
    public static final String NAME = "Liquid Mechanics";
    public static final String CHANNEL = "liquidMech";

    public static final String PATH = "/liquidmechanics/";
    public static final String RESOURCE_PATH = PATH + "resource/";
    public static final String BLOCK_TEXTURE_FILE = RESOURCE_PATH + "blocks.png";
    public static final String ITEM_TEXTURE_FILE = RESOURCE_PATH + "items.png";
    public static final String LANGUAGE_PATH = RESOURCE_PATH + "lang/";

    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };

    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir() + "/UniversalElectricity/", NAME + ".cfg"));

    public final static int BLOCK_ID_PREFIX = 3100;
    public final static int LIQUID_ID_PREFIX = 200;
    public final static int ITEM_ID_PREFIX = 13200;

    public static Block blockPipe;
    public static Block blockTank;
    public static Block blockMachine;
    public static Block blockRod;
    public static Block blockGenerator;
    public static Block blockReleaseValve;
    
    public static Block blockWasteLiquid;

    public static LiquidStack liquidSteam;

    public static Item itemParts;
    // public static Item itemPipes;
    public static Item itemGauge;

    @SidedProxy(clientSide = "liquidmechanics.client.ClientProxy", serverSide = "liquidmechanics.common.CommonProxy")
    public static CommonProxy proxy;

    @Instance(NAME)
    public static LiquidMechanics instance;

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;

        CONFIGURATION.load();

        // Blocks
        blockPipe = new BlockPipe(this.CONFIGURATION.getBlock("Pipes", BLOCK_ID_PREFIX).getInt());
        blockMachine = new BlockPumpMachine(this.CONFIGURATION.getBlock("Machines", BLOCK_ID_PREFIX + 1).getInt());
        blockRod = new BlockRod(this.CONFIGURATION.getBlock("Mechanical Rod", BLOCK_ID_PREFIX + 3).getInt());
        blockGenerator = new BlockGenerator((this.CONFIGURATION.getBlock("Generator", BLOCK_ID_PREFIX + 4).getInt()));
        blockReleaseValve = new BlockReleaseValve((this.CONFIGURATION.getBlock("Release Valve", BLOCK_ID_PREFIX + 5).getInt()));
        blockTank = new BlockTank(this.CONFIGURATION.getBlock("Tank", BLOCK_ID_PREFIX + 6).getInt());
        
        
        // Items
        itemParts = new ItemParts(this.CONFIGURATION.getItem("Parts", ITEM_ID_PREFIX).getInt());
        // itemPipes = new ItemPipe(this.CONFIGURATION.getItem("PipeItem",
        // ITEM_ID_PREFIX + 1).getInt());

        // Valve item
        itemGauge = new ItemGuage(this.CONFIGURATION.getItem("PipeGuage", ITEM_ID_PREFIX + 3).getInt());

        // Liquid Registry
        blockWasteLiquid = new BlockWasteLiquid(this.CONFIGURATION.getBlock("WasteLiquid", LIQUID_ID_PREFIX).getInt());
        CONFIGURATION.save();

        proxy.preInit();

        // block registry
        GameRegistry.registerBlock(blockPipe, ItemPipe.class, "lmPipe");
        GameRegistry.registerBlock(blockReleaseValve, "eValve");
        GameRegistry.registerBlock(blockRod, "mechRod");
        GameRegistry.registerBlock(blockGenerator, "lmGen");
        GameRegistry.registerBlock(blockMachine, ItemLiquidMachine.class, "lmMachines");
        GameRegistry.registerBlock(blockTank,ItemTank.class, "lmTank");
    }

    @Init
    public void Init(FMLInitializationEvent event)
    {
        proxy.Init();
        // TileEntities
        GameRegistry.registerTileEntity(TileEntityPipe.class, "lmPipeTile");
        GameRegistry.registerTileEntity(TileEntityPump.class, "lmPumpTile");
        GameRegistry.registerTileEntity(TileEntityRod.class, "lmRodTile");
        GameRegistry.registerTileEntity(TileEntityReleaseValve.class, "lmeValve");
        GameRegistry.registerTileEntity(TileEntityTank.class, "lmTank");
        GameRegistry.registerTileEntity(TileEntityGenerator.class, "lmGen");
        System.out.println("Fluid Mechanics Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " Languages.");

    }

    @PostInit
    public void PostInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
        TabLiquidMechanics.setItemStack(new ItemStack(blockPipe, 1, 4));
        // generator
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(this.blockGenerator, 1), new Object[] {
                "@T@", "OVO", "@T@",
                'T', new ItemStack(LiquidMechanics.blockRod, 1),
                '@', "plateSteel",
                'O', "basicCircuit",
                'V', "motor" }));
        // pipe gauge
        GameRegistry.addRecipe(new ItemStack(this.itemGauge, 1, 0), new Object[] {
                "TVT", " T ",
                'V', new ItemStack(itemParts, 1, 7),
                'T', new ItemStack(itemParts, 1, Parts.Iron.ordinal()) });
        // iron tube
        GameRegistry.addRecipe(new ItemStack(itemParts, 2, Parts.Iron.ordinal()), new Object[] {
                "@@@",
                '@', Item.ingotIron });
        // bronze tube
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(itemParts, 2, Parts.Bronze.ordinal()), new Object[] {
                "@@@",
                '@', "ingotBronze" }));
        // obby tube
        GameRegistry.addRecipe(new ItemStack(itemParts, 2, Parts.Obby.ordinal()), new Object[] {
                "@@@",
                '@', Block.obsidian });
        // nether tube
        GameRegistry.addRecipe(new ItemStack(itemParts, 2, Parts.Nether.ordinal()), new Object[] {
                "N@N",
                'N', Block.netherrack,
                '@', new ItemStack(itemParts, 2, Parts.Obby.ordinal()) });
        // seal
        GameRegistry.addRecipe(new ItemStack(itemParts, 2, Parts.Seal.ordinal()), new Object[] {
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

        // steam tank
        GameRegistry.addShapelessRecipe(new ItemStack(blockTank, 1, PipeColor.ORANGE.ordinal()), new Object[] {
                new ItemStack(itemParts, 1, Parts.Tank.ordinal()),
                new ItemStack(itemParts, 1, Parts.Seal.ordinal()),
                new ItemStack(Item.dyePowder, 1, PipeColor.ORANGE.ordinal()) });
        // lava tank
        GameRegistry.addRecipe(new ItemStack(blockTank, 1, PipeColor.RED.ordinal()), new Object[] {
                " @ ", "@T@", " @ ",
                'T', new ItemStack(itemParts, 1, Parts.Tank.ordinal()),
                '@', Block.obsidian, });

        // pump
        GameRegistry.addRecipe(new ItemStack(blockMachine, 1, 0), new Object[] {
                "@T@", "BPB", "@P@",
                '@', new ItemStack(Item.ingotIron, 2),
                'B', new ItemStack(itemParts, 1, Parts.Valve.ordinal()),
                'P', new ItemStack(Block.pistonBase),
                'T', new ItemStack(itemParts, 1, Parts.Tank.ordinal()) });

        // release valve
        GameRegistry.addRecipe(new ItemStack(blockMachine, 1), new Object[] {
                "RPR", "PVP", "RPR", " P ",
                'P', new ItemStack(blockPipe, 1, 15),
                'V', new ItemStack(itemParts, 1, Parts.Valve.ordinal()),
                'R', Item.redstone });

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
        LiquidHandler.addDefaultLiquids();
    }
}
