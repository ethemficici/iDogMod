// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.ethem00.idogmod.entity.client;

import com.ethem00.idogmod.entity.client.render.entity.animation.iDogAnimations;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.CamelAnimations;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.util.math.MathHelper;

public class iDogEntityModel<T extends iDogEntity> extends SinglePartEntityModel<T> {
	private final ModelPart iDog;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

	public iDogEntityModel(ModelPart root) {
		this.iDog = root.getChild("iDog");
		this.head = iDog.getChild("head");
        this.leftFrontLeg = iDog.getChild("left_front_leg");
        this.rightFrontLeg = iDog.getChild("right_front_leg");
        this.leftHindLeg = iDog.getChild("left_hind_leg");
        this.rightHindLeg = iDog.getChild("right_hind_leg");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData iDog = modelPartData.addChild("iDog", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData head = iDog.addChild("head", ModelPartBuilder.create().uv(0, 16).cuboid(4.0F, -11.0F, 0.5F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 16).cuboid(-7.0F, -11.0F, 0.5F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.5F, -7.0F));

		ModelPartData nose_r1 = head.addChild("nose_r1", ModelPartBuilder.create().uv(25, 0).cuboid(-1.0F, 5.0F, 16.0F, 6.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -10.0F, -17.0F, -0.3054F, 0.0F, 0.0F));

		ModelPartData head_r1 = head.addChild("head_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -6.3523F, -4.2081F, 10.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 2.0F, -0.3054F, 0.0F, 0.0F));

		ModelPartData lEar = head.addChild("left_ear", ModelPartBuilder.create(), ModelTransform.pivot(-5.5F, -9.5F, 1.5F));

		ModelPartData earPieceR_r1 = lEar.addChild("earPieceR_r1", ModelPartBuilder.create().uv(0, 21).cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 10.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

		ModelPartData rEar = head.addChild("right_ear", ModelPartBuilder.create(), ModelTransform.pivot(5.5F, -9.5F, 1.5F));

		ModelPartData earPieceL_r1 = rEar.addChild("earPieceL_r1", ModelPartBuilder.create().uv(0, 21).cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 10.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4363F));

		ModelPartData upperBody = iDog.addChild("upperBody", ModelPartBuilder.create(), ModelTransform.of(0.0F, -8.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

		ModelPartData body_r1 = upperBody.addChild("body_r1", ModelPartBuilder.create().uv(36, 0).cuboid(-4.0F, -5.5F, -3.0F, 8.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5F, 0.0F, -0.2618F, 0.0F, 0.0F));

		ModelPartData lowerBody = iDog.addChild("lowerBody", ModelPartBuilder.create(), ModelTransform.of(0.0F, -8.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

		ModelPartData upperBody_r1 = lowerBody.addChild("upperBody_r1", ModelPartBuilder.create().uv(32, 19).cuboid(-5.0F, -3.0F, -3.95F, 9.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 3.0F, -1.5F, -0.2618F, 0.0F, 0.0F));

		ModelPartData bLegL = iDog.addChild("left_hind_leg", ModelPartBuilder.create().uv(26, 16).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, -5.0F, 6.0F));

		ModelPartData bLegR = iDog.addChild("right_hind_leg", ModelPartBuilder.create().uv(26, 16).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, -5.0F, 6.0F));

		ModelPartData fLegL = iDog.addChild("left_front_leg", ModelPartBuilder.create().uv(12, 20).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 9.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -8.0F, -3.5F));

		ModelPartData fLegR = iDog.addChild("right_front_leg", ModelPartBuilder.create().uv(12, 20).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 9.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -8.0F, -3.5F));

		ModelPartData tail = iDog.addChild("tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -8.0F, 7.5F));

		ModelPartData tail_r1 = tail.addChild("tail_r1", ModelPartBuilder.create().uv(28, 5).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.618F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}
	@Override
	public void setAngles(iDogEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        setHeadAngles(netHeadYaw, headPitch);
        this.animateMovement(iDogAnimations.WALKING_EARS_TAIL, limbSwing *2, limbSwingAmount *2, 2.0F, 2.5F);
    }

    private void setHeadAngles(float headYaw, float headPitch) {
        headYaw = MathHelper.clamp(headYaw, -40.0F, 40.0F);
        headPitch = MathHelper.clamp(headPitch, -15.0F, 35.0F);
        this.head.yaw = headYaw * (float) (Math.PI / 180.0);
        this.head.pitch = headPitch * (float) (Math.PI / 180.0);
    }

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		iDog.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

    @Override
    public ModelPart getPart() {
        return iDog;
    }
}