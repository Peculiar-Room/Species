package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.disguise.dsl.CompositeDisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;

/**
 * Hippogryph: reflective {@code airBorneCounter}=0 reset in postTick when grounded (the model uses it as a
 * fallback flight-pose trigger past 50), composed with {@link StraightenViaBridgeBehavior} for inventory.
 */
public final class HippogryphBehavior {

    private static volatile boolean reflectionInited;
    private static Field airBorneCounterField;

    public static final DisguiseBehavior INSTANCE = CompositeDisguiseBehavior.of(
            new DisguiseBehavior() {
                @Override
                public void postTick(LivingEntity wearer, LivingEntity disguise) {
                    if (!wearer.onGround()) return;
                    initReflection(disguise.getClass());
                    if (airBorneCounterField == null) return;
                    try {
                        airBorneCounterField.setInt(disguise, 0);
                    } catch (IllegalAccessException ignored) {}
                }
            },
            StraightenViaBridgeBehavior.INSTANCE);

    private HippogryphBehavior() {}

    private static void initReflection(Class<?> hippoClass) {
        if (reflectionInited) return;
        synchronized (HippogryphBehavior.class) {
            if (reflectionInited) return;
            airBorneCounterField = ReflectionHelper.declaredField(hippoClass, "airBorneCounter");
            reflectionInited = true;
        }
    }
}
