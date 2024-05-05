package com.ninni.species.client.inventory;

import com.ninni.species.data.CruncherPelletManager;
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

        this.itemSlot = this.addSlot(new Slot(container, 0, 36, 41) {
            @Override
            public void setChanged() {
                Cruncher cruncher = CruncherInventoryMenu.this.cruncher;
                CruncherPelletManager.CruncherPelletData dataInput = null;

                if (this.getItem().isEmpty()) {
                    cruncher.setPelletData(null);
                    return;
                }

                if (cruncher.getPelletData() == null) {

                    for (ItemStack itemStack : CruncherPelletManager.DATA.keySet()) {

                        if (ItemStack.isSameItemSameTags(itemStack, this.getItem())) {
                            dataInput = CruncherPelletManager.DATA.get(itemStack);
                            break;
                        }

                    }

                    if (dataInput != null) cruncher.setPelletData(dataInput);

                }
            }
        });

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
        Slot slot = this.slots.get(i);
        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            int containerSize = this.container.getContainerSize();
            if (i < containerSize) {
                if (!this.moveItemStackTo(itemStack2, containerSize, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).mayPlace(itemStack2) && !this.getSlot(0).hasItem()) {
                if (!this.moveItemStackTo(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return !this.cruncher.hasInventoryChanged(this.container) && this.container.stillValid(player) && this.cruncher.isAlive() && this.cruncher.distanceTo(player) < 8.0f;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
