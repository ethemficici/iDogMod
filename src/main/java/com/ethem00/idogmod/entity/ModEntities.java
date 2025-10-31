package com.ethem00.idogmod.entity;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static final EntityType<iDogEntity> IDOG = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "idog"),
            EntityType.create(MobCategory.CREATURE, iDogEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.95f)).trackRangeBlocks(32).build());

    public static void registerModEntities() {
        iDogMod.LOGGER.info("Registering Entities for " + iDogMod.MOD_ID);
    }

}
