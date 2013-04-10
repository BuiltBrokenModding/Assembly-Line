package assemblyline.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCraneArmMount extends ModelBase
{
	// fields
	private ModelRenderer RailGuard1;
	private ModelRenderer ArmMount;
	private ModelRenderer RailGuard2;
	private ModelRenderer Base;
	private ModelRenderer WheelMount1;
	private ModelRenderer Wheel2;
	private ModelRenderer WheelMount2;
	private ModelRenderer Wheel1;

	public ModelCraneArmMount()
	{
		textureWidth = 64;
		textureHeight = 64;

		RailGuard1 = new ModelRenderer(this, 0, 20);
		RailGuard1.addBox(0F, 0F, 0F, 16, 8, 2);
		RailGuard1.setRotationPoint(-8F, 12F, 4F);
		RailGuard1.setTextureSize(64, 32);
		RailGuard1.mirror = true;
		setRotation(RailGuard1, 0F, 0F, 0F);
		ArmMount = new ModelRenderer(this, 36, 26);
		ArmMount.addBox(0F, 0F, 0F, 8, 4, 2);
		ArmMount.setRotationPoint(-4F, 4F, -8F);
		ArmMount.setTextureSize(64, 32);
		ArmMount.mirror = true;
		setRotation(ArmMount, 0F, 0F, 0F);
		RailGuard2 = new ModelRenderer(this, 0, 20);
		RailGuard2.addBox(0F, 0F, 0F, 16, 8, 2);
		RailGuard2.setRotationPoint(-8F, 12F, -6F);
		RailGuard2.setTextureSize(64, 32);
		RailGuard2.mirror = true;
		setRotation(RailGuard2, 0F, 0F, 0F);
		Base = new ModelRenderer(this, 0, 0);
		Base.addBox(0F, 0F, 0F, 16, 4, 16);
		Base.setRotationPoint(-8F, 8F, -8F);
		Base.setTextureSize(64, 32);
		Base.mirror = true;
		setRotation(Base, 0F, 0F, 0F);
		WheelMount1 = new ModelRenderer(this, 0, 30);
		WheelMount1.addBox(0F, 0F, 0F, 14, 4, 4);
		WheelMount1.setRotationPoint(-7F, 4F, 2F);
		WheelMount1.setTextureSize(64, 32);
		WheelMount1.mirror = true;
		setRotation(WheelMount1, 0F, 0F, 0F);
		Wheel2 = new ModelRenderer(this, 36, 20);
		Wheel2.addBox(0F, 0F, 0F, 5, 4, 2);
		Wheel2.setRotationPoint(1F, 6F, -1F);
		Wheel2.setTextureSize(64, 32);
		Wheel2.mirror = true;
		setRotation(Wheel2, 0F, 0F, 0F);
		WheelMount2 = new ModelRenderer(this, 0, 30);
		WheelMount2.addBox(0F, 0F, 0F, 14, 4, 4);
		WheelMount2.setRotationPoint(-7F, 4F, -6F);
		WheelMount2.setTextureSize(64, 32);
		WheelMount2.mirror = true;
		setRotation(WheelMount2, 0F, 0F, 0F);
		Wheel1 = new ModelRenderer(this, 36, 20);
		Wheel1.addBox(0F, 0F, 0F, 5, 4, 2);
		Wheel1.setRotationPoint(-6F, 6F, -1F);
		Wheel1.setTextureSize(64, 32);
		Wheel1.mirror = true;
		setRotation(Wheel1, 0F, 0F, 0F);
	}

	public void render(float scale)
	{
		RailGuard1.render(scale);
		ArmMount.render(scale);
		RailGuard2.render(scale);
		Base.render(scale);
		WheelMount1.render(scale);
		Wheel2.render(scale);
		WheelMount2.render(scale);
		Wheel1.render(scale);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
