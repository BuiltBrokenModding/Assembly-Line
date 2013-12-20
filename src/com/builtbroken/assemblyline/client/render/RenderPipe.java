package com.builtbroken.assemblyline.client.render;

import java.util.HashMap;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.FluidPartsMaterial;
import com.builtbroken.assemblyline.client.model.ModelLargePipe;
import com.builtbroken.assemblyline.fluid.pipes.EnumPipeType;
import com.builtbroken.assemblyline.fluid.pipes.TileEntityPipe;
import com.builtbroken.common.Pair;
import com.dark.DarkCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPipe extends TileEntitySpecialRenderer
{
    public ModelLargePipe SixPipe;
    private static HashMap<Pair<FluidPartsMaterial, Integer>, ResourceLocation> TEXTURES = new HashMap<Pair<FluidPartsMaterial, Integer>, ResourceLocation>();
    public static ResourceLocation TEXTURE = new ResourceLocation(AssemblyLine.DOMAIN, DarkCore.MODEL_DIRECTORY + "pipes/Pipe.png");

    public RenderPipe()
    {
        SixPipe = new ModelLargePipe();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double d, double d1, double d2, float f)
    {
        // Texture file
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        GL11.glScalef(1.0F, -1F, -1F);

        FluidPartsMaterial mat = FluidPartsMaterial.IRON;
        if (te.getBlockMetadata() < FluidPartsMaterial.values().length)
        {
            mat = FluidPartsMaterial.values()[te.getBlockMetadata()];
        }

        if (te instanceof TileEntityPipe)
        {
            this.render(mat, ((TileEntityPipe) te).getSubID(), ((TileEntityPipe) te).renderConnection);
        }
        else
        {
            this.render(FluidPartsMaterial.STONE, 0, new boolean[6]);
        }
        GL11.glPopMatrix();

    }

    public static ResourceLocation getTexture(FluidPartsMaterial mat, int pipeID)
    {
        if (mat != null)
        {
            Pair<FluidPartsMaterial, Integer> index = new Pair<FluidPartsMaterial, Integer>(mat, pipeID);
            if (!TEXTURES.containsKey(index))
            {
                String pipeName = "";
                if (EnumPipeType.get(pipeID) != null)
                {
                    pipeName = EnumPipeType.get(pipeID).getName(pipeID);
                }
                TEXTURES.put(index, new ResourceLocation(AssemblyLine.DOMAIN, DarkCore.MODEL_DIRECTORY + "pipes/" + mat.matName + "/" + pipeName + "Pipe.png"));
            }
            return TEXTURES.get(index);
        }
        return TEXTURE;
    }

    public void render(FluidPartsMaterial mat, int pipeID, boolean[] side)
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

}