package com.ethem00.idogmod.screen;

import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<iDogScreenHandler> IDOG_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(iDogMod.MOD_ID, "idog"),
                    new ScreenHandlerType<>(
                            (syncId, inventory) -> new iDogScreenHandler(syncId, inventory, null, null),
                            FeatureFlags.VANILLA_FEATURES
                    )
            );
    public static void registerScreenHandlers() {
        iDogMod.LOGGER.info("Registering Screen Handlers for " + iDogMod.MOD_ID);
    }
}
