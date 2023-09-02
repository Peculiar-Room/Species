package com.ninni.species.entity.ai.goal;


import com.ninni.species.entity.Goober;
import com.ninni.species.entity.enums.GooberBehavior;
import com.ninni.species.entity.pose.SpeciesPose;

public class GooberRearUpGoal extends GooberBehaviorGoal {

    public GooberRearUpGoal(Goober goober) {
        super(goober, GooberBehavior.REAR_UP);
    }

    @Override
    public boolean canUse() {
        return goober.getRearUpCooldown() == 0 && !goober.isGooberLayingDown() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !goober.isGooberLayingDown() && super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        goober.setPose(SpeciesPose.REARING_UP.get());
        goober.playSound(behavior.getSound(), 1.0f, 1.0f);
    }

    @Override
    public void stop() {
        super.stop();
        goober.rearUpCooldown();
    }
}
