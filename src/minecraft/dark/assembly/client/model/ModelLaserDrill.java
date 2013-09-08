package dark.assembly.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class ModelLaserDrill extends ModelBase
{
    // fields
    ModelRenderer Tip;
    ModelRenderer Upper_plating;
    ModelRenderer Middle_plating;
    ModelRenderer Shape3_1;
    ModelRenderer Shape3_2;
    ModelRenderer Shape3_3;
    ModelRenderer Shape3_4;
    ModelRenderer LowerPlating_1;
    ModelRenderer LowerPlating_2;
    ModelRenderer Bump_1;
    ModelRenderer Bump_2;
    ModelRenderer Bump_3;
    ModelRenderer Bump_4;
    ModelRenderer Cross;

    public ModelLaserDrill()
    {
        textureWidth = 64;
        textureHeight = 32;

        Tip = new ModelRenderer(this, 0, 8);
        Tip.addBox(-1F, 0F, -1F, 2, 16, 2);
        Tip.setRotationPoint(0F, 8F, 0F);
        Tip.setTextureSize(64, 32);
        Tip.mirror = true;
        setRotation(Tip, 0F, 0F, 0F);
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
        LowerPlating_1 = new ModelRenderer(this, 40, 0);
        LowerPlating_1.addBox(-2F, 0F, -2F, 4, 1, 4);
        LowerPlating_1.setRotationPoint(0F, 18F, 0F);
        LowerPlating_1.setTextureSize(64, 32);
        LowerPlating_1.mirror = true;
        setRotation(LowerPlating_1, 0F, 0F, 0F);
        LowerPlating_2 = new ModelRenderer(this, 40, 0);
        LowerPlating_2.addBox(-2F, 0F, -2F, 4, 1, 4);
        LowerPlating_2.setRotationPoint(0F, 20F, 0F);
        LowerPlating_2.setTextureSize(64, 32);
        LowerPlating_2.mirror = true;
        setRotation(LowerPlating_2, 0F, 0F, 0F);
        Bump_1 = new ModelRenderer(this, 56, 0);
        Bump_1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1);
        Bump_1.setRotationPoint(0F, 12F, 3F);
        Bump_1.setTextureSize(64, 32);
        Bump_1.mirror = true;
        setRotation(Bump_1, 0F, 0.7853982F, 0F);
        Bump_2 = new ModelRenderer(this, 56, 0);
        Bump_2.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1);
        Bump_2.setRotationPoint(3F, 12F, 0F);
        Bump_2.setTextureSize(64, 32);
        Bump_2.mirror = true;
        setRotation(Bump_2, 0F, 0.7853982F, 0F);
        Bump_3 = new ModelRenderer(this, 56, 0);
        Bump_3.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1);
        Bump_3.setRotationPoint(0F, 12F, -3F);
        Bump_3.setTextureSize(64, 32);
        Bump_3.mirror = true;
        setRotation(Bump_3, 0F, 0.7853982F, 0F);
        Bump_4 = new ModelRenderer(this, 56, 0);
        Bump_4.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1);
        Bump_4.setRotationPoint(-3F, 12F, 0F);
        Bump_4.setTextureSize(64, 32);
        Bump_4.mirror = true;
        setRotation(Bump_4, 0F, 0.7853982F, 0F);
        Cross = new ModelRenderer(this, 14, 8);
        Cross.addBox(-0.5F, -0.5F, -0.5F, 3, 1, 3);
        Cross.setRotationPoint(-1.5F, 11F, 0F);
        Cross.setTextureSize(64, 32);
        Cross.mirror = true;
        setRotation(Cross, 0F, 0.7853982F, 0F);
    }

    public void render(float rotation, float f5)
    {
        Tip.render(f5);
        Upper_plating.render(f5);
        Middle_plating.render(f5);
        Shape3_1.render(f5);
        Shape3_2.render(f5);
        Shape3_3.render(f5);
        Shape3_4.render(f5);
        LowerPlating_1.render(f5);
        LowerPlating_2.render(f5);
        Bump_1.render(f5);
        Bump_2.render(f5);
        Bump_3.render(f5);
        Bump_4.render(f5);
        Cross.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
