package com.ninni.species.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AlphaceneMushroomGrowthBlock extends Block {
    private static final VoxelShape SHAPE = box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public AlphaceneMushroomGrowthBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBlockState(blockPos.above()).isFaceSturdy(levelReader, blockPos.above(), Direction.DOWN);
    }

}