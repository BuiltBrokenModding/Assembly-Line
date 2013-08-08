package dark.mech.steam;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
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
import dark.mech.steam.boiler.BlockBoiler;
import dark.mech.steam.firebox.BlockHeaters;
import dark.mech.steam.steamengine.BlockSteamPiston;
import dark.mech.steam.steamengine.ItemSteamPiston;

@Mod(modid = "SteamPower", name = "Steam Power", version = "0.2.3", dependencies = "after:basicPipes")
@NetworkMod(channels = { SteamPowerMain.channel }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class SteamPowerMain
{
    public static Configuration config = new Configuration((new File(cpw.mods.fml.common.Loader.instance().getConfigDir(), "SteamPower.cfg")));
    public static final String channel = "SPpack";
    public static String textureFile = "/dark/SteamPower/textures/";
    public final static int BLOCK_ID_PREFIX = 3100;
    // Blocks and items
    public static Block heaters = new BlockHeaters(Integer.parseInt(config.get(Configuration.CATEGORY_BLOCK, "Burners", BLOCK_ID_PREFIX).value)).setBlockName("machine");
    public static Block piston = new BlockSteamPiston(Integer.parseInt(config.get(Configuration.CATEGORY_BLOCK, "SteamPiston", BLOCK_ID_PREFIX + 1).value)).setBlockName("SteamEngien");
    public static Block boilers = new BlockBoiler(Integer.parseInt(config.get(Configuration.CATEGORY_BLOCK, "Boilers", BLOCK_ID_PREFIX + 2).value)).setBlockName("machine");
    public static Item itemEngine = new ItemSteamPiston(Integer.parseInt(config.get(Configuration.CATEGORY_ITEM, "SteamPistonItem", 30308).value)).setItemName("SteamEngine");

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

        GameRegistry.registerBlock(heaters, "heater units");
        GameRegistry.registerBlock(piston, "steam piston");
        GameRegistry.registerBlock(boilers, "boiler tanks");
    }

    @Init
    public void load(FMLInitializationEvent evt)
    {
        proxy.init();

        // Burner Names

        LanguageRegistry.addName((new ItemStack(heaters, 1, 0)), BlockHeaters.Burners.values()[0].name);
        // Boiler Names
        LanguageRegistry.addName((new ItemStack(boilers, 1, 0)), BlockBoiler.Boilers.values()[0].name);
        // Steam Piston Name
        LanguageRegistry.addName((new ItemStack(itemEngine, 1, 0)), "Steam Piston");

    }

    @PostInit
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();

        // Boiler basic
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(boilers, 1, 0), new Object[] { "TT", "VV", "TT", 'T', new ItemStack(BasicUtilitiesMain.parts, 1, 6), 'V', new ItemStack(BasicUtilitiesMain.parts, 1, 7) }));
        // Burner Coal
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(heaters, 1, 0), new Object[] { "@", "F", 'F', Block.stoneOvenIdle, '@', "plateSteel" }));
        // SteamPiston
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(new ItemStack(itemEngine, 1, 0), new Object[] { "GGG", "VPV", "@T@", 'T', new ItemStack(BasicUtilitiesMain.parts, 1, 1), 'G', BasicUtilitiesMain.rod, '@', "plateSteel", 'P', Block.pistonBase, 'V', new ItemStack(BasicUtilitiesMain.parts, 1, 7), 'M', "motor" }));

    }

}
