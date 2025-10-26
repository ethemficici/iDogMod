package com.ethem00.idogmod.screen;

import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<iDogScreenHandler> IDOG_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(iDogMod.MOD_ID, "idog"),
                    new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> {

                        //Byte Buffer
                        //Non-reversed order
                        int entityID = buf.readInt();
                        Entity entity = inventory.player.getWorld().getEntityById(entityID);

                        if(entity instanceof iDogEntity idog) {

                            return new iDogScreenHandler(syncId, inventory, null, idog);
                        } else {
                            return null;
                        }
                    })
            );
    public static void registerScreenHandlers() {
        iDogMod.LOGGER.info("Registering Screen Handlers for " + iDogMod.MOD_ID);
    }
}
