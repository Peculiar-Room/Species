package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.Goober;
import com.ninni.species.entity.enums.GooberBehavior;
import com.ninni.species.entity.pose.SpeciesPose;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;

public class GooberBehaviorGoal extends Goal {
    GooberBehavior behavior;
    Goober goober;
    int timer;

    public GooberBehaviorGoal(Goober goober, GooberBehavior behavior) {
        this.goober = goober;
        this.behavior = behavior;
    }


    @Override
    public boolean canUse() {
        return goober.getBehavior() == GooberBehavior.IDLE.getName() && !goober.isInWater() && goober.onGround();
    }

    @Override
    public boolean canContinueToUse() {
        return timer > 0 && !goober.isInWater() && goober.onGround();
    }

    @Override
    public void start() {
        super.start();
        timer = behavior.getLength();
        goober.setBehavior(behavior.getName());
    }

    @Override
    public void tick() {
        super.tick();
        timer--;
    }

    @Override
    public void stop() {
        super.stop();
        if (goober.isGooberLayingDown()) goober.setPose(SpeciesPose.LAYING_DOWN.get());
        else goober.setPose(Pose.STANDING);
        goober.setBehavior(GooberBehavior.IDLE.getName());
    }
}