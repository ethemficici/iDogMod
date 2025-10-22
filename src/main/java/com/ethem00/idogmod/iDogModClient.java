package com.ethem00.idogmod;

import com.ethem00.idogmod.entity.ModEntities;
import com.ethem00.idogmod.entity.client.ModModelLayers;
import com.ethem00.idogmod.entity.client.gui.screen.ingame.iDogScreen;
import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.client.iDogRenderer;
import com.ethem00.idogmod.entity.client.sound.iDogMovingAlertInstance;
import com.ethem00.idogmod.entity.client.sound.iDogMovingSoundInstance;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.screen.ModScreenHandlers;
import com.ethem00.idogmod.sound.ModSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;


public class iDogModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(ModEntities.IDOG, iDogRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.IDOG, iDogEntityModel::getTexturedModelData);
        HandledScreens.register(ModScreenHandlers.IDOG_SCREEN_HANDLER, iDogScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(iDogMod.PLAY_IDOG_MUSIC,
                (client, handler, buf, responseSender)-> {
            int entityId = buf.readInt();
            Identifier discId = buf.readIdentifier();

                    //System.out.println("Recieved from: " + entityId);

        client.execute(() -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world == null) return;
            Entity entity = world.getEntityById(entityId);
            //System.out.println("Client world confirmed, entity grabbed.");

                    if(entity instanceof iDogEntity) {
                        Item item = Registries.ITEM.get(discId);
                        //System.out.println("Entity is iDog");

                        //TODO: Instance is no longer accepting the loopBoolean. Looping is handled in the iDog's songDisplayLogic.
                        // Trim the packet
                        if(item instanceof MusicDiscItem) {
                            //System.out.println("Item is of instance MusicDiscItem");
                            SoundEvent sound = ((MusicDiscItem) item).getSound();
                            MinecraftClient.getInstance().getSoundManager().play(new iDogMovingSoundInstance(((iDogEntity) entity), sound, ((iDogEntity) entity).getLoopBool(), ((iDogEntity) entity).getSongVolume(false)));
                        }
                    }
                });
        });


        ClientPlayNetworking.registerGlobalReceiver(iDogMod.PLAY_IDOG_ALERT,
                (client, handler, buf, responseSender)-> {
                    int entityId = buf.readInt();
                    int alertType = buf.readInt();

                    //System.out.println("Recieved from: " + entityId);

                    client.execute(() -> {
                        ClientWorld world = MinecraftClient.getInstance().world;
                        if (world == null) return;
                        Entity entity = world.getEntityById(entityId);
                        //System.out.println("Client world confirmed, entity grabbed.");

                        if(entity instanceof iDogEntity) {
                            //System.out.println("Entity is iDog");


                            SoundEvent sound = switch(alertType) {
                                case -1 -> ModSounds.ENTITY_IDOG_ALERT_HAPPY;
                                case 0 -> ModSounds.ENTITY_IDOG_ALERT_ZOMBIE;
                                case 1 -> ModSounds.ENTITY_IDOG_ALERT_SKELETON; //SKELETON
                                case 2 -> ModSounds.ENTITY_IDOG_ALERT_SPIDER;
                                case 3 -> ModSounds.ENTITY_IDOG_ALERT_CREEPER; //CREEPER
                                case 4 -> ModSounds.ENTITY_IDOG_ALERT_ENDERMAN; //ENDERMAN
                                default -> ModSounds.ENTITY_IDOG_ALERT_MISC;
                            };

                            float seconds = switch(alertType) {
                                case -1 -> 8;
                                case 0 -> 17.4F;
                                case 1 -> 20.5F; //TODO SKELETON
                                case 2 -> 15.2F;
                                case 3 -> 15.9F; //TODO CREEPER
                                case 4 -> 20; //TODO ENDERMAN
                                default -> 8;
                            };

                            MinecraftClient.getInstance().getSoundManager().play(new iDogMovingAlertInstance(((iDogEntity) entity), sound, ((iDogEntity) entity).getSongVolume(true), seconds));
                        }
                    });
                });
    }
}