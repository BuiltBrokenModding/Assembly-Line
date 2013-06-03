package dark.fluid.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import dark.fluid.client.model.ModelPump;
import dark.fluid.common.FluidMech;
import dark.fluid.common.pump.TileEntityStarterPump;


public class RenderPump extends TileEntitySpecialRenderer
{
    int type = 0;
    private ModelPump model;

    public RenderPump()
    {
        model = new ModelPump();
    }

    public void renderAModelAt(TileEntityStarterPump te, double d, double d1, double d2, float f)
    {
        int meta = te.worldObj.getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);

        bindTextureByName(FluidMech.MODEL_TEXTURE_DIRECTORY + "pumps/WaterPump.png");
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        switch (meta)
        {
            case 2:
                GL11.glRotatef(0f, 0f, 1f, 0f);
                break;
            case 3:
                GL11.glRotatef(90f, 0f, 1f, 0f);
                break;
            case 0:
                GL11.glRotatef(180f, 0f, 1f, 0f);
                break;
            case 1:
                GL11.glRotatef(270f, 0f, 1f, 0f);
                break;
        }
        model.render(0.0625F);
        model.renderMotion(0.0625F, te.pos);
        GL11.glPopMatrix();

    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        this.renderAModelAt((TileEntityStarterPump) tileEntity, var2, var4, var6, var8);
    }

}