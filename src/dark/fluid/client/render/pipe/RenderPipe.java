package dark.fluid.client.render.pipe;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.ColorCode;
import dark.core.client.renders.RenderTileMachine;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.helpers.FluidHelper;
import dark.fluid.client.model.ModelLargePipe;
import dark.fluid.common.FMRecipeLoader;
import dark.fluid.common.FluidMech;
import dark.fluid.common.pipes.TileEntityPipe;

@SideOnly(Side.CLIENT)
public class RenderPipe extends RenderTileMachine
{
    public ModelLargePipe SixPipe;
    private boolean[] renderSide = new boolean[6];

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
        int blockID = FMRecipeLoader.blockPipe.blockID;

        if (te instanceof TileEntityPipe)
        {
            meta = te.getBlockMetadata();
            blockID = te.getBlockType().blockID;

            TileEntityPipe pipe = ((TileEntityPipe) te);
            this.renderSide = pipe.renderConnection;

        }
        this.render(blockID, meta, renderSide);
        GL11.glPopMatrix();

    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        String name = "";
        if (block == FMRecipeLoader.blockPipe.blockID)
        {
            Fluid stack = FluidHelper.getStackForColor(ColorCode.get(meta));
            name = stack != null ? stack.getName() : "";
        }
        else
        {
            name = ColorCode.get(meta).getName();
        }
        return new ResourceLocation(FluidMech.instance.DOMAIN, ModPrefab.MODEL_DIRECTORY + "pipes/" + name + "Pipe.png");
    }

    public void render(int blockID, int meta, boolean[] side)
    {
        bindTextureByName(this.getTexture(blockID, meta));
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
    public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
    {
        this.renderAModelAt(tileEntity, var2, var4, var6, var8);
    }

}