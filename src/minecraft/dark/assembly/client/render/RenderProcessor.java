package dark.assembly.client.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.client.model.ModelCrusher;
import dark.assembly.client.model.ModelGrinder;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.machine.processor.TileEntityProcessor;
import dark.core.client.renders.RenderTileMachine;

@SideOnly(Side.CLIENT)
public class RenderProcessor extends RenderTileMachine
{
    private ModelCrusher crusherModel;
    private ModelGrinder grinderModel;

    public RenderProcessor()
    {
        this.crusherModel = new ModelCrusher();
        this.grinderModel = new ModelGrinder();
    }

    private void renderAModelAt(TileEntityProcessor tileEntity, double x, double y, double z, float f)
    {
        func_110628_a(this.getTexture(tileEntity.getBlockType().blockID, tileEntity.getBlockMetadata()));

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);

        if (tileEntity.getDirection() == ForgeDirection.NORTH)
        {
            GL11.glRotatef(180f, 0f, 1f, 0f);
        }
        else if (tileEntity.getDirection() == ForgeDirection.SOUTH)
        {
            GL11.glRotatef(0f, 0f, 1f, 0f);
        }
        else if (tileEntity.getDirection() == ForgeDirection.WEST)
        {
            GL11.glRotatef(90f, 0f, 1f, 0f);
        }
        else if (tileEntity.getDirection() == ForgeDirection.EAST)
        {
            GL11.glRotatef(270f, 0f, 1f, 0f);
        }

        if (tileEntity.blockMetadata >= 0 && tileEntity.blockMetadata <= 3)
        {
            crusherModel.renderBody(0.0625F);
            crusherModel.renderPiston(0.0625F, tileEntity.renderStage);
        }
        else if (tileEntity.blockMetadata >= 4 && tileEntity.blockMetadata <= 7)
        {
            grinderModel.renderBody(0.0625F);
            grinderModel.renderRotation(0.0625F, tileEntity.renderStage);
        }
        else if (tileEntity.blockMetadata >= 8 && tileEntity.blockMetadata <= 11)
        {

        }
        else if (tileEntity.blockMetadata >= 12 && tileEntity.blockMetadata <= 15)
        {

        }

        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        if (tileEntity instanceof TileEntityProcessor)
            this.renderAModelAt((TileEntityProcessor) tileEntity, var2, var4, var6, var8);
    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        return new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.MODEL_DIRECTORY + "CrusherBlock.png");
    }

}