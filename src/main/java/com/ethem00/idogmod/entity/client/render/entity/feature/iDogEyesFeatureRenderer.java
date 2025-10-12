package com.ethem00.idogmod.entity.client.render.entity.feature;

import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class iDogEyesFeatureRenderer<T extends iDogEntity, M extends iDogEntityModel<T>> extends EyesFeatureRenderer<T, M> {
    private static final RenderLayer DEFAULT_EYES = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/idog_eyes.png"));

    private static final RenderLayer EYES_5 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_5.png"));
    private static final RenderLayer EYES_11 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_11.png"));
    private static final RenderLayer EYES_13 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_13.png"));
    private static final RenderLayer EYES_BLOCKS = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_blocks.png"));
    private static final RenderLayer EYES_CAT = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_cat.png"));
    private static final RenderLayer EYES_CHIRP = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_chirp.png"));
    private static final RenderLayer EYES_FAR = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_far.png"));
    private static final RenderLayer EYES_MALL = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_mall.png"));
    private static final RenderLayer EYES_MELLOHI = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_mellohi.png"));
    private static final RenderLayer EYES_OTHERSIDE = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_otherside.png"));
    private static final RenderLayer EYES_PIGSTEP = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_pigstep.png"));
    private static final RenderLayer EYES_RELIC = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_relic.png"));
    private static final RenderLayer EYES_STAL = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_stal.png"));
    private static final RenderLayer EYES_STRAD = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_strad.png"));
    private static final RenderLayer EYES_WAIT = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_wait.png"));
    private static final RenderLayer EYES_WARD = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/eyes/idog_eyes_ward.png"));

    public iDogEyesFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    public String retrieveDisc() {
        return iDog.getCurrentDisc();
    }

    //TODO: Retrieve disk type from iDog entity and sort through textures.
    // I don't know how to get information from the rendered entity instance yet.
    @Override
    public RenderLayer getEyesTexture() {
        String disc = retrieveDisc();

        return switch (disc) {
            case "5" -> EYES_5;
            case "11" -> EYES_11;
            case "13" -> EYES_13;
            case "blocks" -> EYES_BLOCKS;
            case "cat" -> EYES_CAT;
            case "chirp" -> EYES_CHIRP;
            case "far" -> EYES_FAR;
            case "mall" -> EYES_MALL;
            case "mellohi" -> EYES_MELLOHI;
            case "otherside" -> EYES_OTHERSIDE;
            case "pigstep" -> EYES_PIGSTEP;
            case "relic" -> EYES_RELIC;
            case "stal" -> EYES_STAL;
            case "strad" -> EYES_STRAD;
            case "wait" -> EYES_WAIT;
            case "ward" -> EYES_WARD;
            default -> DEFAULT_EYES;
        };
    }
}
