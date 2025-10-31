package com.ethem00.idogmod.entity.client;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation IDOG =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "idog"), "main");
}

