package com.ethem00.idogmod.entity.client.render.entity.feature;

import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.client.render.entity.animation.iDogEasing;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
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
public class iDogEyesFeatureRenderer<T extends iDogEntity, M extends iDogEntityModel<T>> extends EyesFeatureRenderer<T, M> {
    private static final RenderLayer DEFAULT_EYES_RENDERLAYER = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_5.png"));
    private static final Identifier DEFAULT_EYES = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/idog_eyes.png");

    private static final Identifier EYES_5 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_5.png");
    private static final Identifier EYES_11 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_11.png");
    private static final Identifier EYES_13 = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_13.png");
    private static final Identifier EYES_BLOCKS = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_blocks.png");
    private static final Identifier EYES_CAT = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_cat.png");
    private static final Identifier EYES_CHIRP = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_chirp.png");
    private static final Identifier EYES_FAR = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_far.png");
    private static final Identifier EYES_MALL = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_mall.png");
    private static final Identifier EYES_MELLOHI = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_mellohi.png");
    private static final Identifier EYES_OTHERSIDE = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_otherside.png");
    private static final Identifier EYES_PIGSTEP = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_pigstep.png");
    private static final Identifier EYES_RELIC = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_relic.png");
    private static final Identifier EYES_STAL = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_stal.png");
    private static final Identifier EYES_STRAD = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_strad.png");
    private static final Identifier EYES_WAIT = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_wait.png");
    private static final Identifier EYES_WARD = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_ward.png");

    public iDogEyesFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }
    @Override
    public RenderLayer getEyesTexture() {
        return DEFAULT_EYES_RENDERLAYER;
    }

    /**
     * See {@link iDogEntity#songDisplayLogic()}
     * Clockwise eye order
     *
     *      *   2   *
     *      1   *   3
     *          0
     *      6   *   4
     *      *   5   *
     */
    //TODO: REWORK? All of this needs to be achieved using individual eyes. Not grouped.
    // Each eye needs a vectorConsumer.
    // Individual
    private Identifier getEyesIdentifier(String disc) {

        return switch (disc) {
            case "none" -> DEFAULT_EYES;
            case "music_disc_5" -> EYES_5;
            case "music_disc_11" -> EYES_11;
            case "music_disc_13" -> EYES_13;
            case "music_disc_blocks" -> EYES_BLOCKS;
            case "music_disc_cat" -> EYES_CAT;
            case "music_disc_chirp" -> EYES_CHIRP;
            case "music_disc_far" -> EYES_FAR;
            case "music_disc_mall" -> EYES_MALL;
            case "music_disc_mellohi" -> EYES_MELLOHI;
            case "music_disc_otherside" -> EYES_OTHERSIDE;
            case "music_disc_pigstep" -> EYES_PIGSTEP;
            case "music_disc_relic" -> EYES_RELIC;
            case "music_disc_stal" -> EYES_STAL;
            case "music_disc_strad" -> EYES_STRAD;
            case "music_disc_wait" -> EYES_WAIT;
            case "music_disc_ward" -> EYES_WARD;
            default -> DEFAULT_EYES; //Replace with random getter, maybe check first letter from music_disc_[???] for modded songs.
        };
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       T iDog, float limbAngle, float limbDistance, float tickDelta,
                       float animationProgress, float headYaw, float headPitch) {

        VertexConsumer eyeVertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getEyesIdentifier(iDog.getCurrentDisc())));

        //TODO: Interpolate light with tick count to achieve pulsing effect
        // Maybe... int min = light/2; int max = light; finalDelta = (BPM * tickDelta)/10;
        // lerp(min, max, (finalDelta)
        // Every 10 seconds it will go from min to max brightness. Maybe tie the alpha channel into it and vary the RGB values based on the current disc.
        // EG mellohi is purple so it would lessen the G values, or alternate red and blue so that if one is 0 the other is 1. Or something similar.
        // Varying the lerp delta to match the BPM of the current disc would also look interesting.
        this.getContextModel().render(matrices, eyeVertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, iDog.getEyeAlpha());
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
