package com.ninni.species.server.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ninni.species.server.block.entity.MobHeadBlockEntity;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.registry.SpeciesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class WallMobHeadBlock extends BaseEntityBlock implements Equipable {
    private final MobHeadBlock.Type type;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction, VoxelShape> GHOUL_AABBS = Maps.newEnumMap(
            ImmutableMap.of(
            Direction.NORTH, Block.box(2, 4, 8, 14, 11, 16),
            Direction.SOUTH, Block.box(2, 4, 0, 14, 11, 8),
            Direction.EAST, Block.box(0, 4, 2, 8, 11, 14),
            Direction.WEST, Block.box(8, 4, 2, 16, 11, 14)
            )
    );
    private static final Map<Direction, VoxelShape> WICKED_AABBS = Maps.newEnumMap(
            ImmutableMap.of(
            Direction.NORTH, Block.box(4, 4, 8, 12, 10, 16),
            Direction.SOUTH, Block.box(4, 4, 0, 12, 10, 8),
            Direction.EAST, Block.box(0, 4, 4, 8, 10, 12),
            Direction.WEST, Block.box(8, 4, 4, 16, 10, 12)
            )
    );
    private static final Map<Direction, VoxelShape> QUAKE_AABBS = Maps.newEnumMap(
            ImmutableMap.of(
            Direction.NORTH, Block.box(1.5, 4, 6, 14.5, 11, 16),
            Direction.SOUTH, Block.box(1.5, 4, 0, 14.5, 11, 10),
            Direction.EAST, Block.box(0, 4, 1.5, 10, 11, 14.5),
            Direction.WEST, Block.box(6, 4, 1.5, 16, 11, 14.5)
            )
    );
    private static final Map<Direction, VoxelShape> BEWEREAGER_AABBS = Maps.newEnumMap(
            ImmutableMap.of(
            Direction.NORTH, Block.box(3, 3, 6, 13, 13, 16),
            Direction.SOUTH, Block.box(3, 3, 0, 13, 13, 10),
            Direction.EAST, Block.box(0, 3, 3, 10, 13, 13),
            Direction.WEST, Block.box(6, 3, 3, 16, 13, 13)
            )
    );

    public WallMobHeadBlock(MobHeadBlock.Type type, Properties properties) {
        super(properties);
        this.type = type;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public VoxelShape getShape(BlockState p_56331_, BlockGetter p_56332_, BlockPos p_56333_, CollisionContext p_56334_) {
        if (this.getType() == MobHeadBlock.Types.WICKED) return WICKED_AABBS.get(p_56331_.getValue(FACING));
        else if (this.getType() == MobHeadBlock.Types.GHOUL) return GHOUL_AABBS.get(p_56331_.getValue(FACING));
        else if (this.getType() == MobHeadBlock.Types.BEWEREAGER) return BEWEREAGER_AABBS.get(p_56331_.getValue(FACING));
        else return QUAKE_AABBS.get(p_56331_.getValue(FACING));
    }

    public VoxelShape getOcclusionShape(BlockState p_56336_, BlockGetter p_56337_, BlockPos p_56338_) {
        return Shapes.empty();
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_58104_) {
        BlockState blockstate = this.defaultBlockState();
        BlockGetter blockgetter = p_58104_.getLevel();
        BlockPos blockpos = p_58104_.getClickedPos();
        Direction[] adirection = p_58104_.getNearestLookingDirections();

        for(Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (!blockgetter.getBlockState(blockpos.relative(direction)).canBeReplaced(p_58104_)) {
                    return blockstate;
                }
            }
        }

        return null;
    }

    public BlockState rotate(BlockState p_58109_, Rotation p_58110_) {
        return p_58109_.setValue(FACING, p_58110_.rotate(p_58109_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_58106_, Mirror p_58107_) {
        return p_58106_.rotate(p_58107_.getRotation(p_58106_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58112_) {
        p_58112_.add(FACING);
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

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MobHeadBlockEntity(pos, state);
    }
}