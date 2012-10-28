package assemblyline.model;

import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelDropBox extends ModelBase
{
	// fields
	ModelRenderer Left;
	ModelRenderer Top;
	ModelRenderer Front;
	ModelRenderer LeftB;
	ModelRenderer Back;
	ModelRenderer Right;
	ModelRenderer RightB;
	ModelRenderer Bottom;
	ModelRenderer Back3;
	ModelRenderer Back2;

	public ModelDropBox()
	{
		textureWidth = 128;
		textureHeight = 128;

		Left = new ModelRenderer(this, 33, 43);
		Left.addBox(7F, -10F, -8F, 1, 7, 15);
		Left.setRotationPoint(0F, 19F, 0F);
		Left.setTextureSize(128, 128);
		Left.mirror = true;
		setRotation(Left, 0F, 0F, 0F);
		Top = new ModelRenderer(this, 0, 65);
		Top.addBox(-7F, -11F, -7F, 14, 1, 14);
		Top.setRotationPoint(0F, 19F, 0F);
		Top.setTextureSize(128, 128);
		Top.mirror = true;
		setRotation(Top, 0F, 0F, 0F);
		Front = new ModelRenderer(this, 0, 33);
		Front.addBox(-8F, -4F, -7.5F, 16, 5, 1);
		Front.setRotationPoint(0F, 19F, 0F);
		Front.setTextureSize(128, 128);
		Front.mirror = true;
		setRotation(Front, 0.5235988F, 0F, 0F);
		LeftB = new ModelRenderer(this, 36, 21);
		LeftB.addBox(7F, -3F, -6F, 1, 8, 13);
		LeftB.setRotationPoint(0F, 19F, 0F);
		LeftB.setTextureSize(128, 128);
		LeftB.mirror = true;
		setRotation(LeftB, 0F, 0F, 0F);
		Back = new ModelRenderer(this, 0, 96);
		Back.addBox(-5F, -8F, 4F, 10, 10, 4);
		Back.setRotationPoint(0F, 19F, 0F);
		Back.setTextureSize(128, 128);
		Back.mirror = true;
		setRotation(Back, 0F, 0F, 0F);
		Right = new ModelRenderer(this, 0, 42);
		Right.addBox(-8F, -10F, -8F, 1, 7, 15);
		Right.setRotationPoint(0F, 19F, 0F);
		Right.setTextureSize(128, 128);
		Right.mirror = true;
		setRotation(Right, 0F, 0F, 0F);
		RightB = new ModelRenderer(this, 36, 0);
		RightB.addBox(-8F, -3F, -6F, 1, 8, 13);
		RightB.setRotationPoint(0F, 19F, 0F);
		RightB.setTextureSize(128, 128);
		RightB.mirror = true;
		setRotation(RightB, 0F, 0F, 0F);
		Bottom = new ModelRenderer(this, 0, 81);
		Bottom.addBox(-7F, 4F, -6F, 14, 1, 13);
		Bottom.setRotationPoint(0F, 19F, 0F);
		Bottom.setTextureSize(128, 128);
		Bottom.mirror = true;
		setRotation(Bottom, 0F, 0F, 0F);
		Back3 = new ModelRenderer(this, 0, 23);
		Back3.addBox(-7F, -10F, -9F, 14, 8, 1);
		Back3.setRotationPoint(0F, 19F, 0F);
		Back3.setTextureSize(128, 128);
		Back3.mirror = true;
		setRotation(Back3, -0.1919862F, 0F, 0F);
		Back2 = new ModelRenderer(this, 0, 7);
		Back2.addBox(-7F, -10F, 3F, 14, 14, 1);
		Back2.setRotationPoint(0F, 19F, 0F);
		Back2.setTextureSize(128, 128);
		Back2.mirror = true;
		setRotation(Back2, 0F, 0F, 0F);
	}

	public void render(float f5)
	{
		Left.render(f5);
		Top.render(f5);
		Front.render(f5);
		LeftB.render(f5);
		Back.render(f5);
		Right.render(f5);
		RightB.render(f5);
		Bottom.render(f5);
		Back.render(f5);
		Back.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
