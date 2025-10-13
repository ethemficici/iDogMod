package com.ethem00.idogmod;

import com.ethem00.idogmod.entity.ModEntities;
import com.ethem00.idogmod.entity.client.ModModelLayers;
import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.client.iDogRenderer;
import com.ethem00.idogmod.entity.client.sound.iDogMovingSoundInstance;
import com.ethem00.idogmod.entity.iDogEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;


public class iDogModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(ModEntities.IDOG, iDogRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.IDOG, iDogEntityModel::getTexturedModelData);

        ClientPlayNetworking.registerGlobalReceiver(iDogMod.PLAY_IDOG_MUSIC,
                (client, handler, buf, responseSender)-> {
            int entityId = buf.readInt();
            Identifier discId = buf.readIdentifier();

                    System.out.println("Recieved from: " + entityId);

        client.execute(() -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world == null) return;
            Entity entity = world.getEntityById(entityId);
            System.out.println("Client world confirmed, entity grabbed.");

                    if(entity instanceof iDogEntity) {
                        Item item = Registries.ITEM.get(discId);
                        System.out.println("Entity is iDog");

                        if(item instanceof MusicDiscItem) {
                            System.out.println("Item is of instance MusicDiscItem");
                            SoundEvent sound = ((MusicDiscItem) item).getSound();
                            MinecraftClient.getInstance().getSoundManager().play(new iDogMovingSoundInstance(((iDogEntity) entity), sound, ((iDogEntity) entity).getLoopBool(), ((iDogEntity) entity).getSongVolume()));
                        }
                    }
                });
        });
    }
}
