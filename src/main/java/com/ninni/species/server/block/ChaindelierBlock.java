package com.ninni.species.server.block;

import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.block.entity.ChaindelierBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChaindelierBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape AABB = Shapes.or(Block.box(0, 7, 0, 16, 13, 16), Block.box(6.5, 13, 6.5, 9.5, 16, 9.5));

    public ChaindelierBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_153467_) {
        FluidState fluidstate = p_153467_.getLevel().getFluidState(p_153467_.getClickedPos());

        for(Direction direction : p_153467_.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState blockstate = this.defaultBlockState();
                if (blockstate.canSurvive(p_153467_.getLevel(), p_153467_.getClickedPos())) {
                    return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
                }
            }
        }

        return null;
    }

    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState p_153474_, BlockGetter p_153475_, BlockPos p_153476_, CollisionContext p_153477_) {
        return AABB;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153490_) {
        p_153490_.add(WATERLOGGED);
    }

    public boolean canSurvive(BlockState p_153479_, LevelReader p_153480_, BlockPos p_153481_) {
        Direction direction = Direction.UP;
        return Block.canSupportCenter(p_153480_, p_153481_.relative(direction), direction.getOpposite());
    }


    public BlockState updateShape(BlockState p_153483_, Direction p_153484_, BlockState p_153485_, LevelAccessor p_153486_, BlockPos p_153487_, BlockPos p_153488_) {
        if (p_153483_.getValue(WATERLOGGED)) {
            p_153486_.scheduleTick(p_153487_, Fluids.WATER, Fluids.WATER.getTickDelay(p_153486_));
        }

        return  Direction.UP == p_153484_ && !p_153483_.canSurvive(p_153486_, p_153487_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_153483_, p_153484_, p_153485_, p_153486_, p_153487_, p_153488_);
    }

    public FluidState getFluidState(BlockState p_153492_) {
        return p_153492_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_153492_);
    }

    public boolean isPathfindable(BlockState p_153469_, BlockGetter p_153470_, BlockPos p_153471_, PathComputationType p_153472_) {
        return false;
    }


    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        double soundX = (double)pos.getX() + randomSource.nextGaussian() * 8;
        double soundY = pos.getY() - randomSource.nextFloat() * 8;
        double soundZ = (double)pos.getZ() + randomSource.nextGaussian() * 8;

        if (randomSource.nextInt(20) == 0) {
            level.playLocalSound(soundX, soundY, soundZ, SpeciesSoundEvents.SPECTRE_IDLE.get(), SoundSource.BLOCKS, 0.25F, 0.5F, false);
        }
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new ChaindelierBlockEntity(p_153215_, p_153216_);
    }
}
