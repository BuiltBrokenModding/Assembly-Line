package EUIClient.SteamPower;


import EUI.SteamPower.SteamProxy;
import EUI.SteamPower.TileEntityNuller;
import EUI.SteamPower.boiler.TileEntityBoiler;
import EUI.SteamPower.burner.TileEntityFireBox;
import EUI.SteamPower.turbine.TileEntityGenerator;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.RenderBiped;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class SteamClientProxy extends SteamProxy{

	public void preInit()
	{ 
		 MinecraftForgeClient.preloadTexture("/EUIClient/textures/blocks.png");
	   	 MinecraftForgeClient.preloadTexture("/EUIClient/textures/Items.png");
	}
	@Override
	public void init()
	{
		ClientRegistry.registerTileEntity(TileEntityBoiler.class, "boiler", new RenderBoiler(0f));
		ClientRegistry.registerTileEntity(TileEntityFireBox.class, "fireBox", new RenderFurnace());
		ClientRegistry.registerTileEntity(TileEntityGenerator.class, "generator", new RenderSteamEngine());
	}
	public void postInit()
	{
		
	}
	
}
