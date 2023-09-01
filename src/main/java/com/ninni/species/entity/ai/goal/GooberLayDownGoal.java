package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.Goober;
import net.minecraft.world.entity.ai.goal.Goal;

public class GooberLayDownGoal extends Goal {
    protected final Goober goober;
    private final int minimalPoseTicks;

    public GooberLayDownGoal(Goober goober) {
        this.goober = goober;
        this.minimalPoseTicks = 20 * 20;
    }

    @Override
    public boolean canUse() {
        return !goober.isInWater() && goober.getPoseTime() >= (long)this.minimalPoseTicks && !goober.isLeashed() && goober.onGround() && goober.getActionCooldown() == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return !goober.isInWater() && goober.getPoseTime() >= (long)this.minimalPoseTicks && !goober.isLeashed() && goober.onGround();
    }

    @Override
    public void start() {
            if (goober.isGooberLayingDown()) {
                goober.standUp();
            } else {
                goober.layDown();
            }
    }

}
