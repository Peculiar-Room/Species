package com.ninni.species.server.entity.ai.goal;

import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.mob.update_3.Bewereager;
import com.ninni.species.server.entity.util.WolfAccess;
import com.ninni.species.registry.SpeciesEntities;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;

public class TransformDuringFullMoonGoal extends Goal {
    protected final Wolf wolf;

    public TransformDuringFullMoonGoal(Wolf wolf) {
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (!wolf.level().getBlockState(wolf.getOnPos().above(2)).isAir()) return false;
        if (wolf instanceof WolfAccess wolfAccess && !wolfAccess.getIsBewereager()) return false;
        return wolf.getOwnerUUID() != null && wolf.level().getMoonBrightness() > 0.9F && !wolf.isInWater() && wolf.level().isNight() && wolf.onGround() && wolf.level().getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override
    public void start() {
        wolf.playSound(SpeciesSoundEvents.BEWEREAGER_TRANSFORM.get(), 1, 1);
        Bewereager bewereager = wolf.convertTo(SpeciesEntities.BEWEREAGER.get(), false);
        bewereager.setCollarColor(wolf.getCollarColor());
        bewereager.setFromWolf(true);
        if (wolf.getOwnerUUID() != null) bewereager.setOwnerUUID(wolf.getOwnerUUID());
        bewereager.addTransformingParticles();
        wolf.level().addFreshEntity(bewereager);
    }

}