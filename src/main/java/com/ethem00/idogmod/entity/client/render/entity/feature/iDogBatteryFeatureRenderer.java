package com.ethem00.idogmod.entity.client.render.entity.feature;

import com.ethem00.idogmod.entity.client.iDogEntityModel;
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


@OnlyIn(Dist.CLIENT)
public class iDogBatteryFeatureRenderer<T extends iDogEntity, M extends iDogEntityModel<T>> extends EyesLayer<T, M> {
    private static final RenderType BATTERY_100 = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/battery/idog_battery_100.png"));
    private static final RenderType BATTERY_75 = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/battery/idog_battery_75.png"));
    private static final RenderType BATTERY_50 = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/battery/idog_battery_50.png"));
    private static final RenderType BATTERY_25 = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/entity/idog/battery/idog_battery_25.png"));

    public iDogBatteryFeatureRenderer(RenderLayerParent<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    private RenderType getBatteryTexture(float health, Boolean tamed) {

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
    public RenderType renderType() {
        return BATTERY_100;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T iDog,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float netHeadPitch) {


        RenderType batteryLayer = this.getBatteryTexture(iDog.getHealth(), iDog.isTame());
        VertexConsumer vc = buffer.getBuffer(batteryLayer);

        this.getParentModel().renderToBuffer(poseStack, vc, 15728640, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
