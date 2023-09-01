package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.Goober;
import com.ninni.species.entity.pose.SpeciesPose;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;

public class GooberRearUpGoal extends Goal {
    protected final Goober goober;
    private int time = 80;

    public GooberRearUpGoal(Goober goober) {
        this.goober = goober;
    }

    @Override
    public boolean canUse() {
        return !goober.isInWater() && goober.onGround() && !goober.isGooberLayingDown() && time > 0 && goober.getActionCooldown() == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return !goober.isInWater() && !goober.isGooberLayingDown() && goober.onGround() && time > 0 && goober.getActionCooldown() == 0;
    }

    @Override
    public void start() {
        goober.setPose(SpeciesPose.REARING_UP.get());
        goober.playSound(SpeciesSoundEvents.ENTITY_GOOBER_REAR_UP, 1.0f, goober.getVoicePitch());
    }

    @Override
    public void tick() {
        super.tick();
        time--;
    }

    @Override
    public void stop() {
        goober.setActionCooldown(2 * 20 + goober.getRandom().nextInt(40 * 20));

        goober.setPose(Pose.STANDING);
    }
}
