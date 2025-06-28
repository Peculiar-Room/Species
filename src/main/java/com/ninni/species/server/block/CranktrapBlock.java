package com.ninni.species.server.block;

import com.ninni.species.registry.SpeciesDamageTypes;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesStatusEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class CranktrapBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 5, 15);
    protected static final VoxelShape SHAPE_NS_CLOSED = Block.box(1, 0, 5, 15, 6, 11);
    protected static final VoxelShape SHAPE_WE_CLOSED = Block.box(5, 0, 1, 11, 6, 15);
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CranktrapBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(OPEN, true));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN)) return SHAPE;
        else {
            return switch (state.getValue(FACING)) {
                case NORTH, SOUTH, DOWN, UP -> SHAPE_NS_CLOSED;
                case WEST, EAST -> SHAPE_WE_CLOSED;
            };
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if ((entity instanceof Mob || entity instanceof Player) && !state.getValue(OPEN)) {
            ((LivingEntity)entity).addEffect(new MobEffectInstance(SpeciesStatusEffects.STUCK.get(), 2, 0, false, false, false));
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide()) {
            boolean isPowered = level.hasNeighborSignal(pos);
            boolean wasPowered = state.getValue(POWERED);

            if (isPowered && !wasPowered) {
                level.setBlock(pos, state.setValue(POWERED, true), 3);
                level.scheduleTick(pos, this, 10);
            } else if (!isPowered && wasPowered) {
                level.setBlock(pos, state.setValue(POWERED, false), 3);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).deflate(0, 0.5, 0));
        boolean hasMob = !list.isEmpty();

        if (hasMob) {
            if (state.getValue(OPEN)) {
                close(level, pos, state);
            } else {
                open(level, pos, state);

                for (LivingEntity entity : list) {
                    entity.addEffect(new MobEffectInstance(SpeciesStatusEffects.STUCK.get(), 8, 0, false, false, false));
                }
                level.scheduleTick(pos, this, 4);
                return;
            }

            for (LivingEntity entity : list) {
                float amount = 2 + ((entity.getMaxHealth() / 8) * (1 - (entity.getMaxHealth() - entity.getHealth()) / entity.getMaxHealth()));
                for (int i = 0; i < amount; i++) {
                    level.sendParticles(
                            SpeciesParticles.BEWEREAGER_SLOW.get(),
                            pos.getX() + 0.5 + (random.nextGaussian() * 0.5),
                            pos.getY() + random.nextFloat(),
                            pos.getZ() + 0.5 + (random.nextGaussian() * 0.5),
                            1,
                            0.3, 0.3, 0.3,
                            1.0D
                    );
                }
                entity.addEffect(new MobEffectInstance(SpeciesStatusEffects.STUCK.get(), 4, 0, false, false, false));
                entity.hurt(level.damageSources().source(SpeciesDamageTypes.CRANKTRAP, entity), amount);
            }
        } else if (!state.getValue(OPEN)) {
            resetTrap(level, pos, state);
        }
    }

    private void resetTrap(ServerLevel level, BlockPos pos, BlockState state) {
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).deflate(0, 0.5, 0));

        if (list.isEmpty()) {
            open(level, pos, state);
        } else {
            level.scheduleTick(pos, this, 10);
        }
    }

    public void open(Level level, BlockPos pos, BlockState state) {
        if (!state.getValue(OPEN)) level.playSound(null, pos, SpeciesSoundEvents.CRANKTRAP_OPEN.get(), SoundSource.BLOCKS, 1, 1);
        level.setBlock(pos, state.setValue(OPEN, true), 3);
    }

    public void close(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(OPEN)) level.playSound(null, pos, SpeciesSoundEvents.CRANKTRAP_CLOSE.get(), SoundSource.BLOCKS, 1, 1);
        level.setBlock(pos, state.setValue(OPEN, false), 3);
    }

    @Override
    public boolean isPathfindable(BlockState p_60475_, BlockGetter p_60476_, BlockPos p_60477_, PathComputationType p_60478_) {
        return p_60475_.getValue(OPEN);
    }
}
