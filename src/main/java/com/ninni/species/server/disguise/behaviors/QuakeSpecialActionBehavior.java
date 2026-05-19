package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

/** Quake disguise: keybind triggers the hurt-attack animation via {@code DATA_POSE → ATTACK};
 *  entity tick auto-reverts after the 190t {@code attackTimer} drains. */
public final class QuakeSpecialActionBehavior implements DisguiseBehavior {

    public static final QuakeSpecialActionBehavior INSTANCE = new QuakeSpecialActionBehavior();

    private QuakeSpecialActionBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        // Skip if already attacking — re-pressing during the 190-tick window is a no-op visually
        // (animation state stays started) and would re-trigger the recharge sound chain.
        if (disguise.getPose() == SpeciesPose.ATTACK.get()
                || disguise.getPose() == SpeciesPose.RECHARGE.get()) return;
        disguise.setPose(SpeciesPose.ATTACK.get());
    }
}
