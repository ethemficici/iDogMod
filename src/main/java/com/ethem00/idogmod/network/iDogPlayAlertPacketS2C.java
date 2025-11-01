package com.ethem00.idogmod.network;

import com.ethem00.idogmod.entity.client.sound.iDogMovingAlertInstance;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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
            cntx.enqueueWork(() -> handleClient(context));
        }

        cntx.setPacketHandled(true);
    }

    public void handleClient(Supplier<NetworkEvent.Context> context) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        Entity entity = level.getEntity(entityID);

        if (entity instanceof iDogEntity dog) {

            SoundEvent sound = switch(alertType) {
                case -1 -> ModSounds.ENTITY_IDOG_ALERT_HAPPY.get();
                case 0 -> ModSounds.ENTITY_IDOG_ALERT_ZOMBIE.get();
                case 1 -> ModSounds.ENTITY_IDOG_ALERT_SKELETON.get();
                case 2 -> ModSounds.ENTITY_IDOG_ALERT_SPIDER.get();
                case 3 -> ModSounds.ENTITY_IDOG_ALERT_CREEPER.get();
                case 4 -> ModSounds.ENTITY_IDOG_ALERT_ENDERMAN.get();
                default -> ModSounds.ENTITY_IDOG_ALERT_MISC.get();
            };

            float seconds = switch(alertType) {
                case -1 -> 8.0F;
                case 0 -> 17.4F;
                case 1 -> 20.5F;
                case 2 -> 15.2F;
                case 3 -> 15.9F;
                case 4 -> 20;
                default -> 8;
            };

            minecraft.getSoundManager().play(
                    new iDogMovingAlertInstance(dog, sound, dog.getSongVolume(true), seconds));
        }
    }
}


