package com.ninni.species.client.inventory;

import com.ninni.species.entity.Cruncher;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CruncherInventoryMenu extends AbstractContainerMenu {
    private final Container container;
    private final Cruncher cruncher;
    final Slot itemSlot;

    public CruncherInventoryMenu(int i, Inventory inventory, Container container, final Cruncher cruncher) {
        super(null, i);
        this.cruncher = cruncher;
        this.container = container;
        container.startOpen(inventory.player);
        int l;
        int m;

        this.itemSlot = this.addSlot(new Slot(container, 0, 36, 41));

        for (l = 0; l < 3; ++l) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(inventory, m + l * 9 + 9, 8 + m * 18, 102 + l * 18 - 18));
            }
        }
        for (l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l, 8 + l * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemStack = ItemStack.EMPTY;

        //TODO

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
