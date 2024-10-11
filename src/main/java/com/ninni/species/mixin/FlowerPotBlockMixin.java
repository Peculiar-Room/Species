package com.ninni.species.mixin;

import com.ninni.species.block.PottedTrooperBlock;
import com.ninni.species.registry.SpeciesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowerPotBlock.class)
public abstract class FlowerPotBlockMixin {
    @Shadow
    protected abstract boolean isEmpty();

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void applyWitherResistance(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (this.isEmpty() && stack.is(SpeciesBlocks.TROOPER.get().asItem())) {
            cir.cancel();
            Direction direction = blockHitResult.getDirection().getAxis() == Direction.Axis.Y ? Direction.NORTH : blockHitResult.getDirection();
            BlockState state = SpeciesBlocks.POTTED_TROOPER.get().defaultBlockState().setValue(PottedTrooperBlock.FACING, direction);
            level.setBlock(blockPos, state, 3);
            player.awardStat(Stats.POT_FLOWER);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
        }
    }
}
