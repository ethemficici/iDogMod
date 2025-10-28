package com.ethem00.idogmod.network;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record iDogButtonPayload(int entityID, int packetType, RegistryKey<World> worldKey) implements CustomPayload {
    public static final Id<iDogButtonPayload> ID = new Id<>(Identifier.of(iDogMod.MOD_ID, "idog_button_press"));
    public static final PacketCodec<PacketByteBuf, iDogButtonPayload> CODEC =
            PacketCodec.of(iDogButtonPayload::write, iDogButtonPayload::read);

    @Override
    public Id<? extends CustomPayload> getId() {
    return ID;
    }

    public static iDogButtonPayload read(PacketByteBuf buf) {
        return new iDogButtonPayload(buf.readInt(), buf.readInt(), buf.readRegistryKey(RegistryKeys.WORLD));
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(entityID); //iDog entity ID
        buf.writeInt(packetType); //Alert Type
        buf.writeRegistryKey(worldKey);
    }
}
