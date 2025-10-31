package com.ethem00.idogmod.network;

import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class iDogButtonPressedPacketC2S {

    private final int entityID;
    private final int buttonType;

    public iDogButtonPressedPacketC2S(int pID, int pButtonType) {
        this.entityID = pID;
        this.buttonType = pButtonType;
    }

    public iDogButtonPressedPacketC2S(FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.buttonType = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeInt(buttonType);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context cntx = context.get();

        if (context.get().getDirection().getReceptionSide().isServer()) {
            cntx.enqueueWork(() -> handleServer(context));
        }

        cntx.setPacketHandled(true);

    }

    public void handleServer(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null) return;

        Level world = player.level();
        Entity entity = world.getEntity(entityID);

        if (entity instanceof iDogEntity dog) {
            dog.handleReceivedPacket(buttonType);
        }
    }
}
