package dark.core;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class DarkCorePlugin implements IFMLLoadingPlugin, IFMLCallHook
{
    public static File minecraftDir;

    public DarkCorePlugin()
    {
        //get called twice, once for IFMLCallHook
        if (minecraftDir != null)
        {
            return;
        }
        minecraftDir = (File) FMLInjectionData.data()[6];
        DepLoader.load();
    }

    @Override
    public Void call() throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getLibraryRequestClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getASMTransformerClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getModContainerClass()
    {
        return "dark.core.DarkCoreModContainer";
    }

    @Override
    public String getSetupClass()
    {
        return getClass().getName();
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        // TODO Auto-generated method stub

    }

}
