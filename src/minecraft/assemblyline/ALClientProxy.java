package assemblyline;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
import assemblyline.belts.TileEntityConveyorBelt;
import assemblyline.gui.GuiSorter;
import assemblyline.machines.TileEntityManipulator;
import assemblyline.machines.TileEntityRejector;
import assemblyline.render.RenderConveyorBelt;
import assemblyline.render.RenderHelper;
import assemblyline.render.RenderManipulator;
import assemblyline.render.RenderSorter;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ALClientProxy extends ALCommonProxy
{
	@Override
	public void preInit()
	{
		MinecraftForgeClient.preloadTexture(AssemblyLine.TEXTURE_PATH + "/Items.png");
		RenderingRegistry.registerBlockHandler(new RenderHelper());
	}

	@Override
	public void init()
	{
		// ClientRegistry.registerTileEntity(TileEntityConveyorBelt.class,
		// "belt", new RenderConveyorBelt());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorBelt.class, new RenderConveyorBelt());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRejector.class, new RenderSorter());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityManipulator.class, new RenderManipulator());
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
					return new GuiSorter(player.inventory, ((TileEntityRejector) tileEntity));
				case GUI_ARCHITECHT_TABLE:
					return null;
			}
		}

		return null;
	}

}
