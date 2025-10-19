package com.ethem00.idogmod.entity.client.sound;

import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class iDogMovingSoundInstance extends MovingSoundInstance {
    private iDogEntity iDog;

    public iDogMovingSoundInstance(iDogEntity iDogPassed, SoundEvent sound, boolean loopBoolPassed, float volumePassed) {
        super(sound, SoundCategory.RECORDS, SoundInstance.createRandom());
        this.iDog = iDogPassed;
        this.repeat = loopBoolPassed;
        this.volume = volumePassed;
        this.x = iDog.getX();
        this.y = iDog.getY();
        this.z = iDog.getZ();

        System.out.println("iDog is now playing: " + sound.getId().toString());
        System.out.println("With volume of: " + volume);
        if(this.repeat) { System.out.println("With looping"); } else { System.out.println("Without looping"); }
    }

    @Override
    public void tick() {
        this.repeat = iDog.getLoopBool();
        this.volume = iDog.getSongVolume();
        this.x = iDog.getX();
        this.y = iDog.getY();
        this.z = iDog.getZ();

        //TODO: INSTANT STOP IS HAPPENING AGAIN! FIGURE OUT WHY! Not clearing songEnd data?

        //Todo: Ping original entity when SoundInstance is completed..? Could help with eye animation desync/pause desync...
        // Currently if the iDog teleports, this audio is finished due to unload and the player has to manually reset the song.
        // The iDog entity itself never realizes the sound instance was cancelled.
        if(iDog.isRemoved()) {
            this.setDone();
            System.out.println("iDog song playback has stopped.");
            System.out.println("Due to iDog entity removal.");
            System.out.println("Volume was: " + volume);
            if(this.repeat) { System.out.println("And it was looping"); } else { System.out.println("And it wasn't looping"); }
            iDog.soundInstanceFinishedAlert(); //When teleporting
        }
        if(!iDog.isPlayingRecord()) {
            this.setDone();
            System.out.println("iDog song playback has stopped.");
            System.out.println("Due to record removal.");
            System.out.println("Volume was: " + volume);
            if(this.repeat) { System.out.println("And it was looping"); } else { System.out.println("And it wasn't looping"); }
        }
        if(this.volume <= 0) {
            this.setDone();
            System.out.println("iDog song playback has stopped.");
            System.out.println("Due to muted volume.");
            System.out.println("Volume was: " + volume);
            if(this.repeat) { System.out.println("And it was looping"); } else { System.out.println("And it wasn't looping"); }
            // DON'T FORGET TO USE >> this. << FOR INSTANCES! ALWAYS TRIPLE CHECK! TRIPLE BAKA!
            // THAT'S WHY IT WASN'T WORKING BEFORE!
        }
    }
}
