package com.ninni.species.server.block;

import com.ninni.species.server.block.entity.CruncherPelletBlockEntity;
import com.ninni.species.registry.SpeciesParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CruncherPelletBlock extends BaseEntityBlock {

    public CruncherPelletBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CruncherPelletBlockEntity(blockPos, blockState);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        super.playerWillDestroy(level, blockPos, blockState, player);
        if (!player.getAbilities().instabuild && level.getBlockEntity(blockPos) instanceof CruncherPelletBlockEntity cruncherPelletBlock) {
            cruncherPelletBlock.unpackLootTable(player);
            level.destroyBlock(blockPos, false);
        }
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(5) == 0) {
            return;
        }
        Direction direction = Direction.getRandom(randomSource);
        if (direction == Direction.UP) {
            return;
        }
        BlockPos blockPos2 = blockPos.relative(direction);
        BlockState blockState2 = level.getBlockState(blockPos2);
        if (blockState.canOcclude() && blockState2.isFaceSturdy(level, blockPos2, direction.getOpposite())) {
            return;
        }
        double d = direction.getStepX() == 0 ? randomSource.nextDouble() : 0.5 + (double)direction.getStepX() * 0.6;
        double e = direction.getStepY() == 0 ? randomSource.nextDouble() : 0.5 + (double)direction.getStepY() * 0.6;
        double f = direction.getStepZ() == 0 ? randomSource.nextDouble() : 0.5 + (double)direction.getStepZ() * 0.6;
        level.addParticle(SpeciesParticles.DRIPPING_PELLET_DRIP.get(), (double)blockPos.getX() + d, (double)blockPos.getY() + e, (double)blockPos.getZ() + f, 0.0, 0.0, 0.0);
    }

}