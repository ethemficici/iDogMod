package com.ethem00.idogmod;

import com.ethem00.idogmod.entity.ModEntities;
import com.ethem00.idogmod.entity.client.ModModelLayers;
import com.ethem00.idogmod.entity.client.gui.screen.ingame.iDogScreen;
import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.client.iDogRenderer;
import com.ethem00.idogmod.entity.client.sound.iDogMovingAlertInstance;
import com.ethem00.idogmod.entity.client.sound.iDogMovingSoundInstance;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.network.PlayAlertPayload;
import com.ethem00.idogmod.network.PlayMusicPayload;
import com.ethem00.idogmod.network.iDogOpenScreenPayload;
import com.ethem00.idogmod.screen.ModScreenHandlers;
import com.ethem00.idogmod.screen.iDogScreenHandler;
import com.ethem00.idogmod.sound.ModSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class iDogModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {


        //TODO: NEW PACKET TYPE
        // MOVING SOUND INSTANCE PINGS BACK TO IDOG EVERY ~5 SECONDS
        // TO CONFIRM THE SONG IS STILL PLAYING.
        // IF NOT, SET ISPLAYING TO FALSE
        // int missedPackets
        // missedPackets++
        // resetPackets { missedPackets = 0)
        // if(missPackets > 5) {stopPlaying()}

        //TODO HAVE IDOGS LOAD CHUNKS WHILE PLAYING MUSIC?

        EntityRendererRegistry.register(ModEntities.IDOG, iDogRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.IDOG, iDogEntityModel::getTexturedModelData);
        HandledScreens.register(ModScreenHandlers.IDOG_SCREEN_HANDLER, iDogScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(iDogOpenScreenPayload.ID, ((payload, context) -> {

            int entityId = payload.entityID();
            context.client().execute(() -> {

                Entity e = context.client().world.getEntityById(entityId);
                if (e instanceof iDogEntity idog) {
                    MinecraftClient.getInstance().setScreen(
                            new iDogScreen(new iDogScreenHandler(0, MinecraftClient.getInstance().player.getInventory(), idog.getInventory(), idog), MinecraftClient.getInstance().player.getInventory(), Text.empty())
                    );
                }
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(PlayMusicPayload.ID, ((payload, context) -> {
                    //System.out.println("Recieved from: " + entityId);

                    int entityId = payload.entityID();
                    Identifier discID = payload.itemStack();
                    String discString = payload.currentDisc();
                    Identifier songID = payload.soundEvent();

                    context.client().execute(() -> {
                        ClientWorld world = MinecraftClient.getInstance().world;
                        if (world == null) return;
                        Entity entity = world.getEntityById(entityId);
                        //System.out.println("Client world confirmed, entity grabbed.");

                        if (entity instanceof iDogEntity) {
                            ItemStack item = new ItemStack(Registries.ITEM.get(discID));
                            //System.out.println("Entity is iDog");


                            if (item.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "music_discs")))) {
                                //System.out.println("Item is of instance MusicDiscItem");

                                //Tell the iDog to sync with server data
                                ((iDogEntity) entity).forceSync(item, discString);

                                //Moving sound instance
                                SoundEvent sound = Registries.SOUND_EVENT.get(songID);
                                //Needs a sound event, maybe I can send an identifier for one inside the packet?
                                //System.out.println("IM PLAYING A SONGE");

                                MinecraftClient.getInstance().getSoundManager().play(new iDogMovingSoundInstance(((iDogEntity) entity), sound, ((iDogEntity) entity).getSongVolume(false)));
                            }
                        }
                    });
        }));

        ClientPlayNetworking.registerGlobalReceiver(PlayAlertPayload.ID,
                (payload, context)-> {
                    int entityId = payload.entityID();
                    int alertType = payload.alertType();

                    //System.out.println("Recieved from: " + entityId);

                    context.client().execute(() -> {
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