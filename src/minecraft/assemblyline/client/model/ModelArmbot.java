package assemblyline.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelArmbot extends ModelBase
{
	// fields
	ModelRenderer BaseTop;
	ModelRenderer Base;
	ModelRenderer ArmMountRight;
	ModelRenderer ArmMountLeft;
	ModelRenderer ArmLower;
	ModelRenderer ArmUpper;
	ModelRenderer BaseRotation;
	ModelRenderer ClampBody;
	ModelRenderer ClampBody2;
	ModelRenderer ClampClawLower;
	ModelRenderer ClampClawLower2;

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
		ArmMountRight = new ModelRenderer(this, 0, 0);
		ArmMountRight.addBox(-4F, -5F, -1.5F, 2, 5, 3);
		ArmMountRight.setRotationPoint(0F, 17F, 0F);
		ArmMountRight.setTextureSize(64, 32);
		ArmMountRight.mirror = true;
		setRotation(ArmMountRight, 0F, 0F, 0F);
		ArmMountLeft = new ModelRenderer(this, 0, 0);
		ArmMountLeft.addBox(2F, -5F, -1.5F, 2, 5, 3);
		ArmMountLeft.setRotationPoint(0F, 17F, 0F);
		ArmMountLeft.setTextureSize(64, 32);
		ArmMountLeft.mirror = true;
		setRotation(ArmMountLeft, 0F, 0F, 0F);
		ArmLower = new ModelRenderer(this, 0, 0);
		ArmLower.addBox(-2F, -15F, -2.5F, 4, 16, 5);
		ArmLower.setRotationPoint(0F, 14F, 0F);
		ArmLower.setTextureSize(64, 32);
		ArmLower.mirror = true;
		setRotation(ArmLower, 0.5235988F, 0F, 0F);
		ArmUpper = new ModelRenderer(this, 0, 0);
		ArmUpper.addBox(-1.5F, -15F, -2F, 3, 16, 4);
		ArmUpper.setRotationPoint(0F, 2F, -7F);
		ArmUpper.setTextureSize(64, 32);
		ArmUpper.mirror = true;
		setRotation(ArmUpper, 2.007129F, 0F, 0F);
		BaseRotation = new ModelRenderer(this, 0, 0);
		BaseRotation.addBox(-4.5F, 0F, -4.5F, 9, 1, 9);
		BaseRotation.setRotationPoint(0F, 17F, 0F);
		BaseRotation.setTextureSize(64, 32);
		BaseRotation.mirror = true;
		setRotation(BaseRotation, 0F, 0F, 0F);
		ClampBody = new ModelRenderer(this, 0, 0);
		ClampBody.addBox(-2F, -17F, -2.5F, 4, 2, 5);
		ClampBody.setRotationPoint(0F, 2F, -7F);
		ClampBody.setTextureSize(64, 32);
		ClampBody.mirror = true;
		setRotation(ClampBody, 2.007129F, 0F, 0F);
		ClampBody2 = new ModelRenderer(this, 0, 0);
		ClampBody2.addBox(-1.5F, -19F, -1F, 3, 2, 2);
		ClampBody2.setRotationPoint(0F, 2F, -7F);
		ClampBody2.setTextureSize(64, 32);
		ClampBody2.mirror = true;
		setRotation(ClampBody2, 2.007129F, 0F, 0F);
		ClampClawLower = new ModelRenderer(this, 0, 0);
		ClampClawLower.addBox(-2.5F, -5F, -1F, 5, 6, 1);
		ClampClawLower.setRotationPoint(0F, 10F, -23F);
		ClampClawLower.setTextureSize(64, 32);
		ClampClawLower.mirror = true;
		setRotation(ClampClawLower, 2.007129F, 0F, 0F);
		ClampClawLower2 = new ModelRenderer(this, 0, 0);
		ClampClawLower2.addBox(-2.5F, -5F, 1F, 5, 6, 1);
		ClampClawLower2.setRotationPoint(0F, 10F, -23F);
		ClampClawLower2.setTextureSize(64, 32);
		ClampClawLower2.mirror = true;
		setRotation(ClampClawLower2, 2.007129F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		this.render(f5);
	}

	public void render(float f5)
	{
		BaseTop.render(f5);
		Base.render(f5);
		ArmMountRight.render(f5);
		ArmMountLeft.render(f5);
		ArmLower.render(f5);
		ArmUpper.render(f5);
		BaseRotation.render(f5);
		ClampBody.render(f5);
		ClampBody2.render(f5);
		ClampClawLower.render(f5);
		ClampClawLower2.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
