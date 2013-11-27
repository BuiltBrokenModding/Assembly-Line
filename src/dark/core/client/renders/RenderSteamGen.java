package dark.core.client.renders;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.models.ModelSteamGen;
import dark.core.client.models.ModelSteamTurbine;
import dark.core.client.models.ModelElecFurnace;
import dark.core.client.models.ModelMachine;
import dark.core.common.DarkMain;
import dark.core.common.machines.BlockSmallSteamGen;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.machine.TileEntityMachine;

@SideOnly(Side.CLIENT)
public class RenderSteamGen extends RenderTileMachine
{
    public static final ModelSteamTurbine TURBINE_MODEL = new ModelSteamTurbine();
    public static final ModelSteamGen STEAM_GEN_MODEL = new ModelSteamGen();

    public static final ResourceLocation TURBINE_TEXTURE = new ResourceLocation(DarkMain.getInstance().DOMAIN, ModPrefab.MODEL_DIRECTORY + "SmallSteamFan.png");
    public static final ResourceLocation STEAM_GEN_TEXTURE = new ResourceLocation(DarkMain.getInstance().DOMAIN, ModPrefab.MODEL_DIRECTORY + "SteamGenerator.png");
    private static float rot1 = 0;

    @Override
    public void renderModel(TileEntity tileEntity, double x, double y, double z, float size)
    {
        int meta = tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        int face = meta % 4;
        ModelMachine model = getModel(meta);

        if (model != null)
        {
            bindTexture(this.getTexture(tileEntity.getBlockType().blockID, meta));
            rot1 = MathHelper.wrapAngleTo180_float(rot1 + 1);

            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
            GL11.glScalef(1.0F, -1F, -1F);

            if (face == 0)
            {
                GL11.glRotatef(180f, 0f, 1f, 0f);
            }
            if (face == 2)
            {
                GL11.glRotatef(0f, 0f, 1f, 0f);
            }
            else if (face == 3)
            {
                GL11.glRotatef(90f, 0f, 1f, 0f);
            }
            else if (face == 1)
            {
                GL11.glRotatef(270f, 0f, 1f, 0f);
            }

            model.render(0.0625F);

            if (tileEntity instanceof TileEntityMachine)
            {
                if (model instanceof ModelSteamTurbine)
                {
                    if (((TileEntityMachine) tileEntity).isFunctioning())
                    {
                        GL11.glRotatef(this.rot1, 0f, 1f, 0f);
                    }
                    ((ModelSteamTurbine) model).renderFan(0.0625F);
                }
            }

            GL11.glPopMatrix();
        }

    }

    public static ModelMachine getModel(int meta)
    {
        switch (meta / 4)
        {

            case 0:
                return TURBINE_MODEL;
            case 1:
                return TURBINE_MODEL;
            case 2:
                return STEAM_GEN_MODEL;
            case 3:
                return STEAM_GEN_MODEL;
        }
        return null;
    }

    public static ResourceLocation getTexture(int meta)
    {
        switch (meta / 4)
        {

            case 0:
                return TURBINE_TEXTURE;
            case 1:
                return TURBINE_TEXTURE;
            case 2:
                return STEAM_GEN_TEXTURE;
            case 3:
                return STEAM_GEN_TEXTURE;
        }
        return null;
    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        return getTexture(meta);
    }

}