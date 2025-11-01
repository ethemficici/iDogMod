package com.ethem00.idogmod.entity.client.sound;

import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;


public class iDogMovingSoundInstance extends AbstractTickableSoundInstance {
    private iDogEntity iDog;

    public iDogMovingSoundInstance(iDogEntity iDogPassed, SoundEvent sound, float volumePassed) {
        super(sound, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.iDog = iDogPassed;
        this.looping = false;

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
        this.volume = this.iDog.getSongVolume(false);
        this.x = this.iDog.getX();
        this.y = this.iDog.getY();
        this.z = this.iDog.getZ();

        if(iDog.isRemoved()) {
            this.stop();
            System.out.println("iDog song playback has stopped.");
            System.out.println("Due to iDog entity removal.");
            System.out.println("Volume was: " + volume);
        }
        if(!iDog.isPlayingRecord()) {
            this.stop();
            System.out.println("iDog song playback has stopped.");
            System.out.println("Due to record removal.");
            System.out.println("Volume was: " + volume);
            //iDog.soundInstanceFinishedAlert(); //TODO: When teleporting
        }
    }
}
