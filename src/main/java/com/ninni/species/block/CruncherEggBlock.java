package com.ninni.species.block;

import com.ninni.species.block.entity.CruncherEggBlockEntity;
import com.ninni.species.block.property.SpeciesProperties;
import com.ninni.species.registry.SpeciesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class CruncherEggBlock extends BaseEntityBlock {
    public static final BooleanProperty CRACKED = SpeciesProperties.CRUNCHER_EGG_CRACKED;
    public static final IntegerProperty TIMER = SpeciesProperties.CRUNCHER_EGG_TIMER;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    public CruncherEggBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER).setValue(CRACKED, false).setValue(TIMER, 60));
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        DoubleBlockHalf doubleBlockHalf = blockState.getValue(HALF);
        if (!(direction.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.LOWER != (direction == Direction.UP) || blockState2.is(this) && blockState2.getValue(HALF) != doubleBlockHalf)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? Shapes.block() : SHAPE;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        super.randomTick(blockState, serverLevel, blockPos, randomSource);
        if (blockState.getValue(CRACKED) && blockState.getValue(TIMER) == 0) {
            serverLevel.destroyBlock(blockPos, true, null);
        }
    }


    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        Level level = blockPlaceContext.getLevel();
        if (blockPos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockPos.above()).canBeReplaced(blockPlaceContext)) {
            return super.getStateForPlacement(blockPlaceContext);
        }
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        BlockPos blockPos2 = blockPos.above();
        if (level.getBlockEntity(blockPos) instanceof CruncherEggBlockEntity cruncherEggBlockEntity) {
            cruncherEggBlockEntity.setPlayer(livingEntity);
        }
        level.setBlock(blockPos2, CruncherEggBlock.copyWaterloggedFrom(level, blockPos2, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER)), 3);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        if (blockState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState blockState2 = levelReader.getBlockState(blockPos.below());
            return blockState2.is(this) && blockState2.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        return super.canSurvive(blockState, levelReader, blockPos);
    }

    public static BlockState copyWaterloggedFrom(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED)) {
            return blockState.setValue(BlockStateProperties.WATERLOGGED, levelReader.isWaterAt(blockPos));
        }
        return blockState;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                CruncherEggBlock.preventCreativeDropFromBottomPart(level, blockPos, blockState, player);
            } else {
                CruncherEggBlock.dropResources(blockState, level, blockPos, null, player, player.getMainHandItem());
            }
        }
        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        super.playerDestroy(level, player, blockPos, Blocks.AIR.defaultBlockState(), blockEntity, itemStack);
    }

    protected static void preventCreativeDropFromBottomPart(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        BlockPos blockPos2;
        BlockState blockState2;
        DoubleBlockHalf doubleBlockHalf = blockState.getValue(HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER && (blockState2 = level.getBlockState(blockPos2 = blockPos.below())).is(blockState.getBlock()) && blockState2.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockState blockState3 = blockState2.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
            level.setBlock(blockPos2, blockState3, 35);
            level.levelEvent(player, 2001, blockPos2, Block.getId(blockState2));
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return CruncherEggBlock.createTickerHelper(blockEntityType, SpeciesBlockEntities.CRUNCHER_EGG, level.isClientSide ? CruncherEggBlockEntity::clientTick : CruncherEggBlockEntity::serverTick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, CRACKED, TIMER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CruncherEggBlockEntity(blockPos, blockState);
    }
}
