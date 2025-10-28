package com.ethem00.idogmod.network;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record FinishedAlertPayload(int entityID, RegistryKey<World> worldKey) implements CustomPayload {
    public static final Id<FinishedAlertPayload> ID = new Id<>(Identifier.of(iDogMod.MOD_ID, "alert_finished"));
    public static final PacketCodec<PacketByteBuf, FinishedAlertPayload> CODEC =
            PacketCodec.of(FinishedAlertPayload::write, FinishedAlertPayload::read);

    @Override
    public Id<? extends CustomPayload> getId() {
    return ID;
    }

    public static FinishedAlertPayload read(PacketByteBuf buf) {
        return new FinishedAlertPayload(buf.readInt(), buf.readRegistryKey(RegistryKeys.WORLD));
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(entityID); //iDog entity ID
        buf.writeRegistryKey(worldKey);
    }
}
