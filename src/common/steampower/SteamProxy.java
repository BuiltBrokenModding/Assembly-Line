package steampower;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import steampower.boiler.TileEntityBoiler;
import steampower.burner.ContainerFireBox;
import steampower.burner.TileEntityFireBox;
import steampower.turbine.TileEntityGen;
import steampower.turbine.TileEntitySteamPiston;
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
			case 2: return new GUISteamPiston(player.inventory, ((TileEntitySteamPiston)tileEntity));
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
				default: return new ContainerFake(player.inventory, (IInventory) tileEntity);
			}
        }
		
		return null;
	}
}
