package liquidmechanics.client.render;

import liquidmechanics.client.model.ModelLargePipe;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.tileentity.TileEntityPipe;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class RenderPipe extends TileEntitySpecialRenderer
{
    private ModelLargePipe SixPipe;
    private TileEntity[] ents = new TileEntity[6];

    public RenderPipe()
    {
        SixPipe = new ModelLargePipe();
    }

    public void renderAModelAt(TileEntity te, double d, double d1, double d2, float f)
    {
        // Texture file
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        int meta = 0;
        if (te instanceof TileEntityPipe)
        {
            meta = te.getBlockMetadata();
            ents = ((TileEntityPipe) te).connectedBlocks;
        }
        this.render(meta, ents);
        GL11.glPopMatrix();

    }
    public static String getPipeTexture(int meta)
    {
        switch(meta)
        {
            case 0:
            case 1:return LiquidMechanics.RESOURCE_PATH + "pipes/SixLavaPipe.png";
            case 2:
            case 3:
            case 4:return LiquidMechanics.RESOURCE_PATH + "pipes/SixWaterPipe.png";
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:return LiquidMechanics.RESOURCE_PATH + "pipes/SixSteamPipe.png";
            case 15:
            default:return LiquidMechanics.RESOURCE_PATH + "pipes/SixOilPipe.png";
        }
    }
    public void render(int meta, TileEntity[] ents)
    {
        bindTextureByName(this.getPipeTexture(meta));
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

    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        this.renderAModelAt((TileEntityPipe) tileEntity, var2, var4, var6, var8);
    }

}