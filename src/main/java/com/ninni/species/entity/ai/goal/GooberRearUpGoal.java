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
        return !goober.isGooberLayingDown() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !goober.isGooberLayingDown() && super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        goober.setPose(SpeciesPose.REARING_UP.get());
    }
}
