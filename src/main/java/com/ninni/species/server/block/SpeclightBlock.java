package com.ninni.species.server.block;

import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.block.entity.SpeclightBlockEntity;
import com.ninni.species.server.block.entity.SpectreLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SpeclightBlock extends FaceAttachedHorizontalDirectionalBaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape CEILING_AABB = Block.box(5, 13, 5, 11, 16, 11);
    protected static final VoxelShape FLOOR_AABB = Block.box(5, 0, 5, 11, 3, 11);
    protected static final VoxelShape NORTH_AABB = Block.box(5, 5, 13, 11, 11, 16);
    protected static final VoxelShape SOUTH_AABB = Block.box(5, 5, 0, 11, 11, 3);
    protected static final VoxelShape EAST_AABB = Block.box(0, 5, 5, 3, 11, 11);
    protected static final VoxelShape WEST_AABB = Block.box(13, 5, 5, 16, 11, 11);

    public SpeclightBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(FACE, AttachFace.WALL));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_53184_) {
        FluidState fluidstate = p_53184_.getLevel().getFluidState(p_53184_.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        if (p_53184_.getPlayer().isShiftKeyDown()) p_53184_.getLevel().playSound(null, p_53184_.getClickedPos(), SpeciesSoundEvents.SPECLIGHT_ON.get(), SoundSource.BLOCKS);
        return super.getStateForPlacement(p_53184_).setValue(WATERLOGGED, Boolean.valueOf(flag)).setValue(POWERED, p_53184_.getPlayer().isShiftKeyDown());
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState state, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(blockState, direction, state, levelAccessor, blockPos, blockPos1);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean currentlyPowered = state.getValue(POWERED);
        boolean isNowPowered = level.hasNeighborSignal(pos);

        if (isNowPowered) {
            level.playSound(null, pos, currentlyPowered ? SpeciesSoundEvents.SPECLIGHT_OFF.get() : SpeciesSoundEvents.SPECLIGHT_ON.get(), SoundSource.BLOCKS);
            level.setBlock(pos, state.setValue(POWERED, !currentlyPowered), 2);
        }
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.getValue(WATERLOGGED)) return Fluids.WATER.getSource(false);
        return super.getFluidState(blockState);
    }

    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return switch (state.getValue(FACE)) {
            case FLOOR -> FLOOR_AABB;
            case WALL -> switch (direction) {
                case EAST -> EAST_AABB;
                case WEST -> WEST_AABB;
                case SOUTH -> SOUTH_AABB;
                case NORTH, UP, DOWN -> NORTH_AABB;
            };
            default -> CEILING_AABB;
        };
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof SpectreLightBlockEntity blockEntity) {
            stack.getOrCreateTagElement("BlockEntityTag").putInt("color", blockEntity.getColor());
        }
        return stack;
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (player.getItemInHand(hand).isEmpty()) {
            level.setBlock(pos, state.setValue(POWERED, !level.getBlockState(pos).getValue(POWERED)), 3);
            if (!state.getValue(POWERED)) level.playSound(null, pos, SpeciesSoundEvents.SPECLIGHT_ON.get(), SoundSource.BLOCKS);
            else level.playSound(null, pos, SpeciesSoundEvents.SPECLIGHT_OFF.get(), SoundSource.BLOCKS);
            level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, blockHitResult);
    }

    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, FACE, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpeclightBlockEntity(pos, state);
    }
}
