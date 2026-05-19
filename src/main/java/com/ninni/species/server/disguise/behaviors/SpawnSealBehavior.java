package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Spawn Seal disguise: clears NBT-inherited LAY/SURF flags, drives the BOUNCE clip with a
 *  sinusoidal {@code renderYOffset} hop (the entity's Y impulse can't apply to a disguise),
 *  and weights idle clip selection so each cycle restarts cleanly. Per-side state map. */
public final class SpawnSealBehavior implements DisguiseBehavior {

    public static final SpawnSealBehavior INSTANCE = new SpawnSealBehavior();

    private static final float MOVEMENT_EPSILON = 0.05F;
    /** BOUNCE clip is 15 ticks. Cadence below this resets the keyframe mid-clip → tremor. */
    private static final int BOUNCE_CLIP_TICKS = 15;
    private static final int BOUNCE_CADENCE_FAST = 15;
    private static final int BOUNCE_CADENCE_SLOW = 25;
    private static final float SPEED_FAST = 0.5F;
    /** Peak height of the bounce-hop visual (blocks) at full walk speed; native seal hops
     *  with vy=0.3 → ~0.56-block arc, but its body is small so we render a slightly damped
     *  hop scaled by the wearer's actual gait. */
    private static final float BOUNCE_HOP_HEIGHT_MAX = 0.45F;
    /** Wearer walkAnimation.speed() at which the hop saturates to {@link #BOUNCE_HOP_HEIGHT_MAX}.
     *  Player normal walk ≈ 0.42; sneaking ≈ 0.13 — sneaking produces a much shorter hop. */
    private static final float HOP_SPEED_FULL = 0.42F;
    /** Floor on hop height when the wearer is moving at all — keeps the visual present even at
     *  near-stationary creep. */
    private static final float BOUNCE_HOP_HEIGHT_MIN = 0.05F;
    /** After this many stationary ticks with no idle, pick one. */
    private static final int IDLE_TRIGGER_DELAY_TICKS = 40;
    /** Native per-animation cooldowns from {@code Seal.getAnimationCooldown(byte)} (lines 657-666):
     *  HEAD_BOB 800, LOOKING_AROUND 1200, BANANA 4800, SPIN 40 — each plus random(1200). Scaled
     *  down by 4× for disguise responsiveness while preserving the relative rarity. */
    private static final int[] IDLE_COOLDOWN_BASE = {200, 300, 1200, 10};   // /4 of native
    private static final int[] IDLE_COOLDOWN_RANDOM = {300, 300, 300, 300}; // /4 of native +random(1200)

    // Persistent lay-pose variants synced via setLaying(int): 1=flat, 2=side, 3=belly-up.
    // Override all other animations when active. Cleared on equip and every tick.
    public static final int LAY_VARIANT_NONE = -1;
    public static final int LAY_VARIANT_FLAT = 1;
    public static final int LAY_VARIANT_SIDE = 2;
    public static final int LAY_VARIANT_BELLY_UP = 3;
    /** Weighted picks. headBob/look common, banana rare, spin occasional. */
    private static final float[] IDLE_WEIGHTS = {0.45F, 0.45F, 0.05F, 0.05F};
    /** Clip durations in ticks: headBob 1.5s, lookingAround 2.0s, banana 5.0s, spin 1.5s. */
    private static final int[] IDLE_DURATIONS = {30, 40, 100, 30};

    private static final String[] IDLE_FIELDS = {
            "headBobAnimationState",
            "lookingAroundAnimationState",
            "bananaAnimationState",
            "spinAnimationState"
    };

    private static volatile boolean reflectionInited;
    private static Field[] idleStateFields;
    private static Field bounceStateField;
    private static Method setLayingMethod;
    private static Method setSurfingMethod;

    private static final class State {
        int lastBounceStartTick;
        /** walkAnimation.speed() snapshot at the moment the bounce was triggered, used to
         *  scale the visual hop height for that clip. */
        float bounceLaunchSpeed;
        boolean bounceEverStarted;
        int stationaryTicks;
        /** Index into IDLE_FIELDS for the currently active idle, -1 if none. */
        int currentIdleIdx = -1;
        int idleEndTick;
        int nextIdleAllowedTick;
        /** Active LAY pose triggered by the special-action keybind ({@link #LAY_VARIANT_SIDE},
         *  {@link #LAY_VARIANT_BELLY_UP}), or {@link #LAY_VARIANT_NONE} for upright. Auto-clears
         *  when the wearer starts moving. */
        int layOverride = LAY_VARIANT_NONE;
    }

