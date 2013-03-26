package hydraulic.core;

import cpw.mods.fml.common.FMLLog;

public class HydraulicLoader
{
	public static final HydraulicLoader INSTANCE = new HydraulicLoader();

	public static boolean isInitialized = false;

	public void initiate()
	{
		if (!isInitialized)
		{
			// MinecraftForge.EVENT_BUS.register(this);

			FMLLog.finest("Hydraulics v" + Hydraulics.VERSION + " loaded without error!");

			isInitialized = true;
		}
	}
}
