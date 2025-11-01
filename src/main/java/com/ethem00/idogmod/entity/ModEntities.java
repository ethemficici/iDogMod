package com.ethem00.idogmod.entity;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, iDogMod.MOD_ID);

    public static final RegistryObject<EntityType<iDogEntity>> IDOG =
            ENTITY_TYPES.register("idog", () -> EntityType.Builder.of(iDogEntity::new, MobCategory.CREATURE)
                    .sized(0.75f, 0.95f).build("idog"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
