package com.ninni.species.server.entity.ai.goal;

import com.ninni.species.server.entity.mob.update_2.Goober;
import com.ninni.species.server.entity.util.GooberBehavior;
import net.minecraft.world.entity.ai.goal.Goal;

public class GooberLayDownGoal extends Goal {
    protected final Goober goober;
    private final int minimalPoseTicks;

    public GooberLayDownGoal(Goober goober) {
        this.goober = goober;
        this.minimalPoseTicks = 20 * 20 + goober.getRandom().nextInt(20 * 20);
    }

    @Override
    public boolean canUse() {
        return !goober.isInWater()
                && goober.getLayDownCooldown() == 0
                && goober.getPoseTime() >= (long)this.minimalPoseTicks
                && !goober.isLeashed()
                && goober.onGround()
                && goober.getBehavior().equals(GooberBehavior.IDLE.getName());
    }

    @Override
    public boolean canContinueToUse() {
        return !goober.isInWater() && goober.getPoseTime() >= (long)this.minimalPoseTicks && !goober.isLeashed() && goober.onGround();
    }

    @Override
    public void start() {
        if (goober.isGooberLayingDown()) {
            goober.layDownCooldown();
            goober.setRearUpCooldown(6 * 20 + goober.getRandom().nextInt(10 * 20));
            goober.standUp();
        } else {
            goober.standUpCooldown();
            goober.layDown();
        }
    }

}