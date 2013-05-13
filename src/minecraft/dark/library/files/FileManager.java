package dark.library.files;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

public class FileManager
{
	public static final String directory = (Loader.instance().getConfigDir().toString() + File.separator + "Dark");
	public static final String langFile = directory + File.separator + "language";
	public static final String npc = directory + File.separator + "npc";

	/**
	 * Gets all files in a directory
	 */
	public static File[] ListFilesInDir(String dir)
	{
		File folder = new File(dir);

		if (!folder.exists())
		{
			folder.mkdirs();
		}

		File[] listOfFiles = folder.listFiles();

		return listOfFiles;
	}
	/**
	 * Loads language files 
	 */
	public static int loadLangFiles(File[] files)
	{
		int unofficialLanguages = 0;
		
		try
		{
			for (File langFile : files)
			{
				if (langFile.exists())
				{
					String name = langFile.getName();
					if (name.endsWith(".lang"))
					{
						String lang = name.substring(0, name.length() - 4);
						LanguageRegistry.instance().loadLocalization(langFile.toString(), lang, false);
						unofficialLanguages++;
					}
				}
			}
		}
		catch (Exception e)
		{
			// the folder is likely empty, so what...
		}
		
		return unofficialLanguages;
	}
}
