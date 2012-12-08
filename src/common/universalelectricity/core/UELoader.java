package universalelectricity.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Unload;
import universalelectricity.core.electricity.Electricity;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

/**
 * A class used to load Universal Electricity and make it work.
 * 
 * @author Calclavia
 * 
 */
public class UELoader
{
	public static final UELoader INSTANCE = new UELoader();

	public static boolean isInitialized = false;

	public void initiate()
	{
		if (!isInitialized)
		{
			Electricity.instance = new Electricity();
			MinecraftForge.EVENT_BUS.register(this);

			if (UniversalElectricity.BC3_RATIO <= 0 || !Loader.isModLoaded("BuildCraft|Core"))
			{
				FMLLog.fine("Disabled Buildcraft electricity conversion!");
			}
			else
			{
				FMLLog.fine("Buildcraft conversion ratio: " + UniversalElectricity.BC3_RATIO);
			}

			if (UniversalElectricity.IC2_RATIO <= 0 || !Loader.isModLoaded("IC2"))
			{
				FMLLog.fine("Disabled Industrialcraft electricity conversion!");
			}
			else
			{
				FMLLog.fine("IC2 conversion ratio: " + UniversalElectricity.IC2_RATIO);
			}

			FMLLog.finest("Universal Electricity v" + UniversalElectricity.VERSION + " successfully loaded!");

			UniversalElectricity.IC2_RATIO = UniversalElectricity.CONFIGURATION.get("Compatiblity", "IndustrialCraft Conversion Ratio", UniversalElectricity.IC2_RATIO).getInt();
			UniversalElectricity.BC3_RATIO = UniversalElectricity.CONFIGURATION.get("Compatiblity", "BuildCraft Conversion Ratio", UniversalElectricity.BC3_RATIO).getInt();
			UniversalElectricity.TO_IC2_RATIO = 1 / UniversalElectricity.IC2_RATIO;
			UniversalElectricity.TO_BC_RATIO = 1 / UniversalElectricity.BC3_RATIO;

			isInitialized = true;
		}
	}

	@ForgeSubscribe
	public void onWorldUnload(Unload event)
	{
		Electricity.instance = new Electricity();
	}
}
