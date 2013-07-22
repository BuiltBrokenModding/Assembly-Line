package dark.assembly.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.client.model.ModelConveyorBelt;
import dark.assembly.client.model.ModelManipulator;
import dark.assembly.client.model.ModelRejectorPiston;
import dark.assembly.common.AssemblyLine;

@SideOnly(Side.CLIENT)
public class BlockRenderingHandler implements ISimpleBlockRenderingHandler
{
	public static BlockRenderingHandler instance = new BlockRenderingHandler();
	public static final int BLOCK_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	private ModelConveyorBelt modelConveyorBelt = new ModelConveyorBelt();
	private ModelRejectorPiston modelEjector = new ModelRejectorPiston();
	private ModelManipulator modelInjector = new ModelManipulator();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		if (block.blockID == AssemblyLine.recipeLoader.blockConveyorBelt.blockID)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef((float) 0.0F, (float) 1.5F, (float) 0.0F);
			GL11.glRotatef(180f, 0f, 0f, 1f);
			FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + "belt/frame0.png"));
			modelConveyorBelt.render(0.0625F, 0, false, false, false, false);
			GL11.glPopMatrix();
		}
		else if (block.blockID == AssemblyLine.recipeLoader.blockRejector.blockID)
		{
			FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + "rejector.png"));
			GL11.glPushMatrix();
			GL11.glTranslatef((float) 0.6F, (float) 1.5F, (float) 0.6F);
			GL11.glRotatef(180f, 0f, 0f, 1f);
			GL11.glRotatef(-90f, 0f, 1f, 0f);
			modelEjector.render(0.0625F);
			modelEjector.renderPiston(0.0625F, 1);
			GL11.glPopMatrix();
		}
		else if (block.blockID == AssemblyLine.recipeLoader.blockManipulator.blockID)
		{
			FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + "manipulator1.png"));
			GL11.glPushMatrix();
			GL11.glTranslatef((float) 0.6F, (float) 1.5F, (float) 0.6F);
			GL11.glRotatef(180f, 0f, 0f, 1f);
			GL11.glRotatef(-90f, 0f, 1f, 0f);
			modelInjector.render(0.0625F, true, 0);
			GL11.glPopMatrix();
		}
		else if (block.blockID == AssemblyLine.recipeLoader.blockArmbot.blockID)
		{
			FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + RenderArmbot.TEXTURE));
			GL11.glPushMatrix();
			GL11.glTranslatef(0.4f, 0.8f, 0f);
			GL11.glScalef(0.7f, 0.7f, 0.7f);
			GL11.glRotatef(180f, 0f, 0f, 1f);
			GL11.glRotatef(-90f, 0f, 1f, 0f);
			RenderArmbot.MODEL.render(0.0625F, 0, 0);
			GL11.glPopMatrix();
		}
		else if (block.blockID == AssemblyLine.recipeLoader.blockCraneController.blockID)
		{
			FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + RenderCraneController.TEXTURE));
			GL11.glPushMatrix();
			GL11.glTranslatef(0f, 1f, 0f);
			GL11.glRotatef(180f, 0f, 0f, 1f);
			GL11.glRotatef(-90f, 0f, 1f, 0f);
			RenderCraneController.MODEL.render(0.0625f, false, false);
			GL11.glPopMatrix();
		}
		else if (block.blockID == AssemblyLine.recipeLoader.blockCraneFrame.blockID)
		{
			FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + RenderCraneFrame.TEXTURE));
			GL11.glPushMatrix();
			GL11.glTranslatef(0f, 1f, 0f);
			GL11.glRotatef(180f, 0f, 0f, 1f);
			GL11.glRotatef(-90f, 0f, 1f, 0f);
			RenderCraneFrame.MODEL.render(true, true, false, false, false, false, false);
			GL11.glPopMatrix();
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
