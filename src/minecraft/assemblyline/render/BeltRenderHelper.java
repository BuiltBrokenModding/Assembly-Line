package assemblyline.render;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;

import org.lwjgl.opengl.GL11;

import assemblyline.AssemblyLine;
import assemblyline.ModelEjector;
import assemblyline.belts.TileEntityConveyorBelt;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BeltRenderHelper implements ISimpleBlockRenderingHandler {
	public static BeltRenderHelper instance = new BeltRenderHelper();
	 public static int blockRenderId = RenderingRegistry.getNextAvailableRenderId();
	 private static TileEntityConveyorBelt belt = null;
	 private ModelConveyorBelt modelBelt = new ModelConveyorBelt();
	 private ModelEjector modelEj = new ModelEjector();
	 @Override
	 public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		 if(block.blockID == assemblyline.AssemblyLine.beltBlockID)
		 {
			 //TileEntityRenderer.instance.renderTileEntityAt(belt, -0.5D, 0.0D, -0.5D, 0.0F);
			 	GL11.glPushMatrix();
				GL11.glTranslatef((float) 0.0F, (float)1.5F, (float)0.0F);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture("/textures/BeltTexture.png"));				
				//bindTextureByName("/textures/BeltSingle.png");
				modelBelt.render(0.0625F,0, false,false,false);
				GL11.glPopMatrix();
		 }
		 if(block.blockID == assemblyline.AssemblyLine.machineID && metadata == 0)
		 {
			 GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture("/textures/Ejector.png"));
				GL11.glPushMatrix();
				GL11.glTranslatef((float) 0.6F, (float)1.5F, (float)0.6F);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glRotatef(-90f, 0f, 1f, 0f);
				modelEj.renderMain(0.0625F);
				modelEj.renderPiston(0.0625F, 1);
				GL11.glPopMatrix();
		 }
	 }
	 public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return false;
	 }
	 
	 public boolean shouldRender3DInInventory() {
		 
	  return true;
	 }
	 
	 public int getRenderId() 
	 {
	  return blockRenderId;
	 }
}
