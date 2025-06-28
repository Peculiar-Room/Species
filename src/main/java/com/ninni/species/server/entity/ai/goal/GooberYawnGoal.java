package com.ninni.species.server.entity.ai.goal;

import com.ninni.species.server.entity.mob.update_2.Goober;
import com.ninni.species.server.entity.util.GooberBehavior;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;

public class GooberYawnGoal extends Goal {
    Goober goober;
    GooberBehavior behavior;

    public GooberYawnGoal(Goober goober) {
        this.goober = goober;
        this.behavior = GooberBehavior.YAWN;
    }


    @Override
    public boolean canUse() {
        return goober.getYawnCooldown() == 0 && goober.getBehavior().equals(GooberBehavior.IDLE.getName()) && !goober.isInWater() && goober.onGround();
    }

    @Override
    public boolean canContinueToUse() {
        return goober.getYawnTimer() > 0 && !goober.isInWater() && goober.onGround();
    }

    @Override
    public void start() {
        super.start();
        if (goober.isGooberLayingDown()) goober.setPose(SpeciesPose.YAWNING_LAYING_DOWN.get());
        else goober.setPose(SpeciesPose.YAWNING.get());
        goober.setYawnTimer(behavior.getLength());
        goober.setBehavior(behavior.getName());
        goober.playSound(behavior.getSound(), 1.0f, goober.getVoicePitch());
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void stop() {
        super.stop();
        if (goober.isGooberLayingDown()) goober.setPose(SpeciesPose.LAYING_DOWN.get());
        else goober.setPose(Pose.STANDING);
        goober.setBehavior(GooberBehavior.IDLE.getName());
        goober.yawnCooldown();
    }
}