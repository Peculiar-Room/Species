package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Two AM Triops fixes: restore TriopsScale=1 (mask NBT lacks the key, resolves to 0F and collapses
 * the model) and pin onLandProgress=0 (otherwise body rotates -180° around Z on land).
 */
public final class TriopsBehavior implements DisguiseBehavior {

    public static final TriopsBehavior INSTANCE = new TriopsBehavior();

    private volatile boolean reflectionInited;
    private Field onLandProgressField;
    private Field prevOnLandProgressField;
    private Method getTriopsScale;
    private Method setTriopsScale;

    private TriopsBehavior() {}

    @Override
    public void onCreated(LivingEntity wearer, LivingEntity disguise) {
        if (disguise == null) return;
        initReflection(disguise.getClass());
        if (getTriopsScale == null || setTriopsScale == null) return;
        try {
            float current = (float) getTriopsScale.invoke(disguise);
            if (current <= 0.001F) setTriopsScale.invoke(disguise, 1.0F);
        } catch (ReflectiveOperationException ignored) {}
    }

    /** postTick (not preTick): EntityTriops.tick saves prev=current then increments onLandProgress. */
    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        if (disguise == null) return;
        initReflection(disguise.getClass());
        try {
            if (onLandProgressField != null) onLandProgressField.setFloat(disguise, 0.0F);
            if (prevOnLandProgressField != null) prevOnLandProgressField.setFloat(disguise, 0.0F);
        } catch (IllegalAccessException ignored) {}
    }

    private void initReflection(Class<?> clazz) {
        if (reflectionInited) return;
        synchronized (this) {
            if (reflectionInited) return;
            onLandProgressField = ReflectionHelper.declaredFieldOfType(clazz, "onLandProgress", float.class);
            prevOnLandProgressField = ReflectionHelper.declaredFieldOfType(clazz, "prevOnLandProgress", float.class);
            getTriopsScale = ReflectionHelper.publicMethod(clazz, "getTriopsScale");
            setTriopsScale = ReflectionHelper.publicMethod(clazz, "setTriopsScale", float.class);
            reflectionInited = true;
        }
    }
}
