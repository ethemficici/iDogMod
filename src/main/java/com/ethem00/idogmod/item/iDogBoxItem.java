package com.ethem00.idogmod.item;

import com.ethem00.idogmod.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;

public class iDogBoxItem extends SpawnEggItem {
    public iDogBoxItem(EntityType<? extends MobEntity> type, int primaryColor, int secondaryColor, Settings settings) {
        super(type, primaryColor, secondaryColor, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        context.getWorld().playSound(context.getPlayer(), context.getBlockPos(), ModSounds.ITEM_IDOG_BOX_OPEN, SoundCategory.PLAYERS, 1F, 1F);
        return super.useOnBlock(context);
    }

}
