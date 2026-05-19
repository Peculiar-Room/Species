package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** AM Seal idle/movement separation. Pins {@code swimAngle} to zero (entity-side decay is
 *  server-gated and unreliable on a synthetic body) and forces basking/digging off while moving,
 *  otherwise the idle pose blends on top of locomotion (visible as a tail kink). */
public final class AmSealBehavior implements DisguiseBehavior {

    public static final AmSealBehavior INSTANCE = new AmSealBehavior();

    /** Wearer walkAnimation speed below which the wearer is considered "stationary". */
    private static final float MOVEMENT_EPSILON = 0.05F;

    private static volatile boolean reflectionInited;
    private static Method setSwimAngleMethod;
    private static Field prevSwimAngleField;
    private static Method setBaskingMethod;
    private static Method setDiggingMethod;

    private AmSealBehavior() {}

    @Override
    public void preTick(LivingEntity wearer, LivingEntity disguise) {
        initReflection(disguise.getClass());

        // Pin swimAngle to zero — entity-side decay is server-gated and unreliable here.
        if (setSwimAngleMethod != null) {
            try { setSwimAngleMethod.invoke(disguise, 0F); }
            catch (ReflectiveOperationException ignored) {}
        }
        if (prevSwimAngleField != null) {
            try { prevSwimAngleField.setFloat(disguise, 0F); }
            catch (IllegalAccessException ignored) {}
        }

        // While walking, force basking/digging off so locomotion plays alone. When stationary
        // we leave them — the seal's aiStep can opt back into basking on its own roll.
        if (wearer.walkAnimation.speed() > MOVEMENT_EPSILON) {
            if (setBaskingMethod != null) {
                try { setBaskingMethod.invoke(disguise, false); }
                catch (ReflectiveOperationException ignored) {}
            }
            if (setDiggingMethod != null) {
                try { setDiggingMethod.invoke(disguise, false); }
                catch (ReflectiveOperationException ignored) {}
            }
        }
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (AmSealBehavior.class) {
            if (reflectionInited) return;
            setSwimAngleMethod = ReflectionHelper.publicMethod(entityClass, "setSwimAngle", float.class);
            prevSwimAngleField = ReflectionHelper.declaredFieldOfType(entityClass, "prevSwimAngle", float.class);
            setBaskingMethod = ReflectionHelper.publicMethod(entityClass, "setBasking", boolean.class);
            setDiggingMethod = ReflectionHelper.publicMethod(entityClass, "setDigging", boolean.class);
            reflectionInited = true;
        }
    }
}
