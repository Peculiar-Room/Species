package com.ninni.species.mixin;

import com.google.common.collect.Lists;
import com.ninni.species.entity.Mammutilation;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {

    @Inject(at = @At("TAIL"), method = "onPlace")
    private void s$onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl, CallbackInfo ci) {
        List<BlockPos> pos = Lists.newArrayList();
        Direction value = blockState.getValue(CarvedPumpkinBlock.FACING);
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos initialPos = blockPos.relative(value.getOpposite());
                    BlockPos position = BlockPos.containing(initialPos.getX() + x, initialPos.getY() + y, initialPos.getZ() + z);
                    if (!level.getBlockState(position).is(Blocks.CARVED_PUMPKIN)) {
                        if (level.getBlockState(position).is(SpeciesTags.MAMMUTILATION_BODY_BLOCKS)) {
                            pos.add(position);
                        }
                    }
                }
            }
        }
        BlockPos rodPos = blockPos.relative(value.getOpposite()).relative(value.getOpposite().getClockWise(), 2);
        BlockPos rodPos1 = blockPos.relative(value.getOpposite()).relative(value.getOpposite().getCounterClockWise(), 2);
        BlockState rodState = level.getBlockState(rodPos);
        BlockState rodState1 = level.getBlockState(rodPos1);
        if (rodState.is(Blocks.LIGHTNING_ROD) && rodState.getValue(BlockStateProperties.FACING) == value.getOpposite().getClockWise()) {
            pos.add(rodPos);
        }
        if (rodState1.is(Blocks.LIGHTNING_ROD) && rodState1.getValue(BlockStateProperties.FACING) == value.getOpposite().getCounterClockWise()) {
            pos.add(rodPos1);
        }
        Mammutilation mammutilation = null;
        if (pos.size() == 28) {
            pos.add(blockPos);
            pos.forEach(blockPos1 -> {
                level.setBlock(blockPos1, Blocks.AIR.defaultBlockState(), 2);
                level.levelEvent(2001, blockPos1, Block.getId(level.getBlockState(blockPos1)));
                level.blockUpdated(blockPos1, Blocks.AIR);
            });
            mammutilation = SpeciesEntities.MAMMUTILATION.create(level);
            // 90.0F - South
            // 180.0F -
            float x = 0.0F;
            float y = 0.0F;
            mammutilation.moveTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.05, (double)blockPos.getZ() + 0.5, Mth.wrapDegrees(x), Mth.wrapDegrees(y));
            level.addFreshEntity(mammutilation);
        }
        if (mammutilation != null) {
            for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, mammutilation.getBoundingBox().inflate(5.0))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, mammutilation);
            }
        }
    }

}
