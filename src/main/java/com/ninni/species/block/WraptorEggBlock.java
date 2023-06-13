package com.ninni.species.block;

import com.ninni.species.criterion.SpeciesCriterion;
import com.ninni.species.entity.SpeciesEntities;
import com.ninni.species.entity.WraptorEntity;
import com.ninni.species.sound.SpeciesSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class WraptorEggBlock extends Block implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 12, 12);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;

    public WraptorEggBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(HATCH, 0));
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if(entity instanceof FallingBlockEntity fallingBlockEntity && fallingBlockEntity.getBlockState().getBlock() instanceof AnvilBlock && fallDistance > 4) {
            this.breakEgg(world, pos, state);
        }
        super.fallOn(world, state, pos, entity, fallDistance);
    }

    private void breakEgg(Level world, BlockPos pos, BlockState state) {
        int i = state.getValue(HATCH);
        if (i < 2) {
            world.playSound(null, pos, SpeciesSoundEvents.BLOCK_WRAPTOR_EGG_CRACK, SoundSource.BLOCKS, 1.5f, 1.5F + world.random.nextFloat() * 0.2f);
            world.setBlock(pos, this.defaultBlockState().setValue(HATCH, i + 1), 2);
            world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        } else {
            world.playSound(null, pos, SpeciesSoundEvents.BLOCK_WRAPTOR_EGG_HATCH, SoundSource.BLOCKS, 1.5f, 1.5F + world.random.nextFloat() * 0.2f);
            world.removeBlock(pos, false);
            world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
            WraptorEntity wraptor = SpeciesEntities.WRAPTOR.create(world);
            assert wraptor != null;
            wraptor.setAge(-24000);
            wraptor.setBormFromEgg(true);
            wraptor.setPersistenceRequired();
            wraptor.moveTo(pos.getX() + 0.3, pos.getY(), pos.getZ() + 0.3, 0.0f, 0.0f);
            world.addFreshEntity(wraptor);
            if (wraptor.level().getNearestPlayer(wraptor, 16) instanceof ServerPlayer serverPlayer) SpeciesCriterion.HATCH_WRAPTOR.trigger(serverPlayer);
        }
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return !levelReader.isEmptyBlock(blockPos.below());
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.getValue(WATERLOGGED)) levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(WATERLOGGED, blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HATCH, WATERLOGGED);
    }
}

