package EUI.SteamPower;

import universalelectricity.basiccomponents.GUIBatteryBox;
import universalelectricity.basiccomponents.TileEntityBatteryBox;
import EUI.SteamPower.boiler.ContainerBoiler;
import EUI.SteamPower.boiler.TileEntityBoiler;
import EUI.SteamPower.burner.ContainerFireBox;
import EUI.SteamPower.burner.TileEntityFireBox;
import EUI.SteamPower.turbine.ContainerGenerator;
import EUI.SteamPower.turbine.TileEntityGenerator;
import EUIClient.SteamPower.GUIFireBox;
import EUIClient.SteamPower.GUIGenerator;
import EUIClient.SteamPower.GuiBoiler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class SteamProxy implements IGuiHandler{

	public void preInit()
	{ 
    	
	}
	public void init()
	{
		GameRegistry.registerTileEntity(TileEntityBoiler.class, "boiler");
		GameRegistry.registerTileEntity(TileEntityFireBox.class, "fireBox");
		GameRegistry.registerTileEntity(TileEntityGenerator.class, "generator");
		
	}
	public void postInit()
	{
		
	}
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		
		if (tileEntity != null)
        {
			switch(ID)
			{
			case 0: return new GUIFireBox(player.inventory, ((TileEntityFireBox)tileEntity));
			case 1: return new GuiBoiler(player.inventory, ((TileEntityBoiler)tileEntity));
			case 2: return new GUIGenerator(player.inventory, ((TileEntityGenerator)tileEntity));
			}
        }
		
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		
		if (tileEntity != null)
        {
			switch(ID)
			{
			case 0: return new ContainerFireBox(player.inventory, ((TileEntityFireBox)tileEntity));
			case 1: return new ContainerBoiler(player.inventory, ((TileEntityBoiler)tileEntity));
			case 2: return new ContainerGenerator(player.inventory, ((TileEntityGenerator)tileEntity));
			}
        }
		
		return null;
	}
}
