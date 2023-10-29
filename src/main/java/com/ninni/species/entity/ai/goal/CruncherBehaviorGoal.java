package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.enums.CruncherBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;


public class CruncherBehaviorGoal extends Goal {
    CruncherBehavior behavior;
    Cruncher cruncher;
    int timer;

    public CruncherBehaviorGoal(Cruncher cruncher, CruncherBehavior behavior) {
        this.cruncher = cruncher;
        this.behavior = behavior;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }


    @Override
    public boolean canUse() {
        LivingEntity target = this.cruncher.getTarget();
        return target != null && cruncher.getBehavior() == CruncherBehavior.IDLE.getName() && !cruncher.isInWater() && cruncher.onGround();
    }

    @Override
    public void start() {
        super.start();
        timer = behavior.getLength();
        cruncher.setPose(behavior.getPose());
        cruncher.playSound(behavior.getSound());
        cruncher.setBehavior(behavior.getName());
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.cruncher.getTarget();
        return target != null && timer > 0 && !cruncher.isInWater() && cruncher.onGround();
    }


    @Override
    public void tick() {
        super.tick();
        timer--;
    }

    @Override
    public void stop() {
        super.stop();
        cruncher.setBehavior(CruncherBehavior.IDLE.getName());
        cruncher.setPose(CruncherBehavior.IDLE.getPose());
    }
}