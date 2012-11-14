package dark.BasicUtilities;

import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import universalelectricity.core.UEConfig;
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
import dark.BasicUtilities.ItemParts.basicParts;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.machines.BlockMachine;
import dark.BasicUtilities.machines.BlockValve;
import dark.BasicUtilities.machines.ItemMachine;
import dark.BasicUtilities.machines.TileEntityPump;
import dark.BasicUtilities.mechanical.BlockRod;
import dark.BasicUtilities.mechanical.TileEntityRod;
import dark.BasicUtilities.pipes.BlockPipe;
import dark.BasicUtilities.pipes.ItemPipe;
import dark.BasicUtilities.pipes.TileEntityPipe;
import dark.BasicUtilities.tanks.ItemTank;
import dark.BasicUtilities.tanks.TileEntityLTank;

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
    public final static int BLOCK_ID_PREFIX = 2056;
    public final static int ITEM_ID_PREFIX = 10056;

    public static Block pipe = new BlockPipe(UEConfig.getBlockConfigID(
            CONFIGURATION, "block Pipe", BLOCK_ID_PREFIX)).setBlockName("pipe");
    public static Block machine = new BlockMachine(UEConfig.getBlockConfigID(
            CONFIGURATION, "machine set", BLOCK_ID_PREFIX))
            .setBlockName("pump");
    public static Block valve = new BlockValve(UEConfig.getBlockConfigID(
            CONFIGURATION, "valve", BLOCK_ID_PREFIX + 2)).setBlockName("valve");
    public static Block rod = new BlockRod(UEConfig.getBlockConfigID(
            CONFIGURATION, "mechanical rod", BLOCK_ID_PREFIX + 3));

    public static Item parts = new ItemParts(UEConfig.getItemConfigID(
            CONFIGURATION, "parts", ITEM_ID_PREFIX));
    public static Item itemPipes = new ItemPipe(UEConfig.getItemConfigID(
            CONFIGURATION, "item Pipe", ITEM_ID_PREFIX + 1));
    public static Item itemTank = new ItemTank(UEConfig.getItemConfigID(
            CONFIGURATION, "item tank", ITEM_ID_PREFIX + 2));
    public static Item gauge = new ItemGuage(UEConfig.getItemConfigID(
            CONFIGURATION, "guage", ITEM_ID_PREFIX + 3));
    // mod stuff
    @SidedProxy(clientSide = "dark.BasicUtilities.BPClientProxy", serverSide = "dark.BasicUtilities.BPCommonProxy")
    public static BPCommonProxy proxy;

    public static BasicUtilitiesMain instance;

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        proxy.preInit();
        GameRegistry.registerBlock(pipe);
        GameRegistry.registerBlock(rod);
        GameRegistry.registerBlock(machine, ItemMachine.class);

    }

    @Init
    public void Init(FMLInitializationEvent event)
    {
        proxy.Init();
        // register
        GameRegistry.registerTileEntity(TileEntityPipe.class, "pipe");
        GameRegistry.registerTileEntity(TileEntityPump.class, "pump");
        GameRegistry.registerTileEntity(TileEntityRod.class, "rod");
        GameRegistry.registerTileEntity(TileEntityLTank.class, "ltank");
        // Pipe Names
        for (int i = 0; i < Liquid.values().length; i++)
        {
            LanguageRegistry.addName((new ItemStack(itemPipes, 1, i)),
                    Liquid.getLiquid(i).lName + " Pipe");
        }
        // liquid tank names
        for (int i = 0; i < Liquid.values().length; i++)
        {
            LanguageRegistry.addName((new ItemStack(itemTank, 1, i)),
                    Liquid.getLiquid(i).lName + " Tank");
        }
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
