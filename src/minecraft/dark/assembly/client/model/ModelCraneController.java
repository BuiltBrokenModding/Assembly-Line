// Date: 1/27/2013 2:45:10 AM
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX

package dark.assembly.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
@SideOnly(Side.CLIENT)
public class ModelCraneController extends ModelBase
{
    // fields
    ModelRenderer Base2;
    ModelRenderer Base;
    ModelRenderer ConnectorFront;
    ModelRenderer Decoration1;
    ModelRenderer Decoration2;
    ModelRenderer Decoration3;
    ModelRenderer Decoration4;
    ModelRenderer ConnectorTop;
    ModelRenderer ConnectorRight;

    public ModelCraneController()
    {
        textureWidth = 128;
        textureHeight = 64;

        Base2 = new ModelRenderer(this, 0, 24);
        Base2.addBox(0F, 0F, 0F, 12, 4, 12);
        Base2.setRotationPoint(-6F, 12F, -6F);
        Base2.setTextureSize(128, 64);
        Base2.mirror = true;
        setRotation(Base2, 0F, 0F, 0F);
        Base = new ModelRenderer(this, 0, 0);
        Base.addBox(0F, 0F, 0F, 16, 8, 16);
        Base.setRotationPoint(-8F, 16F, -8F);
        Base.setTextureSize(128, 64);
        Base.mirror = true;
        setRotation(Base, 0F, 0F, 0F);
        ConnectorFront = new ModelRenderer(this, 64, 0);
        ConnectorFront.addBox(0F, 0F, 0F, 8, 8, 8);
        ConnectorFront.setRotationPoint(-4F, 12F, 0F);
        ConnectorFront.setTextureSize(128, 64);
        ConnectorFront.mirror = true;
        setRotation(ConnectorFront, 0F, 1.570796F, 0F);
        Decoration1 = new ModelRenderer(this, 54, 24);
        Decoration1.addBox(0F, 0F, 0F, 2, 1, 1);
        Decoration1.setRotationPoint(2F, 15F, 6F);
        Decoration1.setTextureSize(128, 64);
        Decoration1.mirror = true;
        setRotation(Decoration1, 0F, 0F, 0F);
        Decoration2 = new ModelRenderer(this, 54, 24);
        Decoration2.addBox(0F, 0F, 0F, 2, 1, 1);
        Decoration2.setRotationPoint(-4F, 15F, 6F);
        Decoration2.setTextureSize(128, 64);
        Decoration2.mirror = true;
        setRotation(Decoration2, 0F, 0F, 0F);
        Decoration3 = new ModelRenderer(this, 48, 24);
        Decoration3.addBox(0F, 0F, 0F, 1, 1, 2);
        Decoration3.setRotationPoint(-7F, 15F, 2F);
        Decoration3.setTextureSize(128, 64);
        Decoration3.mirror = true;
        setRotation(Decoration3, 0F, 0F, 0F);
        Decoration4 = new ModelRenderer(this, 48, 24);
        Decoration4.addBox(0F, 0F, 0F, 1, 1, 2);
        Decoration4.setRotationPoint(-7F, 15F, -4F);
        Decoration4.setTextureSize(128, 64);
        Decoration4.mirror = true;
        setRotation(Decoration4, 0F, 0F, 0F);
        ConnectorTop = new ModelRenderer(this, 64, 0);
        ConnectorTop.addBox(0F, 0F, 0F, 8, 8, 8);
        ConnectorTop.setRotationPoint(-4F, 16F, -4F);
        ConnectorTop.setTextureSize(128, 64);
        ConnectorTop.mirror = true;
        setRotation(ConnectorTop, 0F, 0F, -1.570796F);
        ConnectorRight = new ModelRenderer(this, 64, 0);
        ConnectorRight.addBox(0F, 0F, 0F, 8, 8, 8);
        ConnectorRight.setRotationPoint(0F, 12F, -4F);
        ConnectorRight.setTextureSize(128, 64);
        ConnectorRight.mirror = true;
        setRotation(ConnectorRight, 0F, 0F, 0F);
    }

    public void render(float scale, boolean connectEast, boolean connectNorth)
    {
        Base2.setRotationPoint(-6F, 12F, -6F);
        setRotation(Base2, 0F, 0F, 0F);
        Base.setRotationPoint(-8F, 16F, -8F);
        setRotation(Base, 0F, 0F, 0F);
        Decoration1.setRotationPoint(2F, 15F, 6F);
        setRotation(Decoration1, 0F, 0F, 0F);
        Decoration2.setRotationPoint(-4F, 15F, 6F);
        setRotation(Decoration2, 0F, 0F, 0F);
        Decoration3.setRotationPoint(-7F, 15F, 2F);
        setRotation(Decoration3, 0F, 0F, 0F);
        Decoration4.setRotationPoint(-7F, 15F, -4F);
        setRotation(Decoration4, 0F, 0F, 0F);
        ConnectorTop.setRotationPoint(-4F, 16F, -4F);
        setRotation(ConnectorTop, 0F, 0F, -1.570796F);
        ConnectorFront.setRotationPoint(-4F, 11.99F, -0.01F);
        setRotation(ConnectorFront, 0F, 1.570796F, 0F);
        ConnectorRight.setRotationPoint(0.01F, 11.99F, -4F);
        setRotation(ConnectorRight, 0F, 0F, 0F);

        Base2.render(scale);
        Base.render(scale);
        ConnectorTop.render(scale);
        if (connectEast)
        {
            ConnectorFront.render(scale);
            Decoration1.render(scale);
            Decoration2.render(scale);
        }
        if (connectNorth)
        {
            ConnectorRight.render(scale);
            Decoration3.render(scale);
            Decoration4.render(scale);
        }
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}