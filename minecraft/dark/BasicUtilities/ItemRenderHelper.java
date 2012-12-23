package dark.BasicUtilities;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dark.BasicUtilities.renders.models.ModelGearRod;
import dark.BasicUtilities.renders.models.ModelGenerator;
import dark.BasicUtilities.renders.models.ModelPump;

public class ItemRenderHelper implements ISimpleBlockRenderingHandler {
	public static ItemRenderHelper instance = new ItemRenderHelper();
	 public static int renderID = RenderingRegistry.getNextAvailableRenderId();
	 private ModelPump modelPump = new ModelPump();
	 private ModelGearRod modelRod = new ModelGearRod();
	 private ModelGenerator modelGen = new ModelGenerator();
	 @Override
	 public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		 if(block.blockID == BasicUtilitiesMain.machine.blockID && metadata < 4)
		 {
			 	GL11.glPushMatrix();
				GL11.glTranslatef((float) 0.0F, (float)1.1F, (float)0.0F);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(BasicUtilitiesMain.textureFile+"pumps/Pump.png"));				
				modelPump.renderMain(0.0725F);
				modelPump.renderC1(0.0725F);
				modelPump.renderC2(0.0725F);
				modelPump.renderC3(0.0725F);
				GL11.glPopMatrix();
		 }
		 if(block.blockID == BasicUtilitiesMain.rod.blockID)
		 {
			 	GL11.glPushMatrix();
				GL11.glTranslatef((float) 0.0F, (float)1.5F, (float)0.0F);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(BasicUtilitiesMain.textureFile+"mechanical/GearRod.png"));				
				modelRod.render(0.0825F,0);
				GL11.glPopMatrix();
		 }
		 if(block.blockID == BasicUtilitiesMain.generator.blockID)
         {
                GL11.glPushMatrix();
                GL11.glTranslatef((float) 0.0F, (float)1.3F, (float)0.0F);
                GL11.glRotatef(180f, 0f, 0f, 1f);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(BasicUtilitiesMain.textureFile+"mechanical/Generator.png"));                
                modelGen.RenderMain(0.0725F);
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
	  return renderID;
	 }
}
