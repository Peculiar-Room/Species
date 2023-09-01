package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.Goober;
import com.ninni.species.entity.pose.SpeciesPose;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;

public class GooberYawnGoal extends Goal {
    protected final Goober goober;
    private int time = 40;

    public GooberYawnGoal(Goober goober) {
        this.goober = goober;
    }

    @Override
    public boolean canUse() {
        return goober.canYawn() && !goober.isInWater() && goober.onGround() && time > 0 && goober.getActionCooldown() == 0;
    }

    @Override
    public void start() {
        if (goober.isGooberLayingDown()) goober.setPose(SpeciesPose.YAWNING_LAYING_DOWN.get());
        else goober.setPose(SpeciesPose.YAWNING.get());
        goober.playSound(SpeciesSoundEvents.ENTITY_GOOBER_YAWN, 1.0f, goober.getVoicePitch());
    }

    @Override
    public void tick() {
        super.tick();
        time--;
    }

    @Override
    public void stop() {
        goober.setActionCooldown(2 * 20 + goober.getRandom().nextInt(40 * 20));
        time = 40;
        if (goober.isGooberLayingDown()) goober.setPose(SpeciesPose.LAYING_DOWN.get());
        else goober.setPose(Pose.STANDING);
    }
}
