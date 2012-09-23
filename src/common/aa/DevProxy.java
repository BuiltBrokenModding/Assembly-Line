package aa;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
import basicpipes.BasicPipesMain;
import basicpipes.pipes.TileEntityPipe;
import basicpipes.pumps.TileEntityPump;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class DevProxy implements IGuiHandler
{
	@SideOnly(Side.CLIENT)
	public void renders()
	{
		MinecraftForgeClient.preloadTexture("textures/Devblocks.png");
	}
	public void preInit()
	{ 
		renders();
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