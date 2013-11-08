package dark.core.prefab.tilenetwork;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import dark.api.parts.ITileNetwork;

/** Manages all the tile networks making sure they get world save events, and updates every so often
 * 
 * @author DarkGuardsman */
public class NetworkHandler implements IScheduledTickHandler
{
    private static HashMap<String, Class<?>> nameToClassMap = new HashMap<String, Class<?>>();
    private static HashMap<Class<?>, String> classToNameMap = new HashMap<Class<?>, String>();

    private byte count = 0;

    private Set<ITileNetwork> networks = new HashSet();

    private static NetworkHandler instance;

    static
    {
        registerNetworkClass("base", NetworkTileEntities.class);
    }

    public static NetworkHandler instance()
    {
        if (instance == null)
        {
            instance = new NetworkHandler();
        }
        return instance;
    }

    public void registerNetwork(ITileNetwork network)
    {
        if (!networks.contains(network))
        {
            this.networks.add(network);
        }
    }

    public static void registerNetworkClass(String id, Class<?> clazz)
    {
        if (!nameToClassMap.containsKey(id) && !classToNameMap.containsKey(clazz))
        {
            nameToClassMap.put(id, clazz);
            classToNameMap.put(clazz, id);
        }
    }

    public static String getID(Class<?> clazz)
    {
        return classToNameMap.get(clazz);
    }

    public static Class<?> getClazz(String id)
    {
        return nameToClassMap.get(id);
    }

    public static ITileNetwork createNewNetwork(String id)
    {
        Class<?> clazz = getClazz(id);
        if (clazz != null)
        {
            try
            {
                Object object = clazz.newInstance();
                if (object instanceof ITileNetwork)
                {
                    return (ITileNetwork) object;
                }
            }
            catch (Exception e)
            {
                System.out.println("[CoreMachine]TileNetworkHandler: Failed to create a new network object");
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("[CoreMachine]TileNetworkHandler: Unkown id: " + id);
        }

        return null;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (count + 1 >= Byte.MAX_VALUE)
        {
            count = 0;
            for (ITileNetwork network : networks)
            {
                if (!network.isInvalid())
                {
                    network.refreshTick();
                }
            }
        }
        else
        {
            count++;
            for (ITileNetwork network : networks)
            {
                if (!network.isInvalid())
                {
                    network.updateTick();
                }
            }
        }

    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        Iterator<ITileNetwork> it = networks.iterator();
        while (it.hasNext())
        {
            ITileNetwork network = it.next();
            if (network.isInvalid())
            {
                network.invalidate();
                it.remove();
            }
        }

    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel()
    {
        return "[CoreMachine]TileNetworkHandler";
    }

    @Override
    public int nextTickSpacing()
    {
        return 20;
    }
}
