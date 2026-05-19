package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.disguise.StackatickStackManager;
import net.minecraft.world.entity.LivingEntity;

/** Stackatick disguise: shift+keybind cycles 0 → 1 → 2 → 3 companions stacked above the
 *  wearer, then wraps to 0. Mirrors the native emergent stack (sitting Stackaticks ridden
 *  by others). Sub-entities are managed by {@link StackatickStackManager}. */
public final class StackatickStackBehavior implements DisguiseBehavior {

    public static final StackatickStackBehavior INSTANCE = new StackatickStackBehavior();

    private StackatickStackBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        if (!context.shifted()) return;
        StackatickStackManager.cycle(wearer, disguise);
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        StackatickStackManager.tickStack(wearer, disguise);
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        StackatickStackManager.clear(wearer);
    }
}
