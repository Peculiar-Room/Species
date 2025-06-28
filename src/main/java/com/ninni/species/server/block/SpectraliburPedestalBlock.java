package com.ninni.species.server.block;

import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.block.property.SpeciesProperties;
import com.ninni.species.server.entity.mob.update_3.Spectre;
import com.ninni.species.registry.SpeciesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.world.entity.EntitySelector.NO_SPECTATORS;

public class SpectraliburPedestalBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ACTIVE = SpeciesProperties.ACTIVE;

    public SpectraliburPedestalBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(ACTIVE, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {

        if (state.getValue(POWERED) || !state.getValue(ACTIVE) || world.getBlockState(pos.above()).is(SpeciesBlocks.SPECTRALIBUR.get()) || world.getDifficulty() == Difficulty.PEACEFUL) {
            return InteractionResult.CONSUME;
        } else {
            tryToSummonSpectres(state, world, pos, player);
            return InteractionResult.SUCCESS;
        }
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos1, boolean b) {
        boolean hasNeighborSignal = level.hasNeighborSignal(pos);
        if (hasNeighborSignal && !state.getValue(POWERED) && state.getValue(ACTIVE) && !level.getBlockState(pos.above()).is(SpeciesBlocks.SPECTRALIBUR.get()) && level.getDifficulty() != Difficulty.PEACEFUL) {
            tryToSummonSpectres(state, level, pos, null);
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos pos, BlockPos pos1) {
        return levelAccessor.getBlockState(pos.above()).is(SpeciesBlocks.SPECTRALIBUR.get()) ? super.updateShape(state, direction, state1, levelAccessor, pos, pos1) : state.setValue(ACTIVE, true);
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, false), 3);
            level.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, pos);
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SpeciesSoundEvents.SPECTRALIBUR_PEDESTAL_DEACTIVATE.get(), SoundSource.BLOCKS, 1.0F, 1F);
        }
    }

    private void tryToSummonSpectres(BlockState state, Level world, BlockPos pos, @Nullable Player player) {
        world.setBlock(pos, state.setValue(POWERED, true), 3);
        world.scheduleTick(pos, this, 40);
        if (player != null) world.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);

        if (world instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SpeciesSoundEvents.SPECTRALIBUR_PEDESTAL_ACTIVATE.get(), SoundSource.BLOCKS, 1.0F, 1F);
            serverLevel.sendParticles(SpeciesParticles.SPECTRALIBUR.get(), pos.getX() + 0.5F, pos.getY() + 1.01F, pos.getZ() + 0.5F, 1,0, 0, 0, 0);
            List<Player> list = world.getEntitiesOfClass(Player.class, new AABB(pos).inflate(12), NO_SPECTATORS);
            int amount = list.size();
            if (amount > 0) {
                amount += world.random.nextInt(2);
                if (world.random.nextInt(5) == 0)
                    Spectre.spawnSpectre(serverLevel, player != null ? player : list.get(0), pos.above(), Spectre.Type.JOUSTING_SPECTRE, false);
                if (world.random.nextInt(5) == 0)
                    Spectre.spawnSpectre(serverLevel, player != null ? player : list.get(0), pos.above(), Spectre.Type.HULKING_SPECTRE, false);
                for (int i = 0; i < amount; i++)
                    Spectre.spawnSpectre(serverLevel, player != null ? player : list.get(0), pos.above(), Spectre.Type.SPECTRE, false);

            }
        }
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity livingEntity, ItemStack stack) {
        level.setBlock(pos.above(), SpeciesBlocks.SPECTRALIBUR.get().defaultBlockState(), 3);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context) ? super.getStateForPlacement(context) : null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51101_) {
        p_51101_.add(POWERED, ACTIVE);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource randomSource) {
        int i = pos.getX();
        int j = pos.getY() + 7;
        int k = pos.getZ();


        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int l = 0; l < 10; ++l) {
            mutable.set(i + Mth.nextInt(randomSource, -6, 6), j - randomSource.nextInt(6), k + Mth.nextInt(randomSource, -6, 6));
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.isCollisionShapeFullBlock(world, mutable)) {
                continue;
            }
            world.addParticle(ParticleTypes.WHITE_ASH, (double) mutable.getX() + randomSource.nextDouble(), (double) mutable.getY() + randomSource.nextDouble(), (double) mutable.getZ() + randomSource.nextDouble(), 0,0,0);
        }
    }

}
