package com.ninni.species.server.entity.ai.goal;


import com.ninni.species.server.entity.mob.update_2.Goober;
import com.ninni.species.server.entity.util.GooberBehavior;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.world.entity.ai.goal.Goal;

public class GooberRearUpGoal extends Goal {
    Goober goober;
    GooberBehavior behavior;

    public GooberRearUpGoal(Goober goober) {
        this.goober = goober;
        this.behavior = GooberBehavior.REAR_UP;
    }


    @Override
    public boolean canUse() {
        return goober.getRearUpCooldown() == 0 && goober.getBehavior().equals(GooberBehavior.IDLE.getName()) && !goober.isInWater() && goober.onGround() && !goober.isGooberLayingDown();
    }

    @Override
    public boolean canContinueToUse() {
        return goober.getRearUpTimer() > 0 && !goober.isInWater() && goober.onGround() && !goober.isGooberLayingDown();
    }

    @Override
    public void start() {
        super.start();
        goober.setPose(SpeciesPose.REARING_UP.get());
        goober.setRearUpTimer(behavior.getLength());
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
    }
}