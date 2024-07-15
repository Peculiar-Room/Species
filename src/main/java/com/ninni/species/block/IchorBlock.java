package com.ninni.species.block;

import com.ninni.species.registry.SpeciesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IchorBlock extends FallingBlock {
    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public IchorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(16) == 0 && FallingBlock.isFree(level.getBlockState(blockPos.below()))) {
            level.removeBlock(blockPos, false);
        }
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        Item item = itemStack.getItem();

        if (itemStack.is(Items.GLASS_BOTTLE)) {
            itemStack.shrink(1);
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (itemStack.isEmpty()) {
                player.setItemInHand(interactionHand, new ItemStack(SpeciesItems.ICHOR_BOTTLE));
            } else if (!player.getInventory().add(new ItemStack(SpeciesItems.ICHOR_BOTTLE))) {
                player.drop(new ItemStack(SpeciesItems.ICHOR_BOTTLE), false);
            }
            level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
            player.awardStat(Stats.ITEM_USED.get(item));
            level.removeBlock(blockPos, false);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }
}