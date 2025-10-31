package com.ethem00.idogmod.screen;

import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, iDogMod.MOD_ID);

    public static final RegistryObject<MenuType<iDogScreenHandler>> IDOG_MENU =
            MENUS.register("idog", () ->
                    IForgeMenuType.create((windowId, playerInv, buf) -> {
                        // Read extra data sent from client
                        int entityId = buf.readInt();
                        Entity entity = playerInv.player.level().getEntity(entityId);

                        if (entity instanceof iDogEntity idog) {
                            return new iDogScreenHandler(windowId, playerInv, null, idog);
                        }

                        // Fall back to empty container to avoid crash
                        return null;
                    })
            );
}