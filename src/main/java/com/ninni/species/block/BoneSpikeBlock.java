package com.ninni.species.block;

import com.ninni.species.block.property.BoneSpikeThickness;
import com.ninni.species.block.property.SpeciesProperties;
import com.ninni.species.registry.SpeciesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class BoneSpikeBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
    public static final EnumProperty<BoneSpikeThickness> THICKNESS = SpeciesProperties.BONE_SPIKE_THICKNESS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape TIP_SHAPE_UP = Block.box(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
    private static final VoxelShape TIP_SHAPE_DOWN = Block.box(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
    private static final VoxelShape MIDDLE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    private static final VoxelShape BASE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public BoneSpikeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((((this.stateDefinition.any()).setValue(TIP_DIRECTION, Direction.UP)).setValue(THICKNESS, BoneSpikeThickness.TIP)).setValue(WATERLOGGED, false));
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return BoneSpikeBlock.isValidSpikePlacement(levelReader, blockPos, blockState.getValue(TIP_DIRECTION));
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        if (direction != Direction.UP && direction != Direction.DOWN) {
            return blockState;
        }
        Direction direction2 = blockState.getValue(TIP_DIRECTION);
        if (direction2 == Direction.DOWN && levelAccessor.getBlockTicks().hasScheduledTick(blockPos, this)) {
            return blockState;
        }
        if (direction == direction2.getOpposite() && !this.canSurvive(blockState, levelAccessor, blockPos)) {
            if (direction2 == Direction.DOWN) {
                levelAccessor.scheduleTick(blockPos, this, 2);
            } else {
                levelAccessor.scheduleTick(blockPos, this, 1);
            }
            return blockState;
        }
        BoneSpikeThickness BoneSpikeThickness = BoneSpikeBlock.calculateBoneSpikeThickness(levelAccessor, blockPos, direction2);
        return blockState.setValue(THICKNESS, BoneSpikeThickness);
    }

    @Override
    public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f) {
        //Do we need this??
        //if (blockState.getValue(TIP_DIRECTION) == Direction.UP && blockState.getValue(THICKNESS) == BoneSpikeThickness.TIP) {
        //    entity.causeFallDamage(f + 2.0f, 2.0f, level.damageSources().stalagmite());
        //} else {
        super.fallOn(level, blockState, blockPos, entity, f);
        //}
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!this.canSurvive(blockState, serverLevel, blockPos)) {
            serverLevel.destroyBlock(blockPos, true);
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockPos blockPos;
        Level levelAccessor = blockPlaceContext.getLevel();
        Direction direction2 = BoneSpikeBlock.calculateTipDirection(levelAccessor, blockPos = blockPlaceContext.getClickedPos(), blockPlaceContext.getNearestLookingVerticalDirection().getOpposite());
        if (direction2 == null) {
            return null;
        }
        BoneSpikeThickness BoneSpikeThickness = BoneSpikeBlock.calculateBoneSpikeThickness(levelAccessor, blockPos, direction2);
        if (BoneSpikeThickness == null) {
            return null;
        }
        return ((this.defaultBlockState().setValue(TIP_DIRECTION, direction2)).setValue(THICKNESS, BoneSpikeThickness)).setValue(WATERLOGGED, levelAccessor.getFluidState(blockPos).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(blockState);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        BoneSpikeThickness thickness = blockState.getValue(THICKNESS);
        VoxelShape voxelShape = (thickness == BoneSpikeThickness.TIP ? (blockState.getValue(TIP_DIRECTION) == Direction.DOWN ? TIP_SHAPE_DOWN : TIP_SHAPE_UP) : (thickness == BoneSpikeThickness.MIDDLE ? MIDDLE_SHAPE : BASE_SHAPE));
        Vec3 vec3 = blockState.getOffset(blockGetter, blockPos);
        return voxelShape.move(vec3.x, 0.0, vec3.z);
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }


    @Nullable
    private static Direction calculateTipDirection(LevelReader levelReader, BlockPos blockPos, Direction direction) {
        Direction direction2;
        if (BoneSpikeBlock.isValidSpikePlacement(levelReader, blockPos, direction)) {
            direction2 = direction;
        } else if (BoneSpikeBlock.isValidSpikePlacement(levelReader, blockPos, direction.getOpposite())) {
            direction2 = direction.getOpposite();
        } else {
            return null;
        }
        return direction2;
    }

    private static BoneSpikeThickness calculateBoneSpikeThickness(LevelReader levelReader, BlockPos blockPos, Direction direction) {
        Direction direction2 = direction.getOpposite();
        BlockState blockState = levelReader.getBlockState(blockPos.relative(direction));
        if (BoneSpikeBlock.isBoneSpikeWithDirection(blockState, direction2)) {
            return BoneSpikeThickness.TIP;
        }
        if (!BoneSpikeBlock.isBoneSpikeWithDirection(blockState, direction)) {
            return BoneSpikeThickness.TIP;
        }
        BlockState blockState2 = levelReader.getBlockState(blockPos.relative(direction2));
        if (!BoneSpikeBlock.isBoneSpikeWithDirection(blockState2, direction)) {
            return BoneSpikeThickness.BASE;
        }
        return BoneSpikeThickness.MIDDLE;
    }

    private static boolean isValidSpikePlacement(LevelReader levelReader, BlockPos blockPos, Direction direction) {
        BlockPos blockPos2 = blockPos.relative(direction.getOpposite());
        BlockState blockState = levelReader.getBlockState(blockPos2);
        return blockState.isFaceSturdy(levelReader, blockPos2, direction) || BoneSpikeBlock.isBoneSpikeWithDirection(blockState, direction);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }

    private static boolean isBoneSpikeWithDirection(BlockState blockState, Direction direction) {
        return blockState.is(SpeciesBlocks.BONE_SPIKE.get()) && blockState.getValue(TIP_DIRECTION) == direction;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIP_DIRECTION, THICKNESS, WATERLOGGED);
    }
}