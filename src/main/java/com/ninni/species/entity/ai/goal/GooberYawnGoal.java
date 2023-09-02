package com.ninni.species.entity.ai.goal;


import com.ninni.species.entity.Goober;
import com.ninni.species.entity.enums.GooberBehavior;
import com.ninni.species.entity.pose.SpeciesPose;

public class GooberYawnGoal extends GooberBehaviorGoal {

    public GooberYawnGoal(Goober goober) {
        super(goober, GooberBehavior.YAWN);
    }

    @Override
    public boolean canUse() {
        return goober.getYawnCooldown() == 0 && super.canUse();
    }

    @Override
    public void start() {
        super.start();
        if (goober.isGooberLayingDown()) goober.setPose(SpeciesPose.YAWNING_LAYING_DOWN.get());
        else goober.setPose(SpeciesPose.YAWNING.get());
        goober.playSound(behavior.getSound(), 1.0f, goober.getVoicePitch());
    }

    @Override
    public void stop() {
        super.stop();
        goober.yawnCooldown();
    }
}
