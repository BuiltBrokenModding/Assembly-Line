package dark.fluid.client.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.client.renders.RenderMachine;
import dark.fluid.client.model.ModelLargePipe;
import dark.fluid.client.model.ModelReleaseValve;
import dark.fluid.common.FluidMech;
import dark.fluid.common.machines.TileEntityReleaseValve;
import dark.interfaces.ColorCode;

@SideOnly(Side.CLIENT)
public class RenderReleaseValve extends RenderMachine
{
    private ModelLargePipe SixPipe;
    private ModelReleaseValve valve;
    private TileEntity[] ents = new TileEntity[6];

    public RenderReleaseValve()
    {
        SixPipe = new ModelLargePipe();
        valve = new ModelReleaseValve();
    }

    public void renderAModelAt(TileEntity te, double d, double d1, double d2, float f)
    {
        // Texture file
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        if (te instanceof TileEntityReleaseValve)
        {
            ents = ((TileEntityReleaseValve) te).connected;
        }
        bindTextureByName(this.getTexture(te.getBlockType().blockID, te.getBlockMetadata()));
        if (ents[0] != null)
            SixPipe.renderBottom();
        if (ents[1] != null)
            SixPipe.renderTop();
        if (ents[3] != null)
            SixPipe.renderFront();
        if (ents[2] != null)
            SixPipe.renderBack();
        if (ents[5] != null)
            SixPipe.renderRight();
        if (ents[4] != null)
            SixPipe.renderLeft();
        SixPipe.renderMiddle();
        bindTextureByName(FluidMech.instance.DOMAIN, FluidMech.MODEL_DIRECTORY + "ReleaseValve.png");
        if (ents[1] == null)
            valve.render();
        GL11.glPopMatrix();

    }

    public static String getPipeTexture(int meta)
    {
        return FluidMech.MODEL_DIRECTORY + "pipes/" + ColorCode.get(meta).getName() + "Pipe.png";
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        this.renderAModelAt(tileEntity, var2, var4, var6, var8);
    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        return new ResourceLocation(FluidMech.instance.DOMAIN, FluidMech.MODEL_DIRECTORY + "pipes/" + ColorCode.get(15).getName() + "Pipe.png");
    }
}