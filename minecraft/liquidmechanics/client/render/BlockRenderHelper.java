package liquidmechanics.client.render;

import liquidmechanics.api.helpers.connectionHelper;
import liquidmechanics.client.model.ModelGearRod;
import liquidmechanics.client.model.ModelGenerator;
import liquidmechanics.client.model.ModelLargePipe;
import liquidmechanics.client.model.ModelLiquidTank;
import liquidmechanics.client.model.ModelPump;
import liquidmechanics.client.model.ModelReleaseValve;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.tileentity.TileEntityReleaseValve;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BlockRenderHelper implements ISimpleBlockRenderingHandler
{
    public static BlockRenderHelper instance = new BlockRenderHelper();
    public static int renderID = RenderingRegistry.getNextAvailableRenderId();
    private ModelPump modelPump = new ModelPump();
    private ModelGearRod modelRod = new ModelGearRod();
    private ModelGenerator modelGen = new ModelGenerator();
    private ModelLargePipe SixPipe = new ModelLargePipe();
    private ModelLiquidTank tank = new ModelLiquidTank();
    private ModelReleaseValve valve = new ModelReleaseValve();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (block.blockID == LiquidMechanics.blockMachine.blockID && metadata < 4)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) 0.0F, (float) 1.1F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(LiquidMechanics.RESOURCE_PATH + "pumps/Pump.png"));
            modelPump.renderMain(0.0725F);
            modelPump.renderC1(0.0725F);
            modelPump.renderC2(0.0725F);
            modelPump.renderC3(0.0725F);
            GL11.glPopMatrix();
        }
        if (block.blockID == LiquidMechanics.blockTank.blockID)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) 0.0F, (float) 1.1F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);            
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(RenderTank.getTankTexture(metadata)));
            tank.renderMain(0.0625F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(RenderTank.getGuageTexture(metadata,4)));
            tank.renderMeter(null, 0.0625F);
            GL11.glPopMatrix();
        }
        if (block.blockID == LiquidMechanics.blockRod.blockID)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) 0.0F, (float) 1.5F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(LiquidMechanics.RESOURCE_PATH + "mechanical/GearRod.png"));
            modelRod.render(0.0825F, 0);
            GL11.glPopMatrix();
        }
        if (block.blockID == LiquidMechanics.blockGenerator.blockID)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) 0.0F, (float) 1.3F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(LiquidMechanics.RESOURCE_PATH + "mechanical/Generator.png"));
            modelGen.render(null);
            GL11.glPopMatrix();
        }
    }

    public void renderPipeItem(RenderBlocks renderer, int meta)
    {

        GL11.glPushMatrix();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(RenderPipe.getPipeTexture(meta)));

        GL11.glTranslatef(0.5F, -0.5F, 0.5F);
        
        SixPipe.renderRight();
        SixPipe.renderLeft();
        SixPipe.renderMiddle();

        GL11.glPopMatrix();
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
