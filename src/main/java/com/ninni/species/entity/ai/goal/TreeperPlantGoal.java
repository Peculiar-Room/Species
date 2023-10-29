package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.Treeper;
import net.minecraft.world.entity.ai.goal.Goal;

public class TreeperPlantGoal extends Goal {
    protected final Treeper treeper;

    public TreeperPlantGoal(Treeper treeper) {
        this.treeper = treeper;
    }

    @Override
    public boolean canUse() {
        return !treeper.isInWater() && treeper.level().isDay() && treeper.onGround() && !treeper.isPlanted();
    }

    @Override
    public boolean canContinueToUse() {
        return !treeper.isInWater() && treeper.level().isDay() && treeper.onGround() && !treeper.isPlanted();
    }

    @Override
    public void start() {
        treeper.plant();
    }

}
