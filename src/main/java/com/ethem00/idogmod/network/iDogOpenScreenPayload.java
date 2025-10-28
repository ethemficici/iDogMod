package com.ethem00.idogmod.network;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record iDogOpenScreenPayload(int entityID) implements CustomPayload {
    public static final Id<iDogOpenScreenPayload> ID = new Id<>(Identifier.of(iDogMod.MOD_ID, "idog_open_screen"));
    public static final PacketCodec<PacketByteBuf, iDogOpenScreenPayload> CODEC =
            PacketCodec.of(iDogOpenScreenPayload::write, iDogOpenScreenPayload::read);

    @Override
    public Id<? extends CustomPayload> getId() {
    return ID;
    }

    public static iDogOpenScreenPayload read(PacketByteBuf buf) {
        return new iDogOpenScreenPayload(buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(entityID); //iDog entity ID
    }
}
