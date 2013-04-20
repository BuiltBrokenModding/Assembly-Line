package dark.library.locking;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import dark.library.saving.INbtSave;
import dark.library.saving.SaveManager;

public class GlobalAccessLoader implements INbtSave
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
			SaveManager.intance.registerNbtSave(this);
			isInitialized = true;
		}
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		if (!GlobalAccessList.hasLoaded)
		{
			GlobalAccessList.getMasterSaveFile();
		}
	}

	@Override
	public String saveFileName()
	{
		return this.SAVE_NAME;
	}

	@Override
	public NBTTagCompound getSaveData()
	{
		return GlobalAccessList.getMasterSaveFile();
	}

	@Override
	public boolean shouldSave(boolean isServer)
	{
		return isServer && GlobalAccessList.hasLoaded && !GlobalAccessList.loading;
	}
}
