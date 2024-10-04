package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.Trooper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class TrooperSwellGoal extends Goal {
    private final Trooper trooper;
    @Nullable
    private LivingEntity target;

    public TrooperSwellGoal(Trooper trooper) {
        this.trooper = trooper;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.trooper.getTarget();
        return this.trooper.getSwellDir() > 0 || livingEntity != null && this.trooper.distanceToSqr(livingEntity) < 2.0;
    }

    @Override
    public void start() {
        this.trooper.getNavigation().stop();
        this.target = this.trooper.getTarget();
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            this.trooper.setSwellDir(-1);
            return;
        }
        if (this.trooper.distanceToSqr(this.target) > 49.0) {
            this.trooper.setSwellDir(-1);
            return;
        }
        if (!this.trooper.getSensing().hasLineOfSight(this.target)) {
            this.trooper.setSwellDir(-1);
            return;
        }
        this.trooper.setSwellDir(1);
    }
}
