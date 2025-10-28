package com.ethem00.idogmod.network;

import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public class ModPackets {
    public static void registerC2SPackets() {
        iDogMod.LOGGER.info("Registering client to server packets for " + iDogMod.MOD_ID);
        PayloadTypeRegistry.playC2S().register(FinishedAlertPayload.ID, FinishedAlertPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(iDogButtonPayload.ID, iDogButtonPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(iDogButtonPayload.ID, (payload, context) -> {
            int entityId = payload.entityID();
            int packetType = payload.packetType();
            RegistryKey<World> worldKey = payload.worldKey();

            //System.out.println("Packet of " + packetType + " received from entity " + entityId);

            context.server().execute(() -> {
                Entity entity = context.server().getWorld(worldKey).getEntityById(entityId); //Key???
                if (entity instanceof iDogEntity idog) {
                    // iDog has switch cases varying on int.
                    // -10, -5, 5, 10 all decrement or increment volume
                    // -1, 1 either mute or set volume to max.
                    // -2, 2 either disable or enable looping.
                    // -3, 3 either disable or enable entity alerts.
                    idog.handleReceivedPacket(packetType);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(FinishedAlertPayload.ID, (payload, context) -> {
            int entityId = payload.entityID();
            RegistryKey<World> worldKey = payload.worldKey();

            //System.out.println("Packet of " + packetType + " received from entity " + entityId);

            context.server().execute(() -> {
                Entity entity = context.server().getWorld(worldKey).getEntityById(entityId);
                if (entity instanceof iDogEntity idog) {

                    idog.handleReceivedPacket(-100);
                    // iDog has switch cases varying on int.
                    // -10, -5, 5, 10 all decrement or increment volume
                    // -1, 1 either mute or set volume to max.
                    // -2, 2 either disable or enable looping.
                    // -3, 3 either disable or enable entity alerts.
                }
            });
        });
    }
}


