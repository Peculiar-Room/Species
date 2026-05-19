package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.disguise.dsl.WearerPredicates;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;

/**
 * Gloomoth body tilt: writes {@code flightRoll} from the wearer's yaw delta in preTick (AC's tickRotation
 * sees delta=0 because aiStep zeros it). FLYING is handled by {@link com.ninni.species.server.disguise.dsl.AutoFlightSyncBehavior}.
 */
public final class GloomothBehavior implements DisguiseBehavior {

    public static final GloomothBehavior INSTANCE = new GloomothBehavior();

    private static final float TURN_THRESHOLD = 1.0F;
    private static final float ROLL_DELTA = 10.0F;
    private static final float ROLL_DECAY = 5.0F;
    private static final float ROLL_CLAMP = 60.0F;

    private static volatile boolean reflectionInited;
    private static Field flightRollField;
    private static Field prevFlightRollField;

    private GloomothBehavior() {}

    @Override
    public void preTick(LivingEntity wearer, LivingEntity disguise) {
        initReflection(disguise.getClass());
        if (flightRollField == null) return;
        if (!WearerPredicates.FLYING.test(wearer)) return;
        try {
            float currentRoll = flightRollField.getFloat(disguise);
            if (prevFlightRollField != null) {
                prevFlightRollField.setFloat(disguise, currentRoll);
            }
            float yawDelta = wearer.yRotO - wearer.getYRot();
            if (yawDelta > TURN_THRESHOLD) {
                currentRoll += ROLL_DELTA;
            } else if (yawDelta < -TURN_THRESHOLD) {
                currentRoll -= ROLL_DELTA;
            } else {
                if (currentRoll > 0) currentRoll = Math.max(currentRoll - ROLL_DECAY, 0);
                else if (currentRoll < 0) currentRoll = Math.min(currentRoll + ROLL_DECAY, 0);
            }
            flightRollField.setFloat(disguise, Mth.clamp(currentRoll, -ROLL_CLAMP, ROLL_CLAMP));
        } catch (IllegalAccessException ignored) {}
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (GloomothBehavior.class) {
            if (reflectionInited) return;
            flightRollField = ReflectionHelper.declaredFieldOfType(entityClass, "flightRoll", float.class);
            prevFlightRollField = ReflectionHelper.declaredFieldOfType(entityClass, "prevFlightRoll", float.class);
            reflectionInited = true;
        }
    }
}
