package com.ethem00.idogmod;

import com.ethem00.idogmod.entity.ModEntities;
import com.ethem00.idogmod.entity.client.ModModelLayers;
import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.client.iDogRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class iDogModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(ModEntities.IDOG, iDogRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.IDOG, iDogEntityModel::getTexturedModelData);
    }
}
