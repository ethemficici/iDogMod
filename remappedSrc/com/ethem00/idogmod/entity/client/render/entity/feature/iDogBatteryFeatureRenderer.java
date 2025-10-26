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
public class iDogBatteryFeatureRenderer<T extends iDogEntity, M extends iDogEntityModel<T>> extends EyesFeatureRenderer<T, M> {
    private static final RenderLayer BATTERY_100 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/battery/idog_battery_100.png"));
    private static final RenderLayer BATTERY_75 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/battery/idog_battery_75.png"));
    private static final RenderLayer BATTERY_50 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/battery/idog_battery_50.png"));
    private static final RenderLayer BATTERY_25 = RenderLayer.getEyes(new Identifier(iDogMod.MOD_ID, "textures/entity/idog/battery/idog_battery_25.png"));

    public iDogBatteryFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    private RenderLayer getBatteryTexture(float health, Boolean tamed) {

        //Battery texture is 5x3. Maybe redo to check increments of 20.
        if(tamed) {
            if(health > 22.5F) {return BATTERY_100;}
            if(health <= 22.5F && health > 15) {return BATTERY_75;}
            if(health <= 15 && health > 7.5F) {return BATTERY_50;}
            if(health <= 7.5F) {return BATTERY_25;}
        } else {
            if(health > 9) {return BATTERY_100;}
            if(health <= 9 && health > 6) {return BATTERY_75;}
            if(health <= 6 && health > 3) {return BATTERY_50;}
            if(health <= 3) {return BATTERY_25;}
        }
        return BATTERY_100;
    }

    @Override
    public RenderLayer getEyesTexture() {
        return BATTERY_100;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            T idogEntity, float limbAngle, float limbDistance, float tickDelta,
            float animationProgress, float headYaw, float headPitch) {

        VertexConsumer batteryVertexConsumer = vertexConsumers.getBuffer(this.getBatteryTexture(idogEntity.getHealth(), idogEntity.isTamed()));
        this.getContextModel().render(matrices, batteryVertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
