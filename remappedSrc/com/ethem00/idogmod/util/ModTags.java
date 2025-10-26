package com.ethem00.idogmod.util;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Items {

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(iDogMod.MOD_ID, name));
        }
    }
}
