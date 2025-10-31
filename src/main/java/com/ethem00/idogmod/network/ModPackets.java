package com.ethem00.idogmod.network;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPackets {

    private static int packetId = 0;
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        CHANNEL.registerMessage(packetId++, iDogAlertFinishedPacketC2S.class, iDogAlertFinishedPacketC2S::encode, iDogAlertFinishedPacketC2S::new, iDogAlertFinishedPacketC2S::handle);
        CHANNEL.registerMessage(packetId++, iDogButtonPressedPacketC2S.class, iDogButtonPressedPacketC2S::encode, iDogButtonPressedPacketC2S::new, iDogButtonPressedPacketC2S::handle);
        CHANNEL.registerMessage(packetId++, iDogPlayAlertPacketS2C.class, iDogPlayAlertPacketS2C::encode, iDogPlayAlertPacketS2C::new, iDogPlayAlertPacketS2C::handle);
        CHANNEL.registerMessage(packetId++, iDogPlayDiscPacketS2C.class, iDogPlayDiscPacketS2C::encode, iDogPlayDiscPacketS2C::new, iDogPlayDiscPacketS2C::handle);
        //iDogMod.LOGGER.info("Registered network packets for " + iDogMod.MOD_ID);
    }

}
