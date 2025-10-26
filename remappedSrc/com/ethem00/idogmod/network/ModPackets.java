package com.ethem00.idogmod.network;

import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier IDOG_BUTTON_PACKET = new Identifier(iDogMod.MOD_ID, "idog_button_packet");
    public static final Identifier IDOG_ALERT_PACKET = new Identifier(iDogMod.MOD_ID, "idog_alert_packet");

    public static void registerC2SPackets() {
        iDogMod.LOGGER.info("Registering client to server packets for " + iDogMod.MOD_ID);

        ServerPlayNetworking.registerGlobalReceiver(IDOG_BUTTON_PACKET, (server, player, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            int packetType = buf.readInt();

            //System.out.println("Packet of " + packetType + " received from entity " + entityId);

            server.execute(() -> {
                Entity entity = player.getWorld().getEntityById(entityId);
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

        ServerPlayNetworking.registerGlobalReceiver(IDOG_ALERT_PACKET, (server, player, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            boolean bufBool = buf.readBoolean();

            //System.out.println("Packet of " + packetType + " received from entity " + entityId);

            server.execute(() -> {
                Entity entity = player.getWorld().getEntityById(entityId);
                if (entity instanceof iDogEntity idog) {

                    if(bufBool) {
                        idog.handleReceivedPacket(-100);
                    } else {
                        //ERROR
                        System.out.println("Packet of TRUE received as FALSE from entity " + entityId);
                    }
                    // iDog has switch cases varying on int.
                    // -10, -5, 5, 10 all decrement or increment volume
                    // -1, 1 either mute or set volume to max.
                    // -2, 2 either disable or enable looping.
                    // -3, 3 either disable or enable entity alerts.
                }
            });
        });
    };
}


