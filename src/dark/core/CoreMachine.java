package dark.core;

import com.dark.ExternalModHandler;
import com.dark.ModObjectRegistry;
import com.dark.save.SaveManager;

import net.minecraftforge.common.MinecraftForge;
import universalelectricity.compatibility.Compatibility;
import universalelectricity.core.UniversalElectricity;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import dark.core.prefab.LaserEntityDamageSource;
import dark.core.prefab.fluids.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkUpdateHandler;

public class CoreMachine
{
    private static CoreMachine instance;
    private boolean pre, load, post;
    private boolean loadOres, loadItems, loadItemRecipes;

    public static CoreMachine instance()
    {
        if (instance == null)
        {
            instance = new CoreMachine();
        }
        return instance;
    }

    public void requestItems(boolean recipes)
    {
        this.loadItems = true;
        if (recipes)
            this.loadItemRecipes = recipes;
    }

    public void requestOres()
    {
        this.loadOres = true;
    }

    public void preLoad()
    {
        if (!pre)
        {
            MinecraftForge.EVENT_BUS.register(new FluidHelper());
            MinecraftForge.EVENT_BUS.register(SaveManager.instance());
            MinecraftForge.EVENT_BUS.register(new LaserEntityDamageSource(null));
            TickRegistry.registerTickHandler(NetworkUpdateHandler.instance(), Side.SERVER);
            TickRegistry.registerScheduledTickHandler(new PlayerKeyHandler(), Side.CLIENT);
            UniversalElectricity.initiate();
            Compatibility.initiate();
            pre = true;
        }
    }

    public void Load()
    {
        if (!load)
        {
            ExternalModHandler.init();
            ModObjectRegistry.masterBlockConfig.load();
            load = true;
        }
    }

    public void postLoad()
    {
        if (!post)
        {
            ModObjectRegistry.masterBlockConfig.save();
            post = true;
        }
    }
}
