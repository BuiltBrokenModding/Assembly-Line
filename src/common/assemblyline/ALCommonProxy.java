package assemblyline;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import assemblyline.machine.ContainerSorter;
import assemblyline.machine.TileEntityRejector;
import cpw.mods.fml.common.network.IGuiHandler;

public class ALCommonProxy implements IGuiHandler
{
	public static final int GUI_ARCHITECHT_TABLE = 4;

	public void preInit()
	{

	}

	public void init()
	{

	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			switch (ID)
			{
				case 0:
					return new ContainerSorter(player.inventory, ((TileEntityRejector) tileEntity));
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
}
