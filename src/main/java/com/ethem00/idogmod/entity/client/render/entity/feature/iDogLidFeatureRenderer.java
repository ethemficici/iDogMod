package com.ethem00.idogmod.entity.client.render.entity.feature;

import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.client.render.entity.animation.iDogEasing;
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

/**
 * Eye interpolation and easing logic handled in {@link iDogEntity#songDisplayLogic()}
 * Also see {@link iDogEasing}
 *
 * https://easings.net/
 * Easing mathematical functions provided by Andrey Sitnik and Ivan Solovev
 */

@Environment(EnvType.CLIENT)
public class iDogLidFeatureRenderer<T extends iDogEntity, M extends iDogEntityModel<T>> extends EyesFeatureRenderer<T, M> {
    private static final RenderLayer COVER_NONE_RENDERLAYER = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_none.png"));
    //Render layer!
    private static final Identifier COVER_MISSING_NUMBER = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_missing_number.png");

    private static final Identifier COVER_INVERTED_CENTER = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_center.png");
    private static final Identifier COVER_NONE = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_none.png");
    private static final Identifier COVER_ALL = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_all.png");
    private static final Identifier COVER_CENTER = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_center.png");

    private static final Identifier COVER_3 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_3.png");
    private static final Identifier COVER_4 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_4.png");
    private static final Identifier COVER_5 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_5.png");
    private static final Identifier COVER_6 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_6.png");
    private static final Identifier COVER_7 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_7.png");
    private static final Identifier COVER_8 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_8.png");

    private static final Identifier COVER_3_INVERTED = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_3.png");
    private static final Identifier COVER_4_INVERTED = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_4.png");
    private static final Identifier COVER_5_INVERTED = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_5.png");
    private static final Identifier COVER_6_INVERTED = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_6.png");
    private static final Identifier COVER_7_INVERTED = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_7.png");
    private static final Identifier COVER_8_INVERTED = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_8.png");

    private static final Identifier COVER_TOP = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_top.png");
    private static final Identifier COVER_BOTTOM = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_bottom.png");
    private static final Identifier COVER_TOP_CENTER = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_top_center.png");
    private static final Identifier COVER_BOTTOM_CENTER = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_bottom_center.png");

    private static final Identifier COVER_40 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_40.png");
    private static final Identifier COVER_407 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_407.png");
    private static final Identifier COVER_70 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_70.png");

    private static final Identifier COVER_TRI_LEFT_DOWN = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_left_down.png");
    private static final Identifier COVER_TRI_LEFT = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_left.png");
    private static final Identifier COVER_TRI_LEFT_UP = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_left_up.png");
    private static final Identifier COVER_TRI_RIGHT_DOWN = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_right_down.png");
    private static final Identifier COVER_TRI_RIGHT = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_right.png");
    private static final Identifier COVER_TRI_RIGHT_UP = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_right_up.png");

    private static final Identifier COVER_INVERTED_TRI_LEFT_DOWN = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_left_down.png");
    private static final Identifier COVER_INVERTED_TRI_LEFT = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_left.png");
    private static final Identifier COVER_INVERTED_TRI_LEFT_UP = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_left_up.png");
    private static final Identifier COVER_INVERTED_TRI_RIGHT_DOWN = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_right_down.png");
    private static final Identifier COVER_INVERTED_TRI_RIGHT = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_right.png");
    private static final Identifier COVER_INVERTED_TRI_RIGHT_UP = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_right_up.png");

    private static final Identifier COVER_407_INVERTED = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_407_inverted.png");


    public iDogLidFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }
    @Override
    public RenderLayer getEyesTexture() {
        return COVER_NONE_RENDERLAYER;
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
    private Identifier getCoverTexture(int cover) {
        return switch (cover) {

            case -333 -> COVER_INVERTED_TRI_LEFT;
            case -334 -> COVER_INVERTED_TRI_LEFT_UP;
            case -335 -> COVER_INVERTED_TRI_RIGHT_UP;
            case -336 -> COVER_INVERTED_TRI_RIGHT;
            case -337 -> COVER_INVERTED_TRI_RIGHT_DOWN;
            case -338 -> COVER_INVERTED_TRI_LEFT_DOWN;
            case -407 -> COVER_407_INVERTED; // Stripe middle closed
            case -8 -> COVER_8_INVERTED;
            case -7 -> COVER_7_INVERTED;
            case -6 -> COVER_6_INVERTED;
            case -5 -> COVER_5_INVERTED;
            case -4 -> COVER_4_INVERTED;
            case -3 -> COVER_3_INVERTED;
            case -2 -> COVER_NONE;
            case -1 -> COVER_INVERTED_CENTER;
            case 0 -> COVER_NONE;
            case 1 -> COVER_CENTER;
            case 2 -> COVER_ALL;
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
            case 40 -> COVER_40;
            case 70 -> COVER_70;
            case 407 -> COVER_407; //Stripe middle open
            case 333 -> COVER_TRI_LEFT;
            case 334 -> COVER_TRI_LEFT_UP;
            case 335 -> COVER_TRI_RIGHT_UP;
            case 336 -> COVER_TRI_RIGHT;
            case 337 -> COVER_TRI_RIGHT_DOWN;
            case 338 -> COVER_TRI_LEFT_DOWN;
            default -> COVER_MISSING_NUMBER;
        };
    }

    //ONLY ONE LAYER AT A TIME!
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            T idogEntity, float limbAngle, float limbDistance, float tickDelta,
            float animationProgress, float headYaw, float headPitch) {

        VertexConsumer eyeVertexConsumerThree = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getCoverTexture(idogEntity.getEyeCover())));
        this.getContextModel().render(matrices, eyeVertexConsumerThree, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
