package com.ethem00.idogmod.sound;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, iDogMod.MOD_ID);

    //CALM4
    public static final RegistryObject<SoundEvent> MUSIC_DISC_CALM4 = registerSoundEvents("music_disc.calm4");

    //BOX OPEN
    public static final RegistryObject<SoundEvent> ITEM_IDOG_BOX_OPEN = registerSoundEvents("item_idog_box_open");

    //iDog Sounds
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_DEATH = registerSoundEvents("entity_idog_death");
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_AMBIENT = registerSoundEvents("entity_idog_ambient");
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_GROWL = registerSoundEvents("entity_idog_growl");
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_PANT = registerSoundEvents("entity_idog_pant");
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_HURT = registerSoundEvents("entity_idog_hurt");
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_WHINE = registerSoundEvents("entity_idog_whine");
    //-----------------------------------------------------------------------------------------------------------
    //MISC ALERTS
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_ALERT_MISC = registerSoundEvents("entity_idog_alert_misc");
    //HAPPY ALERTS
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_ALERT_HAPPY = registerSoundEvents("entity_idog_alert_happy");
    //ZOMBIE ALERTS
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_ALERT_ZOMBIE = registerSoundEvents("entity_idog_alert_zombie");
    //SKELETON ALERTS
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_ALERT_SKELETON = registerSoundEvents("entity_idog_alert_skeleton");
    //SPIDER ALERTS
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_ALERT_SPIDER = registerSoundEvents("entity_idog_alert_spider");
    //CREEPER ALERTS
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_ALERT_CREEPER = registerSoundEvents("entity_idog_alert_creeper");
    //ENDERMAN ALERTS
    public static final RegistryObject<SoundEvent> ENTITY_IDOG_ALERT_ENDERMAN = registerSoundEvents("entity_idog_alert_enderman");


    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {SOUND_EVENTS.register(eventBus);}
}
