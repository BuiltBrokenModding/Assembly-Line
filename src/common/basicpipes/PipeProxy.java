package basicpipes;

import basicpipes.pipes.TileEntityPipe;
import basicpipes.pipes.TileEntityPump;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class PipeProxy implements IGuiHandler
{
	
	public void preInit()
	{ 
    	
	}
	public void init()
	{
		GameRegistry.registerTileEntity(TileEntityPipe.class, "pipe");
		GameRegistry.registerTileEntity(TileEntityPump.class, "pump");
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
			}
        }
		
		return null;
	}
}
