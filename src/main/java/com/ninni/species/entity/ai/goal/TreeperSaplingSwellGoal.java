package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.TreeperSapling;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class TreeperSaplingSwellGoal extends Goal {
    private final TreeperSapling creeper;
    @Nullable
    private LivingEntity target;

    public TreeperSaplingSwellGoal(TreeperSapling creeper) {
        this.creeper = creeper;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.creeper.getTarget();
        return this.creeper.getSwellDir() > 0 || livingEntity != null && this.creeper.distanceToSqr(livingEntity) < 9.0;
    }

    @Override
    public void start() {
        this.creeper.getNavigation().stop();
        this.target = this.creeper.getTarget();
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
            this.creeper.setSwellDir(-1);
            return;
        }
        if (this.creeper.distanceToSqr(this.target) > 49.0) {
            this.creeper.setSwellDir(-1);
            return;
        }
        if (!this.creeper.getSensing().hasLineOfSight(this.target)) {
            this.creeper.setSwellDir(-1);
            return;
        }
        this.creeper.setSwellDir(1);
    }
}

