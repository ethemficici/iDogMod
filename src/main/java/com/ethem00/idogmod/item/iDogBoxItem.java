package com.ethem00.idogmod.item;

import com.ethem00.idogmod.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class iDogBoxItem extends ForgeSpawnEggItem {
    public iDogBoxItem(Supplier<? extends EntityType<? extends Mob>> type, int primaryColor, int secondaryColor, Properties settings) {
        super(type, primaryColor, secondaryColor, settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        context.getLevel().playSound(context.getPlayer(), context.getPlayer().blockPosition(), ModSounds.ITEM_IDOG_BOX_OPEN.get(), SoundSource.PLAYERS, 1F, 1F);
        return super.useOn(context);
    }

}
