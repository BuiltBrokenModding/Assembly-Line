package assemblyline.client;

import java.awt.Color;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.core.vector.Vector3;
import assemblyline.client.gui.GuiEncoder;
import assemblyline.client.gui.GuiImprinter;
import assemblyline.client.render.BlockRenderingHandler;
import assemblyline.client.render.RenderArmbot;
import assemblyline.client.render.RenderConveyorBelt;
import assemblyline.client.render.RenderCraneController;
import assemblyline.client.render.RenderCraneFrame;
import assemblyline.client.render.RenderCrate;
import assemblyline.client.render.RenderDetector;
import assemblyline.client.render.RenderManipulator;
import assemblyline.client.render.RenderRejector;
import assemblyline.common.CommonProxy;
import assemblyline.common.armbot.TileEntityArmbot;
import assemblyline.common.block.TileEntityCrate;
import assemblyline.common.imprinter.TileEntityImprinter;
import assemblyline.common.machine.TileEntityManipulator;
import assemblyline.common.machine.TileEntityRejector;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;
import assemblyline.common.machine.crane.TileEntityCraneController;
import assemblyline.common.machine.crane.TileEntityCraneRail;
import assemblyline.common.machine.detector.TileEntityDetector;
import assemblyline.common.machine.encoder.TileEntityEncoder;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import dark.core.DarkMain;
import dark.library.effects.FXBeam;

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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDetector.class, new RenderDetector());
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
	
	/**
	 * Renders a laser beam from one power to another by a set color for a set time
	 * 
	 * @param world - world this laser is to be rendered in
	 * @param position - start vector3
	 * @param target - end vector3
	 * @param color - color of the beam
	 * @param age - life of the beam in 1/20 secs
	 */
	public void renderBeam(World world, Vector3 position, Vector3 target, Color color, int age)
	{
		if (world.isRemote || FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXBeam(world, position, target, color, DarkMain.TEXTURE_DIRECTORY + "", age));
		}
	}
}
