package dark.core;

import net.minecraft.tileentity.TileEntity;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import cpw.mods.fml.common.Loader;

/** Handles working with other mod without or without the need of the APIs.
 * 
 * @author DarkGuardsman */
public class ExternalModHandler
{
    private static boolean init = false;

    /** Calls this to load all external mod settings and handling */
    public static void init()
    {
        if (!init)
        {
            for (MOD_ID mod : MOD_ID.values())
            {
                mod.loaded = Loader.isModLoaded(mod.modID);
            }
        }
    }

    /** Is the tileEntity an instanceof IPipeTile and of type fluid from BuildCraft */
    public static boolean isBCFluidPipe(TileEntity entity)
    {
        return MOD_ID.BUILCRAFT_TRANSPORT_MOD.loaded && entity instanceof IPipeTile && ((IPipeTile) entity).getPipeType() == PipeType.FLUID;
    }

    /** Is the tileEntity an instanceof IPipeTile and of type power from BuildCraft */
    public static boolean isBCPowerPipe(TileEntity entity)
    {
        return MOD_ID.BUILCRAFT_TRANSPORT_MOD.loaded && entity instanceof IPipeTile && ((IPipeTile) entity).getPipeType() == PipeType.POWER;
    }

    /** Checks to see if something can run powerless based on mods loaded
     * 
     * @param optional - power system that the device can use
     * @return true if free power is to be generated */
    public static boolean runPowerLess()
    {
        for (MOD_ID mod : MOD_ID.values())
        {
            if (mod.validPowerSystem)
            {
                return false;
            }
        }
        return true;
    }

    /** Enum storing MOD_IDs and some general info on mods */
    public static enum MOD_ID
    {
        BUILCRAFT_MOD("BuildCraft|Core", false),
        BUILCRAFT_ENERGY_MOD("BuildCraft|Energy", true),
        BUILCRAFT_FACTORY_MOD("BuildCraft|Factory", false),
        BUILCRAFT_SILICON_MOD("BuildCraft|Silicon", false),
        BUILCRAFT_BUILDERS_MOD("BuildCraft|Builders", false),
        BUILCRAFT_TRANSPORT_MOD("BuildCraft|Transport", false),
        INDUSTRIALCRAFT_MOD("IC2", true),
        MEKANISM_MOD("Mekanism", true); //TODO add mek sub mods

        public final String modID;
        public String modNAME;
        public boolean loaded;
        public boolean validPowerSystem;

        private MOD_ID(String modID, boolean power)
        {
            this.modID = modID;
            this.validPowerSystem = power;
        }
    }

}
