package assemblyline.render;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;

import org.lwjgl.opengl.GL11;

import assemblyline.AssemblyLine;
import assemblyline.interaction.BlockInteraction.MachineType;
import assemblyline.model.ModelConveyorBelt;
import assemblyline.model.ModelEjector;
import assemblyline.model.ModelInjector;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderHelper implements ISimpleBlockRenderingHandler
{
	public static RenderHelper instance = new RenderHelper();
	public static final int BLOCK_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	private ModelConveyorBelt modelConveyorBelt = new ModelConveyorBelt();
	private ModelEjector modelEjector = new ModelEjector();
	private ModelInjector modelInjector = new ModelInjector();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		if (block.blockID == AssemblyLine.blockConveyorBelt.blockID)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef((float) 0.0F, (float) 1.5F, (float) 0.0F);
			GL11.glRotatef(180f, 0f, 0f, 1f);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(AssemblyLine.TEXTURE_PATH+"BeltTexture.png"));
			modelConveyorBelt.render(0.0625F, 0, false, false, false);
			GL11.glPopMatrix();
		}
		else if (block.blockID == AssemblyLine.blockInteraction.blockID)
		{
			if(metadata == MachineType.EJECTOR.metadata)
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(AssemblyLine.TEXTURE_PATH+"ejector.png"));
				GL11.glPushMatrix();
				GL11.glTranslatef((float) 0.6F, (float) 1.5F, (float) 0.6F);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glRotatef(-90f, 0f, 1f, 0f);
				modelEjector.renderMain(0.0625F);
				modelEjector.renderPiston(0.0625F, 1);
				GL11.glPopMatrix();
			}
			else if(metadata == MachineType.INJECTOR.metadata)
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(AssemblyLine.TEXTURE_PATH+"injector.png"));
				GL11.glPushMatrix();
				GL11.glTranslatef((float) 0.6F, (float) 1.5F, (float) 0.6F);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glRotatef(-90f, 0f, 1f, 0f);
				modelInjector.render(0.0625F, true, 0);
				GL11.glPopMatrix();
			}
		}
	}

	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		return false;
	}

	public boolean shouldRender3DInInventory()
	{
		return true;
	}

	public int getRenderId()
	{
		return BLOCK_RENDER_ID;
	}
}
