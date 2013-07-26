package dark.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;

public class SaveManager
{
    public static List<INbtSave> nbtSaveList = new ArrayList<INbtSave>();

    public static boolean isInitialized = false;

    public static SaveManager intance = new SaveManager();

    /** registers a class that uses INbtSave to save data to a file in the worldSave file
     * 
     * @param saveClass */
    public void registerNbtSave(INbtSave saveClass)
    {
        if (!isInitialized)
        {
            MinecraftForge.EVENT_BUS.register(this);
            isInitialized = true;
        }

        if (saveClass != null && !nbtSaveList.contains(saveClass))
        {
            nbtSaveList.add(saveClass);
        }
    }

    /** Called to get all INbtSave classes to do a save to world */
    public static void save(boolean isServer)
    {
        for (INbtSave save : nbtSaveList)
        {
            if (save.shouldSave(isServer))
            {
                NBTFileLoader.saveData(save.saveFileName(), save.getSaveData());
            }
        }
    }
}
