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
import dark.BasicUtilities.Blocks.BlockEValve;
import dark.BasicUtilities.Blocks.BlockGenerator;
import dark.BasicUtilities.Blocks.BlockMachine;
import dark.BasicUtilities.Blocks.BlockOilFlowing;
import dark.BasicUtilities.Blocks.BlockOilStill;
import dark.BasicUtilities.Blocks.BlockPipe;
import dark.BasicUtilities.Blocks.BlockRod;
import dark.BasicUtilities.Blocks.BlockSteam;
import dark.BasicUtilities.Blocks.BlockValve;
import dark.BasicUtilities.Items.ItemEValve;
import dark.BasicUtilities.Items.ItemGuage;
import dark.BasicUtilities.Items.ItemMachine;
import dark.BasicUtilities.Items.ItemOilBucket;
import dark.BasicUtilities.Items.ItemParts;
import dark.BasicUtilities.Items.ItemPipe;
import dark.BasicUtilities.Items.ItemTank;
import dark.BasicUtilities.Items.ItemParts.basicParts;
import dark.BasicUtilities.Tile.TileEntityEValve;
import dark.BasicUtilities.Tile.TileEntityGen;
import dark.BasicUtilities.Tile.TileEntityTank;
import dark.BasicUtilities.Tile.TileEntityPipe;
import dark.BasicUtilities.Tile.TileEntityPump;
import dark.BasicUtilities.Tile.TileEntityRod;
import dark.BasicUtilities.api.Liquid;

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
    public static final String textureFile = "/dark/BasicUtilities/zResources/";
    public static final String BlOCK_PNG = "/dark/BasicUtilities/zResources/blocks.png";
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
    public static Block eValve = new BlockEValve((UniversalElectricity.CONFIGURATION.getBlock("EValve", BLOCK_ID_PREFIX + 5).getInt()));

    public static Block SteamBlock = new BlockSteam(UniversalElectricity.CONFIGURATION.getBlock("SteamBlock", LIQUID_ID_PREFIX).getInt());

    public static Block oilMoving = new BlockOilFlowing(UniversalElectricity.CONFIGURATION.getBlock("Oil_FlowingBU", LIQUID_ID_PREFIX + 1).getInt());
    public static Block oilStill = new BlockOilStill(UniversalElectricity.CONFIGURATION.getBlock("Oil_StillBU", LIQUID_ID_PREFIX + 2).getInt());

    public static LiquidStack Steam = LiquidDictionary.getOrCreateLiquid("Steam", new LiquidStack(SteamBlock, LiquidContainerRegistry.BUCKET_VOLUME));

    public static Item parts = new ItemParts(UniversalElectricity.CONFIGURATION.getItem("Parts", ITEM_ID_PREFIX).getInt());
    public static Item itemPipes = new ItemPipe(UniversalElectricity.CONFIGURATION.getItem("PipeItem", ITEM_ID_PREFIX + 1).getInt());
    // public static Item itemEValve = new
    // ItemEValve(UniversalElectricity.CONFIGURATION.getItem("EValveItem",
    // ITEM_ID_PREFIX + 2).getInt());

    public static Item gauge = new ItemGuage(UniversalElectricity.CONFIGURATION.getItem("PipeGuage", ITEM_ID_PREFIX + 3).getInt());
    public static Item itemOilBucket = new ItemOilBucket(UniversalElectricity.CONFIGURATION.getItem("Oil Bucket", ITEM_ID_PREFIX + 4).getInt(), 4);
    public static Item itemTank = new ItemTank(UniversalElectricity.CONFIGURATION.getItem("TankItem", ITEM_ID_PREFIX + 5).getInt());
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
        GameRegistry.registerBlock(eValve, ItemEValve.class, "eValve");
        GameRegistry.registerBlock(rod, "mech rod");
        GameRegistry.registerBlock(generator, "EU Generator");
        GameRegistry.registerBlock(machine, ItemMachine.class, "Machines");
        GameRegistry.registerBlock(SteamBlock, "steam");
        GameRegistry.registerBlock(oilStill, "oil s");
        GameRegistry.registerBlock(oilMoving, "oil m");
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
        GameRegistry.registerTileEntity(TileEntityEValve.class, "EValve");
        GameRegistry.registerTileEntity(TileEntityTank.class, "ltank");
        GameRegistry.registerTileEntity(TileEntityGen.class, "WattGenerator");
        // Liquid Item/Block common name writer
        for (int i = 0; i < Liquid.values().length; i++)
        {
            // eValves
            LanguageRegistry.addName((new ItemStack(eValve, 1, i)), Liquid.getLiquid(i).displayerName + " release Valve");
            // pipes
            LanguageRegistry.addName((new ItemStack(itemPipes, 1, i)), Liquid.getLiquid(i).displayerName + " Pipe");

            // Storage Tanks
            LanguageRegistry.addName((new ItemStack(itemTank, 1, i)), Liquid.getLiquid(i).displayerName + " Tank");
        }
        for (int i = 0; i < ItemParts.basicParts.values().length; i++)
        {
            // parts
            LanguageRegistry.addName((new ItemStack(parts, 1, i)),
                    ItemParts.basicParts.values()[i].name);
        }
        // machines
        LanguageRegistry.addName((new ItemStack(machine, 1, 0)), "Pump");
        LanguageRegistry.addName((new ItemStack(machine, 1, 4)), "WaterCondensor");

        LanguageRegistry.addName((new ItemStack(generator, 1)), "EU Generator");
        // mechanical rod
        LanguageRegistry.addName((new ItemStack(rod, 1)), "GearedRod");
        // Tools
        LanguageRegistry.addName((new ItemStack(gauge, 1, 0)), "PipeGuage");
    }

    @PostInit
    public void PostInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
        PipeTab.setItemStack(new ItemStack(itemPipes, 1, Liquid.WATER.ordinal()));
        // generator
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(this.generator, 1), new Object[] {
                "@T@", "OVO", "@T@",
                'T', new ItemStack(BasicUtilitiesMain.rod, 1),
                '@', "plateSteel",
                'O', "basicCircuit",
                'V', "motor" }));
        // pipe gauge
        GameRegistry.addRecipe(new ItemStack(this.gauge, 1, 0), new Object[] {
                "TVT", " T ",
                'V', new ItemStack(parts, 1, 7),
                'T', new ItemStack(parts, 1, basicParts.Iron.ordinal()) });
        // iron tube
        GameRegistry.addRecipe(new ItemStack(parts, 2, basicParts.Bronze.ordinal()), new Object[] {
                "@@@",
                '@', Item.ingotIron });
        // obby tube
        GameRegistry.addRecipe(new ItemStack(parts, 2, basicParts.Obby.ordinal()), new Object[] {
                "@@@",
                '@', Block.obsidian });
        // nether tube
        GameRegistry.addRecipe(new ItemStack(parts, 2, basicParts.Nether.ordinal()), new Object[] {
                "N@N",
                'N', Block.netherrack,
                '@', new ItemStack(parts, 2, basicParts.Obby.ordinal()) });
        // seal
        GameRegistry.addRecipe(new ItemStack(parts, 2, basicParts.Seal.ordinal()), new Object[] {
                "@@", "@@",
                '@', Item.leather });
        // slime steal
        GameRegistry.addShapelessRecipe(new ItemStack(parts, 1, basicParts.SlimeSeal.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Seal.ordinal()),
                new ItemStack(Item.slimeBall, 1) });
        // part valve
        GameRegistry.addRecipe(new ItemStack(parts, 1, basicParts.Valve.ordinal()), new Object[] {
                "T@T",
                'T', new ItemStack(parts, 1, basicParts.Iron.ordinal()),
                '@', Block.lever });

        // unfinished tank
        GameRegistry.addRecipe(new ItemStack(parts, 1, basicParts.Tank.ordinal()), new Object[] {
                " @ ", "@ @", " @ ",
                '@', Item.ingotIron });
        // mechanical rod
        GameRegistry.addRecipe(new ItemStack(rod, 1), new Object[] {
                "I@I",
                'I', Item.ingotIron,
                '@', new ItemStack(parts, 1, basicParts.Iron.ordinal()) });

        // steam pipe
        GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1, Liquid.STEAM.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Iron.ordinal()),
                new ItemStack(parts, 1, basicParts.Seal.ordinal()) });
        // water pipe
        GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1, Liquid.WATER.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Iron.ordinal()),
                new ItemStack(parts, 1, basicParts.Seal.ordinal()),
                new ItemStack(Item.dyePowder, 1, 4) });
        // lava pipe
        GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1, Liquid.LAVA.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Obby.ordinal()),
                new ItemStack(Item.dyePowder, 1, 1) });
        // oil pipe
        GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1, Liquid.OIL.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Iron.ordinal()),
                new ItemStack(parts, 1, basicParts.Seal.ordinal()),
                new ItemStack(Item.dyePowder, 1, 0) });
        // fuel pipe
        GameRegistry.addShapelessRecipe(new ItemStack(itemPipes, 1, Liquid.FUEL.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Iron.ordinal()),
                new ItemStack(parts, 1, basicParts.Seal.ordinal()),
                new ItemStack(Item.dyePowder, 1, 11) });

        // steam tank
        GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1, Liquid.STEAM.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Tank.ordinal()),
                new ItemStack(parts, 1, basicParts.Seal.ordinal()),
                new ItemStack(Item.dyePowder, 1, 15) });
        // water tank
        GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1, Liquid.WATER.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Tank.ordinal()),
                new ItemStack(parts, 1, basicParts.Seal.ordinal()),
                new ItemStack(Item.dyePowder, 1, 4) });
        // lava tank
        GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1, Liquid.LAVA.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Tank.ordinal()),
                Block.obsidian,
                Block.obsidian,
                Block.obsidian,
                Block.obsidian });
        // oil tank
        GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1, Liquid.OIL.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Tank.ordinal()),
                new ItemStack(parts, 1, basicParts.Seal.ordinal()),
                new ItemStack(Item.dyePowder, 1, 0) });
        // fuel tank
        GameRegistry.addShapelessRecipe(new ItemStack(itemTank, 1, Liquid.FUEL.ordinal()), new Object[] {
                new ItemStack(parts, 1, basicParts.Tank.ordinal()),
                new ItemStack(parts, 1, basicParts.Seal.ordinal()),
                new ItemStack(Item.dyePowder, 1, 11) });
        // pump
        GameRegistry.addRecipe(new ItemStack(machine, 1, 0), new Object[] {
                "@T@", "BPB", "@P@",
                '@', new ItemStack(Item.ingotIron, 2),
                'B', new ItemStack(parts, 1, basicParts.Valve.ordinal()),
                'P', new ItemStack(Block.pistonBase),
                'T', new ItemStack(parts, 1, basicParts.Tank.ordinal()) });
        // eVavles
        for (int i = 0; i < Liquid.values().length - 1; i++)
        {
            GameRegistry.addRecipe(new ItemStack(machine, 1, i), new Object[] {
                    " P ", "PVP", " P ",
                    'P', new ItemStack(itemPipes, 1, i),
                    'V', new ItemStack(parts, 1, basicParts.Valve.ordinal()), });
        }
    }
}
