package hydraulic.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import universalelectricity.core.electricity.Electricity;
import cpw.mods.fml.common.FMLLog;


public class HydraulicLoader
{
	public static final HydraulicLoader INSTANCE = new HydraulicLoader();

	public static boolean isInitialized = false;

	public void initiate()
	{
		if (!isInitialized)
		{
			Electricity.instance = new Electricity();
			MinecraftForge.EVENT_BUS.register(this);			

			FMLLog.finest("Universal Electricity v" + UniversalElectricity.VERSION + " successfully loaded!");

			isInitialized = true;
		}
	}

	@ForgeSubscribe
	public void onWorldUnLoad(WorldEvent.Unload event)
	{
		Electricity.instance = new Electricity();
		Electricity.instance.cleanUpNetworks();
	}

	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load event)
	{
		Electricity.instance = new Electricity();
		Electricity.instance.cleanUpNetworks();
	}
}
