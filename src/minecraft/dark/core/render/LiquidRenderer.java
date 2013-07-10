/**
 * Copyright (c) SpaceToad, 2011 http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License
 * 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package dark.core.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import dark.core.render.RenderEntityBlock.BlockInterface;

/** @author CovertJaguar <railcraft.wikispaces.com> */
public class LiquidRenderer
{

	private static Map<FluidStack, int[]> flowingRenderCache = new HashMap<FluidStack, int[]>();
	private static Map<FluidStack, int[]> stillRenderCache = new HashMap<FluidStack, int[]>();
	public static final int DISPLAY_STAGES = 100;
	private static final BlockInterface liquidBlock = new BlockInterface();

	public static class LiquidTextureException extends RuntimeException
	{

		private final FluidStack liquid;

		public LiquidTextureException(FluidStack liquid)
		{
			super();
			this.liquid = liquid;
		}

		@Override
		public String getMessage()
		{
			String liquidName = liquid.getFluid().getName();
			if (liquidName == null)
			{
				liquidName = String.format("ID: %d", liquid.getFluid().getBlockID());
			}
			return String.format("Liquid %s has no icon. Please contact the author of the mod the liquid came from.", liquidName);
		}
	}

	public static Icon getLiquidTexture(FluidStack liquid)
	{
		if (liquid == null || liquid.getFluid() == null)
		{
			return null;
		}
		Icon icon = liquid.getFluid().getIcon();
		if (icon == null)
		{
			throw new LiquidTextureException(liquid);
		}
		return icon;
	}

	public static String getLiquidSheet(Fluid liquid)
	{
		return "/terrain.png";
	}

	public static int[] getLiquidDisplayLists(FluidStack liquid, World world, boolean flowing)
	{
		if (liquid == null || liquid.getFluid() == null)
		{
			return null;
		}
		Fluid fluid = liquid.getFluid();

		Map<FluidStack, int[]> cache = flowing ? flowingRenderCache : stillRenderCache;

		int[] diplayLists = cache.get(liquid);
		if (diplayLists != null)
		{
			return diplayLists;
		}

		diplayLists = new int[DISPLAY_STAGES];

		if (fluid.getBlockID() < Block.blocksList.length && Block.blocksList[fluid.getBlockID()] != null)
		{
			liquidBlock.baseBlock = Block.blocksList[fluid.getBlockID()];
			if (!flowing)
			{
				liquidBlock.texture = getLiquidTexture(liquid);
			}
		}
		else if (Item.itemsList[fluid.getBlockID()] != null)
		{
			liquidBlock.baseBlock = Block.waterStill;
			liquidBlock.texture = getLiquidTexture(liquid);
		}
		else
		{
			return null;
		}

		cache.put(liquid, diplayLists);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);

		int color = fluid.getColor();
		float c1 = (float) (color >> 16 & 255) / 255.0F;
		float c2 = (float) (color >> 8 & 255) / 255.0F;
		float c3 = (float) (color & 255) / 255.0F;
		GL11.glColor4f(c1, c2, c3, 1);
		for (int s = 0; s < DISPLAY_STAGES; ++s)
		{
			diplayLists[s] = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(diplayLists[s], 4864 /*GL_COMPILE*/);

			liquidBlock.minX = 0.01f;
			liquidBlock.minY = 0;
			liquidBlock.minZ = 0.01f;

			liquidBlock.maxX = 0.99f;
			liquidBlock.maxY = (float) s / (float) DISPLAY_STAGES;
			liquidBlock.maxZ = 0.99f;

			RenderEntityBlock.renderBlock(liquidBlock, world, 0, 0, 0, false, true);

			GL11.glEndList();
		}

		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);

		return diplayLists;
	}
}
