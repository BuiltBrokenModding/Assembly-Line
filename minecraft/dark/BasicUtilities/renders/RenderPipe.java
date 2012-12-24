package dark.BasicUtilities.renders;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.Tile.TileEntityPipe;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.renders.models.ModelLargePipe;
import dark.BasicUtilities.renders.models.ModelPipe;

public class RenderPipe extends TileEntitySpecialRenderer
{
    private Liquid type = Liquid.DEFUALT;
    private ModelPipe fourPipe;
    private ModelLargePipe SixPipe;
    private TileEntity[] ents = new TileEntity[6];

    public RenderPipe()
    {
        fourPipe = new ModelPipe();
        SixPipe = new ModelLargePipe();
    }

    public void renderAModelAt(TileEntity te, double d, double d1, double d2, float f)
    {
        // Texture file
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        if (te instanceof TileEntityPipe)
        {
            type = ((TileEntityPipe) te).getType();
            ents = ((TileEntityPipe) te).connectedBlocks;
        }
        this.render(type, ents);
        GL11.glPopMatrix();

    }

    public void render(Liquid type, TileEntity[] ents)
    {

        switch (type.ordinal())
        {
            case 0:
                bindTextureByName(BasicUtilitiesMain.textureFile + "pipes/SixSteamPipe.png");
                break;
            case 1:
                bindTextureByName(BasicUtilitiesMain.textureFile + "pipes/SixWaterPipe.png");
                break;
            case 2:
                bindTextureByName(BasicUtilitiesMain.textureFile + "pipes/SixLavaPipe.png");
                break;
            case 3:
                bindTextureByName(BasicUtilitiesMain.textureFile + "pipes/SixOilPipe.png");
                break;
            default:
                bindTextureByName(BasicUtilitiesMain.textureFile + "pipes/DefaultPipe.png");
                break;
        }
        if (ents[0] != null) SixPipe.renderBottom();
        if (ents[1] != null) SixPipe.renderTop();
        if (ents[3] != null) SixPipe.renderFront();
        if (ents[2] != null) SixPipe.renderBack();
        if (ents[5] != null) SixPipe.renderRight();
        if (ents[4] != null) SixPipe.renderLeft();
        SixPipe.renderMiddle();

    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        this.renderAModelAt((TileEntityPipe) tileEntity, var2, var4, var6, var8);
    }

}