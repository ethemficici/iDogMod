package com.ethem00.idogmod.entity.client.sound;

import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.network.ModPackets;
import com.ethem00.idogmod.network.iDogAlertFinishedPacketC2S;
import io.netty.buffer.Unpooled;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class iDogMovingAlertInstance extends AbstractTickableSoundInstance {
    private iDogEntity iDog;
    private float storedVolume;
    private float secondsInTicks;
    private int ticks;
    private boolean doOnce;

    public iDogMovingAlertInstance(iDogEntity iDogPassed, SoundEvent sound, float volumePassed, float seconds) {
        super(sound, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.iDog = iDogPassed;
        this.looping = false;
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
        //System.out.println("iDog is now playing: " + sound.getId().toString());
        //System.out.println("With volume of: " + volume);
    }

    @Override
    public void tick() {
        this.volume = this.iDog.getSongVolume(true);
        this.x = this.iDog.getX();
        this.y = this.iDog.getY();
        this.z = this.iDog.getZ();

        if(iDog.isRemoved()) {
            this.isStopped();
            System.out.println("iDog alert playback has stopped.");
            System.out.println("Due to iDog entity removal.");
            this.packetSender();
            this.stop();
        }
        if(!iDog.getAlertBool()) {
            System.out.println("iDog alert playback has stopped.");
            System.out.println("Due to iDog alerts disabled.");
            this.packetSender();
            this.stop();
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

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(this.iDog.getId());

        System.out.println("Packet of -100 being sent by entity " + this.iDog.getId());
        ModPackets.CHANNEL.sendToServer(new iDogAlertFinishedPacketC2S(buf));
    }
}
