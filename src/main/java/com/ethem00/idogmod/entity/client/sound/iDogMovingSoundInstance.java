package com.ethem00.idogmod.entity.client.sound;

import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class iDogMovingSoundInstance extends MovingSoundInstance {
    private iDogEntity iDog;

    public iDogMovingSoundInstance(iDogEntity iDogPassed, SoundEvent sound, float volumePassed) {
        super(sound, SoundCategory.RECORDS, SoundInstance.createRandom());
        this.iDog = iDogPassed;
        this.repeat = false;

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
        this.volume = this.iDog.getSongVolume(false);
        this.x = this.iDog.getX();
        this.y = this.iDog.getY();
        this.z = this.iDog.getZ();

        if(iDog.isRemoved()) {
            this.setDone();
            System.out.println("iDog song playback has stopped.");
            System.out.println("Due to iDog entity removal.");
            System.out.println("Volume was: " + volume);
        }
        if(!iDog.isPlayingRecord()) {
            this.setDone();
            System.out.println("iDog song playback has stopped.");
            System.out.println("Due to record removal.");
            System.out.println("Volume was: " + volume);
            //iDog.soundInstanceFinishedAlert(); //TODO: When teleporting
        }
    }
}
