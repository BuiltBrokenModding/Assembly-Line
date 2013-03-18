package assemblyline.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import assemblyline.client.gui.GuiEncoder;
import assemblyline.client.gui.GuiImprinter;
import assemblyline.client.render.BlockRenderingHandler;
import assemblyline.client.render.RenderArmbot;
import assemblyline.client.render.RenderConveyorBelt;
import assemblyline.client.render.RenderCraneController;
import assemblyline.client.render.RenderCraneFrame;
import assemblyline.client.render.RenderCrate;
import assemblyline.client.render.RenderManipulator;
import assemblyline.client.render.RenderRejector;
import assemblyline.common.AssemblyLine;
import assemblyline.common.CommonProxy;
import assemblyline.common.block.TileEntityCrate;
import assemblyline.common.machine.TileEntityManipulator;
import assemblyline.common.machine.TileEntityRejector;
import assemblyline.common.machine.armbot.TileEntityArmbot;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;
import assemblyline.common.machine.crane.TileEntityCraneController;
import assemblyline.common.machine.crane.TileEntityCraneRail;
import assemblyline.common.machine.encoder.TileEntityEncoder;
import assemblyline.common.machine.imprinter.TileEntityImprinter;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit()
	{
		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
		MinecraftForge.EVENT_BUS.register(SoundHandler.INSTANCE);
	}

	@Override
	public void init()
	{
		super.init();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorBelt.class, new RenderConveyorBelt());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRejector.class, new RenderRejector());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityManipulator.class, new RenderManipulator());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrate.class, new RenderCrate());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmbot.class, new RenderArmbot());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraneController.class, new RenderCraneController());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraneRail.class, new RenderCraneFrame());
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			switch (ID)
			{
				case GUI_IMPRINTER:
					return new GuiImprinter(player.inventory, (TileEntityImprinter) tileEntity);
				case GUI_ENCODER:
					if (tileEntity != null && tileEntity instanceof TileEntityEncoder)
						return new GuiEncoder(player.inventory, (TileEntityEncoder) tileEntity);
			}
		}

		return null;
	}

	@Override
	public boolean isCtrKeyDown()
	{
		return GuiScreen.isCtrlKeyDown();
	}
}
