package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.entity.mob.update_2.Treeper;
import net.minecraft.world.entity.LivingEntity;

/**
 * Treeper disguise: keybind toggles {@code plant()} / {@code uproot()}. The uproot path emits a
 * server-side screen-shake to nearby players, matching the native uproot effect.
 */
public final class TreeperSpecialActionBehavior implements DisguiseBehavior {

    public static final TreeperSpecialActionBehavior INSTANCE = new TreeperSpecialActionBehavior();

    private TreeperSpecialActionBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        if (!(disguise instanceof Treeper treeper)) return;
        if (treeper.isPlanted()) treeper.uproot();
        else treeper.plant();
    }
}
