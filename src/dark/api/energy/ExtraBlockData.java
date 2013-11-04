package dark.api.energy;

import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import com.builtbroken.common.Pair;
import com.builtbroken.common.science.ChemElement;

/** Information about blocks not provided by minecraft such as density, mass, volume, heating values,
 * chemical properties, etc etc
 *
 * @author DarkGuardsman */
public class ExtraBlockData
{
    private HashMap<Pair<Integer, Integer>, HeatEnergyData> blockTempature = new HashMap();
    /** Map of blocks that can directly be linked to an element. This will be rare as most blocks are
     * not one element type. */
    private HashMap<Pair<Integer, Integer>, ChemElement> blockElement = new HashMap();
    /** Very basic list of default temp of blocks */
    private HashMap<Pair<Integer, Integer>, Integer> blockTempDefaults = new HashMap();

    private static ExtraBlockData instance;

    static
    {

    }

    public static ExtraBlockData instance()
    {
        if (instance == null)
        {
            instance = new ExtraBlockData();
        }
        return instance;
    }

    /** Returns a temp at the given location in kelvin
     *
     * @param world
     * @param vec
     * @return */
    public static double getTempature(World world, Vector3 vec)
    {
        if (world != null && vec != null)
        {
            int blockID = vec.getBlockID(world);
            int meta = vec.getBlockMetadata(world);
            TileEntity entity = vec.getTileEntity(world);
        }
        return 270;
    }

    public static class HeatEnergyData
    {
        public float maxTempature;
        public float defaultTempature;
    }

    public void onWorldUpdate()
    {
        /*TODO tap into the world update to allow a slow recalculation of heat level per location.
         *  Remember to keep CPU time very low for this as it should be passive letting machines
         *  control the heat level rather than the world. Though a small amount of the machines heat
         *  output can be managed by this class
        */
    }

    public void loadMap()
    {

    }

    public void saveMap()
    {
        //TODO tap into the chunk manager to save an extra layer of date for heat level for each block.
    }
}
