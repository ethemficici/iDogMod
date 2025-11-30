package com.ethem00.idogmod.network;

import com.ethem00.idogmod.entity.client.sound.iDogMovingSoundInstance;
import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class iDogPlayDiscPacketS2C {

    private final int entityID;
    private final ResourceLocation resource;
    private final String currentDisc;

    public iDogPlayDiscPacketS2C(int pID, ResourceLocation pResource, String pCurrentDisc) {
        this.entityID = pID;
        this.resource = pResource;
        this.currentDisc = pCurrentDisc;
    }

    public iDogPlayDiscPacketS2C(FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.resource = buf.readResourceLocation();
        this.currentDisc = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeResourceLocation(resource);
        buf.writeUtf(currentDisc);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context cntx = context.get();

        if (context.get().getDirection().getReceptionSide().isClient()) {
            cntx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> ClientDiscHandler.handleClient(context, entityID, resource, currentDisc)));
        }

        cntx.setPacketHandled(true);
    }
}
