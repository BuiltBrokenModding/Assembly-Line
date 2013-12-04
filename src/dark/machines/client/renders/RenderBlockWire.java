package dark.machines.client.renders;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import universalelectricity.compatibility.Compatibility;
import universalelectricity.core.block.IConnector;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import buildcraft.api.power.IPowerReceptor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.machines.client.models.ModelCopperWire;
import dark.machines.common.DarkMain;

@SideOnly(Side.CLIENT)
public class RenderBlockWire extends RenderTileMachine
{
    private static final ResourceLocation copperWireTexture = new ResourceLocation(DarkMain.getInstance().DOMAIN, "textures/models/copperWire.png");

    public static final ModelCopperWire model = new ModelCopperWire();

    @Override
    public void renderModel(TileEntity tileEntity, double d, double d1, double d2, float f)
    {
        // Texture file
        this.bindTexture(this.getTexture(tileEntity.getBlockType().blockID, tileEntity.getBlockMetadata()));
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);

        boolean[] renderSide = new boolean[6];

        for (byte i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            TileEntity ent = VectorHelper.getTileEntityFromSide(tileEntity.worldObj, new Vector3(tileEntity), dir);

            if (ent instanceof IConnector)
            {
                if (((IConnector) ent).canConnect(dir.getOpposite()))
                {
                    renderSide[i] = true;
                }
            }
            else if (Compatibility.isIndustrialCraft2Loaded() && ent instanceof IEnergyTile)
            {
                if (ent instanceof IEnergyAcceptor)
                {
                    if (((IEnergyAcceptor) ent).acceptsEnergyFrom(tileEntity, dir.getOpposite()))
                    {
                        renderSide[i] = true;
                    }
                }
                else
                {
                    renderSide[i] = true;
                }
            }
            else if (Compatibility.isBuildcraftLoaded() && ent instanceof IPowerReceptor)
            {
                renderSide[i] = true;
            }
        }

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
        {
            if (renderSide[side.ordinal()])
            {
                model.renderSide(side);
            }
        }
        model.renderSide(ForgeDirection.UNKNOWN);
        GL11.glPopMatrix();
    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        return copperWireTexture;
    }
}