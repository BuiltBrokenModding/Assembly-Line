package dark.fluid.client.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.renders.RenderTileMachine;
import dark.core.prefab.ModPrefab;
import dark.fluid.client.model.ModelLargePipe;
import dark.fluid.common.FluidMech;
import dark.fluid.common.pipes.EnumPipeType;
import dark.fluid.common.pipes.PipeMaterial;
import dark.fluid.common.pipes.TileEntityPipe;

@SideOnly(Side.CLIENT)
public class RenderPipe extends RenderTileMachine
{
    public ModelLargePipe SixPipe;

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

        PipeMaterial mat = PipeMaterial.IRON;
        if (te.getBlockMetadata() < PipeMaterial.values().length)
        {
            System.out.println("Pipe meta " + te.getBlockMetadata());
            mat = PipeMaterial.values()[te.getBlockMetadata()];
        }

        if (te instanceof TileEntityPipe)
        {
            this.render(mat, ((TileEntityPipe) te).getPipeID(), ((TileEntityPipe) te).renderConnection);
        }
        else
        {
            this.render(PipeMaterial.STONE, 0, new boolean[6]);
        }
        GL11.glPopMatrix();

    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        return new ResourceLocation(FluidMech.instance.DOMAIN, ModPrefab.MODEL_DIRECTORY + "pipes/Pipe.png");
    }

    public static ResourceLocation getTexture(PipeMaterial mat, int pipeID)
    {
        if (mat != null)
        {
            String s = "";
            if (EnumPipeType.get(pipeID) != null)
            {
                s = EnumPipeType.get(pipeID).getName(pipeID);
            }
            return new ResourceLocation(FluidMech.instance.DOMAIN, ModPrefab.MODEL_DIRECTORY + "pipes/" + mat.matName + "/" + s + "Pipe.png");
        }
        return new ResourceLocation(FluidMech.instance.DOMAIN, ModPrefab.MODEL_DIRECTORY + "pipes/Pipe.png");
    }

    public void render(PipeMaterial mat, int pipeID, boolean[] side)
    {
        bindTexture(RenderPipe.getTexture(mat, pipeID));
        if (side[0])
        {
            SixPipe.renderBottom();
        }
        if (side[1])
        {
            SixPipe.renderTop();
        }
        if (side[3])
        {
            SixPipe.renderFront();
        }
        if (side[2])
        {
            SixPipe.renderBack();
        }
        if (side[5])
        {
            SixPipe.renderRight();
        }
        if (side[4])
        {
            SixPipe.renderLeft();
        }
        SixPipe.renderMiddle();
    }

    @Override
    public void renderModel(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        this.renderAModelAt(tileEntity, var2, var4, var6, var8);
    }

}