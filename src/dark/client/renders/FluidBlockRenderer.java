package dark.client.renders;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import dark.core.helpers.BlockRenderInfo;

import universalelectricity.core.vector.Vector3;

/** @author CovertJaguar <railcraft.wikispaces.com> from BuildCraft , modified by DarkGuardsman */
public class FluidBlockRenderer
{
    private static Map<Fluid, int[]> flowingRenderCache = new HashMap<Fluid, int[]>();
    private static Map<Fluid, int[]> stillRenderCache = new HashMap<Fluid, int[]>();
    public static final int DISPLAY_STAGES = 100;
    private static final BlockRenderInfo liquidBlock = new BlockRenderInfo();

    /** Gets the icon for the given fluid */
    public static Icon getFluidTexture(Fluid fluid)
    {
        if (fluid == null)
        {
            return null;
        }

        Icon icon = fluid.getStillIcon();
        if (icon == null)
        {
            Block block = Block.blocksList[fluid.getBlockID()];
            if (block != null)
            {
                if (block.blockID == Block.waterStill.blockID)
                {
                    icon = Block.waterStill.getIcon(0, 0);
                }
                if (block.blockID == Block.lavaStill.blockID)
                {
                    icon = Block.lavaStill.getIcon(0, 0);
                }
            }
            if (icon == null)
            {
                icon = Block.waterStill.getIcon(0, 0);
            }
        }
        return icon;
    }

    /** Get the texture sheet used to bind textures to the render */
    public static ResourceLocation getFluidSheet(FluidStack liquid)
    {
        return TextureMap.field_110575_b;
    }

    /** Gets the GL11 display list used to render the fluidStack as a block */
    public static int[] getFluidDisplayLists(FluidStack fluidStack, World world, boolean flowing)
    {
        if (fluidStack == null || fluidStack.getFluid() == null)
        {
            return null;
        }
        Fluid fluid = fluidStack.getFluid();

        Map<Fluid, int[]> cache = flowing ? flowingRenderCache : stillRenderCache;
        int[] diplayLists = cache.get(fluid);
        if (diplayLists != null)
        {
            return diplayLists;
        }

        diplayLists = new int[DISPLAY_STAGES];

        liquidBlock.baseBlock = fluid.getBlockID() > 0 ? Block.blocksList[fluid.getBlockID()] : Block.waterStill;
        liquidBlock.texture = getFluidTexture(fluid);

        cache.put(fluid, diplayLists);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        int color = fluid.getColor(fluidStack);
        if (color != 0xFFFFFF)
        {
            float c1 = (float) (color >> 16 & 255) / 255.0F;
            float c2 = (float) (color >> 8 & 255) / 255.0F;
            float c3 = (float) (color & 255) / 255.0F;
            GL11.glColor4f(c1, c2, c3, 1);
        }
        for (int s = 0; s < DISPLAY_STAGES; ++s)
        {
            diplayLists[s] = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(diplayLists[s], 4864 /*GL_COMPILE*/);

            liquidBlock.min = new Vector3(0.01f, 0, 0.01f);
            liquidBlock.max = new Vector3(0.99f, (float) s / (float) DISPLAY_STAGES, 0.99f);

            RenderFakeBlock.INSTANCE.renderBlock(liquidBlock, world, new Vector3());

            GL11.glEndList();
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        return diplayLists;
    }

}