package hydraulic.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeVersion;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

public class Hydraulics
{
	/**
	 * The version of the Universal Electricity API.
	 */
	public static final int MAJOR_VERSION = 0;
	public static final int MINOR_VERSION = 0;
	public static final int REVISION_VERSION = 1;
	public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION;

	/**
	 * The Universal Electricity configuration file.
	 */
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "hydraulics/hydraulics.cfg"));
	
	

	/**
	 * A list of all mods Universal Electricity has loaded.
	 */
	public static final List<Object> mods = new ArrayList<Object>();

	/**
	 * You must register your mod with Universal Electricity. Call this in your mod's
	 * pre-initialization stage.
	 */
	public static void register(Object mod, int major, int minor, int revision, boolean strict)
	{
		if (MAJOR_VERSION != major)
		{
			throw new RuntimeException("A Hydraulics mod " + mod.getClass().getSimpleName() + " is way too old! Make sure it is update to v" + major + "." + minor + "." + revision);
		}

		if (MINOR_VERSION < minor)
		{
			throw new RuntimeException("A Hydraulics mod " + mod.getClass().getSimpleName() + " is too old! Make sure it is update to v" + major + "." + minor + "." + revision);
		}

		if (REVISION_VERSION < revision)
		{
			if (strict)
			{
				throw new RuntimeException("A Hydraulics mod " + mod.getClass().getSimpleName() + " is too old! Require v" + major + "." + minor + "." + revision);
			}
			else
			{
				FMLLog.warning("The version of Hydraulics detected is not the recommended version by the mod " + mod.getClass().getSimpleName() + ". Odd things might happen. Recommended to try v" + major + "." + minor + "." + revision);
			}
		}

		mods.add(mod);

		FMLLog.fine(mod.getClass().getSimpleName() + " has been registered to Hydraulics.");

		HydraulicLoader.INSTANCE.initiate();
	}

	
}
