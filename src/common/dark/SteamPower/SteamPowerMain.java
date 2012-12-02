package dark.SteamPower;

import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dark.BasicUtilities.BasicUtilitiesMain;
import dark.SteamPower.steamengine.BlockSteamPiston;
import dark.SteamPower.steamengine.ItemSteamPiston;
import dark.SteamPower.steamengine.TileEntitytopGen;

@Mod(modid = "SteamPower", name = "Steam Power", version = "1.9", dependencies = "after:basicPipes")
@NetworkMod(channels =
    { "SPpack" }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class SteamPowerMain
{
    static Configuration config = new Configuration((new File(cpw.mods.fml.common.Loader.instance().getConfigDir(), "/UniversalElectricity/SteamPower.cfg")));
    public static final String channel = "SPpack";
    public static String textureFile = "/dark/SteamPower/textures/";
    // Blocks and items
    public static Block machine = new SteamMachines(Integer.parseInt(config.get(Configuration.CATEGORY_BLOCK, "MachinesID", 3030).value)).setBlockName("machine");
    public static Block engine = new BlockSteamPiston(Integer.parseInt(config.get(Configuration.CATEGORY_BLOCK, "SteamEngineID", 3031).value)).setBlockName("SteamEngien");
    public static Item itemEngine = new ItemSteamPiston(Integer.parseInt(config.get(Configuration.CATEGORY_ITEM, "EngineItem", 30308).value)).setItemName("SteamEngine");

    public static SteamPowerMain instance;

    @SidedProxy(clientSide = "dark.SteamPower.SteamClientProxy", serverSide = "dark.SteamPower.SteamProxy")
    public static SteamProxy proxy;
    // extra configs
    public static int steamOutBoiler = Integer.parseInt(config.get(Configuration.CATEGORY_GENERAL, "steamOutPerCycle", 10).value);
    public static int boilerHeat = Integer.parseInt(config.get(Configuration.CATEGORY_GENERAL, "boilerInKJNeed", 4500).value);
    public static int fireOutput = Integer.parseInt(config.get(Configuration.CATEGORY_GENERAL, "fireBoxOutKJMax", 250).value);

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
        proxy.preInit();
        GameRegistry.registerBlock(machine);
        GameRegistry.registerBlock(engine);
    }

    @Init
    public void load(FMLInitializationEvent evt)
    {
        proxy.init();
        GameRegistry.registerTileEntity(TileEntitytopGen.class, "gentop");
        // Names...............
        LanguageRegistry.addName((new ItemStack(machine, 1, 4)), "Boiler");
        LanguageRegistry.addName((new ItemStack(machine, 1, 0)), "FireBox");
        LanguageRegistry.addName((new ItemStack(itemEngine, 1, 0)), "SteamPiston");

    }

    @PostInit
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
        try
        {
            // Boiler
            CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(machine, 1, 4), new Object[]
                { "TT", "VV", "TT", 'T', new ItemStack(BasicUtilitiesMain.parts, 1, 6), 'V', new ItemStack(BasicUtilitiesMain.parts, 1, 7) }));
            // Furnace
            CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(machine, 1, 0), new Object[]
                { "@", "F", 'F', Block.stoneOvenIdle, '@', "plateSteel" }));
            // SteamPiston
            CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(itemEngine, 1, 0), new Object[]
                { "GGG", "VPV", "@T@",
                        'T', new ItemStack(BasicUtilitiesMain.parts, 1, 1),
                        'G', BasicUtilitiesMain.rod, '@', "plateSteel",
                        'P', Block.pistonBase,
                        'V', new ItemStack(BasicUtilitiesMain.parts, 1, 7),
                        'M', "motor" }));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.print("UE based recipes not loaded");
        }
    }

}