    /** Per-side state. */
    private final Map<UUID, State> serverStates = new ConcurrentHashMap<>();
    private final Map<UUID, State> clientStates = new ConcurrentHashMap<>();

    private SpawnSealBehavior() {}

    @Override
    public void onCreated(LivingEntity wearer, LivingEntity disguise) {
        initReflection(disguise.getClass());
        // Equip-time clear: no override yet, force LAY_VARIANT_NONE.
        clearLockingStates(disguise, LAY_VARIANT_NONE);
        if (idleStateFields != null) {
            for (Field f : idleStateFields) stopState(f, disguise);
        }
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        initReflection(disguise.getClass());
        if (bounceStateField == null || idleStateFields == null) return;

        Map<UUID, State> states = sideStates(wearer.level().isClientSide);
        State ws = states.computeIfAbsent(wearer.getUUID(), k -> new State());

        // Pin laying/surfing every tick — but respect the active special-action override.
        // Walking auto-clears the override (handled in the moving branch below).
        clearLockingStates(disguise, ws.layOverride);

        AnimationState bounce = readState(bounceStateField, disguise);

        if (disguise.isInWater() || disguise.isInWaterOrBubble()) {
            if (bounce != null && bounce.isStarted()) bounce.stop();
            stopAllIdles(disguise, ws);
            ws.bounceEverStarted = false;
            ws.stationaryTicks = 0;
            ws.layOverride = LAY_VARIANT_NONE;
            return;
        }

        boolean moving = wearer.walkAnimation.speed() > MOVEMENT_EPSILON;

        if (moving) {
            stopAllIdles(disguise, ws);
            ws.stationaryTicks = 0;
            ws.layOverride = LAY_VARIANT_NONE; // walking off the special pose
            int cadence = (int) Mth.clamp(
                    BOUNCE_CADENCE_SLOW - wearer.walkAnimation.speed() / SPEED_FAST
                            * (BOUNCE_CADENCE_SLOW - BOUNCE_CADENCE_FAST),
                    BOUNCE_CADENCE_FAST, BOUNCE_CADENCE_SLOW);
            int requiredGap = Math.max(BOUNCE_CLIP_TICKS, cadence);
            boolean shouldFire = !ws.bounceEverStarted
                    || (disguise.tickCount - ws.lastBounceStartTick) >= requiredGap;
            if (bounce != null && shouldFire) {
                bounce.start(disguise.tickCount);
                ws.lastBounceStartTick = disguise.tickCount;
                ws.bounceEverStarted = true;
                ws.bounceLaunchSpeed = wearer.walkAnimation.speed();
            }
            return;
        }

        // Stationary path.
        if (bounce != null && bounce.isStarted()) bounce.stop();
        ws.bounceEverStarted = false;
        ws.stationaryTicks++;

        // Stop the active idle when its clip duration is up so the next pick can fire.
        // Per-animation cooldown after stop — matches native Seal's IdleAnimationGoal pattern
        // where each animation type has its own gate before any idle can fire again.
        if (ws.currentIdleIdx >= 0 && disguise.tickCount >= ws.idleEndTick) {
            int finishedIdx = ws.currentIdleIdx;
            stopState(idleStateFields[finishedIdx], disguise);
            ws.currentIdleIdx = -1;
            int cooldown = IDLE_COOLDOWN_BASE[finishedIdx]
                    + wearer.getRandom().nextInt(IDLE_COOLDOWN_RANDOM[finishedIdx]);
            ws.nextIdleAllowedTick = disguise.tickCount + cooldown;
        }

        // Pick a new idle once the trigger delay has passed and no idle is currently running.
        boolean delayPassed = ws.stationaryTicks >= IDLE_TRIGGER_DELAY_TICKS;
        boolean cooldownPassed = disguise.tickCount >= ws.nextIdleAllowedTick;
        if (ws.currentIdleIdx < 0 && delayPassed && cooldownPassed) {
            int picked = pickWeightedIdle(wearer);
            if (picked >= 0) {
                AnimationState s = readState(idleStateFields[picked], disguise);
                if (s != null) {
                    s.start(disguise.tickCount);
                    ws.currentIdleIdx = picked;
                    ws.idleEndTick = disguise.tickCount + IDLE_DURATIONS[picked];
                }
            }
        }
    }

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        // Toggle: if currently laying via override, stand up; otherwise pick LAY_SIDE or
        // LAY_BELLY_UP at 50/50. Auto-clears when the wearer starts moving (see postTick).
        initReflection(disguise.getClass());
        if (setLayingMethod == null) return;
        State ws = sideStates(wearer.level().isClientSide)
                .computeIfAbsent(wearer.getUUID(), k -> new State());
        if (ws.layOverride != LAY_VARIANT_NONE) {
            ws.layOverride = LAY_VARIANT_NONE;
        } else {
            ws.layOverride = wearer.getRandom().nextBoolean() ? LAY_VARIANT_SIDE : LAY_VARIANT_BELLY_UP;
        }
        try { setLayingMethod.invoke(disguise, ws.layOverride); }
        catch (ReflectiveOperationException ignored) {}
    }

    @Override
    public float renderYOffset(LivingEntity wearer, LivingEntity disguise, float partialTick) {
        // Bounce-hop visual: a sin arch over the BOUNCE clip duration, amplitude scaled by the
        // wearer's gait speed at launch (sneaking → small hop, walking → full hop).
        State ws = clientStates.get(wearer.getUUID());
        if (ws == null || !ws.bounceEverStarted) return 0F;
        float elapsed = (disguise.tickCount + partialTick) - ws.lastBounceStartTick;
        if (elapsed < 0F || elapsed >= BOUNCE_CLIP_TICKS) return 0F;
        float t = elapsed / BOUNCE_CLIP_TICKS;
        float speedFactor = Mth.clamp(ws.bounceLaunchSpeed / HOP_SPEED_FULL, 0F, 1F);
        float height = Mth.lerp(speedFactor, BOUNCE_HOP_HEIGHT_MIN, BOUNCE_HOP_HEIGHT_MAX);
        return Mth.sin(t * (float) Math.PI) * height;
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        if (wearer != null) {
            UUID id = wearer.getUUID();
            serverStates.remove(id);
            clientStates.remove(id);
        }
    }

    private Map<UUID, State> sideStates(boolean clientSide) {
        return clientSide ? clientStates : serverStates;
    }

    private static int pickWeightedIdle(LivingEntity wearer) {
        float roll = wearer.getRandom().nextFloat();
        float cumulative = 0F;
        for (int i = 0; i < IDLE_WEIGHTS.length && i < idleStateFields.length; i++) {
            cumulative += IDLE_WEIGHTS[i];
            if (roll < cumulative) return i;
        }
        return -1;
    }

    private static void stopAllIdles(LivingEntity disguise, State ws) {
        for (Field f : idleStateFields) stopState(f, disguise);
        ws.currentIdleIdx = -1;
    }

    private static void clearLockingStates(LivingEntity disguise, int layOverride) {
        if (setLayingMethod != null) {
            try { setLayingMethod.invoke(disguise, layOverride); }
            catch (ReflectiveOperationException ignored) {}
        }
        if (setSurfingMethod != null) {
            try { setSurfingMethod.invoke(disguise, false); }
            catch (ReflectiveOperationException ignored) {}
        }
    }

    private static AnimationState readState(Field field, LivingEntity disguise) {
        if (field == null) return null;
        try {
            Object v = field.get(disguise);
            return (v instanceof AnimationState s) ? s : null;
        } catch (IllegalAccessException e) { return null; }
    }

    private static void stopState(Field field, LivingEntity disguise) {
        AnimationState s = readState(field, disguise);
        if (s != null) s.stop();
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (SpawnSealBehavior.class) {
            if (reflectionInited) return;
            Field[] fields = new Field[IDLE_FIELDS.length];
            for (int i = 0; i < IDLE_FIELDS.length; i++) {
                fields[i] = ReflectionHelper.declaredFieldOfType(entityClass, IDLE_FIELDS[i], AnimationState.class);
            }
            idleStateFields = fields;
            bounceStateField = ReflectionHelper.declaredFieldOfType(entityClass, "bounceAnimationState", AnimationState.class);
            setLayingMethod = ReflectionHelper.publicMethod(entityClass, "setLaying", int.class);
            setSurfingMethod = ReflectionHelper.publicMethod(entityClass, "setSurfing", boolean.class);
            reflectionInited = true;
        }
    }
}
