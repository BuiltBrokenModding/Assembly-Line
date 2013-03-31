package fluidmech.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;

import fluidmech.client.model.ModelSink;
import fluidmech.common.FluidMech;
import fluidmech.common.machines.TileEntitySink;

public class RenderSink extends TileEntitySpecialRenderer
{
    int type = 0;
    private ModelSink model;

    public RenderSink()
    {
        model = new ModelSink();
    }

    public void renderWater(LiquidStack stack)
    {
        if (stack == null || stack.amount <= 1) { return; }
        bindTextureByName(FluidMech.MODEL_TEXTURE_DIRECTORY + "blue.png");
        float p = 0;
        if(stack.amount > 0)p = 0.5f;
        if(stack.amount > 500)p=1.5f;
        if(stack.amount > 1000)p=2.5f;
        if(stack.amount > 1500)p=3.5f;
        
        model.renderLiquid(0.0625F, p);
    }

    public void renderAModelAt(TileEntitySink te, double d, double d1, double d2, float f)
    {
        int meta = te.worldObj.getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);

        bindTextureByName(FluidMech.MODEL_TEXTURE_DIRECTORY + "Sink.png");
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        switch (meta)
        {
            case 3:
                GL11.glRotatef(90f, 0f, 1f, 0f);
                break;
            case 0:
                GL11.glRotatef(180f, 0f, 1f, 0f);
                break;
            case 1:
                GL11.glRotatef(270f, 0f, 1f, 0f);
                break;
            case 2:
                GL11.glRotatef(0f, 0f, 1f, 0f);
                break;
        }
        model.render(0.0625F);
        renderWater(te.getStoredLiquid());
        GL11.glPopMatrix();

    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        this.renderAModelAt((TileEntitySink) tileEntity, var2, var4, var6, var8);
    }

}