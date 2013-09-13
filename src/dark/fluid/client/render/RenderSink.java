package dark.fluid.client.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.renders.RenderTileMachine;
import dark.fluid.client.model.ModelSink;
import dark.fluid.common.FluidMech;
import dark.fluid.common.machines.TileEntitySink;

@SideOnly(Side.CLIENT)
public class RenderSink extends RenderTileMachine
{
    int type = 0;
    private ModelSink model;

    public RenderSink()
    {
        model = new ModelSink();
    }

    public void renderWater(FluidStack stack)
    {
        if (stack == null || stack.amount <= 1)
        {
            return;
        }
        //bindTextureByName(Block.waterStill.getBlockTextureFromSide(0) + "blue.png");
        float p = 0;
        if (stack.amount > 0)
            p = 0.5f;
        if (stack.amount > 500)
            p = 1.5f;
        if (stack.amount > 1000)
            p = 2.5f;
        if (stack.amount > 1500)
            p = 3.5f;

        model.renderLiquid(0.0625F, p);
    }

    public void renderAModelAt(TileEntitySink te, double d, double d1, double d2, float f)
    {
        int meta = te.worldObj.getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);

        bindTextureByName(this.getTexture(te.getBlockType().blockID, te.getBlockMetadata()));
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
        renderWater(te.getTank().getFluid());
        GL11.glPopMatrix();

    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        this.renderAModelAt((TileEntitySink) tileEntity, var2, var4, var6, var8);
    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        return new ResourceLocation(FluidMech.instance.DOMAIN, FluidMech.MODEL_DIRECTORY + "Sink.png");
    }

}