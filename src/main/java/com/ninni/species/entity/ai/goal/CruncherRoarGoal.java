package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.enums.CruncherBehavior;
import net.minecraft.world.entity.LivingEntity;

public class CruncherRoarGoal extends CruncherBehaviorGoal {

    public CruncherRoarGoal(Cruncher cruncher) {
        super(cruncher, CruncherBehavior.ROAR);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.cruncher.getTarget();
        return super.canUse() && this.cruncher.distanceToSqr(target) > 5;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.cruncher.getTarget();
        return super.canContinueToUse() && this.cruncher.distanceToSqr(target) > 5;
    }
}
