package dark.SteamPower;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dark.BasicUtilities.BasicUtilitiesMain;
import dark.SteamPower.renders.ModelFurnace;
import dark.SteamPower.renders.ModelGenerator;
import dark.SteamPower.renders.ModelTank;
//ItemRenderHelperS.renderID
public class ItemRenderHelperS implements ISimpleBlockRenderingHandler {
	public static ItemRenderHelperS instance = new ItemRenderHelperS();
	 public static int renderID = RenderingRegistry.getNextAvailableRenderId();
	 private ModelGenerator modelGen = new ModelGenerator();
	 private ModelTank modelTank = new ModelTank(0f);
	 private ModelFurnace modelFurnace = new ModelFurnace();
	 @Override
	 public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		 if(block.blockID == SteamPowerMain.machine.blockID && metadata == 4)
		 {
			 	GL11.glPushMatrix();
				GL11.glTranslatef((float) 0.0F, (float)1F, (float)0.0F);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(SteamPowerMain.textureFile+"tankTexture.png"));				
				modelTank.generalRender(0.0625F);
				GL11.glPopMatrix();
		 }
		 if(block.blockID == SteamPowerMain.machine.blockID && metadata >= 0 && metadata <= 3)
		 {
			 	GL11.glPushMatrix();
				GL11.glTranslatef((float) 0.0F, (float)1F, (float)0.0F);
				GL11.glRotatef(180f, 0f, 0f, 1f);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(SteamPowerMain.textureFile+"Furnace.png"));				
				modelFurnace.genRender(0.0625F);
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
