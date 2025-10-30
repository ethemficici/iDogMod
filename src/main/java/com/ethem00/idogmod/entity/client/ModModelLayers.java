package com.ethem00.idogmod.entity.client;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final ModModelLayers IDOG =
            new ModModelLocation(ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "idog"), "main");
}

