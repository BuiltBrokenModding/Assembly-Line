/**
 * Copyright (c) SpaceToad, 2011 http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License
 * 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package dark.core.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector3;

public class RenderFakeBlock extends Render
{
	/** Render instance */
	public static RenderFakeBlock INSTANCE = new RenderFakeBlock();

	@Override
	public void doRender(Entity entity, double i, double j, double k, float f, float f1)
	{
		doRenderBlock((EntityFakeBlock) entity, i, j, k);
	}

	/** Renders a block as an Entity */
	public void doRenderBlock(EntityFakeBlock entity, double i, double j, double k)
	{
		if (entity.isDead)
		{
			return;
		}

		shadowSize = entity.shadowSize;
		World world = entity.worldObj;
		BlockRenderInfo util = new BlockRenderInfo();
		util.texture = entity.texture;
		func_110776_a(TextureMap.field_110575_b);

		for (int iBase = 0; iBase < entity.iSize; ++iBase)
		{
			for (int jBase = 0; jBase < entity.jSize; ++jBase)
			{
				for (int kBase = 0; kBase < entity.kSize; ++kBase)
				{

					util.min = new Vector3();

					double remainX = entity.iSize - iBase;
					double remainY = entity.jSize - jBase;
					double remainZ = entity.kSize - kBase;

					util.max = new Vector3((remainX > 1.0 ? 1.0 : remainX), (remainY > 1.0 ? 1.0 : remainY), (remainZ > 1.0 ? 1.0 : remainZ));

					GL11.glPushMatrix();
					GL11.glTranslatef((float) i, (float) j, (float) k);
					GL11.glRotatef(entity.rotationX, 1, 0, 0);
					GL11.glRotatef(entity.rotationY, 0, 1, 0);
					GL11.glRotatef(entity.rotationZ, 0, 0, 1);
					GL11.glTranslatef(iBase, jBase, kBase);

					int lightX, lightY, lightZ;

					lightX = (int) (Math.floor(entity.posX) + iBase);
					lightY = (int) (Math.floor(entity.posY) + jBase);
					lightZ = (int) (Math.floor(entity.posZ) + kBase);

					GL11.glDisable(2896 /* GL_LIGHTING */);
					renderBlock(util, world, lightX, lightY, lightZ);
					GL11.glEnable(2896 /* GL_LIGHTING */);
					GL11.glPopMatrix();

				}
			}
		}
	}

	/** Renders a block at given location
	 * 
	 * @param blockInterface - class used to store info for the render process
	 * @param world - world rendering in
	 * @param x - position on x axis
	 * @param y - position on y axis
	 * @param z - position on z axis */
	public void renderBlock(BlockRenderInfo blockInterface, IBlockAccess world, int x, int y, int z)
	{
		renderBlocks.renderMaxX = blockInterface.max.x;
		renderBlocks.renderMinX = blockInterface.min.x;
		renderBlocks.renderMaxY = blockInterface.max.y;
		renderBlocks.renderMinY = blockInterface.min.y;
		renderBlocks.renderMaxZ = blockInterface.max.z;
		renderBlocks.renderMinZ = blockInterface.min.z;
		renderBlocks.enableAO = false;

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		//TODO do a check for "should render side" to solve issue with transparent blocks. Mainly rendering water blocks next to each other
		renderBlocks.renderFaceYNeg(blockInterface.baseBlock, 0, 0, 0, blockInterface.getBlockIconFromSideAndMetadata(0, blockInterface.meta));
		renderBlocks.renderFaceYPos(blockInterface.baseBlock, 0, 0, 0, blockInterface.getBlockIconFromSideAndMetadata(1, blockInterface.meta));

		renderBlocks.renderFaceZNeg(blockInterface.baseBlock, 0, 0, 0, blockInterface.getBlockIconFromSideAndMetadata(2, blockInterface.meta));
		renderBlocks.renderFaceZPos(blockInterface.baseBlock, 0, 0, 0, blockInterface.getBlockIconFromSideAndMetadata(3, blockInterface.meta));

		renderBlocks.renderFaceXNeg(blockInterface.baseBlock, 0, 0, 0, blockInterface.getBlockIconFromSideAndMetadata(4, blockInterface.meta));
		renderBlocks.renderFaceXPos(blockInterface.baseBlock, 0, 0, 0, blockInterface.getBlockIconFromSideAndMetadata(5, blockInterface.meta));

		tessellator.draw();

	}

	@Override
	protected ResourceLocation func_110775_a(Entity entity)
	{
		throw new UnsupportedOperationException("Getting resoure location for this is not support as of yet");
	}
}
