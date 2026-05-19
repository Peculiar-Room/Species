package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;

/** Spawn Booby disguise: keybind plays {@code danceAnimationState} (one-shot ~40t). Native goal
 *  uses {@code broadcastEntityEvent(byte 69)} which doesn't reach the unregistered disguise
 *  body, so we call {@code start} on the AnimationState directly. */
public final class SpawnBoobyDanceBehavior implements DisguiseBehavior {

    public static final SpawnBoobyDanceBehavior INSTANCE = new SpawnBoobyDanceBehavior();

    private static volatile boolean reflectionInited;
    private static Field danceStateField;

    private SpawnBoobyDanceBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        initReflection(disguise.getClass());
        if (danceStateField == null) return;
        try {
            Object value = danceStateField.get(disguise);
            if (value instanceof AnimationState anim) anim.start(disguise.tickCount);
        } catch (IllegalAccessException ignored) {}
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (SpawnBoobyDanceBehavior.class) {
            if (reflectionInited) return;
            danceStateField = ReflectionHelper.declaredFieldOfType(
                    entityClass, "danceAnimationState", AnimationState.class);
            reflectionInited = true;
        }
    }
}
