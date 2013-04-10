package fluidmech.client.render;

import hydraulic.api.ColorCode;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import fluidmech.client.model.ModelLargePipe;
import fluidmech.client.model.ModelReleaseValve;
import fluidmech.common.FluidMech;
import fluidmech.common.tiles.TileEntityReleaseValve;

public class RenderReleaseValve extends TileEntitySpecialRenderer
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
        ForgeDirection dir = ForgeDirection.UNKNOWN;
        if (te instanceof TileEntityReleaseValve)
        {
            ents = ((TileEntityReleaseValve) te).connected;
        }
        bindTextureByName(this.getPipeTexture(15));
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
        bindTextureByName(FluidMech.MODEL_TEXTURE_DIRECTORY + "ReleaseValve.png"); 
        if(ents[1] == null)valve.render();
        GL11.glPopMatrix();

    }

    public static String getPipeTexture(int meta)
    {
        return FluidMech.MODEL_TEXTURE_DIRECTORY + "pipes/" + ColorCode.get(meta).getName() + "Pipe.png";
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        this.renderAModelAt(tileEntity, var2, var4, var6, var8);
    }
}