package assemblyline.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import universalelectricity.core.vector.Vector3;
import assemblyline.client.gui.GuiRejector;
import assemblyline.client.gui.GuiStamper;
import assemblyline.client.render.BlockRenderingHandler;
import assemblyline.client.render.RenderConveyorBelt;
import assemblyline.client.render.RenderCrate;
import assemblyline.client.render.RenderManipulator;
import assemblyline.client.render.RenderSorter;
import assemblyline.common.AssemblyLine;
import assemblyline.common.CommonProxy;
import assemblyline.common.block.TileEntityCrate;
import assemblyline.common.machine.TileEntityManipulator;
import assemblyline.common.machine.TileEntityRejector;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit()
	{
		MinecraftForgeClient.preloadTexture(AssemblyLine.BLOCK_TEXTURE_PATH);
		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
	}

	@Override
	public void init()
	{
		super.init();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorBelt.class, new RenderConveyorBelt());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRejector.class, new RenderSorter());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityManipulator.class, new RenderManipulator());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrate.class, new RenderCrate());
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		switch (ID)
		{
			case GUI_REJECTOR:
				return new GuiRejector(player.inventory, ((TileEntityRejector) tileEntity));
			case GUI_STAMPER:
				return new GuiStamper(player.inventory, world, new Vector3(x, y, z));
		}

		return null;
	}

	@Override
	public boolean isCtrKeyDown()
	{
		return GuiScreen.isCtrlKeyDown();
	}
}
