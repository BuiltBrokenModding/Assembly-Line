package dark.assembly.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCraneRail extends ModelBase
{
    // left
    ModelRenderer SegmentUBLeft;
    ModelRenderer SegmentUFLeft;
    ModelRenderer SegmentBFLeft;
    ModelRenderer SegmentBBLeft;
    // back
    ModelRenderer SegmentBLBack;
    ModelRenderer SegmentLUBack;
    ModelRenderer SegmentRUBack;
    ModelRenderer SegmentBRBack;
    // front
    ModelRenderer SegmentBLFront;
    ModelRenderer SegmentLUFront;
    ModelRenderer SegmentRUFront;
    ModelRenderer SegmentBRFront;
    // up
    ModelRenderer SegmentLBUp;
    ModelRenderer SegmentRBUp;
    ModelRenderer SegmentLFUp;
    ModelRenderer SegmentRFUp;
    // right
    ModelRenderer SegmentUBRight;
    ModelRenderer SegmentBFRight;
    ModelRenderer SegmentUFRight;
    ModelRenderer SegmentBBRight;
    // down
    ModelRenderer SegmentLFDown;
    ModelRenderer SegmentLBDown;
    ModelRenderer SegmentRBDown;
    ModelRenderer SegmentRFDown;

    // middle
    ModelRenderer SegmentBFMid;
    ModelRenderer SegmentUBMid;
    ModelRenderer SegmentBBMid;
    ModelRenderer SegmentUFMid;
    ModelRenderer SegmentLBMid;
    ModelRenderer SegmentLFMid;
    ModelRenderer SegmentRBMid;
    ModelRenderer SegmentRFMid;
    ModelRenderer SegmentRUMid;
    ModelRenderer SegmentBRMid;
    ModelRenderer SegmentBLMid;
    ModelRenderer SegmentLUMid;
    ModelRenderer SegmentMidDiag1;
    ModelRenderer SegmentMidDiag2;
    ModelRenderer SegmentMidDiag3;
    ModelRenderer SegmentMidDiag4;
    ModelRenderer SegmentMidDiag5;
    ModelRenderer SegmentMidDiag6;
    ModelRenderer SegmentMidDiag7;
    ModelRenderer SegmentMidDiag8;
    ModelRenderer FootBottom;
    ModelRenderer FootTop;

    public ModelCraneRail()
    {
        textureWidth = 64;
        textureHeight = 32;

        SegmentBLBack = new ModelRenderer(this, 10, 13);
        SegmentBLBack.addBox(0F, 0F, 0F, 1, 1, 4);
        SegmentBLBack.setRotationPoint(3F, 19F, 4F);
        SegmentBLBack.setTextureSize(64, 32);
        SegmentBLBack.mirror = true;
        setRotation(SegmentBLBack, 0F, 0F, 0F);
        SegmentLUBack = new ModelRenderer(this, 10, 13);
        SegmentLUBack.addBox(0F, 0F, 0F, 1, 1, 4);
        SegmentLUBack.setRotationPoint(3F, 12F, 4F);
        SegmentLUBack.setTextureSize(64, 32);
        SegmentLUBack.mirror = true;
        setRotation(SegmentLUBack, 0F, 0F, 0F);
        SegmentRUBack = new ModelRenderer(this, 10, 13);
        SegmentRUBack.addBox(0F, 0F, 0F, 1, 1, 4);
        SegmentRUBack.setRotationPoint(-4F, 12F, 4F);
        SegmentRUBack.setTextureSize(64, 32);
        SegmentRUBack.mirror = true;
        setRotation(SegmentRUBack, 0F, 0F, 0F);
        SegmentBRBack = new ModelRenderer(this, 10, 13);
        SegmentBRBack.addBox(0F, 0F, 0F, 1, 1, 4);
        SegmentBRBack.setRotationPoint(-4F, 19F, 4F);
        SegmentBRBack.setTextureSize(64, 32);
        SegmentBRBack.mirror = true;
        setRotation(SegmentBRBack, 0F, 0F, 0F);
        SegmentBLFront = new ModelRenderer(this, 10, 13);
        SegmentBLFront.addBox(0F, 0F, 0F, 1, 1, 4);
        SegmentBLFront.setRotationPoint(3F, 19F, -8F);
        SegmentBLFront.setTextureSize(64, 32);
        SegmentBLFront.mirror = true;
        setRotation(SegmentBLFront, 0F, 0F, 0F);
        SegmentLUFront = new ModelRenderer(this, 10, 13);
        SegmentLUFront.addBox(0F, 0F, 0F, 1, 1, 4);
        SegmentLUFront.setRotationPoint(3F, 12F, -8F);
        SegmentLUFront.setTextureSize(64, 32);
        SegmentLUFront.mirror = true;
        setRotation(SegmentLUFront, 0F, 0F, 0F);
        SegmentRUFront = new ModelRenderer(this, 10, 13);
        SegmentRUFront.addBox(0F, 0F, 0F, 1, 1, 4);
        SegmentRUFront.setRotationPoint(-4F, 12F, -8F);
        SegmentRUFront.setTextureSize(64, 32);
        SegmentRUFront.mirror = true;
        setRotation(SegmentRUFront, 0F, 0F, 0F);
        SegmentBRFront = new ModelRenderer(this, 10, 13);
        SegmentBRFront.addBox(0F, 0F, 0F, 1, 1, 4);
        SegmentBRFront.setRotationPoint(-4F, 19F, -8F);
        SegmentBRFront.setTextureSize(64, 32);
        SegmentBRFront.mirror = true;
        setRotation(SegmentBRFront, 0F, 0F, 0F);
        SegmentLBUp = new ModelRenderer(this, 20, 13);
        SegmentLBUp.addBox(0F, 0F, 0F, 1, 4, 1);
        SegmentLBUp.setRotationPoint(3F, 8F, 3F);
        SegmentLBUp.setTextureSize(64, 32);
        SegmentLBUp.mirror = true;
        setRotation(SegmentLBUp, 0F, 0F, 0F);
        SegmentRBUp = new ModelRenderer(this, 20, 13);
        SegmentRBUp.addBox(0F, 0F, 0F, 1, 4, 1);
        SegmentRBUp.setRotationPoint(-4F, 8F, 3F);
        SegmentRBUp.setTextureSize(64, 32);
        SegmentRBUp.mirror = true;
        setRotation(SegmentRBUp, 0F, 0F, 0F);
        SegmentLFUp = new ModelRenderer(this, 20, 13);
        SegmentLFUp.addBox(0F, 0F, 0F, 1, 4, 1);
        SegmentLFUp.setRotationPoint(3F, 8F, -4F);
        SegmentLFUp.setTextureSize(64, 32);
        SegmentLFUp.mirror = true;
        setRotation(SegmentLFUp, 0F, 0F, 0F);
        SegmentRFUp = new ModelRenderer(this, 20, 13);
        SegmentRFUp.addBox(0F, 0F, 0F, 1, 4, 1);
        SegmentRFUp.setRotationPoint(-4F, 8F, -4F);
        SegmentRFUp.setTextureSize(64, 32);
        SegmentRFUp.mirror = true;
        setRotation(SegmentRFUp, 0F, 0F, 0F);
        SegmentUBRight = new ModelRenderer(this, 0, 13);
        SegmentUBRight.addBox(0F, 0F, 0F, 4, 1, 1);
        SegmentUBRight.setRotationPoint(-8F, 12F, 3F);
        SegmentUBRight.setTextureSize(64, 32);
        SegmentUBRight.mirror = true;
        setRotation(SegmentUBRight, 0F, 0F, 0F);
        SegmentBFRight = new ModelRenderer(this, 0, 13);
        SegmentBFRight.addBox(0F, 0F, 0F, 4, 1, 1);
        SegmentBFRight.setRotationPoint(-8F, 19F, -4F);
        SegmentBFRight.setTextureSize(64, 32);
        SegmentBFRight.mirror = true;
        setRotation(SegmentBFRight, 0F, 0F, 0F);
        SegmentUFRight = new ModelRenderer(this, 0, 13);
        SegmentUFRight.addBox(0F, 0F, 0F, 4, 1, 1);
        SegmentUFRight.setRotationPoint(-8F, 12F, -4F);
        SegmentUFRight.setTextureSize(64, 32);
        SegmentUFRight.mirror = true;
        setRotation(SegmentUFRight, 0F, 0F, 0F);
        SegmentBBRight = new ModelRenderer(this, 0, 13);
        SegmentBBRight.addBox(0F, 0F, 0F, 4, 1, 1);
        SegmentBBRight.setRotationPoint(-8F, 19F, 3F);
        SegmentBBRight.setTextureSize(64, 32);
        SegmentBBRight.mirror = true;
        setRotation(SegmentBBRight, 0F, 0F, 0F);
        SegmentLFDown = new ModelRenderer(this, 20, 13);
        SegmentLFDown.addBox(0F, 0F, 0F, 1, 4, 1);
        SegmentLFDown.setRotationPoint(3F, 20F, -4F);
        SegmentLFDown.setTextureSize(64, 32);
        SegmentLFDown.mirror = true;
        setRotation(SegmentLFDown, 0F, 0F, 0F);
        SegmentLBDown = new ModelRenderer(this, 20, 13);
        SegmentLBDown.addBox(0F, 0F, 0F, 1, 4, 1);
        SegmentLBDown.setRotationPoint(3F, 20F, 3F);
        SegmentLBDown.setTextureSize(64, 32);
        SegmentLBDown.mirror = true;
        setRotation(SegmentLBDown, 0F, 0F, 0F);
        SegmentRBDown = new ModelRenderer(this, 20, 13);
        SegmentRBDown.addBox(0F, 0F, 0F, 1, 4, 1);
        SegmentRBDown.setRotationPoint(-4F, 20F, 3F);
        SegmentRBDown.setTextureSize(64, 32);
        SegmentRBDown.mirror = true;
        setRotation(SegmentRBDown, 0F, 0F, 0F);
        FootTop = new ModelRenderer(this, 24, 0);
        FootTop.addBox(0F, 0F, 0F, 10, 2, 10);
        FootTop.setRotationPoint(-5F, 20F, -5F);
        FootTop.setTextureSize(64, 32);
        FootTop.mirror = true;
        setRotation(FootTop, 0F, 0F, 0F);
        SegmentRFDown = new ModelRenderer(this, 20, 13);
        SegmentRFDown.addBox(0F, 0F, 0F, 1, 4, 1);
        SegmentRFDown.setRotationPoint(-4F, 20F, -4F);
        SegmentRFDown.setTextureSize(64, 32);
        SegmentRFDown.mirror = true;
        setRotation(SegmentRFDown, 0F, 0F, 0F);
        FootBottom = new ModelRenderer(this, 0, 18);
        FootBottom.addBox(0F, 0F, 0F, 12, 2, 12);
        FootBottom.setRotationPoint(-6F, 22F, -6F);
        FootBottom.setTextureSize(64, 32);
        FootBottom.mirror = true;
        setRotation(FootBottom, 0F, 0F, 0F);
        SegmentBFMid = new ModelRenderer(this, 30, 12);
        SegmentBFMid.addBox(0F, 0F, 0F, 8, 1, 1);
        SegmentBFMid.setRotationPoint(-4F, 19F, -4F);
        SegmentBFMid.setTextureSize(64, 32);
        SegmentBFMid.mirror = true;
        setRotation(SegmentBFMid, 0F, 0F, 0F);
        SegmentUBMid = new ModelRenderer(this, 30, 12);
        SegmentUBMid.addBox(0F, 0F, 0F, 8, 1, 1);
        SegmentUBMid.setRotationPoint(-4F, 12F, 3F);
        SegmentUBMid.setTextureSize(64, 32);
        SegmentUBMid.mirror = true;
        setRotation(SegmentUBMid, 0F, 0F, 0F);
        SegmentBBMid = new ModelRenderer(this, 30, 12);
        SegmentBBMid.addBox(0F, 0F, 0F, 8, 1, 1);
        SegmentBBMid.setRotationPoint(-4F, 19F, 3F);
        SegmentBBMid.setTextureSize(64, 32);
        SegmentBBMid.mirror = true;
        setRotation(SegmentBBMid, 0F, 0F, 0F);
        SegmentUFMid = new ModelRenderer(this, 30, 12);
        SegmentUFMid.addBox(0F, 0F, 0F, 8, 1, 1);
        SegmentUFMid.setRotationPoint(-4F, 12F, -4F);
        SegmentUFMid.setTextureSize(64, 32);
        SegmentUFMid.mirror = true;
        setRotation(SegmentUFMid, 0F, 0F, 0F);
        SegmentLBMid = new ModelRenderer(this, 48, 19);
        SegmentLBMid.addBox(0F, 0F, 0F, 1, 6, 1);
        SegmentLBMid.setRotationPoint(3F, 13F, 3F);
        SegmentLBMid.setTextureSize(64, 32);
        SegmentLBMid.mirror = true;
        setRotation(SegmentLBMid, 0F, 0F, 0F);
        SegmentLFMid = new ModelRenderer(this, 48, 19);
        SegmentLFMid.addBox(0F, 0F, 0F, 1, 6, 1);
        SegmentLFMid.setRotationPoint(3F, 13F, -4F);
        SegmentLFMid.setTextureSize(64, 32);
        SegmentLFMid.mirror = true;
        setRotation(SegmentLFMid, 0F, 0F, 0F);
        SegmentRBMid = new ModelRenderer(this, 48, 19);
        SegmentRBMid.addBox(0F, 0F, 0F, 1, 6, 1);
        SegmentRBMid.setRotationPoint(-4F, 13F, 3F);
        SegmentRBMid.setTextureSize(64, 32);
        SegmentRBMid.mirror = true;
        setRotation(SegmentRBMid, 0F, 0F, 0F);
        SegmentRFMid = new ModelRenderer(this, 48, 19);
        SegmentRFMid.addBox(0F, 0F, 0F, 1, 6, 1);
        SegmentRFMid.setRotationPoint(-4F, 13F, -4F);
        SegmentRFMid.setTextureSize(64, 32);
        SegmentRFMid.mirror = true;
        setRotation(SegmentRFMid, 0F, 0F, 0F);
        SegmentMidDiag4 = new ModelRenderer(this, 0, 0);
        SegmentMidDiag4.addBox(0F, 0F, 0F, 1, 1, 10);
        SegmentMidDiag4.setRotationPoint(-3.99F, 12F, -3F);
        SegmentMidDiag4.setTextureSize(64, 32);
        SegmentMidDiag4.mirror = true;
        setRotation(SegmentMidDiag4, -0.7853982F, 0F, 0F);
        SegmentRUMid = new ModelRenderer(this, 48, 12);
        SegmentRUMid.addBox(0F, 0F, 0F, 1, 1, 6);
        SegmentRUMid.setRotationPoint(-4F, 12F, -3F);
        SegmentRUMid.setTextureSize(64, 32);
        SegmentRUMid.mirror = true;
        setRotation(SegmentRUMid, 0F, 0F, 0F);
        SegmentBRMid = new ModelRenderer(this, 48, 12);
        SegmentBRMid.addBox(0F, 0F, 0F, 1, 1, 6);
        SegmentBRMid.setRotationPoint(-4F, 19F, -3F);
        SegmentBRMid.setTextureSize(64, 32);
        SegmentBRMid.mirror = true;
        setRotation(SegmentBRMid, 0F, 0F, 0F);
        SegmentBLMid = new ModelRenderer(this, 48, 12);
        SegmentBLMid.addBox(0F, 0F, 0F, 1, 1, 6);
        SegmentBLMid.setRotationPoint(3F, 19F, -3F);
        SegmentBLMid.setTextureSize(64, 32);
        SegmentBLMid.mirror = true;
        setRotation(SegmentBLMid, 0F, 0F, 0F);
        SegmentLUMid = new ModelRenderer(this, 48, 12);
        SegmentLUMid.addBox(0F, 0F, 0F, 1, 1, 6);
        SegmentLUMid.setRotationPoint(3F, 12F, -3F);
        SegmentLUMid.setTextureSize(64, 32);
        SegmentLUMid.mirror = true;
        setRotation(SegmentLUMid, 0F, 0F, 0F);
        SegmentMidDiag3 = new ModelRenderer(this, 0, 0);
        SegmentMidDiag3.addBox(0F, 0F, 0F, 1, 1, 10);
        SegmentMidDiag3.setRotationPoint(-4F, 19F, -4F);
        SegmentMidDiag3.setTextureSize(64, 32);
        SegmentMidDiag3.mirror = true;
        setRotation(SegmentMidDiag3, 0.7853982F, 0F, 0F);
        SegmentMidDiag7 = new ModelRenderer(this, 0, 0);
        SegmentMidDiag7.addBox(0F, 0F, 0F, 1, 1, 10);
        SegmentMidDiag7.setRotationPoint(-2.99F, 12F, 4F);
        SegmentMidDiag7.setTextureSize(64, 32);
        SegmentMidDiag7.mirror = true;
        setRotation(SegmentMidDiag7, -0.7853982F, 1.570796F, 0F);
        SegmentMidDiag8 = new ModelRenderer(this, 0, 0);
        SegmentMidDiag8.addBox(0F, 0F, 0F, 1, 1, 10);
        SegmentMidDiag8.setRotationPoint(-4F, 19F, 4F);
        SegmentMidDiag8.setTextureSize(64, 32);
        SegmentMidDiag8.mirror = true;
        setRotation(SegmentMidDiag8, 0.7853982F, 1.570796F, 0F);
        SegmentMidDiag1 = new ModelRenderer(this, 0, 0);
        SegmentMidDiag1.addBox(0F, 0F, 0F, 1, 1, 10);
        SegmentMidDiag1.setRotationPoint(3F, 19F, -4F);
        SegmentMidDiag1.setTextureSize(64, 32);
        SegmentMidDiag1.mirror = true;
        setRotation(SegmentMidDiag1, 0.7853982F, 0F, 0F);
        SegmentMidDiag2 = new ModelRenderer(this, 0, 0);
        SegmentMidDiag2.addBox(0F, 0F, 0F, 1, 1, 10);
        SegmentMidDiag2.setRotationPoint(3.01F, 12F, -3F);
        SegmentMidDiag2.setTextureSize(64, 32);
        SegmentMidDiag2.mirror = true;
        setRotation(SegmentMidDiag2, -0.7853982F, 0F, 0F);
        SegmentMidDiag6 = new ModelRenderer(this, 0, 0);
        SegmentMidDiag6.addBox(0F, 0F, 0F, 1, 1, 10);
        SegmentMidDiag6.setRotationPoint(-4F, 19F, -3F);
        SegmentMidDiag6.setTextureSize(64, 32);
        SegmentMidDiag6.mirror = true;
        setRotation(SegmentMidDiag6, 0.7853982F, 1.570796F, 0F);
        SegmentMidDiag5 = new ModelRenderer(this, 0, 0);
        SegmentMidDiag5.addBox(0F, 0F, 0F, 1, 1, 10);
        SegmentMidDiag5.setRotationPoint(-2.99F, 12F, -3F);
        SegmentMidDiag5.setTextureSize(64, 32);
        SegmentMidDiag5.mirror = true;
        setRotation(SegmentMidDiag5, -0.7853982F, 1.570796F, 0F);
        SegmentUBLeft = new ModelRenderer(this, 0, 13);
        SegmentUBLeft.addBox(0F, 0F, 0F, 4, 1, 1);
        SegmentUBLeft.setRotationPoint(4F, 12F, 3F);
        SegmentUBLeft.setTextureSize(64, 32);
        SegmentUBLeft.mirror = true;
        setRotation(SegmentUBLeft, 0F, 0F, 0F);
        SegmentUFLeft = new ModelRenderer(this, 0, 13);
        SegmentUFLeft.addBox(0F, 0F, 0F, 4, 1, 1);
        SegmentUFLeft.setRotationPoint(4F, 12F, -4F);
        SegmentUFLeft.setTextureSize(64, 32);
        SegmentUFLeft.mirror = true;
        setRotation(SegmentUFLeft, 0F, 0F, 0F);
        SegmentBFLeft = new ModelRenderer(this, 0, 13);
        SegmentBFLeft.addBox(0F, 0F, 0F, 4, 1, 1);
        SegmentBFLeft.setRotationPoint(4F, 19F, -4F);
        SegmentBFLeft.setTextureSize(64, 32);
        SegmentBFLeft.mirror = true;
        setRotation(SegmentBFLeft, 0F, 0F, 0F);
        SegmentBBLeft = new ModelRenderer(this, 0, 13);
        SegmentBBLeft.addBox(0F, 0F, 0F, 4, 1, 1);
        SegmentBBLeft.setRotationPoint(4F, 19F, 3F);
        SegmentBBLeft.setTextureSize(64, 32);
        SegmentBBLeft.mirror = true;
        setRotation(SegmentBBLeft, 0F, 0F, 0F);
        fixPositions();
    }

    // this offsets some positions to avoid Z-Fighting
    public void fixPositions()
    {
        SegmentBLBack.setRotationPoint(3F, 19F, 4F);
        SegmentLUBack.setRotationPoint(3F, 12F, 4F);
        SegmentRUBack.setRotationPoint(-4F, 12F, 4F);
        SegmentBRBack.setRotationPoint(-4F, 19F, 4F);
        SegmentBLFront.setRotationPoint(3F, 19F, -8F);
        SegmentLUFront.setRotationPoint(3F, 12F, -8F);
        SegmentRUFront.setRotationPoint(-4F, 12F, -8F);
        SegmentBRFront.setRotationPoint(-4F, 19F, -8F);
        SegmentLBUp.setRotationPoint(3F, 8F, 3F);
        SegmentRBUp.setRotationPoint(-4F, 8F, 3F);
        SegmentLFUp.setRotationPoint(3F, 8F, -4F);
        SegmentRFUp.setRotationPoint(-4F, 8F, -4F);
        SegmentUBRight.setRotationPoint(-8F, 12F, 3F);
        SegmentBFRight.setRotationPoint(-8F, 19F, -4F);
        SegmentUFRight.setRotationPoint(-8F, 12F, -4F);
        SegmentBBRight.setRotationPoint(-8F, 19F, 3F);
        SegmentLFDown.setRotationPoint(3F, 20F, -4F);
        SegmentLBDown.setRotationPoint(3F, 20F, 3F);
        SegmentRBDown.setRotationPoint(-4F, 20F, 3F);
        SegmentRFDown.setRotationPoint(-4F, 20F, -4F);
        SegmentBFMid.setRotationPoint(-4F, 19F, -4F);
        SegmentUBMid.setRotationPoint(-4F, 12F, 3F);
        SegmentBBMid.setRotationPoint(-4F, 19F, 3F);
        SegmentUFMid.setRotationPoint(-4F, 12F, -4F);
        SegmentLBMid.setRotationPoint(3F, 13F, 3F);
        SegmentLFMid.setRotationPoint(3F, 13F, -4F);
        SegmentRBMid.setRotationPoint(-4F, 13F, 3F);
        SegmentRFMid.setRotationPoint(-4F, 13F, -4F);
        SegmentRUMid.setRotationPoint(-4F, 12F, -3F);
        SegmentBRMid.setRotationPoint(-4F, 19F, -3F);
        SegmentBLMid.setRotationPoint(3F, 19F, -3F);
        SegmentLUMid.setRotationPoint(3F, 12F, -3F);
        SegmentMidDiag1.setRotationPoint(2.99F, 19.1F, -4F);
        SegmentMidDiag2.setRotationPoint(2.99F, 12F, -3.1F);
        SegmentMidDiag3.setRotationPoint(-3.99F, 19.1F, -4F);
        SegmentMidDiag4.setRotationPoint(-3.99F, 12F, -3.1F);
        SegmentMidDiag5.setRotationPoint(-3.1F, 12F, -2.99F);
        SegmentMidDiag6.setRotationPoint(-4F, 19.1F, -2.99F);
        SegmentMidDiag7.setRotationPoint(-3.1F, 12F, 3.99F);
        SegmentMidDiag8.setRotationPoint(-4F, 19F, 3.99F);
        SegmentUBLeft.setRotationPoint(4F, 12F, 3F);
        SegmentUFLeft.setRotationPoint(4F, 12F, -4F);
        SegmentBFLeft.setRotationPoint(4F, 19F, -4F);
        SegmentBBLeft.setRotationPoint(4F, 19F, 3F);
    }

    public void render(boolean up, boolean down, boolean left, boolean right, boolean front, boolean back, boolean foot)
    {
        float scale = 0.0625f;
        if (up)
        {
            SegmentLBUp.render(scale);
            SegmentRBUp.render(scale);
            SegmentLFUp.render(scale);
            SegmentRFUp.render(scale);
        }
        if (down)
        {
            SegmentLFDown.render(scale);
            SegmentLBDown.render(scale);
            SegmentRBDown.render(scale);
            SegmentRFDown.render(scale);
        }
        if (left)
        {
            SegmentUBLeft.render(scale);
            SegmentUFLeft.render(scale);
            SegmentBFLeft.render(scale);
            SegmentBBLeft.render(scale);
        }
        if (right)
        {
            SegmentUBRight.render(scale);
            SegmentBFRight.render(scale);
            SegmentUFRight.render(scale);
            SegmentBBRight.render(scale);
        }
        if (front)
        {
            SegmentBLFront.render(scale);
            SegmentLUFront.render(scale);
            SegmentRUFront.render(scale);
            SegmentBRFront.render(scale);
        }
        if (back)
        {
            SegmentBLBack.render(scale);
            SegmentLUBack.render(scale);
            SegmentRUBack.render(scale);
            SegmentBRBack.render(scale);
        }
        if (foot)
        {
            FootBottom.render(scale);
            FootTop.render(scale);
        }
        SegmentBFMid.render(scale);
        SegmentUBMid.render(scale);
        SegmentBBMid.render(scale);
        SegmentUFMid.render(scale);
        SegmentLBMid.render(scale);
        SegmentLFMid.render(scale);
        SegmentRBMid.render(scale);
        SegmentRFMid.render(scale);
        SegmentRUMid.render(scale);
        SegmentBRMid.render(scale);
        SegmentBLMid.render(scale);
        SegmentLUMid.render(scale);
        SegmentMidDiag1.render(scale);
        SegmentMidDiag2.render(scale);
        SegmentMidDiag3.render(scale);
        SegmentMidDiag4.render(scale);
        SegmentMidDiag5.render(scale);
        SegmentMidDiag6.render(scale);
        SegmentMidDiag7.render(scale);
        SegmentMidDiag8.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
