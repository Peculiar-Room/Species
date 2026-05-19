package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.disguise.dsl.WearerPredicates;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Drives an AC flier's body pitch and roll from wearer state — native math zeroes both
 *  axes for non-AI-rotated disguise bodies, so both stay flat without this override. */
public final class FlightPitchOverrideBehavior implements DisguiseBehavior {

    /** Yaw delta below this counts as no input (mouse-jitter floor). */
    private static final float YAW_DELTA_THRESHOLD = 0.1F;
    /** Degrees of roll increment per degree of wearer yaw delta. */
    private static final float ROLL_GAIN = 1.0F;
    private static final float ROLL_DECAY = 5.0F;
    private static final float ROLL_CLAMP = 60.0F;

    private final float pitchSign;

    private volatile boolean reflectionInited;
    private Field flightPitchField;
    private Field prevFlightPitchField;
    private Field flightRollField;
    private Field prevFlightRollField;

    private static final class Targets {
        float pitch;
        float roll;
        /** Cached wearer yaw to recover per-tick delta — aiStep zeroes it before postTick reads. */
        float lastWearerYaw;
        boolean wearerYawSeen;
    }

    /** Per-side caches — singleplayer shares the JVM, and a single map causes both sides to
     *  trample each other's {@code lastWearerYaw}: whichever ticks first sets it to current,
     *  the other reads zero delta and roll never grows. */
    private final Map<UUID, Targets> serverTargets = new ConcurrentHashMap<>();
    private final Map<UUID, Targets> clientTargets = new ConcurrentHashMap<>();

    /**
     * @param pitchSign +1 or -1 to match the model's body-axis convention. Roll uses the
     *                  native sign always (yaw delta drives directly).
     */
    public FlightPitchOverrideBehavior(float pitchSign) {
        this.pitchSign = pitchSign;
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        initReflection(disguise.getClass());
        UUID key = wearer.getUUID();
        Map<UUID, Targets> targets = wearer.level().isClientSide ? clientTargets : serverTargets;

        if (!WearerPredicates.FLYING.test(wearer)) {
            targets.remove(key);
            return;
        }

        Targets prev = targets.computeIfAbsent(key, k -> new Targets());
        float prevPitch = prev.pitch;
        float prevRoll = prev.roll;

        // Pitch: 1:1 from camera xRot, no smoothing — paired-prev keeps the lerp clean.
        float targetPitch = pitchSign * wearer.getXRot();

        // Roll: native AC math reads {@code yRotO - yRot} on the entity. We can't read that on
        // the wearer here because LivingEntity.aiStep already set yRotO = yRot (delta = 0) by
        // the time postTick runs. Cache the wearer's yaw across ticks ourselves to recover the
        // real per-tick yaw change.
        float currentYaw = wearer.getYRot();
        float yawDelta = prev.wearerYawSeen ? (prev.lastWearerYaw - currentYaw) : 0F;
        // Wrap to [-180, 180] so wrap-around (e.g. 179 → -179) doesn't register as a 358° spin.
        yawDelta = Mth.wrapDegrees(yawDelta);
        prev.lastWearerYaw = currentYaw;
        prev.wearerYawSeen = true;

        float targetRoll = prevRoll;
        float absDelta = Math.abs(yawDelta);
        if (absDelta > YAW_DELTA_THRESHOLD) {
            float increment = absDelta * ROLL_GAIN;
            targetRoll += yawDelta > 0 ? increment : -increment;
        } else if (targetRoll > 0) {
            targetRoll = Math.max(targetRoll - ROLL_DECAY, 0);
        } else if (targetRoll < 0) {
            targetRoll = Math.min(targetRoll + ROLL_DECAY, 0);
        }
        targetRoll = Mth.clamp(targetRoll, -ROLL_CLAMP, ROLL_CLAMP);

        try {
            if (prevFlightPitchField != null) prevFlightPitchField.setFloat(disguise, prevPitch);
            if (flightPitchField != null) flightPitchField.setFloat(disguise, targetPitch);
            if (prevFlightRollField != null) prevFlightRollField.setFloat(disguise, prevRoll);
            if (flightRollField != null) flightRollField.setFloat(disguise, targetRoll);
        } catch (IllegalAccessException ignored) {}

        prev.pitch = targetPitch;
        prev.roll = targetRoll;
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        if (wearer != null) {
            UUID id = wearer.getUUID();
            serverTargets.remove(id);
            clientTargets.remove(id);
        }
    }

    private void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (this) {
            if (reflectionInited) return;
            flightPitchField = ReflectionHelper.declaredFieldOfType(entityClass, "flightPitch", float.class);
            prevFlightPitchField = ReflectionHelper.declaredFieldOfType(entityClass, "prevFlightPitch", float.class);
            flightRollField = ReflectionHelper.declaredFieldOfType(entityClass, "flightRoll", float.class);
            prevFlightRollField = ReflectionHelper.declaredFieldOfType(entityClass, "prevFlightRoll", float.class);
            reflectionInited = true;
        }
    }
}
