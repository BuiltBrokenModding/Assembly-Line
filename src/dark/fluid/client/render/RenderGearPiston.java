package dark.fluid.client.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.renders.RenderTileMachine;
import dark.fluid.client.model.ModelGearPiston;
import dark.fluid.common.FluidMech;
import dark.mech.common.machines.TileEntitySteamPiston;
@SideOnly(Side.CLIENT)
public class RenderGearPiston extends RenderTileMachine
{
    private ModelGearPiston model;

    public RenderGearPiston()
    {
        model = new ModelGearPiston();
    }

    public void renderTileEntityAt(TileEntitySteamPiston tileEntity, double d, double d1, double d2, float d3)
    {
        bindTextureByName(FluidMech.instance.PREFIX, FluidMech.MODEL_DIRECTORY + "GearShaftPiston.png");
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        int meta = tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);

        switch (meta)
        {
            case 1:
                GL11.glRotatef(0f, 0f, 1f, 0f);
                break;
            case 2:
                GL11.glRotatef(90f, 0f, 1f, 0f);
                break;
            case 3:
                GL11.glRotatef(180f, 0f, 1f, 0f);
                break;
            case 0:
                GL11.glRotatef(270f, 0f, 1f, 0f);
                break;
        }
        model.renderGear(0.0625F);
        model.renderR(0.0625F, 0);//TODO fix
        model.renderBody(0.0625F);
        model.renderBack(0.0625F);
        model.renderFront(0.0625F);
        model.renderLeft(0.0625F);
        model.renderRight(0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity var1, double d, double d1, double d2, float d3)
    {
        this.renderTileEntityAt(((TileEntitySteamPiston) var1), d, d1, d2, d3);

    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        // TODO Auto-generated method stub
        return null;
    }

}