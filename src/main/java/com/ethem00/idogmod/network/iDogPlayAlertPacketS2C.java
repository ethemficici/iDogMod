package com.ethem00.idogmod.network;

import com.ethem00.idogmod.entity.client.sound.iDogMovingAlertInstance;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class iDogPlayAlertPacketS2C {

    private final int entityID;
    private final int alertType;

    public iDogPlayAlertPacketS2C(int pID, int pAlertType) {
        this.entityID = pID;
        this.alertType = pAlertType;
    }

    public iDogPlayAlertPacketS2C(FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.alertType = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeInt(alertType);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context cntx = context.get();

        if (context.get().getDirection().getReceptionSide().isClient()) {
            cntx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> ClientAlertHandler.handleClient(context, entityID, alertType)));
        }

        cntx.setPacketHandled(true);
    }
}


