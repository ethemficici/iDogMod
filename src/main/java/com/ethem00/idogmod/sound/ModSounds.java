package com.ethem00.idogmod.sound;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    //DEATH
    public static final SoundEvent ENTITY_IDOG_DEATH = registerSoundEvent("entity_idog_death");
    //BARK
    public static final SoundEvent ENTITY_IDOG_AMBIENT = registerSoundEvent("entity_idog_ambient");
    //GROWL
    public static final SoundEvent ENTITY_IDOG_GROWL = registerSoundEvent("entity_idog_growl");
    //PANTING
    public static final SoundEvent ENTITY_IDOG_PANT = registerSoundEvent("entity_idog_pant");
    //HURT
    public static final SoundEvent ENTITY_IDOG_HURT = registerSoundEvent("entity_idog_hurt");
    //WHINE
    public static final SoundEvent ENTITY_IDOG_WHINE = registerSoundEvent("entity_idog_whine");
    //-----------------------------------------------------------------------------------------------------------
    //MISC ALERT
    public static final SoundEvent ENTITY_IDOG_ALERT_MISC = registerSoundEvent("entity_idog_alert_misc");
    //HAPPY ALERT
    public static final SoundEvent ENTITY_IDOG_ALERT_HAPPY = registerSoundEvent("entity_idog_alert_happy");
    //ZOMBIE ALERT
    public static final SoundEvent ENTITY_IDOG_ALERT_ZOMBIE = registerSoundEvent("entity_idog_alert_zombie");
    //SKELETON ALERT
    public static final SoundEvent ENTITY_IDOG_ALERT_SKELETON = registerSoundEvent("entity_idog_alert_skeleton");
    //SKELETON ALERT
    public static final SoundEvent ENTITY_IDOG_ALERT_SPIDER = registerSoundEvent("entity_idog_alert_spider");

    public static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(iDogMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        iDogMod.LOGGER.info("Registering sounds for " + iDogMod.MOD_ID);
    }
}
