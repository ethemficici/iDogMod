package com.ethem00.idogmod.entity.client.render.entity.feature;

import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static org.apache.commons.lang3.RandomUtils.nextInt;

@Environment(EnvType.CLIENT)
public class iDodLidFeatureRenderer<T extends iDogEntity, M extends iDogEntityModel<T>> extends EyesFeatureRenderer<T, M> {

    private static final RenderLayer COVER_INVERTED_CENTER = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_center.png"));
    private static final RenderLayer COVER_NONE = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_none.png"));
    private static final RenderLayer COVER_ALL = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_all.png"));
    private static final RenderLayer COVER_CENTER = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_center.png"));

    private static final RenderLayer COVER_3 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_3.png"));
    private static final RenderLayer COVER_4 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_4.png"));
    private static final RenderLayer COVER_5 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_5.png"));
    private static final RenderLayer COVER_6 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_6.png"));
    private static final RenderLayer COVER_7 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_7.png"));
    private static final RenderLayer COVER_8 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_8.png"));

    private static final RenderLayer COVER_3_INVERTED = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_3.png"));
    private static final RenderLayer COVER_4_INVERTED = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_4.png"));
    private static final RenderLayer COVER_5_INVERTED = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_5.png"));
    private static final RenderLayer COVER_6_INVERTED = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_6.png"));
    private static final RenderLayer COVER_7_INVERTED = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_7.png"));
    private static final RenderLayer COVER_8_INVERTED = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_8.png"));


    private static final RenderLayer COVER_TOP = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_top.png"));
    private static final RenderLayer COVER_BOTTOM = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_bottom.png"));
    private static final RenderLayer COVER_TOP_CENTER = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_top_center.png"));
    private static final RenderLayer COVER_BOTTOM_CENTER = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_bottom_center.png"));


    public iDodLidFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    /**
     * See {@link iDogEntity#songDisplayLogic()}
     * Clockwise eye order
     *
     *      *   4   *
     *      3   *   5
     *          1
     *      8   *   6
     *      *   7   *
     */

    //TODO: REWORK? All of this can be achieved using individual "Eye Lids"...
    // Does each lid need a feature renderer?
    private RenderLayer getEyesTexture(int cover) {
        return switch (cover) { //Swap to byte later..?

            case -8 -> COVER_8_INVERTED;
            case -7 -> COVER_7_INVERTED;
            case -6 -> COVER_6_INVERTED;
            case -5 -> COVER_5_INVERTED;
            case -4 -> COVER_4_INVERTED;
            case -3 -> COVER_3_INVERTED;
            case -2 -> COVER_INVERTED_CENTER;
            case -1 -> COVER_INVERTED_CENTER;
            case 0 -> COVER_NONE;
            case 1 -> COVER_CENTER;
            case 2 -> COVER_INVERTED_CENTER;
            case 3 -> COVER_3;
            case 4 -> COVER_4;
            case 5 -> COVER_5;
            case 6 -> COVER_6;
            case 7 -> COVER_7;
            case 8 -> COVER_8;
            case 13 -> COVER_TOP;
            case 14 -> COVER_BOTTOM;
            case 15 -> COVER_TOP_CENTER;
            case 16 -> COVER_BOTTOM_CENTER;
            default -> COVER_NONE;
        };
    }

    @Override
    public RenderLayer getEyesTexture() {
        return COVER_ALL;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            T idogEntity, float limbAngle, float limbDistance, float tickDelta,
            float animationProgress, float headYaw, float headPitch) {

        VertexConsumer eyeVertexConsumerCenter = vertexConsumers.getBuffer(COVER_CENTER);
        VertexConsumer eyeVertexConsumerThree = vertexConsumers.getBuffer(COVER_3);
        this.getContextModel().render(matrices, eyeVertexConsumerCenter, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        this.getContextModel().render(matrices, eyeVertexConsumerThree, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public float pickLerpEffect(Boolean isRandomEnabled) {

        if(isRandomEnabled) {
            return switch (nextInt(0, 8)) {
                case 0 -> stobeInterpolation();
                case 1 -> flashInterpolation();
                case 2 -> slowPulseInterpolation();
                case 3 -> 1F;
                case 4 -> 1F;
                case 5 -> 1F;
                case 6 -> 1F;
                case 7 -> 1F;
                case 8 -> 1F;
                default -> 1F;
            };
        } else {
            return 1F;
        }
    }

    // Random interpolation functions
    public float stobeInterpolation() {
        return 1F;
    }

    public float flashInterpolation() {
        return 1F;
    }

    public float slowPulseInterpolation() {
        return 1F;
    }
}
