package com.ethem00.idogmod.screen;

import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class iDogScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final iDogEntity idog;
    private float volume;
    private boolean loopSong;
    private boolean doAlerts;

    public iDogScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, iDogEntity idog) {
        super(ModScreenHandlers.IDOG_SCREEN_HANDLER, syncId);
        this.inventory = new SimpleInventory(0); //Dummy inventory
        this.idog = idog;
        this.volume = idog.getSongVolume(true);
        this.loopSong = idog.getLoopBool();
        this.doAlerts = idog.getAlertBool();

        //Inventory Setup

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    public iDogEntity getEntity(){
        return this.idog;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) { //TODO: ????
        ItemStack itemStack = ItemStack.EMPTY;
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}
