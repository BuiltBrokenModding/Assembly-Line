package dark.mech.steam;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dark.mech.steam.renders.ModelFurnace;
import dark.mech.steam.renders.ModelGenerator;
import dark.mech.steam.renders.ModelTank;

//ItemRenderHelperS.renderID
public class ItemRenderHelperS implements ISimpleBlockRenderingHandler
{
    public static ItemRenderHelperS instance = new ItemRenderHelperS();
    public static int renderID = RenderingRegistry.getNextAvailableRenderId();
    private ModelGenerator modelGen = new ModelGenerator();
    private ModelTank modelTank = new ModelTank(0f);
    private ModelFurnace modelFurnace = new ModelFurnace();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (block.blockID == SteamPowerMain.boilers.blockID && metadata >= 0 && metadata <= 3)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) 0.0F, (float) 1F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(SteamPowerMain.textureFile + "tankTexture.png"));
            modelTank.generalRender(0.0625F);
            GL11.glPopMatrix();
        }
        if (block.blockID == SteamPowerMain.heaters.blockID && metadata >= 0 && metadata <= 3)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) 0.0F, (float) 1F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(SteamPowerMain.textureFile + "Furnace.png"));
            modelFurnace.genRender(0.0625F);
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
        return renderID;
    }
}
