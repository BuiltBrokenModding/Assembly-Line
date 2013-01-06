package liquidmechanics.client.render;

import liquidmechanics.api.helpers.connectionHelper;
import liquidmechanics.client.model.ModelLiquidTank;
import liquidmechanics.client.model.ModelLiquidTankCorner;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.handlers.LiquidData;
import liquidmechanics.common.handlers.LiquidHandler;
import liquidmechanics.common.tileentity.TileEntityTank;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.LiquidContainerRegistry;

import org.lwjgl.opengl.GL11;

public class RenderTank extends TileEntitySpecialRenderer
{
    private LiquidData type = LiquidHandler.unkown;
    private ModelLiquidTank model;
    private ModelLiquidTankCorner modelC;
    private int pos = 0;

    public RenderTank()
    {
        model = new ModelLiquidTank();
        modelC = new ModelLiquidTankCorner();
    }

    public void renderAModelAt(TileEntityTank te, double d, double d1, double d2, float f)
    {
        int meta = te.getBlockMetadata();
        pos = Math.min((te.volume / LiquidContainerRegistry.BUCKET_VOLUME), 4);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);

        if (connectionHelper.corner(te) > 0)
        {
            bindTextureByName(this.getCornerTexture(meta));
            int corner = connectionHelper.corner(te);
            switch (corner)
            {
                case 2:
                    GL11.glRotatef(270f, 0f, 1f, 0f);
                    break;
                case 3:
                    GL11.glRotatef(0f, 0f, 1f, 0f);
                    break;
                case 4:
                    GL11.glRotatef(90f, 0f, 1f, 0f);
                    break;
                case 1:
                    GL11.glRotatef(180f, 0f, 1f, 0f);
                    break;
            }
            modelC.render(0.0625F);
        }
        else
        {
            bindTextureByName(this.getTankTexture(meta));
            model.renderMain(0.0625F);
            bindTextureByName(this.getGuageTexture(meta, pos));
            model.renderMeter(te, 0.0625F);
        }
        GL11.glPopMatrix();

    }

    public static String getTankTexture(int meta)
    {
        String type = "";
        switch (meta)
        {          
            case 1:type = "Red";break;
            case 14:type = "Orange";break;
            default:type = "";break;
        }

            return LiquidMechanics.RESOURCE_PATH + "tanks/" + type + "Tank.png";

    }

    public static String getGuageTexture(int meta, int pos)
    {
        String type = "";
        switch (meta)
        {
            case 1:type = "Lava";break;
            case 12:type = "Fuel";break;
            default:
                type = "";
                break;
        }

        return LiquidMechanics.RESOURCE_PATH + "tanks/guage/" + pos + type + ".png";
    }

    public static String getCornerTexture(int meta)
    {
        String type = "";
        switch (meta)
        {
            case 1:
                type = "Red";
                break;
            case 4:
                type = "Water";
                break;
            default:
                type = "";
                break;
        }
        return LiquidMechanics.RESOURCE_PATH + "tanks/Corner" + type + "png";

    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        this.renderAModelAt((TileEntityTank) tileEntity, var2, var4, var6, var8);
    }

}