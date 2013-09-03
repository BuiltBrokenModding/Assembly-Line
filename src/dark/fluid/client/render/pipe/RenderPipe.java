package dark.fluid.client.render.pipe;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.renders.RenderMachine;
import dark.core.interfaces.ColorCode;
import dark.core.prefab.helpers.FluidHelper;
import dark.fluid.client.model.ModelLargePipe;
import dark.fluid.common.FluidMech;
import dark.fluid.common.pipes.TileEntityPipe;
import dark.fluid.common.pipes.addon.IPipeExtention;

@SideOnly(Side.CLIENT)
public class RenderPipe extends RenderMachine
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
        int blockID = FluidMech.recipeLoader.blockPipe.blockID;

        if (te instanceof TileEntityPipe)
        {
            meta = te.getBlockMetadata();
            blockID = te.getBlockType().blockID;

            TileEntityPipe pipe = ((TileEntityPipe) te);
            this.renderSide = pipe.renderConnection;

            // Pipes extension rendering
            for (int i = 0; i < 6; i++)
            {
                IPipeExtention extention = (IPipeExtention) pipe.subEntities[i];
                if (extention != null)
                {
                    Object ob;
                    try
                    {
                        ob = extention.getExtentionRenderClass().newInstance();

                        if (ob instanceof IPipeExtentionRender)
                        {
                            IPipeExtentionRender render = (IPipeExtentionRender) ob;
                            if (render != null)
                            {
                                System.out.println("Rendering Pipe Addon side " + i);
                                render.renderAModelAt(this, pipe, new Vector3(0, 0, 0), f, ForgeDirection.getOrientation(i));
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println("Failed to render a pipe extention");
                        e.printStackTrace();
                    }
                }
            }
        }
        this.render(blockID, meta, renderSide);
        GL11.glPopMatrix();

    }

    @Override
    public ResourceLocation getTexture(int block, int meta)
    {
        String name = "";
        if (block == FluidMech.recipeLoader.blockPipe.blockID)
        {
            Fluid stack = FluidHelper.getStackForColor(ColorCode.get(meta));
            name = stack != null ? stack.getName() : "";
        }
        else
        {
            name = ColorCode.get(meta).getName();
        }
        return new ResourceLocation(FluidMech.instance.DOMAIN, FluidMech.MODEL_DIRECTORY + "pipes/" + name + "Pipe.png");
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