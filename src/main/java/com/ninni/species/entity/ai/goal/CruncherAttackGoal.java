package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.enums.CruncherBehavior;
import net.minecraft.world.entity.LivingEntity;

public class CruncherAttackGoal extends CruncherBehaviorGoal {

    public CruncherAttackGoal(Cruncher cruncher) {
        super(cruncher, CruncherBehavior.ATTACK);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.cruncher.getTarget();
        return super.canUse() && cruncher.distanceTo(target) < 5f;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.cruncher.getTarget();
        return super.canContinueToUse() && cruncher.distanceTo(target) < 5f;
    }
}
