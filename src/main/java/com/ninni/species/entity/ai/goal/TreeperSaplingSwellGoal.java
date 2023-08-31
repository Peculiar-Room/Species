package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.TreeperSapling;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class TreeperSaplingSwellGoal extends Goal {
    private final TreeperSapling treeperSapling;
    @Nullable
    private LivingEntity target;

    public TreeperSaplingSwellGoal(TreeperSapling treeperSapling) {
        this.treeperSapling = treeperSapling;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.treeperSapling.getTarget();
        return this.treeperSapling.getSwellDir() > 0 || livingEntity != null && this.treeperSapling.distanceToSqr(livingEntity) < 2.0;
    }

    @Override
    public void start() {
        this.treeperSapling.getNavigation().stop();
        this.target = this.treeperSapling.getTarget();
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
            this.treeperSapling.setSwellDir(-1);
            return;
        }
        if (this.treeperSapling.distanceToSqr(this.target) > 49.0) {
            this.treeperSapling.setSwellDir(-1);
            return;
        }
        if (!this.treeperSapling.getSensing().hasLineOfSight(this.target)) {
            this.treeperSapling.setSwellDir(-1);
            return;
        }
        this.treeperSapling.setSwellDir(1);
    }
}

