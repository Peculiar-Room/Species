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

/** Drives smoothed roll/pitch on {@code IFChainBuffer}-backed IaF flyers (dragons, Amphithere)
 *  from wearer yaw — replaces the native curve that's mostly inert under disguise gates. */
public final class IafFlightRollOverrideBehavior implements DisguiseBehavior {

    public static final IafFlightRollOverrideBehavior INSTANCE = new IafFlightRollOverrideBehavior();

    private static final float YAW_DELTA_THRESHOLD = 0.1F;
    private static final float ROLL_GAIN = 1.0F;
    private static final float ROLL_DECAY = 5.0F;
    private static final float ROLL_CLAMP = 60.0F;

    private static final Map<Class<?>, CachedFields> ENTITY_FIELD_CACHE = new ConcurrentHashMap<>();
    private static volatile boolean bufferFieldsInited;
    private static Field yawVariationField;
    private static Field prevYawVariationField;
    private static Field pitchVariationField;
    private static Field prevPitchVariationField;

    private static final class CachedFields {
        final Field rollBuffer;
        final Field pitchBuffer;
        /** Amphithere-only render gate; pinned to 0 so flight entry shows roll without ramp-down. */
        final Field groundProgress;
        CachedFields(Class<?> entityClass) {
            this.rollBuffer = ReflectionHelper.declaredField(entityClass, "roll_buffer");
            this.pitchBuffer = ReflectionHelper.declaredField(entityClass, "pitch_buffer");
            this.groundProgress = ReflectionHelper.declaredFieldOfType(entityClass, "groundProgress", float.class);
        }
    }

    private static final class Targets {
        float roll;
        float pitch;
        float lastWearerYaw;
        boolean wearerYawSeen;
    }

    private final Map<UUID, Targets> serverTargets = new ConcurrentHashMap<>();
    private final Map<UUID, Targets> clientTargets = new ConcurrentHashMap<>();

    private IafFlightRollOverrideBehavior() {}

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        CachedFields fields = ENTITY_FIELD_CACHE.computeIfAbsent(disguise.getClass(), CachedFields::new);
        if (fields.rollBuffer == null && fields.pitchBuffer == null) return;
        ensureBufferFields(disguise.getClass().getClassLoader());

        UUID key = wearer.getUUID();
        Map<UUID, Targets> targets = wearer.level().isClientSide ? clientTargets : serverTargets;

        if (!WearerPredicates.FLYING.test(wearer)) {
            targets.remove(key);
            return;
        }

        Targets prev = targets.computeIfAbsent(key, k -> new Targets());
        float prevRoll = prev.roll;
        float prevPitch = prev.pitch;

        // Cache wearer yaw across ticks — aiStep zeroes the entity's own delta before postTick.
        float currentYaw = wearer.getYRot();
        float yawDelta = prev.wearerYawSeen ? Mth.wrapDegrees(prev.lastWearerYaw - currentYaw) : 0F;
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

        float targetPitch = wearer.getXRot();

        // Guard via getDeclaringClass().isInstance — roll_buffer/pitch_buffer can be IFChainBuffer
        // OR Citadel ChainBuffer depending on entity; mismatched setFloat throws IllegalArgumentException.
        try {
            if (fields.rollBuffer != null && yawVariationField != null) {
                Object rollBuf = fields.rollBuffer.get(disguise);
                if (rollBuf != null && yawVariationField.getDeclaringClass().isInstance(rollBuf)) {
                    yawVariationField.setFloat(rollBuf, targetRoll);
                    if (prevYawVariationField != null
                            && prevYawVariationField.getDeclaringClass().isInstance(rollBuf)) {
                        prevYawVariationField.setFloat(rollBuf, prevRoll);
                    }
                }
            }
            if (fields.pitchBuffer != null && pitchVariationField != null) {
                Object pitchBuf = fields.pitchBuffer.get(disguise);
                if (pitchBuf != null && pitchVariationField.getDeclaringClass().isInstance(pitchBuf)) {
                    pitchVariationField.setFloat(pitchBuf, targetPitch);
                    if (prevPitchVariationField != null
                            && prevPitchVariationField.getDeclaringClass().isInstance(pitchBuf)) {
                        prevPitchVariationField.setFloat(pitchBuf, prevPitch);
                    }
                }
            }
            if (fields.groundProgress != null) fields.groundProgress.setFloat(disguise, 0F);
        } catch (IllegalAccessException | IllegalArgumentException ignored) {}

        prev.roll = targetRoll;
        prev.pitch = targetPitch;
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        if (wearer != null) {
            UUID id = wearer.getUUID();
            serverTargets.remove(id);
            clientTargets.remove(id);
        }
    }

    private static void ensureBufferFields(ClassLoader cl) {
        if (bufferFieldsInited) return;
        synchronized (IafFlightRollOverrideBehavior.class) {
            if (bufferFieldsInited) return;
            Class<?> bufferClass = findBufferClass(cl);
            if (bufferClass != null) {
                yawVariationField = ReflectionHelper.declaredField(bufferClass, "yawVariation");
                prevYawVariationField = ReflectionHelper.declaredField(bufferClass, "prevYawVariation");
                pitchVariationField = ReflectionHelper.declaredField(bufferClass, "pitchVariation");
                prevPitchVariationField = ReflectionHelper.declaredField(bufferClass, "prevPitchVariation");
            }
            bufferFieldsInited = true;
        }
    }

    private static Class<?> findBufferClass(ClassLoader cl) {
        for (String name : new String[]{
                "com.iafenvoy.iceandfire.render.model.IFChainBuffer",          // IaF-CE
                "com.github.alexthe666.iceandfire.client.model.IFChainBuffer", // stock IaF
                "com.iafenvoy.uranus.client.model.tools.ChainBuffer",          // Uranus
                "com.github.alexthe666.citadel.client.model.ChainBuffer"       // stock Citadel
        }) {
            try { return Class.forName(name, false, cl); }
            catch (ClassNotFoundException ignored) {}
        }
        return null;
    }
}
