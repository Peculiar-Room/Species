package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.mob.update_2.Goober;
import com.ninni.species.server.entity.util.GooberBehavior;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.world.entity.LivingEntity;

/** Goober disguise: keybind toggles lay-down / stand-up; the yawn auto-fires while laid down,
 *  gated by the entity's native {@code yawnCooldown}. We only need to start the yawn — the
 *  entity tick drains {@code yawnTimer}, reverts the pose, and resets the next cooldown. */
public final class GooberSpecialActionBehavior implements DisguiseBehavior {

    public static final GooberSpecialActionBehavior INSTANCE = new GooberSpecialActionBehavior();

    private GooberSpecialActionBehavior() {}

    @Override
    public void onCreated(LivingEntity wearer, LivingEntity disguise) {
        // Mask NBT lacks "Behavior", so living.load resets BEHAVIOR to "" instead of the
        // defineSynchedData default ("Idle"). Restore so canUse-style checks pass.
        if (disguise instanceof Goober goober) {
            goober.setBehavior(GooberBehavior.IDLE.getName());
        }
    }

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        if (!(disguise instanceof Goober goober)) return;
        if (goober.isGooberLayingDown()) goober.standUp();
        else goober.layDown();
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        if (!(disguise instanceof Goober goober)) return;
        // Mirror GooberYawnGoal.canUse, but only while laid down (the standing-yawn path stays
        // goal-only — irrelevant for the keybind context).
        if (!goober.isGooberLayingDown()) return;
        if (!goober.onGround() || goober.isInWater()) return;
        if (goober.getYawnCooldown() > 0) return;
        if (goober.getYawnTimer() > 0) return;
        if (!GooberBehavior.IDLE.getName().equals(goober.getBehavior())) return;
        // Mirror GooberYawnGoal.start; the entity's tick auto-reverts and resets cooldown.
        goober.setPose(SpeciesPose.YAWNING_LAYING_DOWN.get());
        goober.setYawnTimer(GooberBehavior.YAWN.getLength());
        goober.setBehavior(GooberBehavior.YAWN.getName());
        goober.playSound(SpeciesSoundEvents.GOOBER_YAWN.get(), 1.0f, goober.getVoicePitch());
    }
}
