package hydraulic.core;

import hydraulic.core.liquids.Hydraulic;
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
		    Hydraulic.instance = new Hydraulic();
			MinecraftForge.EVENT_BUS.register(this);			

			FMLLog.finest("Hydraulics v" + UniversalElectricity.VERSION + " loaded without error!");

			isInitialized = true;
		}
	}

	@ForgeSubscribe
	public void onWorldUnLoad(WorldEvent.Unload event)
	{
	    Hydraulic.instance = new Hydraulic();
	    Hydraulic.instance.cleanUpNetworks();
	}

	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load event)
	{
	    Hydraulic.instance = new Hydraulic();
	    Hydraulic.instance.cleanUpNetworks();
	}
}
