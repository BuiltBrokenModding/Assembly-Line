package dark.BasicUtilities;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dark.BasicUtilities.Items.ItemGuage;
import dark.BasicUtilities.Items.ItemParts;
import dark.BasicUtilities.Liquids.BlockOilFlowing;
import dark.BasicUtilities.Liquids.BlockOilStill;
import dark.BasicUtilities.Liquids.BlockSteam;
import dark.BasicUtilities.Liquids.ItemOilBucket;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.machines.BlockMachine;
import dark.BasicUtilities.machines.BlockValve;
import dark.BasicUtilities.machines.ItemMachine;
import dark.BasicUtilities.machines.TileEntityPump;
import dark.BasicUtilities.mechanical.BlockGenerator;
import dark.BasicUtilities.mechanical.BlockRod;
import dark.BasicUtilities.mechanical.TileEntityGen;
import dark.BasicUtilities.mechanical.TileEntityRod;
import dark.BasicUtilities.pipes.BlockPipe;
import dark.BasicUtilities.pipes.ItemPipe;
import dark.BasicUtilities.pipes.TileEntityPipe;

/**
 * Used in the creation of a new mod class
 * 
 * @author Rseifert
 * 
 */
@Mod(modid = BasicUtilitiesMain.NAME, name = BasicUtilitiesMain.NAME, version = BasicUtilitiesMain.VERSION, dependencies = "after:BasicComponents")
@NetworkMod(channels =
    { BasicUtilitiesMain.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class BasicUtilitiesMain extends DummyModContainer
{
    // TODO need changed on release
    public static final String VERSION = "0.2.0";
    // Constants
    public static final String NAME = "BasicUtilities";
    public static final String CHANNEL = "BPipes";
    public static final String textureFile = "/dark/BasicUtilities/textures/";
    public static final String BlOCK_PNG = "";
    public static final String ITEM_PNG = "/dark/generaltextures/Items.png";
    public static final Configuration CONFIGURATION = new Configuration(
            new File(Loader.instance().getConfigDir(), NAME + ".cfg"));
    // Block and Item vars
    public final static int BLOCK_ID_PREFIX = 3000;
    public final static int LIQUID_ID_PREFIX = 200;
    public final static int ITEM_ID_PREFIX = 10056;

    public static Block pipe = new BlockPipe(UniversalElectricity.CONFIGURATION.getBlock("Pipe", BLOCK_ID_PREFIX).getInt());
    public static Block machine = new BlockMachine(UniversalElectricity.CONFIGURATION.getBlock("MachineSetOne", BLOCK_ID_PREFIX + 1).getInt());
    public static Block valve = new BlockValve(UniversalElectricity.CONFIGURATION.getBlock("Valve", BLOCK_ID_PREFIX + 2).getInt());
    public static Block rod = new BlockRod(UniversalElectricity.CONFIGURATION.getBlock("MechanicalRod", BLOCK_ID_PREFIX + 3).getInt());
    public static Block generator = new BlockGenerator((UniversalElectricity.CONFIGURATION.getBlock("UEGenerator", BLOCK_ID_PREFIX + 4).getInt()));

    public static Block SteamBlock = new BlockSteam(UniversalElectricity.CONFIGURATION.getBlock("SteamBlock", LIQUID_ID_PREFIX).getInt());

    public static Block oilMoving = new BlockOilFlowing(UniversalElectricity.CONFIGURATION.getBlock("Oil_FlowingBU", LIQUID_ID_PREFIX + 1).getInt());
    public static Block oilStill = new BlockOilStill(UniversalElectricity.CONFIGURATION.getBlock("Oil_StillBU", LIQUID_ID_PREFIX + 2).getInt());

    public static LiquidStack Steam = LiquidDictionary.getOrCreateLiquid("Steam", new LiquidStack(SteamBlock, LiquidContainerRegistry.BUCKET_VOLUME));

    public static Item parts = new ItemParts(UniversalElectricity.CONFIGURATION.getItem("Parts", ITEM_ID_PREFIX).getInt());
    public static Item itemPipes = new ItemPipe(UniversalElectricity.CONFIGURATION.getItem("PipeItem", ITEM_ID_PREFIX + 1).getInt());
   // public static Item itemTank = new ItemTank(UniversalElectricity.CONFIGURATION.getItem("TankItem", ITEM_ID_PREFIX + 2).getInt());
    public static Item gauge = new ItemGuage(UniversalElectricity.CONFIGURATION.getItem("PipeGuage", ITEM_ID_PREFIX + 3).getInt());
    public static Item itemOilBucket = new ItemOilBucket(UniversalElectricity.CONFIGURATION.getItem("Oil Bucket", ITEM_ID_PREFIX + 4).getInt(), 4);
    // mod stuff
    @SidedProxy(clientSide = "dark.BasicUtilities.BPClientProxy", serverSide = "dark.BasicUtilities.BPCommonProxy")
    public static BPCommonProxy proxy;

    public static BasicUtilitiesMain instance;

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        proxy.preInit();
        GameRegistry.registerBlock(pipe, "multi pipe");
        GameRegistry.registerBlock(rod, "mech rod");
        GameRegistry.registerBlock(generator, "EU Generator");
        GameRegistry.registerBlock(machine, ItemMachine.class, "Machines");
        GameRegistry.registerBlock(SteamBlock, "steam");
        LiquidContainerRegistry.registerLiquid(new LiquidContainerData(new LiquidStack(oilStill, LiquidContainerRegistry.BUCKET_VOLUME), new ItemStack(itemOilBucket), new ItemStack(Item.bucketEmpty)));

    }

    @Init
    public void Init(FMLInitializationEvent event)
    {
        proxy.Init();
        // TileEntities
        GameRegistry.registerTileEntity(TileEntityPipe.class, "Pipe");
        GameRegistry.registerTileEntity(TileEntityPump.class, "pump");
        GameRegistry.registerTileEntity(TileEntityRod.class, "rod");
       // GameRegistry.registerTileEntity(TileEntityLTank.class, "ltank");
        GameRegistry.registerTileEntity(TileEntityGen.class, "WattGenerator");
        // Pipe Names
        for (int i = 0; i < Liquid.values().length; i++)
        {
            LanguageRegistry.addName((new ItemStack(itemPipes, 1, i)),
                    Liquid.getLiquid(i).displayerName + " Pipe");
        }
        /** liquid tank names
        for (int i = 0; i < Liquid.values().length; i++)
        {
            LanguageRegistry.addName((new ItemStack(itemTank, 1, i)),
                    Liquid.getLiquid(i).displayerName + " Tank");
        }*/
        for (int i = 0; i < ItemParts.basicParts.values().length; i++)
        {
            LanguageRegistry.addName((new ItemStack(parts, 1, i)),
                    ItemParts.basicParts.values()[i].name);
        }
        // machines
        LanguageRegistry.addName((new ItemStack(machine, 1, 0)), "WaterPump");
        LanguageRegistry.addName((new ItemStack(machine, 1, 4)),
                "WaterCondensor");
        // mechanical rod
        LanguageRegistry.addName((new ItemStack(rod, 1)), "MechRod");
        // Tools
        LanguageRegistry.addName((new ItemStack(gauge, 1, 0)), "PipeGuage");
    }

    @PostInit
    public void PostInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
        CraftingManager
                .getInstance()
                .getRecipeList()
                .add(new ShapedOreRecipe(new ItemStack(this.generator, 1),
                        new Object[]
                            { "@T@", "OVO", "@T@", 'T',
                                    new ItemStack(BasicUtilitiesMain.rod, 1), '@',
                                    "plateSteel", 'O', "basicCircuit", 'V',
                                    "motor" }));
        GameRegistry.addRecipe(new ItemStack(this.gauge, 1, 0), new Object[]
            {
                    "TVT", " T ", 'V', new ItemStack(parts, 1, 7), 'T',
                    new ItemStack(parts, 1, 1) });
        // iron tube
        GameRegistry.addRecipe(new ItemStack(parts, 2, 1), new Object[]
            {
                    "@@@", '@', Item.ingotIron });
        // obby tube
        GameRegistry.addRecipe(new ItemStack(parts, 2, 2), new Object[]
            {
                    "@@@", '@', Block.obsidian });
        // nether tube
        GameRegistry
                .addRecipe(new ItemStack(parts, 2, 3),
                        new Object[]
                            { "N@N", 'N', Block.netherrack, '@',
                                    new ItemStack(parts, 2, 2) });
        // seal
        GameRegistry.addRecipe(new ItemStack(parts, 2, 4), new Object[]
            { "@@",
                    "@@", '@', Item.leather });
        // slime steal
        GameRegistry.addShapelessRecipe(new ItemStack(parts, 1, 5),
                new Object[]
                    { new ItemStack(parts, 1, 4),
                            new ItemStack(Item.slimeBall, 1) });// stick seal
        // crafting pipes
        // {"black", "red", "green", "brown", "blue", "purple", "cyan",
        // "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta",
        // "orange", "white"};
        GameRegistry.addRecipe(new ItemStack(rod, 1), new Object[]
            { "I@I",
                    'I', Item.ingotIron, '@', new ItemStack(parts, 1, 1) });
        // water
        GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1, 1),
                new Object[]
                    { new ItemStack(parts, 1, 1),
                            new ItemStack(parts, 1, 4),
                            new ItemStack(Item.dyePowder, 1, 4) });
        // lava TODO change to use obby pipe and nether items
        GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1, 2),
                new Object[]
                    { new ItemStack(parts, 1, 2),
                            new ItemStack(Item.dyePowder, 1, 1) });
        // oil
        GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1, 3),
                new Object[]
                    { new ItemStack(parts, 1, 1),
                            new ItemStack(parts, 1, 4),
                            new ItemStack(Item.dyePowder, 1, 0) });
        // fuel
        GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1, 4),
                new Object[]
                    { new ItemStack(parts, 1, 1),
                            new ItemStack(parts, 1, 4),
                            new ItemStack(Item.dyePowder, 1, 11) });
        GameRegistry.addRecipe(new ItemStack(parts, 1, 7), new Object[]
            {
                    "T@T", 'T', new ItemStack(parts, 1, 1), '@', Block.lever });// valve

        GameRegistry.addRecipe(new ItemStack(parts, 1, 6), new Object[]
            {
                    " @ ", "@ @", " @ ", '@', Item.ingotIron });// tank
        /**
        GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1, 0),
                new Object[]
                    { new ItemStack(parts, 1, 6),
                            new ItemStack(parts, 1, 4),
                            new ItemStack(Item.dyePowder, 1, 15) });
        GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1, 1),
                new Object[]
                    { new ItemStack(parts, 1, 6),
                            new ItemStack(parts, 1, 4),
                            new ItemStack(Item.dyePowder, 1, 4) });
        // lava TODO change to use obby pipe and nether items
        GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1, 2),
                new Object[]
                    { new ItemStack(parts, 1, 6), Block.obsidian,
                            Block.obsidian, Block.obsidian, Block.obsidian });
        // oil
        GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1, 3),
                new Object[]
                    { new ItemStack(parts, 1, 6),
                            new ItemStack(parts, 1, 4),
                            new ItemStack(Item.dyePowder, 1, 0) });
        // fuel
        GameRegistry.addShapelessRecipe(
                new ItemStack(itemTank, 1, Liquid.Fuel.ordinal()),
                new Object[]
                    {
                            new ItemStack(parts, 1, basicParts.Tank.ordinal()),
                            new ItemStack(parts, 1, 4),
                            new ItemStack(Item.dyePowder, 1, 11) });
                            */
        GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1, 0),
                new Object[]
                    { new ItemStack(parts, 1, 1),
                            new ItemStack(parts, 1, 4) });
        GameRegistry.addRecipe(new ItemStack(machine, 1, 0), new Object[]
            {
                    "@T@", "BPB", "@P@", '@', new ItemStack(Item.ingotIron, 2),
                    'B', new ItemStack(parts, 1, 7), 'P',
                    new ItemStack(Block.pistonBase), 'T',
                    new ItemStack(parts, 1, 6) });
    }
}
