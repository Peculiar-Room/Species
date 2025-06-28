package com.ninni.species.server.block;

import com.ninni.species.server.block.entity.MobHeadBlockEntity;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.registry.SpeciesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class MobHeadBlock extends BaseEntityBlock implements Equipable {
    public static final int MAX = RotationSegment.getMaxSegmentIndex();
    private final MobHeadBlock.Type type;
    private static final int ROTATIONS = MAX + 1;
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    protected static final VoxelShape GHOUL_SHAPE = Block.box(2, 0, 2, 14, 7, 14);
    protected static final VoxelShape WICKED_SHAPE = Block.box(4, 0, 4, 12, 6, 12);
    protected static final VoxelShape QUAKE_SHAPE = Block.box(1.5, 0, 1.5, 14.5, 7, 14.5);
    protected static final VoxelShape BEWEREAGER_SHAPE = Block.box(3, 0, 3, 13, 10, 13);

    public MobHeadBlock(MobHeadBlock.Type type, BlockBehaviour.Properties properties) {
        super(properties);
        this.type = type;
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, Integer.valueOf(0)));
    }

    public VoxelShape getShape(BlockState p_56331_, BlockGetter p_56332_, BlockPos p_56333_, CollisionContext p_56334_) {
        if (this.getType() == Types.WICKED) return WICKED_SHAPE;
        else if (this.getType() == Types.GHOUL) return GHOUL_SHAPE;
        else if (this.getType() == Types.BEWEREAGER) return BEWEREAGER_SHAPE;
        else return QUAKE_SHAPE;
    }

    public VoxelShape getOcclusionShape(BlockState p_56336_, BlockGetter p_56337_, BlockPos p_56338_) {
        return Shapes.empty();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(ROTATION, Integer.valueOf(RotationSegment.convertToSegment(context.getRotation())));
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, Integer.valueOf(rotation.rotate(state.getValue(ROTATION), ROTATIONS)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, Integer.valueOf(mirror.mirror(state.getValue(ROTATION), ROTATIONS)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(ROTATION);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_151992_, BlockState p_151993_, BlockEntityType<T> p_151994_) {
        if (p_151992_.isClientSide) {
            boolean flag = p_151993_.is(SpeciesBlocks.GHOUL_HEAD.get()) || p_151993_.is(SpeciesBlocks.GHOUL_WALL_HEAD.get())
                            || p_151993_.is(SpeciesBlocks.BEWEREAGER_HEAD.get()) || p_151993_.is(SpeciesBlocks.BEWEREAGER_WALL_HEAD.get());
            if (flag) {
                return createTickerHelper(p_151994_, SpeciesBlockEntities.MOB_HEAD.get(), MobHeadBlockEntity::animation);
            }
        }

        return null;
    }

    public MobHeadBlock.Type getType() {
        return this.type;
    }

    public boolean isPathfindable(BlockState state, BlockGetter blockGetter, BlockPos pos, PathComputationType computationType) {
        return false;
    }

    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
    public interface Type {
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MobHeadBlockEntity(pos, state);
    }

    public static enum Types implements Type {
        WICKED,
        QUAKE,
        BEWEREAGER,
        GHOUL;
    }
}