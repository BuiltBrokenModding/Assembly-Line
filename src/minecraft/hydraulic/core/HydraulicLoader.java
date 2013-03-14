package hydraulic.core;

import hydraulic.core.liquidNetwork.HydraulicNetworkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLLog;

public class HydraulicLoader
{
    public static final HydraulicLoader INSTANCE = new HydraulicLoader();

    public static boolean isInitialized = false;

    public void initiate()
    {
        if (!isInitialized)
        {
            HydraulicNetworkManager.instance = new HydraulicNetworkManager();
            MinecraftForge.EVENT_BUS.register(this);

            FMLLog.finest("Hydraulics v" + Hydraulics.VERSION + " loaded without error!");

            isInitialized = true;
        }
    }

    @ForgeSubscribe
    public void onWorldUnLoad(WorldEvent.Unload event)
    {
        HydraulicNetworkManager.instance = new HydraulicNetworkManager();
        HydraulicNetworkManager.instance.cleanUpNetworks();
    }

    @ForgeSubscribe
    public void onWorldLoad(WorldEvent.Load event)
    {
        HydraulicNetworkManager.instance = new HydraulicNetworkManager();
        HydraulicNetworkManager.instance.cleanUpNetworks();
    }
}
