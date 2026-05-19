package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;

/**
 * Spawn Dodo disguise: keybind plays {@code peckAnimationState} (byte 71 in native — the
 * "scratching/pecking the ground for food" gesture). One-shot.
 */
public final class SpawnDodoPeckBehavior implements DisguiseBehavior {

    public static final SpawnDodoPeckBehavior INSTANCE = new SpawnDodoPeckBehavior();

    private static volatile boolean reflectionInited;
    private static Field peckStateField;

    private SpawnDodoPeckBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        initReflection(disguise.getClass());
        if (peckStateField == null) return;
        try {
            Object value = peckStateField.get(disguise);
            if (value instanceof AnimationState anim) anim.start(disguise.tickCount);
        } catch (IllegalAccessException ignored) {}
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (SpawnDodoPeckBehavior.class) {
            if (reflectionInited) return;
            peckStateField = ReflectionHelper.declaredFieldOfType(
                    entityClass, "peckAnimationState", AnimationState.class);
            reflectionInited = true;
        }
    }
}
