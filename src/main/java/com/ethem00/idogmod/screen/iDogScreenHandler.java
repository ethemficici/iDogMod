package com.ethem00.idogmod.screen;

import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class iDogScreenHandler extends AbstractContainerMenu {
    private final SimpleContainer inventory;
    private final iDogEntity idog;
    private float volume;
    private boolean loopSong;
    private boolean doAlerts;

    public iDogScreenHandler(int syncId, Inventory playerInventory, Container inventory, iDogEntity idog) {
        super(ModMenuTypes.IDOG_MENU.get(), syncId);
        this.inventory = new SimpleContainer(0); //Dummy inventory
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
    public ItemStack quickMoveStack(Player player, int slot) { //TODO: ????
        ItemStack itemStack = ItemStack.EMPTY;
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.inventory.stopOpen(player);
    }
}
