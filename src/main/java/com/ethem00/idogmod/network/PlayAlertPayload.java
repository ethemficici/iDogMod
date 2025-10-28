package com.ethem00.idogmod.network;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayAlertPayload(int entityID, int alertType) implements CustomPayload {
    public static final Id<PlayAlertPayload> ID = new Id<>(Identifier.of(iDogMod.MOD_ID, "play_idog_alert"));
    public static final PacketCodec<PacketByteBuf, PlayAlertPayload> CODEC =
            PacketCodec.of(PlayAlertPayload::write, PlayAlertPayload::read);

    @Override
    public Id<? extends CustomPayload> getId() {
    return ID;
    }

    public static PlayAlertPayload read(PacketByteBuf buf) {
        return new PlayAlertPayload(buf.readInt(), buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(entityID); //iDog entity ID
        buf.writeInt(alertType); //Alert Type
    }
}
