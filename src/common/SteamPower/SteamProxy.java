package SteamPower;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import SteamPower.boiler.ContainerBoiler;
import SteamPower.boiler.TileEntityBoiler;
import SteamPower.burner.ContainerFireBox;
import SteamPower.burner.TileEntityFireBox;
import SteamPower.turbine.ContainerGenerator;
import SteamPower.turbine.TileEntityGen;
import SteamPower.turbine.TileEntitySteamPiston;
import SteamPower.turbine.TileEntitytopGen;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class SteamProxy implements IGuiHandler{

	public void preInit()
	{ 
    	
	}
	public void init()
	{
		GameRegistry.registerTileEntity(TileEntityBoiler.class, "boiler");
		GameRegistry.registerTileEntity(TileEntityFireBox.class, "fireBox");
		GameRegistry.registerTileEntity(TileEntitySteamPiston.class, "steamPiston");
		GameRegistry.registerTileEntity(TileEntityGen.class, "elecGen");
		
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
			case 2: return new GUIGenerator(player.inventory, ((TileEntitySteamPiston)tileEntity));
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
			case 2: return new ContainerGenerator(player.inventory, ((TileEntitySteamPiston)tileEntity));
			}
        }
		
		return null;
	}
}
