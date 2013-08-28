package dark.fluid.common;

import net.minecraftforge.common.Configuration;

public class ExtraConfigHandler
{
    public static int steamPerBucket, heatPerBucket, coalHeatOutput;
    static boolean loaded = false;

    public static void loadSettings(Configuration config)
    {
        if (!loaded)
        {
            loaded = true;
            steamPerBucket = config.get("Boiler", "SteamPerBucket", 10).getInt();
            heatPerBucket = config.get("Boiler", "HeatPerBucket", 4500).getInt();
            coalHeatOutput = config.get("Heater", "CoalHeatMax", 250).getInt();
        }
    }
}
