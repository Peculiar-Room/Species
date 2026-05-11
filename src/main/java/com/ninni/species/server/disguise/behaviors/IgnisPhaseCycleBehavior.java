package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.PerSideStateMap;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Ignis (Cataclysm) disguise: shift+keybind cycles {@code BOSS_PHASE} (0→1→2→0) playing the
 *  native PHASE_2/PHASE_3 transitions. Phase write happens at the native trigger tick (34/58)
 *  so texture + shield stay in lockstep. Reverse (2→0) is instant — no native anim. */
public final class IgnisPhaseCycleBehavior implements DisguiseBehavior {

    public static final IgnisPhaseCycleBehavior INSTANCE = new IgnisPhaseCycleBehavior();

    /** PHASE_2: 68t total, setBossPhase(1) at t=34 in the native goal. */
    private static final int PHASE_2_DURATION = 68;
    private static final int PHASE_2_TRIGGER = 34;
    /** PHASE_3: 120t total, setBossPhase(2) at t=58 in the native goal. */
    private static final int PHASE_3_DURATION = 120;
    private static final int PHASE_3_TRIGGER = 58;

    private static volatile boolean reflectionInited;
    private static Method getBossPhaseMethod;
    private static Method setBossPhaseMethod;
    private static Method setShowShieldMethod;
    private static Method setAnimationMethod;
    private static Method setAnimationTickMethod;
    private static Field animationTickField;
    private static Object phase2Animation;
    private static Object phase3Animation;
    private static Object noAnimation;

    private static final class State {
        int targetPhase;
        int triggerTick;
        int durationTicks;
        int elapsed;
        boolean phaseFired;
        boolean active;
    }

    private final PerSideStateMap<State> states = new PerSideStateMap<>();

    private IgnisPhaseCycleBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                com.ninni.species.api.disguise.ActionContext context) {
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

        Object anim = current == 0 ? phase2Animation : phase3Animation;
        int trigger = current == 0 ? PHASE_2_TRIGGER : PHASE_3_TRIGGER;
        int duration = current == 0 ? PHASE_2_DURATION : PHASE_3_DURATION;
        if (anim == null || setAnimationMethod == null) {
            // Animation lookup failed — fall back to instant phase change.
            invokeInt(setBossPhaseMethod, disguise, current + 1);
            return;
        }
        try { setAnimationMethod.invoke(disguise, anim); }
        catch (ReflectiveOperationException ignored) { return; }

        State st = states.computeIfAbsent(wearer, State::new);
        st.targetPhase = current + 1;
        st.triggerTick = trigger;
        st.durationTicks = duration;
        st.elapsed = 0;
        st.phaseFired = false;
        st.active = true;
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        State st = states.get(wearer);
        if (st == null || !st.active) return;
        st.elapsed++;
        if (!st.phaseFired && st.elapsed >= st.triggerTick) {
            invokeInt(setBossPhaseMethod, disguise, st.targetPhase);
            // PHASE_3 (target=2) breaks the shield; mirror native shield-vanish.
            if (st.targetPhase == 2) invokeBoolean(setShowShieldMethod, disguise, false);
            st.phaseFired = true;
        }
        if (st.elapsed >= st.durationTicks) {
            // Clear animation; setAnimation resets tick to 0 internally.
            if (noAnimation != null && setAnimationMethod != null) {
                try { setAnimationMethod.invoke(disguise, noAnimation); }
                catch (ReflectiveOperationException ignored) {}
            }
            st.active = false;
        } else {
            // Drive the animation tick so the renderer interpolates keyframes per-frame.
            // Field write as a fallback if the setter resolution failed.
            if (setAnimationTickMethod != null) {
                invokeInt(setAnimationTickMethod, disguise, st.elapsed);
            } else if (animationTickField != null) {
                try { animationTickField.setInt(disguise, st.elapsed); }
                catch (IllegalAccessException ignored) {}
            }
        }
    }

    // Manual particle/sound emission removed — native Ignis_Entity.aiStep emits them naturally
    // once setAnimation lookup succeeds (was broken by Citadel-vs-Lionfish reflection mismatch).

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        states.remove(wearer);
    }

    private static void invokeInt(Method m, LivingEntity disguise, int value) {
        if (m == null) return;
        try { m.invoke(disguise, value); } catch (ReflectiveOperationException ignored) {}
    }

    private static void invokeBoolean(Method m, LivingEntity disguise, boolean value) {
        if (m == null) return;
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
            animationTickField = ReflectionHelper.declaredFieldOfType(entityClass, "animationTick", int.class);
            try {
                // Cataclysm uses LionfishAPI's animation framework, NOT Citadel — same class names
                // but different packages. Resolving the wrong one makes setAnimation(...) lookup
                // fail (parameter type mismatch) and the entire transition silently no-ops.
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
