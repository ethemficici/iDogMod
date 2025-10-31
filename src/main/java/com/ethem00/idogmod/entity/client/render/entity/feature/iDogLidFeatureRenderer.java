package com.ethem00.idogmod.entity.client.render.entity.feature;

import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.client.render.entity.animation.iDogEasing;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static org.apache.commons.lang3.RandomUtils.nextInt;

/**
 * Eye interpolation and easing logic handled in {@link iDogEntity#songDisplayLogic()}
 * Also see {@link iDogEasing}
 *
 * https://easings.net/
 * Easing mathematical functions provided by Andrey Sitnik and Ivan Solovev
 */

@OnlyIn(Dist.CLIENT)
public class iDogLidFeatureRenderer<T extends iDogEntity, M extends iDogEntityModel<T>> extends EyesLayer<T, M> {
    private static final RenderType COVER_NONE_RENDERLAYER = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_none.png"));
    //Render layer!
    private static final ResourceLocation COVER_MISSING_NUMBER = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_missing_number.png");

    private static final ResourceLocation COVER_INVERTED_CENTER = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_center.png");
    private static final ResourceLocation COVER_NONE = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_none.png");
    private static final ResourceLocation COVER_ALL = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_all.png");
    private static final ResourceLocation COVER_CENTER = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_center.png");

    private static final ResourceLocation COVER_3 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_3.png");
    private static final ResourceLocation COVER_4 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_4.png");
    private static final ResourceLocation COVER_5 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_5.png");
    private static final ResourceLocation COVER_6 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_6.png");
    private static final ResourceLocation COVER_7 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_7.png");
    private static final ResourceLocation COVER_8 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_clockwise_8.png");

    private static final ResourceLocation COVER_3_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_3.png");
    private static final ResourceLocation COVER_4_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_4.png");
    private static final ResourceLocation COVER_5_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_5.png");
    private static final ResourceLocation COVER_6_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_6.png");
    private static final ResourceLocation COVER_7_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_7.png");
    private static final ResourceLocation COVER_8_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_inverted_clockwise_8.png");

    private static final ResourceLocation COVER_TOP = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_top.png");
    private static final ResourceLocation COVER_BOTTOM = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_bottom.png");
    private static final ResourceLocation COVER_TOP_CENTER = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_top_center.png");
    private static final ResourceLocation COVER_BOTTOM_CENTER = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_bottom_center.png");

    private static final ResourceLocation COVER_40 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_40.png");
    private static final ResourceLocation COVER_407 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_407.png");
    private static final ResourceLocation COVER_70 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_70.png");
    private static final ResourceLocation COVER_407_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_407_inverted.png");

    private static final ResourceLocation COVER_TRI_LEFT_DOWN = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_left_down.png");
    private static final ResourceLocation COVER_TRI_LEFT = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_left.png");
    private static final ResourceLocation COVER_TRI_LEFT_UP = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_left_up.png");
    private static final ResourceLocation COVER_TRI_RIGHT_DOWN = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_right_down.png");
    private static final ResourceLocation COVER_TRI_RIGHT = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_right.png");
    private static final ResourceLocation COVER_TRI_RIGHT_UP = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_right_up.png");

    private static final ResourceLocation COVER_INVERTED_TRI_LEFT_DOWN = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_left_down.png");
    private static final ResourceLocation COVER_INVERTED_TRI_LEFT = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_left.png");
    private static final ResourceLocation COVER_INVERTED_TRI_LEFT_UP = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_left_up.png");
    private static final ResourceLocation COVER_INVERTED_TRI_RIGHT_DOWN = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_right_down.png");
    private static final ResourceLocation COVER_INVERTED_TRI_RIGHT = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_right.png");
    private static final ResourceLocation COVER_INVERTED_TRI_RIGHT_UP = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_tri_inverted_right_up.png");

    private static final ResourceLocation COVER_TWIST1 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_twist_1.png");
    private static final ResourceLocation COVER_TWIST2 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_twist_2.png");
    private static final ResourceLocation COVER_TWIST3 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_twist_3.png");
    private static final ResourceLocation COVER_TWIST1_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_twist_1_inverted.png");
    private static final ResourceLocation COVER_TWIST2_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_twist_2_inverted.png");
    private static final ResourceLocation COVER_TWIST3_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_twist_3_inverted.png");

    //Threes
    private static final ResourceLocation COVER_345 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_345.png");
    private static final ResourceLocation COVER_345_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_345_inverted.png");
    private static final ResourceLocation COVER_678 = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_678.png");
    private static final ResourceLocation COVER_678_INVERTED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/eyes/covers/idog_eyes_cover_678_inverted.png");


    public iDogLidFeatureRenderer(RenderLayerParent<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderType renderType() {
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
    private ResourceLocation getCoverTexture(int cover) {
        return switch (cover) {

            case -678 -> COVER_678_INVERTED;
            case -345 -> COVER_345_INVERTED;
            case -333 -> COVER_INVERTED_TRI_LEFT;
            case -334 -> COVER_INVERTED_TRI_LEFT_UP;
            case -335 -> COVER_INVERTED_TRI_RIGHT_UP;
            case -336 -> COVER_INVERTED_TRI_RIGHT;
            case -337 -> COVER_INVERTED_TRI_RIGHT_DOWN;
            case -338 -> COVER_INVERTED_TRI_LEFT_DOWN;
            case -407 -> COVER_407_INVERTED; // Stripe middle closed
            case -74 -> COVER_TWIST1_INVERTED; //Also cover X inverted
            case -85 -> COVER_TWIST2_INVERTED;
            case -36 -> COVER_TWIST3_INVERTED;
            case -47 -> COVER_TWIST1_INVERTED; //Also cover X inverted
            case -58 -> COVER_TWIST2_INVERTED;
            case -63 -> COVER_TWIST3_INVERTED;
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
            case 74 -> COVER_TWIST1; //Also cover X
            case 85 -> COVER_TWIST2;
            case 36 -> COVER_TWIST3;
            case 47 -> COVER_TWIST1; //Also cover X
            case 58 -> COVER_TWIST2;
            case 63 -> COVER_TWIST3;
            case 70 -> COVER_70;
            case 407 -> COVER_407; //Stripe middle open
            case 333 -> COVER_TRI_LEFT;
            case 334 -> COVER_TRI_LEFT_UP;
            case 335 -> COVER_TRI_RIGHT_UP;
            case 336 -> COVER_TRI_RIGHT;
            case 337 -> COVER_TRI_RIGHT_DOWN;
            case 338 -> COVER_TRI_LEFT_DOWN;
            case 345 -> COVER_345;
            case 678 -> COVER_678;
            default -> COVER_MISSING_NUMBER;
        };
    }

    //ONLY ONE LAYER AT A TIME!
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T iDog,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float netHeadPitch) {

        RenderType lidLayer = RenderType.armorCutoutNoCull(getCoverTexture(iDog.getEyeCover()));
        VertexConsumer vc = buffer.getBuffer(lidLayer);

        this.getParentModel().renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
