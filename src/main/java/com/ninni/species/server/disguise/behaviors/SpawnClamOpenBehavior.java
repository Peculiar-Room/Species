package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Spawn Clam disguise: keybind plays the open sequence (giant variant only — others render
 *  statically). Drives {@code openAnimationState} + {@code setOpenTicks} mirroring the native
 *  byte-8 + 120t countdown. */
public final class SpawnClamOpenBehavior implements DisguiseBehavior {

    public static final SpawnClamOpenBehavior INSTANCE = new SpawnClamOpenBehavior();

    private static final int OPEN_TICKS = 120;

    private static volatile boolean reflectionInited;
    private static Field openStateField;
    private static Method setOpenTicksMethod;
    private static Method getBaseColorMethod;
    private static Method baseColorBaseMethod;
    private static Object giantClamEnum;

    private SpawnClamOpenBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        initReflection(disguise.getClass());
        if (!isGiantClam(disguise)) return;

        if (openStateField != null) {
            try {
                Object value = openStateField.get(disguise);
                if (value instanceof AnimationState anim) anim.start(disguise.tickCount);
            } catch (IllegalAccessException ignored) {}
        }
        if (setOpenTicksMethod != null) {
            try { setOpenTicksMethod.invoke(disguise, OPEN_TICKS); }
            catch (ReflectiveOperationException ignored) {}
        }
    }

    private static boolean isGiantClam(LivingEntity disguise) {
        if (getBaseColorMethod == null || baseColorBaseMethod == null || giantClamEnum == null) return false;
        try {
            Object baseColor = getBaseColorMethod.invoke(disguise);
            if (baseColor == null) return false;
            Object base = baseColorBaseMethod.invoke(baseColor);
            return base == giantClamEnum;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (SpawnClamOpenBehavior.class) {
            if (reflectionInited) return;
            openStateField = ReflectionHelper.declaredFieldOfType(
                    entityClass, "openAnimationState", AnimationState.class);
            setOpenTicksMethod = ReflectionHelper.publicMethod(entityClass, "setOpenTicks", int.class);
            getBaseColorMethod = ReflectionHelper.publicMethod(entityClass, "getBaseColor");
            try {
                Class<?> baseColorClass = Class.forName("com.ninni.spawn.server.entity.base.ClamVariant$BaseColor");
                baseColorBaseMethod = baseColorClass.getMethod("base");
                Class<?> baseClass = Class.forName("com.ninni.spawn.server.entity.base.ClamVariant$Base");
                Field giantField = baseClass.getField("GIANT_CLAM");
                giantClamEnum = giantField.get(null);
            } catch (Throwable ignored) {}
            reflectionInited = true;
        }
    }
}
