package com.ninni.species.mixin_util;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface ServerPlayerAccess {
    int getContainerCounter();
    void getNextContainerCounter();
    void doInitMenu(AbstractContainerMenu menu);
}
