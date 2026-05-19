package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.disguise.util.DisguiseSounds;
import com.ninni.species.api.util.PerSideStateMap;
import com.ninni.species.server.entity.mob.update_2.Cruncher;
import com.ninni.species.server.entity.mob.update_2.Cruncher.CruncherState;
import net.minecraft.world.entity.LivingEntity;

/** Cruncher disguise: keybind triggers the STOMP. Mirrors the native goal duration (20t) and
 *  reverts in {@code postTick}. */
public final class CruncherSpecialActionBehavior implements DisguiseBehavior {

    public static final CruncherSpecialActionBehavior INSTANCE = new CruncherSpecialActionBehavior();

    private static final int STOMP_DURATION_TICKS = 20;

    private static final class State {
        int firedTick;
        boolean active;
    }

    private final PerSideStateMap<State> states = new PerSideStateMap<>();

    private CruncherSpecialActionBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        if (!(disguise instanceof Cruncher cruncher)) return;
        // Force IDLE -> STOMP cycle so setState(STOMP) actually changes CRUNCHER_STATE (and
        // dispatches the synced-data hook that drives the animation) even when a previous cycle
        // is still mid-flight on this side.
        cruncher.transitionTo(CruncherState.IDLE);
        cruncher.transitionTo(CruncherState.STOMP);
        State st = states.computeIfAbsent(wearer, State::new);
        st.firedTick = disguise.tickCount;
        st.active = true;
        // CruncherState.STOMP carries SoundEvents.EMPTY natively; the goal-driven path uses
        // broadcastEntityEvent which doesn't reach the unregistered disguise body.
        DisguiseSounds.playServerBroadcast(wearer, SpeciesSoundEvents.CRUNCHER_STOMP.get(), 2.0F);
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        if (!(disguise instanceof Cruncher cruncher)) return;
        State st = states.get(wearer);
        if (st == null || !st.active) return;
        if (disguise.tickCount - st.firedTick < STOMP_DURATION_TICKS) return;
        cruncher.transitionTo(CruncherState.IDLE);
        st.active = false;
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        states.remove(wearer);
    }
}
