package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.AnimatedTransitionSpec;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.disguise.SpeciesAPI;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/** Ignis (Cataclysm) disguise: shift+keybind cycles {@code BOSS_PHASE} (0→1→2→0) playing the
 *  native PHASE_2/PHASE_3 transitions. Stages match the native goals (setBossPhase + shield
 *  at the same tick). Reverse (2→0) is instant. Soft-dep, reflective. */
public final class IgnisPhaseCycleBehavior implements DisguiseBehavior {

    public static final IgnisPhaseCycleBehavior INSTANCE = new IgnisPhaseCycleBehavior();

    private static volatile boolean reflectionInited;
    private static Method getBossPhaseMethod;
    private static Method setBossPhaseMethod;
    private static Method setShowShieldMethod;
    private static Method setAnimationMethod;
    private static Method setAnimationTickMethod;
    private static Object phase2Animation;
    private static Object phase3Animation;
    private static Object noAnimation;

    private IgnisPhaseCycleBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        if (!context.shifted()) return;
        initReflection(disguise.getClass());
        if (getBossPhaseMethod == null || setBossPhaseMethod == null) return;
        int current;
        try { current = (int) getBossPhaseMethod.invoke(disguise); }
        catch (ReflectiveOperationException e) { return; }

        if (current >= 2) {
            // No native reverse animation; jump back to phase 0 and restore shield visual.
            invokeBoolean(setShowShieldMethod, disguise, true);
            invokeInt(setBossPhaseMethod, disguise, 0);
            return;
        }

        AnimatedTransitionSpec spec = current == 0 ? phase2Spec() : phase3Spec();
        if (spec == null) {
            // Reflection didn't resolve the Animation object — fall back to instant phase change.
            invokeInt(setBossPhaseMethod, disguise, current + 1);
            return;
        }
        SpeciesAPI.startAnimatedTransition(wearer, disguise, spec);
    }

    /** PHASE_2 timeline (68t total, setBossPhase at t=34). */
    private static AnimatedTransitionSpec phase2Spec() {
        if (phase2Animation == null || setAnimationMethod == null) return null;
        return new AnimatedTransitionSpec(68,
                List.of(
                        new AnimatedTransitionSpec.Stage(0,  d -> invokeMethod(setAnimationMethod, d, phase2Animation)),
                        new AnimatedTransitionSpec.Stage(34, d -> invokeInt(setBossPhaseMethod, d, 1)),
                        new AnimatedTransitionSpec.Stage(68, d -> invokeMethod(setAnimationMethod, d, noAnimation))),
                (d, elapsed) -> invokeInt(setAnimationTickMethod, d, elapsed));
    }

    /** PHASE_3 timeline (120t total, setBossPhase at t=58 + shield vanish). */
    private static AnimatedTransitionSpec phase3Spec() {
        if (phase3Animation == null || setAnimationMethod == null) return null;
        return new AnimatedTransitionSpec(120,
                List.of(
                        new AnimatedTransitionSpec.Stage(0,   d -> invokeMethod(setAnimationMethod, d, phase3Animation)),
                        new AnimatedTransitionSpec.Stage(58,  d -> {
                            invokeInt(setBossPhaseMethod, d, 2);
                            invokeBoolean(setShowShieldMethod, d, false);
                        }),
                        new AnimatedTransitionSpec.Stage(120, d -> invokeMethod(setAnimationMethod, d, noAnimation))),
                (d, elapsed) -> invokeInt(setAnimationTickMethod, d, elapsed));
    }

    private static void invokeInt(Method m, LivingEntity disguise, int value) {
        if (m == null) return;
        try { m.invoke(disguise, value); } catch (ReflectiveOperationException ignored) {}
    }

    private static void invokeBoolean(Method m, LivingEntity disguise, boolean value) {
        if (m == null) return;
        try { m.invoke(disguise, value); } catch (ReflectiveOperationException ignored) {}
    }

    private static void invokeMethod(Method m, LivingEntity disguise, Object value) {
        if (m == null || value == null) return;
        try { m.invoke(disguise, value); } catch (ReflectiveOperationException ignored) {}
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (IgnisPhaseCycleBehavior.class) {
            if (reflectionInited) return;
            getBossPhaseMethod = ReflectionHelper.publicMethod(entityClass, "getBossPhase");
            setBossPhaseMethod = ReflectionHelper.publicMethod(entityClass, "setBossPhase", int.class);
            setShowShieldMethod = ReflectionHelper.publicMethod(entityClass, "setShowShield", boolean.class);
            setAnimationTickMethod = ReflectionHelper.publicMethod(entityClass, "setAnimationTick", int.class);
            try {
                Class<?> animationCls = Class.forName(
                        "com.github.L_Ender.lionfishapi.server.animation.Animation", false, entityClass.getClassLoader());
                setAnimationMethod = ReflectionHelper.publicMethod(entityClass, "setAnimation", animationCls);
                phase2Animation = readStaticField(entityClass, "PHASE_2");
                phase3Animation = readStaticField(entityClass, "PHASE_3");
                Class<?> animatedCls = Class.forName(
                        "com.github.L_Ender.lionfishapi.server.animation.IAnimatedEntity", false, entityClass.getClassLoader());
                Field noAnimField = animatedCls.getDeclaredField("NO_ANIMATION");
                noAnimation = noAnimField.get(null);
            } catch (ReflectiveOperationException ignored) {}
            reflectionInited = true;
        }
    }

    private static Object readStaticField(Class<?> c, String name) {
        try {
            Field f = c.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(null);
        } catch (ReflectiveOperationException e) { return null; }
    }
}
