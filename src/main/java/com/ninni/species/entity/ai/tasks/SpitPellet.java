package com.ninni.species.entity.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.CruncherPellet;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesMemoryModuleTypes;
import net.minecraft.core.BlockPos;
import com.ninni.species.registry.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class SpitPellet extends Behavior<Cruncher> {
    private static final int DURATION = 30;

    public SpitPellet() {
        super(ImmutableMap.of(
                MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                SpeciesMemoryModuleTypes.SPIT_CHARGING, MemoryStatus.VALUE_ABSENT
        ), DURATION);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Cruncher livingEntity) {
        Optional<Player> nearestPlayer = livingEntity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);

        long day = serverLevel.getDayTime() / 24000L;
        long cruncherDay = livingEntity.getDay();

        if (cruncherDay == -1 || day != cruncherDay && day == 0) {
            livingEntity.setDay(day);
        }

        if (cruncherDay < day) {
            livingEntity.setDay(cruncherDay + 1);
            return livingEntity.getPelletData() != null && nearestPlayer.isPresent();
        }

        return false;
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        return livingEntity.getSpits() <= 10;
    }

    @Override
    protected void start(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        serverLevel.broadcastEntityEvent(livingEntity, (byte) 4);
        livingEntity.playSound(SpeciesSoundEvents.CRUNCHER_SPIT, 2.0F, 1.0F);
        livingEntity.getBrain().setMemoryWithExpiry(SpeciesMemoryModuleTypes.SPIT_CHARGING, Unit.INSTANCE, 12);
    }

    @Override
    protected void tick(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        Brain<Cruncher> brain = livingEntity.getBrain();

        brain.eraseMemory(MemoryModuleType.WALK_TARGET);

        if (brain.getMemory(SpeciesMemoryModuleTypes.SPIT_CHARGING).isPresent()) return;

        BlockPos blockPos = livingEntity.blockPosition();

        BlockState blockState = SpeciesBlocks.CRUNCHER_PELLET.defaultBlockState();

        float angle = (0.0174532925F * livingEntity.yBodyRot);
        double headX = 3F * livingEntity.getScale() * Mth.sin(Mth.PI + angle);
        double headZ = 3F * livingEntity.getScale() * Mth.cos(angle);

        CruncherPellet pellet = new CruncherPellet(serverLevel, (double) blockPos.getX() + headX, blockPos.getY() + livingEntity.getEyeHeight(), (double) blockPos.getZ() + headZ, blockState, livingEntity.getPelletData());

        pellet.setDeltaMovement(livingEntity.getLookAngle().scale(2.0D).multiply(0.25D, 1.0D, 0.25D));

        serverLevel.addFreshEntity(pellet);

        livingEntity.setSpits(livingEntity.getSpits() + 1);

        brain.setMemoryWithExpiry(SpeciesMemoryModuleTypes.SPIT_CHARGING, Unit.INSTANCE, 96);
    }

    @Override
    protected void stop(ServerLevel serverLevel, Cruncher livingEntity, long time) {
        if (livingEntity.getSpits() > 10) {
            long day = serverLevel.getDayTime() / 24000L;
            livingEntity.setSpits(0);
            livingEntity.setDay(day);
        }
    }
}