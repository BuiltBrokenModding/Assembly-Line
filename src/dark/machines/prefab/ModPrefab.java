package dark.machines.prefab;

import java.util.Calendar;
import java.util.Date;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import org.modstats.Modstats;

import com.builtbroken.common.Triple;
import com.builtbroken.minecraft.DarkCore;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public abstract class ModPrefab
{

    public String DOMAIN = this.getDomain();
    public String PREFIX = DOMAIN + ":";

   

    private static Triple<Integer, Integer, Integer> date;

    private static boolean preInit, init, postInit;

    public abstract String getDomain();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        this.loadModMeta();
        Modstats.instance().getReporter().registerMod(this);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new LaserEntityDamageSource(null));
        DarkCore.instance().preLoad();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        DarkCore.instance().Load();
        this.registerObjects();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        DarkCore.instance().postLoad();
        this.loadRecipes();
    }

    public static void printSidedData(String data)
    {
        System.out.print(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? "[C]" : "[S]");
        System.out.println(" " + data);
    }

    @SuppressWarnings("deprecation")
    public static Triple<Integer, Integer, Integer> getDate()
    {
        Calendar cal = Calendar.getInstance();
        Date d = cal.getTime();

        if (date == null || date.getB() != d.getDay())
        {
            date = new Triple<Integer, Integer, Integer>(d.getMonth(), d.getDay(), d.getYear());
        }
        return date;
    }

    public static boolean isOp(String username)
    {
        MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (theServer != null)
        {
            return theServer.getConfigurationManager().getOps().contains(username.trim().toLowerCase());
        }

        return false;
    }

    /** Loads the settings that tell what this mod is named, about, and other info to the user */
    public abstract void loadModMeta();

    /** Tells the mod to start registering its items and blocks */
    public abstract void registerObjects();

    /** Tells the mod to start registering its recipes */
    public abstract void loadRecipes();

}
