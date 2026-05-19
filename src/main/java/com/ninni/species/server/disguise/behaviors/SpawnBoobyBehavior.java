package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.disguise.dsl.WearerPredicates;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Spawn Booby flap-while-flying, dispatched by motion vector: descent → glide; ascent → flap;
 *  stationary → continuous flap; level horizontal → 2s flap / 3s glide. Pins {@code stuckTicks=0}
 *  to suppress the parent's near-zero-velocity ground-pose watchdog. */
public final class SpawnBoobyBehavior implements DisguiseBehavior {

    public static final SpawnBoobyBehavior INSTANCE = new SpawnBoobyBehavior();

    private static final double ASCENT_THRESHOLD = 0.01;  // permissive: any upward Y change
    private static final double DESCENT_THRESHOLD = 0.01;
    private static final double STATIONARY_HORIZONTAL_SQ = 0.0025; // (0.05)^2
    private static final int CLIP_TICKS = 10;
    /** Ascent: faster than stationary — climbing takes more wing effort. Cadence at clip length
     *  (10t) → back-to-back beats, no idle gap. */
    private static final int ASCENT_CADENCE = 10;
    /** Stationary in air: continuous flap, slower than ascent (less effort to hover than climb). */
    private static final int STATIONARY_CADENCE = 11;
    private static final int LEVEL_FLAP_CADENCE = 12;
    private static final int FLAP_PHASE_TICKS = 40;  // 2s
    private static final int GLIDE_PHASE_TICKS = 60; // 3s

    private enum Regime { DESCENT, ASCENT, STATIONARY, LEVEL_HORIZONTAL }

    private static volatile boolean reflectionInited;
    private static Field flapStateField;
    private static Field stuckTicksField;

    private static final class State {
        Regime lastRegime;
        int phaseTick;
        boolean inFlapPhase = true;
        int lastFlapTick;
        boolean everFlapped;
        /** Cached wearer Y across ticks — {@code deltaMovement.y} reads zero in steady-state
         *  creative-fly because the player controller dampens it; raw position delta is reliable. */
        double lastWearerY;
        boolean wearerYSeen;
    }

    /** Per-side maps so the singleplayer client/server race doesn't drop flap starts. */
    private final Map<UUID, State> serverStates = new ConcurrentHashMap<>();
    private final Map<UUID, State> clientStates = new ConcurrentHashMap<>();

    private SpawnBoobyBehavior() {}

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        initReflection(disguise.getClass());
        if (flapStateField == null) return;

        UUID key = wearer.getUUID();
        Map<UUID, State> states = wearer.level().isClientSide ? clientStates : serverStates;
        if (!WearerPredicates.FLYING.test(wearer)) {
            states.remove(key);
            return;
        }

        // Suppress the FlyingWalkingAnimal watchdog flicker.
        if (stuckTicksField != null) {
            try { stuckTicksField.setInt(disguise, 0); }
            catch (IllegalAccessException ignored) {}
        }

        State st = states.computeIfAbsent(key, k -> new State());
        Vec3 motion = wearer.getDeltaMovement();
        double horizontalSq = motion.x * motion.x + motion.z * motion.z;

        // Vertical regime: use raw Y position delta (ascent in creative-fly produces a clear
        // dy each tick, while deltaMovement.y is dampened by the player controller).
        double currentY = wearer.getY();
        double dy = st.wearerYSeen ? currentY - st.lastWearerY : 0.0;
        st.lastWearerY = currentY;
        st.wearerYSeen = true;

        Regime regime;
        if (dy < -DESCENT_THRESHOLD) regime = Regime.DESCENT;
        else if (dy > ASCENT_THRESHOLD) regime = Regime.ASCENT;
        else if (horizontalSq < STATIONARY_HORIZONTAL_SQ) regime = Regime.STATIONARY;
        else regime = Regime.LEVEL_HORIZONTAL;

        // Reset transient phase counters whenever the regime changes so each bout starts clean.
        if (regime != st.lastRegime) {
            st.phaseTick = 0;
            st.inFlapPhase = true;
            st.lastRegime = regime;
        }

        switch (regime) {
            case DESCENT -> {
                // Glide pose handled by Booby's own handleAnimations (flyingAnimationState).
            }
            case ASCENT -> tryFlap(disguise, st, ASCENT_CADENCE);
            case STATIONARY -> tryFlap(disguise, st, STATIONARY_CADENCE);
            case LEVEL_HORIZONTAL -> {
                st.phaseTick++;
                int phaseLength = st.inFlapPhase ? FLAP_PHASE_TICKS : GLIDE_PHASE_TICKS;
                if (st.phaseTick >= phaseLength) {
                    st.phaseTick = 0;
                    st.inFlapPhase = !st.inFlapPhase;
                }
                if (st.inFlapPhase) tryFlap(disguise, st, LEVEL_FLAP_CADENCE);
            }
        }
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        if (wearer != null) {
            UUID id = wearer.getUUID();
            serverStates.remove(id);
            clientStates.remove(id);
        }
    }

    private void tryFlap(LivingEntity disguise, State st, int cadence) {
        boolean shouldFire = !st.everFlapped
                || (disguise.tickCount - st.lastFlapTick) >= Math.max(CLIP_TICKS, cadence);
        if (shouldFire) triggerFlap(disguise, st);
    }

    private void triggerFlap(LivingEntity disguise, State st) {
        try {
            Object value = flapStateField.get(disguise);
            if (value instanceof AnimationState anim) {
                anim.start(disguise.tickCount);
                st.lastFlapTick = disguise.tickCount;
                st.everFlapped = true;
            }
        } catch (IllegalAccessException ignored) {}
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (SpawnBoobyBehavior.class) {
            if (reflectionInited) return;
            flapStateField = ReflectionHelper.declaredFieldOfType(
                    entityClass, "flapAnimationState", AnimationState.class);
            stuckTicksField = ReflectionHelper.declaredFieldOfType(
                    entityClass, "stuckTicks", int.class);
            reflectionInited = true;
        }
    }
}
