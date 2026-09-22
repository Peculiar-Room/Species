package com.ninni.species.mixin;

import com.ninni.species.mixin_util.AbstractArrowAccess;
import com.ninni.species.mixin_util.FallingBlockEntityAccess;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin implements FallingBlockEntityAccess {

    @Shadow
    private BlockState blockState;

    @Override
    public BlockState blockState() {
        return blockState;
    }

    @Override
    public void setBlockState(BlockState state) {
        this.blockState = state;
    }
}
