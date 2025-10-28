package com.ethem00.idogmod.network;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayMusicPayload(int entityID, Identifier itemStack, String currentDisc, Identifier soundEvent) implements CustomPayload {
    public static final Id<PlayMusicPayload> ID = new Id<>(Identifier.of(iDogMod.MOD_ID, "play_idog_music"));
    public static final PacketCodec<PacketByteBuf, PlayMusicPayload> CODEC =
            PacketCodec.of(PlayMusicPayload::write, PlayMusicPayload::read);

    @Override
    public Id<? extends CustomPayload> getId() {
    return ID;
    }

    public static PlayMusicPayload read(PacketByteBuf buf) {
        return new PlayMusicPayload(buf.readInt(), buf.readIdentifier(), buf.readString(), buf.readIdentifier());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(entityID); //iDog entity ID
        buf.writeIdentifier(itemStack); // Itemstack
        buf.writeString(currentDisc); //Disc
        buf.writeIdentifier(soundEvent); //Song soundevent
    }
}
