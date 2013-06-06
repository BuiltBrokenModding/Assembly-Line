// Date: 6/3/2013 6:47:54 AM
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX

package dark.fluid.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelTankSide extends ModelBase
{

	ModelRenderer botSiding;
	ModelRenderer botRightOut;
	ModelRenderer botLeftOut;

	ModelRenderer topSiding;
	ModelRenderer topRightOut;
	ModelRenderer topLeftOut;

	ModelRenderer leftSiding;
	ModelRenderer leftBotSide;
	ModelRenderer leftTopSide;
	ModelRenderer leftSide;

	ModelRenderer rightSiding;
	ModelRenderer rightBotSide;
	ModelRenderer rightTopSide;
	ModelRenderer rightSide;

	public ModelTankSide()
	{
		textureWidth = 128;
		textureHeight = 128;

		rightSiding = new ModelRenderer(this, 0, 19);
		rightSiding.addBox(-7F, 11F, 7F, 2, 10, 1);
		rightSiding.setRotationPoint(0F, 0F, 0F);
		rightSiding.setTextureSize(128, 128);
		rightSiding.mirror = true;
		setRotation(rightSiding, 0F, 0F, 0F);
		leftSiding = new ModelRenderer(this, 0, 19);
		leftSiding.addBox(5F, 11F, 7F, 2, 10, 1);
		leftSiding.setRotationPoint(0F, 0F, 0F);
		leftSiding.setTextureSize(128, 128);
		leftSiding.mirror = true;
		setRotation(leftSiding, 0F, 0F, 0F);
		topSiding = new ModelRenderer(this, 7, 8);
		topSiding.addBox(-5F, 8F, 7F, 10, 3, 1);
		topSiding.setRotationPoint(0F, 0F, 0F);
		topSiding.setTextureSize(128, 128);
		topSiding.mirror = true;
		setRotation(topSiding, 0F, 0F, 0F);
		botSiding = new ModelRenderer(this, 7, 8);
		botSiding.addBox(-5F, 21F, 7F, 10, 3, 1);
		botSiding.setRotationPoint(0F, 0F, 0F);
		botSiding.setTextureSize(128, 128);
		botSiding.mirror = true;
		setRotation(botSiding, 0F, 0F, 0F);
		botRightOut = new ModelRenderer(this, 0, 0);
		botRightOut.addBox(-7F, 21F, 7F, 2, 3, 1);
		botRightOut.setRotationPoint(0F, 0F, 0F);
		botRightOut.setTextureSize(128, 128);
		botRightOut.mirror = true;
		setRotation(botRightOut, 0F, 0F, 0F);
		botLeftOut = new ModelRenderer(this, 0, 0);
		botLeftOut.addBox(5F, 21F, 7F, 2, 3, 1);
		botLeftOut.setRotationPoint(0F, 0F, 0F);
		botLeftOut.setTextureSize(128, 128);
		botLeftOut.mirror = true;
		setRotation(botLeftOut, 0F, 0F, 0F);
		topRightOut = new ModelRenderer(this, 0, 0);
		topRightOut.addBox(-7F, 8F, 7F, 2, 3, 1);
		topRightOut.setRotationPoint(0F, 0F, 0F);
		topRightOut.setTextureSize(128, 128);
		topRightOut.mirror = true;
		setRotation(topRightOut, 0F, 0F, 0F);
		topLeftOut = new ModelRenderer(this, 0, 0);
		topLeftOut.addBox(5F, 8F, 7F, 2, 3, 1);
		topLeftOut.setRotationPoint(0F, 0F, 0F);
		topLeftOut.setTextureSize(128, 128);
		topLeftOut.mirror = true;
		setRotation(topLeftOut, 0F, 0F, 0F);
		leftSide = new ModelRenderer(this, 0, 19);
		leftSide.addBox(7F, 11F, 7F, 1, 10, 1);
		leftSide.setRotationPoint(0F, 0F, 0F);
		leftSide.setTextureSize(128, 128);
		leftSide.mirror = true;
		setRotation(leftSide, 0F, 0F, 0F);
		leftTopSide = new ModelRenderer(this, 0, 0);
		leftTopSide.addBox(7F, 8F, 7F, 1, 3, 1);
		leftTopSide.setRotationPoint(0F, 0F, 0F);
		leftTopSide.setTextureSize(128, 128);
		leftTopSide.mirror = true;
		setRotation(leftTopSide, 0F, 0F, 0F);
		leftBotSide = new ModelRenderer(this, 0, 0);
		leftBotSide.addBox(7F, 21F, 7F, 1, 3, 1);
		leftBotSide.setRotationPoint(0F, 0F, 0F);
		leftBotSide.setTextureSize(128, 128);
		leftBotSide.mirror = true;
		setRotation(leftBotSide, 0F, 0F, 0F);
		rightBotSide = new ModelRenderer(this, 0, 0);
		rightBotSide.addBox(-8F, 21F, 7F, 1, 3, 1);
		rightBotSide.setRotationPoint(0F, 0F, 0F);
		rightBotSide.setTextureSize(128, 128);
		rightBotSide.mirror = true;
		setRotation(rightBotSide, 0F, 0F, 0F);
		rightSide = new ModelRenderer(this, 0, 19);
		rightSide.addBox(-8F, 11F, 7F, 1, 10, 1);
		rightSide.setRotationPoint(0F, 0F, 0F);
		rightSide.setTextureSize(128, 128);
		rightSide.mirror = true;
		setRotation(rightSide, 0F, 0F, 0F);
		rightTopSide = new ModelRenderer(this, 0, 0);
		rightTopSide.addBox(-8F, 8F, 7F, 1, 3, 1);
		rightTopSide.setRotationPoint(0F, 0F, 0F);
		rightTopSide.setTextureSize(128, 128);
		rightTopSide.mirror = true;
		setRotation(rightTopSide, 0F, 0F, 0F);
	}

	/**
	 * 
	 * @param size - render size normal is 0.0625F
	 * @param left - is the an instance of this to the left
	 * @param right - "" to the right
	 * @param bot - "" to the bot
	 * @param top - "" to the top
	 * 
	 * Not this only renders one side of the block. You will need to rotate it to face another
	 * direction then render it. If rotating up or down you will need to translate it a bit
	 */
	public void render(float size, boolean left, boolean right, boolean bot, boolean top)
	{
		if (!top)
		{
			topSiding.render(size);
			topRightOut.render(size);
			topLeftOut.render(size);
			if (right)
			{
				rightTopSide.render(size);
			}
			if (left)
			{
				leftTopSide.render(size);
			}
		}
		if (!bot)
		{
			botSiding.render(size);
			botRightOut.render(size);
			botLeftOut.render(size);
			if (right)
			{
				rightBotSide.render(size);
			}
			if (left)
			{
				leftBotSide.render(size);
			}
		}
		if (!right)
		{
			rightSiding.render(size);
			rightBotSide.render(size);
			rightTopSide.render(size);
			rightSide.render(size);

			if (top)
			{
				topRightOut.render(size);

			}
			if (bot)
			{
				botRightOut.render(size);
			}
		}
		if (!left)
		{
			leftSiding.render(size);
			leftBotSide.render(size);
			leftTopSide.render(size);
			leftSide.render(size);

			if (top)
			{
				topLeftOut.render(size);
			}
			if (bot)
			{
				botLeftOut.render(size);
			}
		}

	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
