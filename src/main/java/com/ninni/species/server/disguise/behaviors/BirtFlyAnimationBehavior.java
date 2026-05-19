package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.entity.mob.update_1.Birt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

/** Birt disguise: mirrors the wearer's airborne state as {@link Pose#FALL_FLYING} (which starts Birt's
 *  flyingAnimationState); set in pre+postTick, and FALL_FLYING is behavior-owned so the render resync keeps it. */
public final class BirtFlyAnimationBehavior implements DisguiseBehavior {

    public static final BirtFlyAnimationBehavior INSTANCE = new BirtFlyAnimationBehavior();

    private BirtFlyAnimationBehavior() {}

    @Override
    public void preTick(LivingEntity wearer, LivingEntity disguise) {
        sync(wearer, disguise);
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        sync(wearer, disguise);
    }

    private static void sync(LivingEntity wearer, LivingEntity disguise) {
        if (!(disguise instanceof Birt)) return;
        boolean wearerFlying = wearer.isFallFlying()
                || wearer.isSwimming()
                || (wearer instanceof Player p && p.getAbilities().flying);
        Pose desired = wearerFlying ? Pose.FALL_FLYING : Pose.STANDING;
        if (disguise.getPose() != desired) {
            disguise.setPose(desired);
        }
    }
}
