package assemblyline.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelLaserDrill extends ModelBase
{
	// fields
	ModelRenderer Spitze;
	ModelRenderer Upper_plating;
	ModelRenderer Middle_plating;
	ModelRenderer Shape3_1;
	ModelRenderer Shape3_2;
	ModelRenderer Shape3_3;
	ModelRenderer Shape3_4;
	ModelRenderer lower_plating;
	ModelRenderer lower_plating_2;
	ModelRenderer Hubbel_1;
	ModelRenderer Hubbel_2;
	ModelRenderer Hubbel_3;
	ModelRenderer Hubbel_4;
	ModelRenderer Querbatzen;

	public ModelLaserDrill()
	{
		textureWidth = 64;
		textureHeight = 32;

		Spitze = new ModelRenderer(this, 0, 8);
		Spitze.addBox(-1F, 0F, -1F, 2, 16, 2);
		Spitze.setRotationPoint(0F, 8F, 0F);
		Spitze.setTextureSize(64, 32);
		Spitze.mirror = true;
		setRotation(Spitze, 0F, 0F, 0F);
		Upper_plating = new ModelRenderer(this, 0, 0);
		Upper_plating.addBox(-2F, 0F, -2F, 4, 1, 4);
		Upper_plating.setRotationPoint(0F, 11F, 0F);
		Upper_plating.setTextureSize(64, 32);
		Upper_plating.mirror = true;
		setRotation(Upper_plating, 0F, 0F, 0F);
		Middle_plating = new ModelRenderer(this, 16, 0);
		Middle_plating.addBox(-3F, 0F, -3F, 6, 1, 6);
		Middle_plating.setRotationPoint(0F, 15F, 0F);
		Middle_plating.setTextureSize(64, 32);
		Middle_plating.mirror = true;
		setRotation(Middle_plating, 0F, 0F, 0F);
		Shape3_1 = new ModelRenderer(this, 8, 8);
		Shape3_1.addBox(2F, 0F, -1F, 1, 6, 2);
		Shape3_1.setRotationPoint(0F, 11F, 0F);
		Shape3_1.setTextureSize(64, 32);
		Shape3_1.mirror = true;
		setRotation(Shape3_1, 0F, -3.141593F, 0F);
		Shape3_2 = new ModelRenderer(this, 8, 8);
		Shape3_2.addBox(2F, 0F, -1F, 1, 6, 2);
		Shape3_2.setRotationPoint(0F, 11F, 0F);
		Shape3_2.setTextureSize(64, 32);
		Shape3_2.mirror = true;
		setRotation(Shape3_2, 0F, -1.570796F, 0F);
		Shape3_3 = new ModelRenderer(this, 8, 8);
		Shape3_3.addBox(2F, 0F, -1F, 1, 6, 2);
		Shape3_3.setRotationPoint(0F, 11F, 0F);
		Shape3_3.setTextureSize(64, 32);
		Shape3_3.mirror = true;
		setRotation(Shape3_3, 0F, 1.570796F, 0F);
		Shape3_4 = new ModelRenderer(this, 8, 8);
		Shape3_4.addBox(2F, 0F, -1F, 1, 6, 2);
		Shape3_4.setRotationPoint(0F, 11F, 0F);
		Shape3_4.setTextureSize(64, 32);
		Shape3_4.mirror = true;
		setRotation(Shape3_4, 0F, 0F, 0F);
		lower_plating = new ModelRenderer(this, 40, 0);
		lower_plating.addBox(-2F, 0F, -2F, 4, 1, 4);
		lower_plating.setRotationPoint(0F, 18F, 0F);
		lower_plating.setTextureSize(64, 32);
		lower_plating.mirror = true;
		setRotation(lower_plating, 0F, 0F, 0F);
		lower_plating_2 = new ModelRenderer(this, 40, 0);
		lower_plating_2.addBox(-2F, 0F, -2F, 4, 1, 4);
		lower_plating_2.setRotationPoint(0F, 20F, 0F);
		lower_plating_2.setTextureSize(64, 32);
		lower_plating_2.mirror = true;
		setRotation(lower_plating_2, 0F, 0F, 0F);
		Hubbel_1 = new ModelRenderer(this, 56, 0);
		Hubbel_1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1);
		Hubbel_1.setRotationPoint(0F, 12F, 3F);
		Hubbel_1.setTextureSize(64, 32);
		Hubbel_1.mirror = true;
		setRotation(Hubbel_1, 0F, 0.7853982F, 0F);
		Hubbel_2 = new ModelRenderer(this, 56, 0);
		Hubbel_2.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1);
		Hubbel_2.setRotationPoint(3F, 12F, 0F);
		Hubbel_2.setTextureSize(64, 32);
		Hubbel_2.mirror = true;
		setRotation(Hubbel_2, 0F, 0.7853982F, 0F);
		Hubbel_3 = new ModelRenderer(this, 56, 0);
		Hubbel_3.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1);
		Hubbel_3.setRotationPoint(0F, 12F, -3F);
		Hubbel_3.setTextureSize(64, 32);
		Hubbel_3.mirror = true;
		setRotation(Hubbel_3, 0F, 0.7853982F, 0F);
		Hubbel_4 = new ModelRenderer(this, 56, 0);
		Hubbel_4.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1);
		Hubbel_4.setRotationPoint(-3F, 12F, 0F);
		Hubbel_4.setTextureSize(64, 32);
		Hubbel_4.mirror = true;
		setRotation(Hubbel_4, 0F, 0.7853982F, 0F);
		Querbatzen = new ModelRenderer(this, 14, 8);
		Querbatzen.addBox(-0.5F, -0.5F, -0.5F, 3, 1, 3);
		Querbatzen.setRotationPoint(-1.5F, 11F, 0F);
		Querbatzen.setTextureSize(64, 32);
		Querbatzen.mirror = true;
		setRotation(Querbatzen, 0F, 0.7853982F, 0F);
	}

	public void render(float rotation, float f5)
	{
		Spitze.render(f5);
		Upper_plating.render(f5);
		Middle_plating.render(f5);
		Shape3_1.render(f5);
		Shape3_2.render(f5);
		Shape3_3.render(f5);
		Shape3_4.render(f5);
		lower_plating.render(f5);
		lower_plating_2.render(f5);
		Hubbel_1.render(f5);
		Hubbel_2.render(f5);
		Hubbel_3.render(f5);
		Hubbel_4.render(f5);
		Querbatzen.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
