package com.ninni.species.mixin;

import com.google.common.collect.Lists;
import com.ninni.species.entity.Mammutilation;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
        BlockState rodState = level.getBlockState(blockPos.relative(value.getOpposite()).relative(value.getOpposite().getClockWise(), 2));
        BlockState rodState1 = level.getBlockState(blockPos.relative(value.getOpposite()).relative(value.getOpposite().getCounterClockWise(), 2));
        if (rodState.is(Blocks.LIGHTNING_ROD) && rodState.getValue(BlockStateProperties.FACING) == value.getOpposite().getClockWise()) {
            pos.add(blockPos.relative(value.getOpposite().getClockWise()));
        }
        if (rodState1.is(Blocks.LIGHTNING_ROD) && rodState1.getValue(BlockStateProperties.FACING) == value.getOpposite().getCounterClockWise()) {
            pos.add(blockPos.relative(value.getOpposite().getCounterClockWise()));
        }
        if (pos.size() == 28) {
            pos.add(blockPos);
            pos.forEach(blockPos1 -> {
                level.setBlock(blockPos1, Blocks.AIR.defaultBlockState(), 2);
                level.levelEvent(2001, blockPos1, Block.getId(level.getBlockState(blockPos1)));
                level.blockUpdated(blockPos1, Blocks.AIR);
            });
            Mammutilation mammutilation = SpeciesEntities.MAMMUTILATION.create(level);
            mammutilation.moveTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.05, (double)blockPos.getZ() + 0.5, 0.0f, 0.0f);
            level.addFreshEntity(mammutilation);
        }
    }

}
