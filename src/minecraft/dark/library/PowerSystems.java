package dark.library;

import cpw.mods.fml.common.Loader;

public enum PowerSystems
{
	INDUSTRIALCRAFT("IC2"), MEKANISM("Mekanism"), BUILDCRAFT("BuildCraft|Energy");
	public String id;

	private PowerSystems(String id)
	{
		this.id = id;
	}
	
	/**
	 * Checks to see if something can run powerless based on mods loaded
	 * 
	 * @param optional - power system that the device can use
	 * @return true if free power is to be generated
	 */
	public static boolean runPowerLess(PowerSystems... optional)
	{
		for (int i = 0; i < optional.length; i++)
		{
			if (isPowerSystemLoaded(optional[i]))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Check to see if one of the mods listed in the PowerSystem enum is loaded
	 */
	public static boolean isPowerSystemLoaded(PowerSystems power)
	{
		return power != null && Loader.isModLoaded(power.id);
	}
}
