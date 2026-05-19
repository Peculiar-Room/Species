package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.entity.mob.update_3.Wicked;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.world.entity.LivingEntity;

/** Wicked disguise: keybind triggers the SPOT pose (auto-reverted by {@code Wicked.tick}). On
 *  creation, restores {@code mana=5} so the ambient particle emitter isn't gated off by a
 *  missing-key tag (which {@code readAdditionalSaveData} treats as zero). */
public final class WickedSpecialActionBehavior implements DisguiseBehavior {

    public static final WickedSpecialActionBehavior INSTANCE = new WickedSpecialActionBehavior();

    /** Matches {@code entityData.define(MANA_AMOUNT, 5)} in {@link Wicked}. */
    private static final int DEFAULT_MANA = 5;

    private WickedSpecialActionBehavior() {}

    @Override
    public void onCreated(LivingEntity wearer, LivingEntity disguise) {
        if (disguise instanceof Wicked wicked && wicked.getMana() == 0) {
            wicked.setMana(DEFAULT_MANA);
        }
    }

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        if (disguise.getPose() == SpeciesPose.SPOT.get()) return;
        disguise.setPose(SpeciesPose.SPOT.get());
    }
}
