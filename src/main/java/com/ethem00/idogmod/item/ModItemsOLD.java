package com.ethem00.idogmod.item;

public class ModItemsOLD {

    /*

    public static final Item MUSIC_DISC_CALM4 = registerItemMethod("music_disc_calm4", new RecordItem(4, ModSounds.MUSIC_DISC_CALM4, new Item.Properties().stacksTo(1), 190));
    public static final Item iDOG_BOX = registerItemMethod("idog_box", new iDogBoxItem(ModEntitiesOLD.IDOG, 0, 0, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    private static Item registerItemMethod(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(iDogMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        iDogMod.LOGGER.info("Registering modded items from " + iDogMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.MUSIC_DISC_RELIC, MUSIC_DISC_CALM4);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.addAfter(Items.SPAWNER, iDOG_BOX);
        });
    }

     */
}
