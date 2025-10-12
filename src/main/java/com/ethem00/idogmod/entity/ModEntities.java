package com.ethem00.idogmod.entity;

import com.ethem00.idogmod.iDogMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<iDogEntity> IDOG = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(iDogMod.MOD_ID, "idog"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, iDogEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.95f)).trackRangeBlocks(10).build());

    public static void registerModEntities() {
        iDogMod.LOGGER.info("Registering Entities for " + iDogMod.MOD_ID);
    }

}
