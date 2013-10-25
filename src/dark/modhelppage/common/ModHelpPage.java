package dark.modhelppage.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ModHelpPage
{
    private static HashMap<String, HelpPageInfo> itemMap = new HashMap();
    private static boolean init = false;

    /** Call this to setup mod page helper for the mod
     * 
     * @param modID - mod's id used to track it, and register all xmls too
     * @param configFolder - location of the mods config folder for storing player crated xmls */
    public static void init(String modID, String configFolder)
    {
        if (!init)
        {
            Map<Integer, ItemData> idMap = ReflectionHelper.getPrivateValue(GameData.class, new GameData(), "idMap");
            if (idMap != null)
            {
                for (Entry<Integer, ItemData> entry : idMap.entrySet())
                {
                    itemMap.put(entry.getValue().getItemType(), new HelpPageInfo(entry.getValue()));
                }
            }
        }
    }

    /** Call this to load an xmlFile from within the mod package
     * 
     * @param modID - mod's id used to track it, and register all xmls too
     * @param xmlFile - path to the file */
    public static void load(String modID, String xmlFile)
    {

    }
}
