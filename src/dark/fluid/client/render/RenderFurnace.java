package dark.fluid.client.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.renders.RenderTileMachine;
import dark.fluid.client.model.ModelFurnace;
import dark.fluid.common.FluidMech;
@SideOnly(Side.CLIENT)
public class RenderFurnace extends RenderTileMachine
{
    int type = 0;
    private ModelFurnace model;

    public RenderFurnace()
    {
        model = new ModelFurnace();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double d, double d1, double d2, float d3)
    {
        bindTextureByName(FluidMech.instance.PREFIX, FluidMech.MODEL_DIRECTORY + "Furnace.png");
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        int meta = te.worldObj.getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
        switch (meta)
        {
            case 0:
                GL11.glRotatef(0f, 0f, 1f, 0f);
                break;
            case 1:
                GL11.glRotatef(90f, 0f, 1f, 0f);
                break;
            case 2:
                GL11.glRotatef(180f, 0f, 1f, 0f);
                break;
            case 3:
                GL11.glRotatef(270f, 0f, 1f, 0f);
                break;
        }
        model.genRender(0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        // TODO Auto-generated method stub
        return null;
    }

}