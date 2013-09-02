package dark.prefab.access;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class GlobalAccessLoader
{
    public static boolean isInitialized = false;

    public static GlobalAccessLoader intance = new GlobalAccessLoader();

    /** Name of the save file **/
    public static final String SAVE_NAME = "Global_Access_List";

    public void initiate()
    {
        if (!isInitialized)
        {
            MinecraftForge.EVENT_BUS.register(this);
            //SaveManager.intance.registerNbtSave(this);
            isInitialized = true;
        }
    }

    @ServerStarting
    public void serverStarting(FMLServerStartingEvent event)
    {
        if (!GlobalAccessManager.hasLoaded)
        {
            GlobalAccessManager.getMasterSaveFile();
        }
    }

}
