package com.ninni.species.server.entity.ai.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.raid.Raid;

import javax.annotation.Nullable;

public class CelebrateWithVillagersGoal extends Goal {
    protected final Mob mob;
    protected final SoundEvent soundEvent;
    @Nullable
    private Raid currentRaid;
    private boolean canUse;

    public CelebrateWithVillagersGoal(Mob mob, SoundEvent soundEvent, boolean canUse) {
        this.mob = mob;
        this.soundEvent = soundEvent;
        this.canUse = canUse;
    }

    @Override
    public boolean canUse() {
        if (!canUse) return false;
        if (mob.level() instanceof ServerLevel serverLevel) this.currentRaid = serverLevel.getRaidAt(mob.getOnPos());
        return this.currentRaid != null && this.currentRaid.isVictory();
    }

    @Override
    public boolean canContinueToUse() {
        if (!canUse) return false;
        if (mob.level() instanceof ServerLevel serverLevel) this.currentRaid = serverLevel.getRaidAt(mob.getOnPos());
        return this.currentRaid != null && !this.currentRaid.isStopped();
    }

    @Override
    public void tick() {
        RandomSource randomsource = mob.getRandom();
        if (randomsource.nextInt(100) == 0) {
            if (mob.level() instanceof ServerLevel serverLevel) serverLevel.playSound(mob, mob.blockPosition(), soundEvent, mob.getSoundSource(), 1,1);
        }
    }

    @Override
    public void stop() {
        this.currentRaid = null;
    }

}