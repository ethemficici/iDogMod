package com.ethem00.idogmod.util;

import com.ethem00.idogmod.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTableModifiers {

    private static final Identifier DUNGEON_ID =
            new Identifier("minecraft", "chests/simple_dungeon");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, identifier, builder, lootTableSource) -> {

            if(DUNGEON_ID.equals(identifier)) {

                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1f))
                        .with(ItemEntry.builder(ModItems.iDOG_BOX))
                        .with(ItemEntry.builder(ModItems.MUSIC_DISC_CALM4))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0F, 0.5F)).build());

                builder.pool(poolBuilder.build());
            }
        });
    }
}
