package com.ninni.species.server.block;

import com.ninni.species.server.block.entity.HopelightBlockEntity;
import com.ninni.species.server.block.entity.SpectreLightBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class HopelightBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty CONNECTED_Y = BooleanProperty.create("connected_y");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    protected static final VoxelShape SHAPE_HANGING = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 3, 16);
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_52346_) -> p_52346_.getKey().getAxis().isHorizontal()).collect(Util.toMap());

    public HopelightBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, false).setValue(WATERLOGGED, false).setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(CONNECTED_Y, Boolean.FALSE));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockPos belowPos = pos.below();
        BlockPos abovePos = pos.above();
        return super.getStateForPlacement(context).setValue(HANGING, context.getClickedFace() != Direction.UP)
                .setValue(CONNECTED_Y, (this.connectsTo(level, pos, belowPos) && level.getBlockState(belowPos).getValue(HANGING) && !level.getBlockState(pos).getValue(HANGING)) || (this.connectsTo(level, pos, abovePos) && !level.getBlockState(abovePos).getValue(HANGING) && level.getBlockState(pos).getValue(HANGING)))
                .setValue(NORTH, this.connectsTo(level, pos, pos.north()))
                .setValue(EAST, this.connectsTo(level, pos, pos.east()))
                .setValue(SOUTH, this.connectsTo(level, pos, pos.south()))
                .setValue(WEST, this.connectsTo(level, pos, pos.west()))
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    public BlockState updateShape(BlockState state, Direction axis, BlockState state1, LevelAccessor level, BlockPos pos, BlockPos pos1) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        BlockPos belowPos = pos.below();
        BlockPos abovePos = pos.above();

        if (axis.getAxis().isHorizontal()) {
            BooleanProperty connectionProperty = PROPERTY_BY_DIRECTION.get(axis);
            return state.setValue(connectionProperty, this.connectsTo(level, pos, pos1));
        }
        return state.setValue(CONNECTED_Y, (this.connectsTo(level, pos, belowPos) && level.getBlockState(belowPos).getValue(HANGING) && !level.getBlockState(pos).getValue(HANGING)) || (this.connectsTo(level, pos, abovePos) && !level.getBlockState(abovePos).getValue(HANGING) && level.getBlockState(pos).getValue(HANGING)));

    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof SpectreLightBlockEntity blockEntity) {
            stack.getOrCreateTagElement("BlockEntityTag").putInt("color", blockEntity.getColor());
        }
        return stack;
    }

    private boolean connectsTo(BlockGetter level, BlockPos pos, BlockPos pos2) {
        return level.getBlockState(pos).getBlock() instanceof HopelightBlock && level.getBlockState(pos2).getBlock() instanceof HopelightBlock;
    }

    public boolean propagatesSkylightDown(BlockState p_52348_, BlockGetter p_52349_, BlockPos p_52350_) {
        return !p_52348_.getValue(WATERLOGGED);
    }

    public VoxelShape getShape(BlockState p_52352_, BlockGetter p_52353_, BlockPos p_52354_, CollisionContext p_52355_) {
        return p_52352_.getValue(HANGING) ? SHAPE_HANGING : SHAPE;
    }

    public FluidState getFluidState(BlockState p_52362_) {
        return p_52362_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_52362_);
    }

    public boolean isPathfindable(BlockState p_52333_, BlockGetter p_52334_, BlockPos p_52335_, PathComputationType p_52336_) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HopelightBlockEntity(pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING, EAST, WEST, NORTH, SOUTH, CONNECTED_Y, WATERLOGGED);
    }
}

