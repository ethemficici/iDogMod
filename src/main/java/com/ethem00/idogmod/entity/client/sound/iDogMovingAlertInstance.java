package com.ethem00.idogmod.entity.client.sound;

import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.network.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class iDogMovingAlertInstance extends MovingSoundInstance {
    private iDogEntity iDog;
    private float storedVolume;
    private float secondsInTicks;
    private int ticks;
    private boolean doOnce;

    public iDogMovingAlertInstance(iDogEntity iDogPassed, SoundEvent sound, float volumePassed, float seconds) {
        super(sound, SoundCategory.RECORDS, SoundInstance.createRandom());
        this.iDog = iDogPassed;
        this.repeat = false;
        this.storedVolume = this.iDog.getSongVolume(true);
        this.ticks = 0;
        this.secondsInTicks = seconds * 20;
        this.doOnce = false;

        if(volumePassed <= 0F) {
            this.volume = 0.05F;
        } else {
            this.volume = volumePassed;
        }
        this.x = iDog.getX();
        this.y = iDog.getY();
        this.z = iDog.getZ();

        //Debug
        System.out.println("iDog is now playing: " + sound.getId().toString());
        System.out.println("With volume of: " + volume);
    }

    @Override
    public void tick() {
        this.volume = this.iDog.getSongVolume(true);
        this.x = this.iDog.getX();
        this.y = this.iDog.getY();
        this.z = this.iDog.getZ();

        if(iDog.isRemoved()) {
            this.setDone();
            System.out.println("iDog alert playback has stopped.");
            System.out.println("Due to iDog entity removal.");
            this.packetSender();
            this.setDone();
        }
        if(!iDog.getAlertBool()) {
            System.out.println("iDog alert playback has stopped.");
            System.out.println("Due to iDog alerts disabled.");
            this.packetSender();
            this.setDone();
        }

        if(this.ticks >= this.secondsInTicks && !this.doOnce) {
            this.packetSender();
        }

        this.ticks++;
        //Debug
        //System.out.println(this.ticks + " of " + this.secondsInTicks);
    }

    private void packetSender() {
        if (this.iDog == null) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(this.iDog.getId());   // Send the iDog entity ID
        buf.writeBoolean(true);      // Send the boolean as true, so server knows the alert has ended.

        System.out.println("Packet of TRUE being sent by entity " + this.iDog.getId());
        ClientPlayNetworking.send(ModPackets.IDOG_ALERT_PACKET, buf);
    }
}
