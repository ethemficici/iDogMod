package com.ethem00.idogmod.network;

import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier IDOG_BUTTON_PACKET = new Identifier(iDogMod.MOD_ID, "idog_button_packet");

    public static void registerC2SPackets() {
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
    };
}


