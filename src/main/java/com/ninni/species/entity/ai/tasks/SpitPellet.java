package com.ninni.species.entity.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.CruncherPellet;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesMemoryModuleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.state.BlockState;

public class SpitPellet extends Behavior<Cruncher> {
    private static final Cruncher.CruncherState cruncherState = Cruncher.CruncherState.ROAR;

    public SpitPellet() {
        super(ImmutableMap.of(
                MemoryModuleType.LIKED_PLAYER, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                SpeciesMemoryModuleTypes.SPIT_CHARGING, MemoryStatus.VALUE_ABSENT
        ), cruncherState.getDuration());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Cruncher livingEntity) {
        return livingEntity.getPelletData() != null;
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        return true;
    }

    @Override
    protected void start(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        if (livingEntity.getState() == Cruncher.CruncherState.IDLE) {
            livingEntity.transitionTo(cruncherState);
        }
        livingEntity.getBrain().setMemoryWithExpiry(SpeciesMemoryModuleTypes.SPIT_CHARGING, Unit.INSTANCE, 64);
    }

    @Override
    protected void tick(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        Brain<Cruncher> brain = livingEntity.getBrain();

        brain.eraseMemory(MemoryModuleType.WALK_TARGET);

        if (brain.getMemory(SpeciesMemoryModuleTypes.SPIT_CHARGING).isPresent()) return;

        BlockPos blockPos = livingEntity.blockPosition();

        BlockState blockState = SpeciesBlocks.CRUNCHER_PELLET.defaultBlockState();

        CruncherPellet pellet = new CruncherPellet(serverLevel, (double) blockPos.getX() + 0.5, blockPos.getY() + livingEntity.getEyeHeight(), (double) blockPos.getZ() + 0.5, blockState, livingEntity.getPelletData());
        pellet.setDeltaMovement(livingEntity.getLookAngle().scale(2.0D).multiply(0.5D, 1.0D, 0.5D));

        serverLevel.addFreshEntity(pellet);

        brain.setMemoryWithExpiry(SpeciesMemoryModuleTypes.SPIT_CHARGING, Unit.INSTANCE, 96);
    }

    @Override
    protected void stop(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        if (livingEntity.getState() == cruncherState) {
            livingEntity.transitionTo(Cruncher.CruncherState.IDLE);
        }
    }
}
