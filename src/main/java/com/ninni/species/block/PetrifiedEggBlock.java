package com.ninni.species.block;

import com.ninni.species.block.property.SpeciesProperties;
import com.ninni.species.entity.Goober;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class PetrifiedEggBlock extends Block {
    public static final int MAX_HATCH_LEVEL = 2;
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
    public static final BooleanProperty HEATED = SpeciesProperties.HEATED;
    private static final int REGULAR_HATCH_TIME_TICKS = 24000;
    private static final int BOOSTED_HATCH_TIME_TICKS = 12000;
    private static final int RANDOM_HATCH_OFFSET_TICKS = 300;
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 14, 15);

    public PetrifiedEggBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(HATCH, 0).setValue(HEATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HATCH, HEATED);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    public int getHatchLevel(BlockState blockState) {
        return blockState.getValue(HATCH);
    }

    private boolean isHeated(BlockState blockState) {
        return blockState.getValue(HEATED);
    }
    private boolean isReadyToHatch(BlockState blockState) {
        return this.getHatchLevel(blockState) == MAX_HATCH_LEVEL;
    }

    @Override
    public void tick(BlockState state, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (this.isHeated(state)) {
            if (!this.isReadyToHatch(state)) {
                serverLevel.playSound(null, blockPos, SpeciesSoundEvents.PETRIFIED_EGG_CRACK.get(), SoundSource.BLOCKS, 0.7f, 0.9f + randomSource.nextFloat() * 0.2f);
                serverLevel.setBlock(blockPos, state.setValue(HATCH, this.getHatchLevel(state) + 1), 2);
                return;
            }
            serverLevel.playSound(null, blockPos, SpeciesSoundEvents.PETRIFIED_EGG_HATCH.get(), SoundSource.BLOCKS, 0.7f, 0.9f + randomSource.nextFloat() * 0.2f);
            serverLevel.destroyBlock(blockPos, false);
            Goober goober = SpeciesEntities.GOOBER.get().create(serverLevel);
            if (goober != null) {
                Vec3 vec3 = blockPos.getCenter();
                goober.setBaby(true);
                goober.moveTo(vec3.x(), vec3.y(), vec3.z(), Mth.wrapDegrees(serverLevel.random.nextFloat() * 360.0f), 0.0f);
                serverLevel.addFreshEntity(goober);
            }
        }
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        boolean bl2 = PetrifiedEggBlock.hatchBoost(level, blockPos);
        if (level.getBlockState(blockPos.below()).is(SpeciesTags.PETRIFIED_EGG_HATCH)) level.setBlock(blockPos, blockState.setValue(HEATED, true), 2);
        if (!level.isClientSide() && bl2) level.levelEvent(3009, blockPos, 0);
        int i = bl2 ? BOOSTED_HATCH_TIME_TICKS : REGULAR_HATCH_TIME_TICKS;
        int j = i / 3;
        level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(blockState));
        level.scheduleTick(blockPos, this, j + level.random.nextInt(RANDOM_HATCH_OFFSET_TICKS));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return (((this.defaultBlockState()).setValue(HEATED, isBelowHeated(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos()))));
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN) {
            return blockState.setValue(HEATED, isBelowHeated(levelAccessor, blockPos));
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }

    public static boolean isBelowHeated(BlockGetter blockGetter, BlockPos blockPos) {
        return blockGetter.getBlockState(blockPos.below()).is(SpeciesTags.PETRIFIED_EGG_HATCH);
    }
    public static boolean hatchBoost(BlockGetter blockGetter, BlockPos blockPos) {
        return blockGetter.getBlockState(blockPos.below()).is(SpeciesTags.PETRIFIED_EGG_HATCH_BOOST);
    }
}
