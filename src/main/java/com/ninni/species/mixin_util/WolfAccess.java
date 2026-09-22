package com.ninni.species.mixin_util;

import net.minecraft.world.item.DyeColor;

public interface WolfAccess {
    boolean getIsCuredBewereager();
    void setIsCuredBewereager(boolean isCuredBewereager);
    boolean getIsBewereager();
    void setIsBewereager(boolean isBewereager);

    void setNewCollarColor(DyeColor color);
}
