package com.ethem00.idogmod.item;

import com.ethem00.idogmod.entity.ModEntities;
import com.ethem00.idogmod.iDogMod;
import com.ethem00.idogmod.sound.ModSounds;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraft.world.item.Rarity.RARE;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, iDogMod.MOD_ID);


    public static final RegistryObject<Item> IDOG_BOX = ITEMS.register("idog_box",
            () -> new iDogBoxItem(ModEntities.IDOG, 0, 0, new Item.Properties().stacksTo(1).rarity(RARE)));

    public static final RegistryObject<Item> MUSIC_DISC_CALM4 = ITEMS.register("music_disc_calm4",
            () -> new RecordItem(4, ModSounds.MUSIC_DISC_CALM4, new Item.Properties().stacksTo(1), 3800));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
