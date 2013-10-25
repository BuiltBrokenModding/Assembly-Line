package dark.api.energy;

import java.util.HashMap;

import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import com.builtbroken.common.Pair;

/** Information about blocks not provided by minecraft such as density, mass, volume, heating values,
 * chemical properties, etc etc
 * 
 * @author DarkGuardsman */
public class ExtraBlockData
{
    HashMap<Pair<Integer, Integer>, HeatEnergyData> blockTempature = new HashMap();

    public static double getTempature(World world, Vector3 vec)
    {
        return 0;
    }

    public static class HeatEnergyData
    {
        public float maxTempature;
        public float defaultTempature;
    }
}
