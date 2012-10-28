package assemblyline;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import asmline.GUIEjectorSettings;
import assemblyline.interaction.ContainerEjector;
import assemblyline.interaction.TileEntityEjector;
import cpw.mods.fml.common.network.IGuiHandler;

public class ALProxy implements IGuiHandler
{
	
	public void preInit()
	{ 
    	
	}
	public void init()
	{
		
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
				case 0: return new GUIEjectorSettings(player.inventory, ((TileEntityEjector)tileEntity));
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
				case 0: return new ContainerEjector(player.inventory, ((TileEntityEjector)tileEntity));
			}
        }
		
		return null;
	}
}
