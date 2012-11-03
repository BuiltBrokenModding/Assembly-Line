package assemblyline;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import assemblyline.gui.GuiSorter;
import assemblyline.machines.ContainerSorter;
import assemblyline.machines.TileEntitySorter;
import cpw.mods.fml.common.network.IGuiHandler;

public class ALCommonProxy implements IGuiHandler
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
			switch (ID)
			{
				case 0:
					return new GuiSorter(player.inventory, ((TileEntitySorter) tileEntity));
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
			switch (ID)
			{
				case 0:
					return new ContainerSorter(player.inventory, ((TileEntitySorter) tileEntity));
			}
		}

		return null;
	}
}
