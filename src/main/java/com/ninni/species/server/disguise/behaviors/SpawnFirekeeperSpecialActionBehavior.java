package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.PerSideStateMap;
import com.ninni.species.server.disguise.DisguiseBodyRegistry;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** Spawn Firekeeper disguise (gated by {@link ActionContext#shifted()}): non-shifted runs the spin chain
 *  ANCHOR_DOWN&rarr;SPIN_LOOP&rarr;BRAKE&rarr;RECOVER (each press +1/6 glow); shift plays AWAKENING. Dormant until awakened. */
public final class SpawnFirekeeperSpecialActionBehavior implements DisguiseBehavior {

    public static final SpawnFirekeeperSpecialActionBehavior INSTANCE = new SpawnFirekeeperSpecialActionBehavior();

    public enum Mode { NONE, ANCHOR_DOWN, SPIN_LOOP, BRAKE, RECOVER,
                       AWAKENING, INVERSE_AWAKENING, HELD_AWAKENED, HELD_DORMANT }

    private static final int SPINS_TO_FULL_GLOW = 6;
    private static final float INCREMENT_PER_SPIN = 1.0F / SPINS_TO_FULL_GLOW;
    /** Glow lost per tick while idle in NONE or in SPIN_LOOP past the press-gap; {@code 1/(6*90)}
     *  ≈ one spin's glow over ~4.5s. Rapid spam bypasses decay. */
    private static final float DECAY_PER_TICK = INCREMENT_PER_SPIN / 90.0F;
    // ---- Environment glow modifiers (one dominant factor per tick — never stacked) ----
    /** Touching water — strongest dousing. */
    private static final float WATER_DECAY_MULTIPLIER = 5.0F;
    /** Exposed to a thunderstorm — between rain and water. */
    private static final float STORM_DECAY_MULTIPLIER = 3.0F;
    /** Exposed to plain rain — gentler than a storm. */
    private static final float RAIN_DECAY_MULTIPLIER = 2.5F;
    /** The Nether's ambient heat RECHARGES the glow (~15s to full); no rain reaches it anyway. */
    private static final float NETHER_GLOW_GAIN_PER_TICK = 1.0F / 300.0F;
    /** Cold biomes dissipate 25% faster. */
    private static final float COLD_BIOME_DECAY_MULTIPLIER = 1.25F;
    /** Base biome temperature below which a biome counts as cold (vanilla snow threshold). */
    private static final float COLD_BIOME_TEMP_THRESHOLD = 0.15F;
    /** Standing in open fire slowly RECHARGES the glow (~10s to full). */
    private static final float FIRE_GLOW_GAIN_PER_TICK = 1.0F / 200.0F;
    /** Standing in lava recharges rapidly (~2s) and overrides the fire gain — no stacking. */
    private static final float LAVA_GLOW_GAIN_PER_TICK = 1.0F / 40.0F;
    /** Ticks of no-press inside SPIN_LOOP before decay resumes, so rapid spam stays decay-free. */
    private static final long PRESS_GAP_DECAY_THRESHOLD = 2L;

    /** 15 ticks ≈ 0.75s — matches the {@code ANCHOR_DOWN} clip length. */
    public static final long ANCHOR_DOWN_TICKS = 15L;
    /** 6 ticks ≈ 0.3s — gap of no-press inside {@code SPIN_LOOP} before transitioning to BRAKE. */
    private static final long SPAM_WINDOW_TICKS = 6L;
    /** 13 ticks ≈ 0.667s — matches the {@code BRAKE} clip (spin + elastic hold so CATMULLROM settles). */
    private static final long BRAKE_TICKS = 13L;
    /** 16 ticks ≈ 0.8s — matches {@code RECOVER} clip length. */
    public static final long RECOVER_TICKS = 16L;
    /** Vanilla AWAKENING duration: matches {@code Firekeeper.handleEntityEvent(67)}'s timeout. */
    private static final long AWAKEN_DURATION_TICKS = 39L;
    /** Mirror of AWAKEN — Species-side INVERSE animation is also 1.95s (39 ticks). */
    private static final long INVERSE_AWAKEN_DURATION_TICKS = 39L;
    /** Wipe stale State after this many idle ticks in NONE mode. */
    private static final long IDLE_RESET_TICKS = 200L;
    /** Re-applied while a phase is active ({@code > 0}) to block vanilla setupAnimationStates' auto-stop. */
    private static final int TIMEOUT_KEEPALIVE = 39;

    private static volatile boolean reflectionInited;
    private static Field awakeningStateField;
    private static Field awakeningTimeoutField;

    private static final class State {
        Mode mode = Mode.NONE;
        long modeEnterTime;
        long lastPressTime;
        long lastTickTime;
        float glowLevel = 0.0F;
        /** Awakened (open) vs dormant (folded); shift alternates AWAKENING / INVERSE_AWAKENING on it. */
        boolean isAwakened = false;
    }

    private final PerSideStateMap<State> states = new PerSideStateMap<>();

    private SpawnFirekeeperSpecialActionBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise, ActionContext context) {
        initReflection(disguise.getClass());
        long now = wearer.level().getGameTime();
        State st = states.computeIfAbsent(wearer, State::new);
        st.lastTickTime = now;

        if (context.shifted()) {
            // Shift+keybind alternates AWAKENING (dormant→awakened) and INVERSE_AWAKENING
            // (awakened→dormant) based on the wearer's tracked state. HELD modes are stable
            // resting poses so they accept shift transitions; locked transient phases ignore.
            if (st.mode == Mode.NONE || st.mode == Mode.SPIN_LOOP
                    || st.mode == Mode.HELD_AWAKENED || st.mode == Mode.HELD_DORMANT) {
                st.mode = st.isAwakened ? Mode.INVERSE_AWAKENING : Mode.AWAKENING;
                st.modeEnterTime = now;
                restartAwakening(disguise);
            }
            return;
        }

        switch (st.mode) {
            case NONE, HELD_AWAKENED -> {
                st.mode = Mode.ANCHOR_DOWN;
                st.modeEnterTime = now;
                st.lastPressTime = now;
                // Glow is NOT reset here. A fresh disguise has no State, so getDisguiseGlowLevel
                // returns 0 (dormant rock) for the first chain; re-triggering after a chain keeps
                // the accumulated/decayed glow so it isn't wiped back to dormant.
                restartAwakening(disguise);
            }
            case ANCHOR_DOWN -> {
                // Queue the press: stamping lastPressTime arms the first SPAM_WINDOW check.
                st.lastPressTime = now;
            }
            case SPIN_LOOP -> {
                st.lastPressTime = now;
                st.glowLevel = Math.min(1.0F, st.glowLevel + INCREMENT_PER_SPIN);
            }
            case BRAKE, RECOVER, AWAKENING, INVERSE_AWAKENING, HELD_DORMANT -> {
                // Dormant ignores the non-shifted keybind — the spin chain is only available once
                // awakened. The transient phases lock until they finish.
            }
        }
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        State st = states.get(wearer);
        if (st == null) return;
        long now = wearer.level().getGameTime();

        switch (st.mode) {
            case NONE -> {
                // Slow background decay so glow doesn't sit forever at fractional values.
                updateGlow(st, wearer);
                if (now - st.lastTickTime > IDLE_RESET_TICKS) states.remove(wearer);
            }
            case ANCHOR_DOWN -> {
                setAwakeningTimeout(disguise, TIMEOUT_KEEPALIVE);
                if (now - st.modeEnterTime >= ANCHOR_DOWN_TICKS) {
                    st.mode = Mode.SPIN_LOOP;
                    st.modeEnterTime = now;
                    // lastPressTime preserved so first SPAM_WINDOW check uses the original press.
                    restartAwakening(disguise);
                }
            }
            case SPIN_LOOP -> {
                setAwakeningTimeout(disguise, TIMEOUT_KEEPALIVE);
                // Decay only if the player has paused — rapid spam (gap <= threshold) builds glow
                // without leak; pausing lets it fall.
                if (now - st.lastPressTime > PRESS_GAP_DECAY_THRESHOLD) {
                    updateGlow(st, wearer);
                }
                if (now - st.lastPressTime > SPAM_WINDOW_TICKS) {
                    st.mode = Mode.BRAKE;
                    st.modeEnterTime = now;
                    restartAwakening(disguise);
                }
            }
            case BRAKE -> {
                setAwakeningTimeout(disguise, TIMEOUT_KEEPALIVE);
                if (now - st.modeEnterTime >= BRAKE_TICKS) {
                    st.mode = Mode.RECOVER;
                    st.modeEnterTime = now;
                    restartAwakening(disguise);
                }
            }
            case RECOVER -> {
                setAwakeningTimeout(disguise, TIMEOUT_KEEPALIVE);
                if (now - st.modeEnterTime >= RECOVER_TICKS) {
                    st.mode = Mode.NONE;
                    // Glow held at whatever level the chain achieved — persists on the entity.
                    stopAwakeningState(disguise);
                }
            }
            case AWAKENING -> {
                setAwakeningTimeout(disguise, TIMEOUT_KEEPALIVE);
                if (now - st.modeEnterTime >= AWAKEN_DURATION_TICKS) {
                    // Transition to HELD_AWAKENED — keep the animation state running so the
                    // clamped-to-t=2.0 AWAKENING values hold the awakened pose indefinitely.
                    st.mode = Mode.HELD_AWAKENED;
                    st.isAwakened = true;
                }
            }
            case INVERSE_AWAKENING -> {
                setAwakeningTimeout(disguise, TIMEOUT_KEEPALIVE);
                if (now - st.modeEnterTime >= INVERSE_AWAKEN_DURATION_TICKS) {
                    // Transition to HELD_DORMANT — clamped AWAKENING_REVERSE end values hold the
                    // compressed-dormant pose indefinitely.
                    st.mode = Mode.HELD_DORMANT;
                    st.isAwakened = false;
                }
            }
            case HELD_AWAKENED, HELD_DORMANT -> {
                // Pose-hold: keep animation state alive so KeyframeAnimations clamps to the last
                // keyframe forever. Glow decays gently.
                setAwakeningTimeout(disguise, TIMEOUT_KEEPALIVE);
                updateGlow(st, wearer);
            }
        }
        st.lastTickTime = now;
    }

    @Override
    public void onCreated(LivingEntity wearer, LivingEntity disguise) {
        // Fresh equip starts DORMANT: play the dormancy fold (AWAKENING_REVERSE) into the rock
        // rather than letting Spawn's default awakening animation run as the first transition.
        initReflection(disguise.getClass());
        State st = states.computeIfAbsent(wearer, State::new);
        st.mode = Mode.INVERSE_AWAKENING;
        st.modeEnterTime = wearer.level().getGameTime();
        st.lastTickTime = st.modeEnterTime;
        st.isAwakened = false;
        restartAwakening(disguise);
    }

    @Override
    public void onBodyRebuilt(LivingEntity wearer, LivingEntity disguise) {
        // Body rebuilt for a still-active disguise (e.g. dimension change). The per-wearer State
        // survives, so resume the matching held pose with its animation clamped to the final frame
        // instead of replaying the equip fold. If no State survived, fall back to a fresh fold.
        initReflection(disguise.getClass());
        State st = states.get(wearer);
        if (st == null) { onCreated(wearer, disguise); return; }
        st.mode = st.isAwakened ? Mode.HELD_AWAKENED : Mode.HELD_DORMANT;
        st.modeEnterTime = wearer.level().getGameTime();
        st.lastTickTime = st.modeEnterTime;
        clampAwakeningToEnd(disguise);
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        states.remove(wearer);
    }

    /** Per-tick glow update under ONE dominant environmental factor (never stacked). Lava, open fire
     *  and the Nether's ambient heat RECHARGE the glow (lava > fire > Nether); water/rain/storm/cold
     *  dissipate it at {@link #DECAY_PER_TICK} times the matching multiplier. */
    private static void updateGlow(State st, LivingEntity wearer) {
        float delta;
        if (wearer.isInLava()) {
            delta = LAVA_GLOW_GAIN_PER_TICK;            // overrides fire — no stacked multipliers
        } else if (wearer.isInWater()) {
            delta = -DECAY_PER_TICK * WATER_DECAY_MULTIPLIER;
        } else if (wearer.isOnFire()) {
            delta = FIRE_GLOW_GAIN_PER_TICK;
        } else if (wearer.level().dimension() == net.minecraft.world.level.Level.NETHER) {
            delta = NETHER_GLOW_GAIN_PER_TICK;          // ambient heat recharges, even off fire/lava
        } else {
            delta = -DECAY_PER_TICK * environmentDecayMultiplier(wearer);
        }
        st.glowLevel = Math.max(0.0F, Math.min(1.0F, st.glowLevel + delta));
    }

    /** Dissipation multiplier from weather/biome — exactly one applies (no overlap). Deserts and
     *  other dry biomes fall through to 1 (the rain checks are false there). The Nether is handled
     *  in {@link #updateGlow} (it recharges, not decays). */
    private static float environmentDecayMultiplier(LivingEntity wearer) {
        var level = wearer.level();
        var pos = wearer.blockPosition();
        if (level.isThundering() && level.isRainingAt(pos)) return STORM_DECAY_MULTIPLIER;
        if (level.isRainingAt(pos)) return RAIN_DECAY_MULTIPLIER;
        if (level.getBiome(pos).value().getBaseTemperature() < COLD_BIOME_TEMP_THRESHOLD) {
            return COLD_BIOME_DECAY_MULTIPLIER;
        }
        return 1.0F;
    }

    /** frontPlane scale the model mixin should force: {@code < 0} means "leave it" (only the
     *  standing idle keeps the plate). Every other state hides it (0). */
    public static float getDesiredFrontPlaneScale(LivingEntity disguise) {
        LivingEntity wearer = DisguiseBodyRegistry.getWearer(disguise);
        if (wearer == null) return -1.0F;
        State st = readSideAgnostic(wearer);
        if (st == null) return -1.0F;
        return st.mode == Mode.NONE ? -1.0F : 0.0F;
    }

    /** Disguise glow level in {@code [0,1]} for the molten-overlay alpha override. Returns 0
     *  (not 1) when wearer or State is missing — initial dormant rock state has no glow until
     *  the chain has run and built it up. Read by {@code FirekeeperMoltenLayerMixin}. */
    public static float getDisguiseGlowLevel(LivingEntity disguise) {
        LivingEntity wearer = DisguiseBodyRegistry.getWearer(disguise);
        if (wearer == null) return 0.0F;
        State st = readSideAgnostic(wearer);
        if (st == null) return 0.0F;
        return st.glowLevel;
    }

    /** Current animation mode for the disguise, read by {@code FirekeeperModelMixin} to pick which
     *  Species animation (or pass-through original AWAKENING for {@code AWAKENING}/{@code NONE}) to
     *  substitute into Spawn's {@code animate(awakeningState, AWAKENING, ...)}. */
    public static Mode getCurrentMode(LivingEntity disguise) {
        LivingEntity wearer = DisguiseBodyRegistry.getWearer(disguise);
        if (wearer == null) return Mode.NONE;
        State st = readSideAgnostic(wearer);
        if (st == null) return Mode.NONE;
        return st.mode;
    }

    /** Side-agnostic state lookup (own side, then the other): the render thread queries from the
     *  opposite side to {@code onSpecialAction}/{@code postTick}, so a single-side read would miss it. */
    private static State readSideAgnostic(LivingEntity wearer) {
        java.util.UUID id = wearer.getUUID();
        boolean clientSide = wearer.level().isClientSide;
        State own = INSTANCE.states.side(clientSide).get(id);
        if (own != null) return own;
        return INSTANCE.states.side(!clientSide).get(id);
    }

    /** Stops then restarts the disguise's {@code awakeningAnimationState} so the substituted
     *  animation plays from {@code t=0}, and pins {@code awakeningAnimationTimeout} so vanilla
     *  {@code setupAnimationStates} doesn't auto-stop it on the next tick. */
    private static void restartAwakening(LivingEntity disguise) {
        if (awakeningStateField == null) return;
        try {
            Object o = awakeningStateField.get(disguise);
            if (o instanceof AnimationState as) {
                as.stop();
                as.start(disguise.tickCount);
            }
            if (awakeningTimeoutField != null) {
                awakeningTimeoutField.setInt(disguise, TIMEOUT_KEEPALIVE);
            }
        } catch (ReflectiveOperationException ignored) {}
    }

    /** Re-establish a held pose on a freshly rebuilt body (e.g. after a dimension change): backdate
     *  the animation state so the substituted clip clamps to its final frame instead of replaying,
     *  and pin the timeout so vanilla doesn't auto-stop it. */
    private static void clampAwakeningToEnd(LivingEntity disguise) {
        if (awakeningStateField == null) return;
        try {
            Object o = awakeningStateField.get(disguise);
            if (o instanceof AnimationState as) {
                as.stop();
                as.start(disguise.tickCount - 60);
            }
            if (awakeningTimeoutField != null) {
                awakeningTimeoutField.setInt(disguise, TIMEOUT_KEEPALIVE);
            }
        } catch (ReflectiveOperationException ignored) {}
    }

    /** Stops the animation state and zeros the timeout. */
    private static void stopAwakeningState(LivingEntity disguise) {
        if (awakeningStateField == null) return;
        try {
            Object o = awakeningStateField.get(disguise);
            if (o instanceof AnimationState as && as.isStarted()) {
                as.stop();
            }
            if (awakeningTimeoutField != null) {
                awakeningTimeoutField.setInt(disguise, 0);
            }
        } catch (ReflectiveOperationException ignored) {}
    }

    private static void setAwakeningTimeout(LivingEntity disguise, int value) {
        if (awakeningTimeoutField == null) return;
        try {
            awakeningTimeoutField.setInt(disguise, value);
        } catch (ReflectiveOperationException ignored) {}
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (SpawnFirekeeperSpecialActionBehavior.class) {
            if (reflectionInited) return;
            reflectionInited = true;
            for (String name : new String[]{"awakeningAnimationState", "awakenAnimationState", "spawnAnimationState"}) {
                try {
                    awakeningStateField = entityClass.getField(name);
                    break;
                } catch (NoSuchFieldException ignored) {}
            }
            if (awakeningStateField == null) {
                List<String> seen = new ArrayList<>();
                for (Field f : entityClass.getFields()) {
                    if (f.getType() == AnimationState.class) {
                        seen.add(f.getName());
                        String lower = f.getName().toLowerCase();
                        if (awakeningStateField == null && (lower.contains("wake") || lower.contains("spawn"))) {
                            awakeningStateField = f;
                        }
                    }
                }
                if (awakeningStateField == null) {
                    org.slf4j.LoggerFactory.getLogger(SpawnFirekeeperSpecialActionBehavior.class)
                            .warn("Firekeeper disguise: no awakening-style AnimationState field found on {}. Available: {}",
                                    entityClass.getName(), seen);
                }
            }
            try {
                awakeningTimeoutField = entityClass.getDeclaredField("awakeningAnimationTimeout");
                awakeningTimeoutField.setAccessible(true);
            } catch (NoSuchFieldException ignored) {}
        }
    }
}
