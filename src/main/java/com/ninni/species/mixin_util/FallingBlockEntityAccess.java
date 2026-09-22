package com.ninni.species.mixin_util;

import net.minecraft.world.level.block.state.BlockState;

public interface FallingBlockEntityAccess {
    BlockState blockState();
    void setBlockState(BlockState state);
}
