package dark.core.prefab;

import java.util.Calendar;
import java.util.Date;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import org.modstats.Modstats;

import universalelectricity.compatibility.Compatibility;
import universalelectricity.core.UniversalElectricity;

import com.builtbroken.common.Triple;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import dark.api.save.SaveManager;
import dark.core.ModObjectRegistry;
import dark.core.prefab.fluids.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkUpdateHandler;
import dark.machines.common.ExternalModHandler;
import dark.machines.common.PlayerKeyHandler;

public abstract class ModPrefab
{

    public String DOMAIN = this.getDomain();
    public String PREFIX = DOMAIN + ":";

    public String DIRECTORY_NO_SLASH = "assets/" + DOMAIN + "/";
    public String DIRECTORY = "/" + DIRECTORY_NO_SLASH;
    public String LANGUAGE_PATH = DIRECTORY + "languages/";
    public String SOUND_PATH = DIRECTORY + "audio/";

    public static final String TEXTURE_DIRECTORY = "textures/";
    public static final String BLOCK_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
    public static final String ITEM_DIRECTORY = TEXTURE_DIRECTORY + "items/";
    public static final String MODEL_DIRECTORY = TEXTURE_DIRECTORY + "models/";
    public static final String GUI_DIRECTORY = TEXTURE_DIRECTORY + "gui/";

    /* START IDS */
    public static int BLOCK_ID_PRE = 3100;
    public static int ITEM_ID_PREFIX = 13200;

    private static Triple<Integer, Integer, Integer> date;

    private static boolean preInit, init, postInit;

    public abstract String getDomain();

    /** Gets the next unused ID in the block list. Does not prevent config file issues after the file
     * has been made */
    public static int getNextID()
    {
        int id = BLOCK_ID_PRE;

        while (id > 255 && id < (Block.blocksList.length - 1))
        {
            Block block = Block.blocksList[id];
            if (block == null)
            {
                break;
            }
            id++;
        }
        BLOCK_ID_PRE = id + 1;
        return id;
    }

    /** Gets the next unused ID in the item list. Does not prevent config file issues after the file
     * has been made */
    public static int getNextItemId()
    {
        int id = ITEM_ID_PREFIX;

        while (id > 255 && id < (Item.itemsList.length - 1))
        {
            Item item = Item.itemsList[id];
            if (item == null)
            {
                break;
            }
            id++;
        }
        ITEM_ID_PREFIX = id + 1;
        return id;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        this.loadModMeta();

        Modstats.instance().getReporter().registerMod(this);
        MinecraftForge.EVENT_BUS.register(this);
        if (!preInit)
        {
            MinecraftForge.EVENT_BUS.register(new FluidHelper());
            MinecraftForge.EVENT_BUS.register(SaveManager.instance());
            MinecraftForge.EVENT_BUS.register(new LaserEntityDamageSource(null));
            TickRegistry.registerTickHandler(NetworkUpdateHandler.instance(), Side.SERVER);
            TickRegistry.registerScheduledTickHandler(new PlayerKeyHandler(), Side.CLIENT);
            UniversalElectricity.initiate();
            Compatibility.initiate();
            preInit = true;
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (!init)
        {
            ExternalModHandler.init();
            ModObjectRegistry.masterBlockConfig.load();
            init = true;
        }
        this.registerObjects();

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (!postInit)
        {
            ModObjectRegistry.masterBlockConfig.save();
            postInit = true;
        }
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
