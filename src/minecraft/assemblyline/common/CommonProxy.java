package assemblyline.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import assemblyline.common.block.TileEntityCrate;
import assemblyline.common.machine.TileEntityManipulator;
import assemblyline.common.machine.TileEntityRejector;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;
import assemblyline.common.machine.detector.TileEntityDetector;
import assemblyline.common.machine.filter.ContainerStamper;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IGuiHandler
{
	public static final int GUI_STAMPER = 1;
	public static final int GUI_ARCHITECHT_TABLE = 2;

	public void preInit()
	{

	}

	public void init()
	{
		GameRegistry.registerTileEntity(TileEntityConveyorBelt.class, "ALConveyorBelt");
		GameRegistry.registerTileEntity(TileEntityRejector.class, "ALSorter");
		GameRegistry.registerTileEntity(TileEntityManipulator.class, "ALManipulator");
		GameRegistry.registerTileEntity(TileEntityCrate.class, "ALCrate");
		GameRegistry.registerTileEntity(TileEntityDetector.class, "ALDetector");
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		switch (ID)
		{
			case GUI_STAMPER:
				return new ContainerStamper(player.inventory, world, new Vector3(x, y, z));
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	public boolean isCtrKeyDown()
	{
		return false;
	}
}
