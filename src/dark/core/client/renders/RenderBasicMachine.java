package dark.core.client.renders;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.models.ModelCoalGenerator;
import dark.core.client.models.ModelElecFurnace;
import dark.core.client.models.ModelMachine;
import dark.core.common.DarkMain;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.machine.TileEntityMachine;

@SideOnly(Side.CLIENT)
public class RenderBasicMachine extends RenderTileMachine
{
    public static final ModelCoalGenerator COAL_GEN_MODEL = new ModelCoalGenerator();
    public static final ModelElecFurnace ELEC_FURNACE_MODEL = new ModelElecFurnace();

    public static final ResourceLocation COAL_GEN_TEXTURE = new ResourceLocation(DarkMain.getInstance().DOMAIN, ModPrefab.MODEL_DIRECTORY + "CoalGenerator.png");
    public static final ResourceLocation FUEL_GEN_TEXTURE = new ResourceLocation(DarkMain.getInstance().DOMAIN, ModPrefab.MODEL_DIRECTORY + ".png");
    public static final ResourceLocation ELEC_FURNACE_TEXTURE = new ResourceLocation(DarkMain.getInstance().DOMAIN, ModPrefab.MODEL_DIRECTORY + "ElecFurnace.png");
    public static final ResourceLocation BAT_BOX_TEXTURE = new ResourceLocation(DarkMain.getInstance().DOMAIN, ModPrefab.MODEL_DIRECTORY + ".png");
    private static float rot1 = 0;
    private static float rot2 = 45;

    @Override
    public void renderModel(TileEntity tileEntity, double x, double y, double z, float size)
    {
        int meta = tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        int face = meta % 4;
        int type = meta / 4;

        rot1 = MathHelper.wrapAngleTo180_float(rot1 + 1);
        rot2 = MathHelper.wrapAngleTo180_float(rot1 + 2);
        ModelMachine model = null;
        switch (type)
        {
            case 0:
                bindTexture(COAL_GEN_TEXTURE);
                model = COAL_GEN_MODEL;
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                bindTexture(ELEC_FURNACE_TEXTURE);
                model = ELEC_FURNACE_MODEL;
                break;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);
        if (face == 1)
        {
            GL11.glRotatef(180f, 0f, 1f, 0f);
        }
        if (face == 0)
        {
            GL11.glRotatef(0f, 0f, 1f, 0f);
        }
        else if (face == 3)
        {
            GL11.glRotatef(90f, 0f, 1f, 0f);
        }
        else if (face == 2)
        {
            GL11.glRotatef(270f, 0f, 1f, 0f);
        }
        model.render(0.0625F);
        if (tileEntity instanceof TileEntityMachine)
        {
            if (model instanceof ModelCoalGenerator)
            {
                if (((TileEntityMachine) tileEntity).isFunctioning())
                {
                    GL11.glRotatef(this.rot1, 0f, 1f, 0f);
                }
                ((ModelCoalGenerator) model).renderFan(0.0625F);
            }
        }
        GL11.glPopMatrix();

    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        // TODO Auto-generated method stub
        return null;
    }

}