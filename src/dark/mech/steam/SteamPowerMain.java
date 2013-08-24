package dark.mech.steam;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraftforge.common.Configuration;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.core.DarkMain;
import dark.core.ModPrefab;
import dark.mech.steam.boiler.BlockBoiler;
import dark.mech.steam.steamengine.BlockSteamPiston;

@Mod(modid = SteamPowerMain.MOD_NAME, name = SteamPowerMain.MOD_ID, version = DarkMain.VERSION, dependencies = ("after:" + DarkMain.MOD_ID))
@NetworkMod(channels = { SteamPowerMain.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class SteamPowerMain extends ModPrefab
{
    public static Configuration config = new Configuration((new File(cpw.mods.fml.common.Loader.instance().getConfigDir(), "SteamPower.cfg")));

    public static final String MOD_ID = "SteamPower";
    public static final String MOD_NAME = "Steam Power";
    public static final String CHANNEL = MOD_ID;

    // Blocks and items
    public static Block blockHeater, blockPiston, blockBoiler;
    // extra configs
    public static int steamOutBoiler, boilerHeat, fireOutput;
    @Instance
    public static SteamPowerMain instance;

    @SidedProxy(clientSide = "dark.SteamPower.SteamClientProxy", serverSide = "dark.SteamPower.SteamProxy")
    public static SteamProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
        super.preInit(event);

        GameRegistry.registerBlock(blockHeater, "SPHeater");
        GameRegistry.registerBlock(blockPiston, "SPPiston");
        GameRegistry.registerBlock(blockBoiler, "SPBoiler");

        proxy.preInit();
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        super.init(event);
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        proxy.postInit();
    }

    @Override
    public String getDomain()
    {
        return "steam";
    }

    @Override
    public void loadConfig()
    {
        config.load();
        //blockHeater = new BlockHeaters(config.get("Heater", "HeaterBlockID", this.getNextID()).getInt());
        blockPiston = new BlockSteamPiston(config.getBlock(Configuration.CATEGORY_BLOCK, "Piston", this.getNextID()).getInt());
        blockBoiler = new BlockBoiler(config.get("Boiler", "BoilerBlockID", this.getNextID()).getInt());
        steamOutBoiler = config.get("Boiler", "SteamPerCycle", 10).getInt();
        boilerHeat = config.get("Boiler", "HeatNeeded", 4500).getInt();
        fireOutput = config.get("Heater", "MaxOutputEnergy", 250).getInt();

        config.save();

    }

    @Override
    public void loadModMeta()
    {
        // TODO Auto-generated method stub

    }

}
