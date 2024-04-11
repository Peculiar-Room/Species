package com.ninni.species.block.entity;

import com.ninni.species.block.CruncherEggBlock;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.registry.SpeciesStatusEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class CruncherEggBlockEntity extends BlockEntity {
    private int cooldown;
    public static LivingEntity player;

    public CruncherEggBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SpeciesBlockEntities.CRUNCHER_EGG, blockPos, blockState);
    }

    public void setPlayer(LivingEntity livingEntity) {
        player = livingEntity;
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState state, CruncherEggBlockEntity blockEntity) {
        if (level.getBlockState(blockPos).getValue(CruncherEggBlock.HALF) == DoubleBlockHalf.UPPER && level.getBlockState(blockPos).getValue(CruncherEggBlock.CRACKED) && level.random.nextInt(15) == 1) {
            level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, (double)blockPos.getX() + 0.5 + level.random.nextDouble() / 3.0 * (double)(level.random.nextBoolean() ? 1 : -1), (double)blockPos.getY() + level.random.nextDouble() + level.random.nextDouble(), (double)blockPos.getZ() + 0.5 + level.random.nextDouble() / 3.0 * (double)(level.random.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        }
        if (state.getValue(CruncherEggBlock.TIMER) == 30) {
            level.addParticle(ParticleTypes.EXPLOSION_EMITTER, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0, 0.0, 0.0);
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState state, CruncherEggBlockEntity blockEntity) {

        if (player != null && state.getValue(CruncherEggBlock.TIMER) > 0 && level.random.nextFloat() < 0.25f) {
            level.setBlockAndUpdate(blockPos, state.setValue(CruncherEggBlock.TIMER, state.getValue(CruncherEggBlock.TIMER) - 1));
        }

        if (!level.getBlockState(blockPos).getValue(CruncherEggBlock.CRACKED)) {

            if (player == null) {
                if (level.hasNearbyAlivePlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 10.0)) {
                    player = level.getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 10.0, true);
                }
            } else {
                if (state.getValue(CruncherEggBlock.TIMER) == 30) {
                    level.setBlockAndUpdate(blockPos, state.setValue(CruncherEggBlock.CRACKED, true));
                    player.addEffect(new MobEffectInstance(SpeciesStatusEffects.GUT_FEELING, 20 * 60 * 20, 0), null);
                }
            }

        }

    }
}