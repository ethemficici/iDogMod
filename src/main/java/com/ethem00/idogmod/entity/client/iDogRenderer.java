package com.ethem00.idogmod.entity.client;

import com.ethem00.idogmod.entity.client.render.entity.feature.iDogEyesFeatureRenderer;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class iDogRenderer extends MobEntityRenderer<iDogEntity, iDogEntityModel<iDogEntity>> {
    private static final Identifier BODY_TEXTURE = new Identifier(iDogMod.MOD_ID, "textures/entity/idog/idog.png");

    public iDogRenderer(EntityRendererFactory.Context context) {
        super(context, new iDogEntityModel<>(context.getPart(ModModelLayers.IDOG)), 0.5f);
        this.addFeature(new iDogEyesFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(iDogEntity entity) {
        return BODY_TEXTURE;
    }

    @Override
    protected RenderLayer getRenderLayer(iDogEntity entity, boolean showBody, boolean translucent, boolean outline) {
        Identifier texture = this.getTexture(entity);
        return RenderLayer.getEntityTranslucent(texture);
    }

    @Override
    public void render(iDogEntity mobEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i){

        if(mobEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        /*
        if(mobEntity.isBegging()) {

        }

         */

        // Now actually render the model!
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}