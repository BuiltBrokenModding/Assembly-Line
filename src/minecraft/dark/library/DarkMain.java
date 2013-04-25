package dark.library;

import java.awt.Color;

import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.SidedProxy;
import dark.library.effects.FXBeam;

public class DarkMain
{
	/* RESOURCE FILE PATHS */
	public static final String RESOURCE_PATH = "/mods/dark/";
	public static final String TEXTURE_DIRECTORY = RESOURCE_PATH + "textures/";
	public static final String GUI_DIRECTORY = TEXTURE_DIRECTORY + "gui/";
	public static final String BLOCK_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
	public static final String ITEM_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "items/";
	public static final String MODEL_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "models/";
	public static final String TEXTURE_NAME_PREFIX = "dark:";
	
	
	public static void renderBeam(World world, Vector3 position, Vector3 target, Color color, int age)
	{
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXBeam(world, position, target, color.getRed(), color.getGreen(), color.getBlue(), age));
	}
	
	@SidedProxy(clientSide = "dark.library.ClientProxy", serverSide = "dark.library..CommonProxy")
	public static CommonProxy proxy;
	
	public void init()
	{
		proxy.preInit();		
	}
}
