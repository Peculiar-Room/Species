package com.ninni.species.client.inventory;

import com.ninni.species.registry.SpeciesMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class BirtdayCakeMenu extends AbstractContainerMenu {
    public final BlockPos blockPos;

    public BirtdayCakeMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, buf.readBlockPos());
    }

    public BirtdayCakeMenu(int id, Inventory inv, BlockPos pos) {
        super(SpeciesMenus.BIRTDAY_CAKE.get(), id);
        this.blockPos = pos;
    }


    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
