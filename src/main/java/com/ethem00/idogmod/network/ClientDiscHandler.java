package com.ethem00.idogmod.network;

import com.ethem00.idogmod.entity.client.sound.iDogMovingSoundInstance;
import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientDiscHandler {

    public static void handleClient(Supplier<NetworkEvent.Context> context, int entityID, ResourceLocation resource, String currentDisc) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        Entity entity = level.getEntity(entityID);

        if (entity instanceof iDogEntity dog) {

            Item item = level.registryAccess()
                    .registryOrThrow(Registries.ITEM)
                    .get(resource);

            if(item instanceof RecordItem) {

                SoundEvent sound = ((RecordItem) item).getSound();
                ItemStack stack = new ItemStack(item, 1);

                dog.forceSync(stack, currentDisc);

                minecraft.getSoundManager().play(
                        new iDogMovingSoundInstance(dog, sound, dog.getSongVolume(false)));
            }
        }
    }
}
