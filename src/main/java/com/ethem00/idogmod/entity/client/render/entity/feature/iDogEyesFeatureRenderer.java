package com.ethem00.idogmod.entity.client.render.entity.feature;

import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.client.render.entity.animation.iDogEasing;
import com.ethem00.idogmod.entity.client.render.entity.animation.iDogEyeVariants;
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
 * You can find the texture identifiers inside of {@link iDogEyeVariants} 
 * Modded discs are first randomized for a fake music disc name inside of {@link iDogEntity#getEyeVariantFromDisc}
 * 
 * https://easings.net/
 * Easing mathematical functions provided by Andrey Sitnik and Ivan Solovev
 */


@Environment(EnvType.CLIENT)
public class iDogEyesFeatureRenderer<T extends iDogEntity, M extends iDogEntityModel<T>> extends EyesFeatureRenderer<T, M> implements iDogEyeVariants {
    private static final RenderLayer DEFAULT_EYES_RENDERLAYER = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_5.png"));

    public iDogEyesFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }
    @Override
    public RenderLayer getEyesTexture() {
        return DEFAULT_EYES_RENDERLAYER;
    }

    private Identifier getEyesIdentifier(String disc, Integer variant) {

        //Renderer is sometimes ahead of client/server sync. So it may choose to render bounds from previous selection.
        return switch (disc) {
            case "none" -> (compareInteger(variant, music_disc_default_variants.length-1)) ? music_disc_default_variants[0] : music_disc_default_variants[variant];
            case "music_disc_5" -> (compareInteger(variant, music_disc_5_variants.length-1)) ? music_disc_5_variants[0] : music_disc_5_variants[variant];
            case "music_disc_11" -> (compareInteger(variant, music_disc_11_variants.length-1)) ? music_disc_11_variants[0] : music_disc_11_variants[variant];
            case "music_disc_13" -> (compareInteger(variant, music_disc_13_variants.length-1)) ? music_disc_13_variants[0] : music_disc_13_variants[variant];
            case "music_disc_blocks" -> (compareInteger(variant, music_disc_blocks_variants.length-1)) ? music_disc_blocks_variants[0] : music_disc_blocks_variants[variant];
            case "music_disc_cat" -> (compareInteger(variant, music_disc_cat_variants.length-1)) ? music_disc_cat_variants[0] : music_disc_cat_variants[variant];
            case "music_disc_chirp" -> (compareInteger(variant, music_disc_chirp_variants.length-1)) ? music_disc_chirp_variants[0] : music_disc_chirp_variants[variant];
            case "music_disc_far" -> (compareInteger(variant, music_disc_far_variants.length-1)) ? music_disc_far_variants[0] : music_disc_far_variants[variant];
            case "music_disc_mall" -> (compareInteger(variant, music_disc_mall_variants.length-1)) ? music_disc_mall_variants[0] : music_disc_mall_variants[variant];
            case "music_disc_mellohi" -> (compareInteger(variant, music_disc_mellohi_variants.length-1)) ? music_disc_mellohi_variants[0] : music_disc_mellohi_variants[variant];
            case "music_disc_otherside" -> (compareInteger(variant, music_disc_otherside_variants.length-1)) ? music_disc_otherside_variants[0] : music_disc_otherside_variants[variant];
            case "music_disc_pigstep" -> (compareInteger(variant, music_disc_pigstep_variants.length-1)) ? music_disc_pigstep_variants[0] : music_disc_pigstep_variants[variant];
            case "music_disc_relic" -> (compareInteger(variant, music_disc_relic_variants.length-1)) ? music_disc_relic_variants[0] : music_disc_relic_variants[variant];
            case "music_disc_stal" -> (compareInteger(variant, music_disc_stal_variants.length-1)) ? music_disc_stal_variants[0] : music_disc_stal_variants[variant];
            case "music_disc_strad" -> (compareInteger(variant, music_disc_strad_variants.length-1)) ? music_disc_strad_variants[0] : music_disc_strad_variants[variant];
            case "music_disc_wait" -> (compareInteger(variant, music_disc_wait_variants.length-1)) ? music_disc_wait_variants[0] : music_disc_wait_variants[variant];
            case "music_disc_ward" -> (compareInteger(variant, music_disc_ward_variants.length-1)) ? music_disc_ward_variants[0] : music_disc_ward_variants[variant];
            //-----------------------------------------------------------------------
            //Modded
            case "music_disc_red" -> (compareInteger(variant, music_disc_randomRed_variants.length-1)) ? music_disc_randomRed_variants[0] : music_disc_randomRed_variants[variant];
            case "music_disc_green" -> (compareInteger(variant, music_disc_randomGreen_variants.length-1)) ? music_disc_randomGreen_variants[0] : music_disc_randomGreen_variants[variant];
            case "music_disc_blue" -> (compareInteger(variant, music_disc_randomBlue_variants.length-1)) ? music_disc_randomBlue_variants[0] : music_disc_randomBlue_variants[variant];
            case "music_disc_rainbow" -> (compareInteger(variant, music_disc_randomRainbow_variants.length-1)) ? music_disc_randomRainbow_variants[0] : music_disc_randomRainbow_variants[variant];
            case "music_disc_creeper" -> (compareInteger(variant, music_disc_randomCreeper_variants.length-1)) ? music_disc_randomCreeper_variants[0] : music_disc_randomCreeper_variants[variant];
            default -> music_disc_default_variants[0];
        };
    }

    //TODO: Render thread seemingly sends warnings after tamed iDogs are unloaded/teleport to player.
    // Investigate. Maybe its a coincidence, but it seems odd.
    // [00:04:51] [Render thread/WARN] (Minecraft) Received passengers for unknown entity
    // [00:05:04] [Render thread/WARN] (Minecraft) Received passengers for unknown entity
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       T iDog, float limbAngle, float limbDistance, float tickDelta,
                       float animationProgress, float headYaw, float headPitch) {

        VertexConsumer eyeVertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getEyesIdentifier(iDog.getCurrentDisc(), iDog.getEyeVariant())));

        this.getContextModel().render(matrices, eyeVertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, iDog.getEyeAlpha());
    }
}
