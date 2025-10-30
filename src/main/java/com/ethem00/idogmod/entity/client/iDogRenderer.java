package com.ethem00.idogmod.entity.client;

import com.ethem00.idogmod.entity.client.render.entity.feature.iDogLidFeatureRenderer;
import com.ethem00.idogmod.entity.client.render.entity.feature.iDogBatteryFeatureRenderer;
import com.ethem00.idogmod.entity.client.render.entity.feature.iDogEyesFeatureRenderer;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;


public class iDogRenderer extends MobRenderer<iDogEntity, iDogEntityModel<iDogEntity>> {
    private static final ResourceLocation BODY_TEXTURE = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/idog.png");
    private static final ResourceLocation BODY_TEXTURE_DAY = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/idog_day.png");
    private static final ResourceLocation BODY_TEXTURE_RED = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/idog_red.png");
    private static final ResourceLocation BODY_TEXTURE_GREEN = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/idog_green.png");
    private static final ResourceLocation BODY_TEXTURE_BLUE = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/idog_blue.png");

    public iDogRenderer(EntityRendererProvider.Context context) {
        super(context, new iDogEntityModel<>(context.bakeLayer(ModModelLayers.IDOG)), 0.5f);
        this.addLayer(new iDogBatteryFeatureRenderer(this));
        this.addLayer(new iDogEyesFeatureRenderer(this));
        this.addLayer(new iDogLidFeatureRenderer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(iDogEntity entity) {

        if(entity.isInvisible()) { //TODO Figure out what to pass here! Maybe do something in render() instead?
            return BODY_TEXTURE;

        } else {
            return BODY_TEXTURE;

        }
    }

    @Override
    protected RenderType getRenderType(iDogEntity entity, boolean showBody, boolean translucent, boolean outline) {
        ResourceLocation texture = this.getTextureLocation(entity);
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void render(iDogEntity idogEntity, float f, float g, PoseStack matrixStack,
                       MultiBufferSource vertexConsumerProvider, int i){

        if(idogEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        // Now actually render the model!
        super.render(idogEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}