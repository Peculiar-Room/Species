package com.ninni.species.mixin;

import com.ninni.species.server.disguise.DisguiseTickContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Stops fire (and soul fire) blocks placed by a disguise body's leaked tick AI (e.g. the
 *  Ignis disguise's boss-phase transition) from being written to the real world. */
@Mixin(Level.class)
public abstract class LevelFireSuppressMixin {

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At("HEAD"), cancellable = true)
    private void species$suppressDisguiseFire(BlockPos pos, BlockState state, int flags, int recursionLeft,
                                              CallbackInfoReturnable<Boolean> cir) {
        if (DisguiseTickContext.isDisguiseTicking() && state.getBlock() instanceof BaseFireBlock) {
            cir.setReturnValue(false);
        }
    }
}
