package com.ethem00.idogmod.entity.client;

import com.ethem00.idogmod.iDogMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer IDOG =
            new EntityModelLayer(new Identifier(iDogMod.MOD_ID, "idog"), "main");
}

