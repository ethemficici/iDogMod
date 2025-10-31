// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.ethem00.idogmod.entity.client;

import com.ethem00.idogmod.entity.client.render.entity.animation.iDogAnimations;
import com.ethem00.idogmod.entity.iDogEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;


public class iDogEntityModel<T extends iDogEntity> extends HierarchicalModel<T> {
	private final ModelPart root;
    private final ModelPart idog;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

	public iDogEntityModel(ModelPart root) {
        this.root = root;
		this.idog = root.getChild("iDog");
		this.head = idog.getChild("head");
        this.leftFrontLeg = idog.getChild("left_front_leg");
        this.rightFrontLeg = idog.getChild("right_front_leg");
        this.leftHindLeg = idog.getChild("left_hind_leg");
        this.rightHindLeg = idog.getChild("right_hind_leg");
	}

	public static LayerDefinition createBodyLayer() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition PartDefinition = modelData.getRoot();
        PartDefinition iDog = PartDefinition.addOrReplaceChild("iDog", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = iDog.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 16).addBox(4.0F, -11.0F, 0.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-7.0F, -11.0F, 0.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.5F, -7.0F));

        PartDefinition nose_r1 = head.addOrReplaceChild("nose_r1", CubeListBuilder.create().texOffs(25, 0).addBox(-1.0F, 5.0F, 16.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -10.0F, -17.0F, -0.3054F, 0.0F, 0.0F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -6.3523F, -4.2081F, 10.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 2.0F, -0.3054F, 0.0F, 0.0F));

        PartDefinition lEar = head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offset(-5.5F, -9.5F, 1.5F));

        PartDefinition earPieceR_r1 = lEar.addOrReplaceChild("earPieceR_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-2.0F, -0.5F, -0.5F, 4.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition rEar = head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offset(5.5F, -9.5F, 1.5F));

		PartDefinition earPieceL_r1 = rEar.addOrReplaceChild("earPieceL_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-2.0F, -0.5F, -0.5F, 4.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition upperBody = iDog.addOrReplaceChild("upperBody", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -8.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition body_r1 = upperBody.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(36, 0).addBox(-4.0F, -5.5F, -3.0F, 8.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.5F, 0.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition lowerBody = iDog.addOrReplaceChild("lowerBody", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -8.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition upperBody_r1 = lowerBody.addOrReplaceChild("upperBody_r1", CubeListBuilder.create().texOffs(32, 19).addBox(-5.0F, -3.0F, -3.95F, 9.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 3.0F, -1.5F, -0.2618F, 0.0F, 0.0F));

		PartDefinition bLegL = iDog.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(26, 16).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -5.0F, 6.0F));

		PartDefinition bLegR = iDog.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(26, 16).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -5.0F, 6.0F));

		PartDefinition fLegL = iDog.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(12, 20).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -8.0F, -3.5F));

		PartDefinition fLegR = iDog.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(12, 20).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -8.0F, -3.5F));

		PartDefinition tail = iDog.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 7.5F));

		PartDefinition tail_r1 = tail.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(28, 5).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.618F, 0.0F, 0.0F));
		return LayerDefinition.create(modelData, 64, 32);
	}

    @Override
    public void setupAnim(T iDog, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.getPart().getAllParts().forEach(ModelPart::resetPose);
        setHeadAngles(pNetHeadYaw, pHeadPitch);

        if(iDog.isInSittingPose()) {
            this.animateWalk(iDogAnimations.SITTING, 1, 1, 2.0F, 2.5F);
        }

        if(iDog.isBegging()) {
            this.animateWalk(iDogAnimations.BEGGING, iDog.getBegDelta(), 60, 2.0F, 2.5F);
        } else if(!iDog.isBegging() && iDog.wasBegging()) {
            this.animateWalk(iDogAnimations.BEGGING, iDog.getInverseBegDelta(), 60, 2.0F, 2.5F);
        }

        if(!iDog.isInSittingPose()) {
            this.animateWalk(iDogAnimations.WALKING_EARS_TAIL, pLimbSwing *2, pLimbSwingAmount *2, 2.0F, 2.5F);
        }
    }

    private void setHeadAngles(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -40.0F, 40.0F);
        headPitch = Mth.clamp(headPitch, -15.0F, 35.0F);
        this.head.yRot = headYaw * (float) (Math.PI / 180.0);
        this.head.xRot = headPitch * (float) (Math.PI / 180.0);
    }

    public ModelPart getPart() {
        return root();
    }

    @Override
    public ModelPart root() {
        return idog;
    }

    public void render(PoseStack matrices, VertexConsumer vertexConsumer, int i, int noOverlay, float v, float v1, float v2, float v3) {
        idog.render(matrices, vertexConsumer, i, noOverlay, v, v1, v2, v3);
    }
}