package com.ninni.species.block.entity;

import com.ninni.species.block.CruncherEggBlock;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesStatusEffects;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class CruncherEggBlockEntity extends BlockEntity {
    public LivingEntity target;
    private int timer;

    public CruncherEggBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SpeciesBlockEntities.CRUNCHER_EGG, blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.putInt("Timer", this.timer);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.timer = compoundTag.getInt("Timer");
    }

    public LivingEntity getTarget() {
        return this.target;
    }

    public void setTarget(LivingEntity livingEntity) {
        this.target = livingEntity;
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (state.getValue(CruncherEggBlock.HALF) == DoubleBlockHalf.UPPER && state.getValue(CruncherEggBlock.CRACKED) && level.random.nextInt(10) == 1) {
            level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, (double)blockPos.getX() + 0.5 + level.random.nextDouble() / 3.0 * (double)(level.random.nextBoolean() ? 1 : -1), (double)blockPos.getY() + level.random.nextDouble() + level.random.nextDouble(), (double)blockPos.getZ() + 0.5 + level.random.nextDouble() / 3.0 * (double)(level.random.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState state, CruncherEggBlockEntity blockEntity) {
        LivingEntity target = blockEntity.getTarget();

        if (state.getValue(CruncherEggBlock.CRACKED) || state.getValue(CruncherEggBlock.HALF) == DoubleBlockHalf.UPPER) return;

        blockEntity.timer++;

        if ((blockEntity.timer > 20 * 15 && level.random.nextInt(30) == 1) || (blockEntity.timer > 20 * 45)) {
            if (target == null) {
                Player nearestPlayer = level.getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 10.0, false);

                boolean validPlayer = nearestPlayer != null && nearestPlayer.isAlive();

                if (validPlayer) {
                    blockEntity.setTarget(nearestPlayer);
                }
            } else {
                level.setBlock(blockPos, state.setValue(CruncherEggBlock.CRACKED, true), 2);
                BlockState aboveState = level.getBlockState(blockPos.above());
                if (aboveState.getBlock() instanceof CruncherEggBlock) {
                    level.setBlock(blockPos.above(), aboveState.setValue(CruncherEggBlock.CRACKED, true), 2);
                }

                if (!target.hasEffect(SpeciesStatusEffects.GUT_FEELING)) {
                    target.addEffect(new MobEffectInstance(SpeciesStatusEffects.GUT_FEELING, 24000, 0, true, true));
                }

                FriendlyByteBuf buf = PacketByteBufs.create();
                if (target instanceof ServerPlayer serverPlayer) {
                    ServerPlayNetworking.send(serverPlayer, SpeciesNetwork.PLAY_GUT_FEELING_SOUND, buf);
                }
            }
        }
    }

}