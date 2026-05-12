package com.ninni.species.server.disguise;

import com.ninni.species.api.disguise.AnimatedTransitionSpec;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.PerSideStateMap;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/** Drives {@link AnimatedTransitionSpec} timelines. Self-registers a global
 *  {@link DisguiseBehavior} on first {@link #start} so its postTick fires for every disguise;
 *  idle cost is one null-returning Map lookup. */
public final class AnimatedTransitionDriver {

    private static final PerSideStateMap<InFlight> IN_FLIGHT = new PerSideStateMap<>();
    private static volatile boolean globalRegistered;

    private AnimatedTransitionDriver() {}

    private static final class InFlight {
        AnimatedTransitionSpec spec;
        int elapsed;
        /** Next stage to consider firing. Stages are sorted by atTick, so we walk forward. */
        int nextStageIndex;
    }

    /** Starts a transition for the wearer, replacing any in-flight transition. Fires stage(s)
     *  with {@code atTick == 0} and the per-tick callback at {@code elapsed = 0} immediately. */
    public static void start(LivingEntity wearer, LivingEntity disguise, AnimatedTransitionSpec spec) {
        if (wearer == null || disguise == null || spec == null) return;
        ensureGlobalRegistered();
        InFlight fresh = new InFlight();
        fresh.spec = spec;
        fresh.elapsed = 0;
        fresh.nextStageIndex = 0;
        IN_FLIGHT.side(wearer).put(wearer.getUUID(), fresh);
        fireDueStages(disguise, fresh, 0);
        if (spec.onEachTick() != null) {
            safeRun(() -> spec.onEachTick().run(disguise, 0), spec);
        }
    }

    /** Whether the wearer has an active transition on the appropriate side. */
    public static boolean isActive(LivingEntity wearer) {
        return wearer != null && IN_FLIGHT.get(wearer) != null;
    }

    /** Forcefully cancels the wearer's in-flight transition on both sides. */
    public static void cancel(LivingEntity wearer) {
        IN_FLIGHT.remove(wearer);
    }

    private static void tick(LivingEntity wearer, LivingEntity disguise) {
        InFlight inFlight = IN_FLIGHT.get(wearer);
        if (inFlight == null) return;
        inFlight.elapsed++;
        if (inFlight.spec.onEachTick() != null) {
            final InFlight ifRef = inFlight;
            safeRun(() -> ifRef.spec.onEachTick().run(disguise, ifRef.elapsed), inFlight.spec);
        }
        fireDueStages(disguise, inFlight, inFlight.elapsed);
        if (inFlight.elapsed >= inFlight.spec.durationTicks()) {
            IN_FLIGHT.side(wearer).remove(wearer.getUUID());
        }
    }

    /** Fires every stage with {@code atTick <= tick} that hasn't fired yet. */
    private static void fireDueStages(LivingEntity disguise, InFlight inFlight, int tick) {
        List<AnimatedTransitionSpec.Stage> stages = inFlight.spec.stages();
        while (inFlight.nextStageIndex < stages.size()) {
            AnimatedTransitionSpec.Stage stage = stages.get(inFlight.nextStageIndex);
            if (stage.atTick() > tick) break;
            final AnimatedTransitionSpec.Stage s = stage;
            safeRun(() -> s.action().accept(disguise), inFlight.spec);
            inFlight.nextStageIndex++;
        }
    }

    private static void safeRun(Runnable r, AnimatedTransitionSpec spec) {
        try { r.run(); }
        catch (Throwable t) {
            DisguiseLogging.rateLimited("AnimatedTransitionDriver:" + spec.hashCode(), t);
        }
    }

    private static synchronized void ensureGlobalRegistered() {
        if (globalRegistered) return;
        DisguiseBehaviorRegistry.registerGlobal(new DisguiseBehavior() {
            @Override
            public void postTick(LivingEntity wearer, LivingEntity disguise) {
                AnimatedTransitionDriver.tick(wearer, disguise);
            }
            @Override
            public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
                AnimatedTransitionDriver.cancel(wearer);
            }
        });
        globalRegistered = true;
    }
}
