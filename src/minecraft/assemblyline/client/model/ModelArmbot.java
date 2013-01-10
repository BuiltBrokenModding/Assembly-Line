package assemblyline.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import assemblyline.common.machine.armbot.TileEntityArmbot;

public class ModelArmbot extends ModelBase
{
	// fields
	private ModelRenderer BaseTop;
	private ModelRenderer Base;
	private ModelRenderer armMountRight;
	private ModelRenderer armMountLeft;
	private ModelRenderer armLower;
	private ModelRenderer armUpper;
	private ModelRenderer baseRotation;
	private ModelRenderer clampBody;
	private ModelRenderer clampBody2;
	private ModelRenderer clampClawLower;
	private ModelRenderer clampClawLower2;

	public ModelArmbot()
	{
		textureWidth = 128;
		textureHeight = 128;

		BaseTop = new ModelRenderer(this, 0, 0);
		BaseTop.addBox(-6F, 0F, -6F, 12, 3, 12);
		BaseTop.setRotationPoint(0F, 18F, 0F);
		BaseTop.setTextureSize(64, 32);
		BaseTop.mirror = true;
		setRotation(BaseTop, 0F, 0F, 0F);
		Base = new ModelRenderer(this, 0, 0);
		Base.addBox(-8F, 0F, -8F, 16, 3, 16);
		Base.setRotationPoint(0F, 21F, 0F);
		Base.setTextureSize(64, 32);
		Base.mirror = true;
		setRotation(Base, 0F, 0F, 0F);
		armMountRight = new ModelRenderer(this, 0, 0);
		armMountRight.addBox(-4F, -5F, -1.5F, 2, 5, 3);
		armMountRight.setRotationPoint(0F, 17F, 0F);
		armMountRight.setTextureSize(64, 32);
		armMountRight.mirror = true;
		setRotation(armMountRight, 0F, 0F, 0F);
		armMountLeft = new ModelRenderer(this, 0, 0);
		armMountLeft.addBox(2F, -5F, -1.5F, 2, 5, 3);
		armMountLeft.setRotationPoint(0F, 17F, 0F);
		armMountLeft.setTextureSize(64, 32);
		armMountLeft.mirror = true;
		setRotation(armMountLeft, 0F, 0F, 0F);
		armLower = new ModelRenderer(this, 0, 0);
		armLower.addBox(-2F, -15F, -2.5F, 4, 16, 5);
		armLower.setRotationPoint(0F, 14F, 0F);
		armLower.setTextureSize(64, 32);
		armLower.mirror = true;
		setRotation(armLower, 0.5235988F, 0F, 0F);
		armUpper = new ModelRenderer(this, 0, 0);
		armUpper.addBox(-1.5F, -15F, -2F, 3, 16, 4);
		armUpper.setRotationPoint(0F, 2F, -7F);
		armUpper.setTextureSize(64, 32);
		armUpper.mirror = true;
		setRotation(armUpper, 2.007129F, 0F, 0F);
		baseRotation = new ModelRenderer(this, 0, 0);
		baseRotation.addBox(-4.5F, 0F, -4.5F, 9, 1, 9);
		baseRotation.setRotationPoint(0F, 17F, 0F);
		baseRotation.setTextureSize(64, 32);
		baseRotation.mirror = true;
		setRotation(baseRotation, 0F, 0F, 0F);
		clampBody = new ModelRenderer(this, 0, 0);
		clampBody.addBox(-2F, -17F, -2.5F, 4, 2, 5);
		clampBody.setRotationPoint(0F, 2F, -7F);
		clampBody.setTextureSize(64, 32);
		clampBody.mirror = true;
		setRotation(clampBody, 2.007129F, 0F, 0F);
		clampBody2 = new ModelRenderer(this, 0, 0);
		clampBody2.addBox(-1.5F, -19F, -1F, 3, 2, 2);
		clampBody2.setRotationPoint(0F, 2F, -7F);
		clampBody2.setTextureSize(64, 32);
		clampBody2.mirror = true;
		setRotation(clampBody2, 2.007129F, 0F, 0F);
		clampClawLower = new ModelRenderer(this, 0, 0);
		clampClawLower.addBox(-2.5F, -5F, -1F, 5, 6, 1);
		clampClawLower.setRotationPoint(0F, 10F, -23F);
		clampClawLower.setTextureSize(64, 32);
		clampClawLower.mirror = true;
		setRotation(clampClawLower, 2.007129F, 0F, 0F);
		clampClawLower2 = new ModelRenderer(this, 0, 0);
		clampClawLower2.addBox(-2.5F, -5F, 1F, 5, 6, 1);
		clampClawLower2.setRotationPoint(0F, 10F, -23F);
		clampClawLower2.setTextureSize(64, 32);
		clampClawLower2.mirror = true;
		setRotation(clampClawLower2, 2.007129F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		this.render(f5, null);
	}

	public void render(float f5, TileEntityArmbot armBot)
	{

		// set yaw from tileentity
		if (armBot != null)
		{
			/*armMountRight.setRotationPoint(baseRotation.rotationPointX, armMountRight.rotationPointY, baseRotation.rotationPointX);
			armMountLeft.setRotationPoint(baseRotation.rotationPointX, armMountLeft.rotationPointY, baseRotation.rotationPointX);
			armLower.setRotationPoint(baseRotation.rotationPointX, armLower.rotationPointY, baseRotation.rotationPointX);
			armUpper.setRotationPoint(baseRotation.rotationPointX, armUpper.rotationPointY, baseRotation.rotationPointX);
			baseRotation.setRotationPoint(baseRotation.rotationPointX, baseRotation.rotationPointY, baseRotation.rotationPointX);
			clampBody.setRotationPoint(baseRotation.rotationPointX, clampBody.rotationPointY, baseRotation.rotationPointX);
			clampBody2.setRotationPoint(baseRotation.rotationPointX, clampBody2.rotationPointY, baseRotation.rotationPointX);
			clampClawLower.setRotationPoint(baseRotation.rotationPointX, clampClawLower.rotationPointY, baseRotation.rotationPointX);
			clampClawLower2.setRotationPoint(baseRotation.rotationPointX, clampClawLower2.rotationPointY, baseRotation.rotationPointX);

			armMountRight.rotateAngleY = armBot.rotationYaw;
			armMountLeft.rotateAngleY = armBot.rotationYaw;
			armLower.rotateAngleY = armBot.rotationYaw;
			armUpper.rotateAngleY = armBot.rotationYaw;
			baseRotation.rotateAngleY = armBot.rotationYaw;
			clampBody.rotateAngleY = armBot.rotationYaw;
			clampBody2.rotateAngleY = armBot.rotationYaw;
			clampClawLower.rotateAngleY = armBot.rotationYaw;
			clampClawLower2.rotateAngleY = armBot.rotationYaw;*/
			BaseTop.render(f5);
			Base.render(f5);
			GL11.glPushMatrix();
			GL11.glRotatef((float) (armBot.rotationYaw * (180f / Math.PI)), 0, 1, 0);
			armMountRight.render(f5);
			armMountLeft.render(f5);
			armLower.render(f5);
			armUpper.render(f5);
			baseRotation.render(f5);
			clampBody.render(f5);
			clampBody2.render(f5);
			clampClawLower.render(f5);
			clampClawLower2.render(f5);
			GL11.glPopMatrix();
		}
		else
		{
			/*armMountRight.setRotationPoint(0F, 17F, 0F);
			setRotation(armMountRight, 0F, 0F, 0F);
			armMountLeft.setRotationPoint(0F, 17F, 0F);
			setRotation(armMountLeft, 0F, 0F, 0F);
			armLower.setRotationPoint(0F, 14F, 0F);
			setRotation(armLower, 0.5235988F, 0F, 0F);
			armUpper.setRotationPoint(0F, 2F, -7F);
			setRotation(armUpper, 2.007129F, 0F, 0F);
			baseRotation.setRotationPoint(0F, 17F, 0F);
			setRotation(baseRotation, 0F, 0F, 0F);
			clampBody.setRotationPoint(0F, 2F, -7F);
			setRotation(clampBody, 2.007129F, 0F, 0F);
			clampBody2.setRotationPoint(0F, 2F, -7F);
			setRotation(clampBody2, 2.007129F, 0F, 0F);
			clampClawLower.setRotationPoint(0F, 10F, -23F);
			setRotation(clampClawLower, 2.007129F, 0F, 0F);
			clampClawLower2.setRotationPoint(0F, 10F, -23F);
			setRotation(clampClawLower2, 2.007129F, 0F, 0F);*/
			BaseTop.render(f5);
			Base.render(f5);
			armMountRight.render(f5);
			armMountLeft.render(f5);
			armLower.render(f5);
			armUpper.render(f5);
			baseRotation.render(f5);
			clampBody.render(f5);
			clampBody2.render(f5);
			clampClawLower.render(f5);
			clampClawLower2.render(f5);
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
