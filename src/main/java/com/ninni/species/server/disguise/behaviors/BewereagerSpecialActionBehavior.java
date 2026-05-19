package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.disguise.util.DisguiseSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

/** Bewereager disguise: keybind triggers the howl pose; sound is played explicitly since the
 *  pose-only path skips it. The "strength" sound at {@code howlTime==1} auto-fires via tick. */
public final class BewereagerSpecialActionBehavior implements DisguiseBehavior {

    public static final BewereagerSpecialActionBehavior INSTANCE = new BewereagerSpecialActionBehavior();

    private BewereagerSpecialActionBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        if (disguise.getPose() == Pose.ROARING) return;
        disguise.setPose(Pose.ROARING);
        DisguiseSounds.playServerBroadcast(wearer, SpeciesSoundEvents.BEWEREAGER_HOWL.get(), 4.0F);
    }
}
