package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.disguise.util.DisguiseSounds;
import com.ninni.species.api.util.PerSideStateMap;
import com.ninni.species.server.entity.mob.update_2.Mammutilation;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

import java.lang.reflect.Field;

/** Mammutilation disguise: keybind plays the moon howl. Native {@code aiStep} reverts only
 *  server-side, so we track the press per-side and stop the animation + pose in
 *  {@code postTick} once the duration elapses. */
public final class MammutilationSpecialActionBehavior implements DisguiseBehavior {

    public static final MammutilationSpecialActionBehavior INSTANCE = new MammutilationSpecialActionBehavior();

    /** Native HowlAtMoonGoal value. */
    private static final int HOWL_DURATION_TICKS = 4 * 20;

    private static volatile boolean reflectionInited;
    private static Field howlTimerField;
    private static Field howlAnimationStateField;

    private static final class State {
        int firedTick;
        boolean active;
    }

    private final PerSideStateMap<State> states = new PerSideStateMap<>();

    private MammutilationSpecialActionBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        if (!(disguise instanceof Mammutilation mammutilation)) return;
        initReflection(disguise.getClass());

        // Force STANDING -> HOWLING so onSyncedDataUpdated dispatches even if the client pose is
        // still stuck on HOWLING from a previous press.
        if (mammutilation.getPose() == SpeciesPose.HOWLING.get()) {
            mammutilation.setPose(Pose.STANDING);
        }
        if (howlTimerField != null) {
            try { howlTimerField.setInt(mammutilation, HOWL_DURATION_TICKS); }
            catch (IllegalAccessException ignored) {}
        }
        mammutilation.setPose(SpeciesPose.HOWLING.get());

        // Direct anim restart for the case where onSyncedDataUpdated didn't dispatch.
        startHowlAnimation(mammutilation, disguise.tickCount);

        State st = states.computeIfAbsent(wearer, State::new);
        st.firedTick = disguise.tickCount;
        st.active = true;

        SoundEvent sound = "mammutiful".equalsIgnoreCase(mammutilation.getName().getString())
                ? SpeciesSoundEvents.MAMMUTIFUL_HOWL.get()
                : SpeciesSoundEvents.MAMMUTILATION_HOWL.get();
        DisguiseSounds.playServerBroadcast(wearer, sound, 1.0F);
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        if (!(disguise instanceof Mammutilation mammutilation)) return;
        State st = states.get(wearer);
        if (st == null || !st.active) return;
        if (disguise.tickCount - st.firedTick < HOWL_DURATION_TICKS) return;
        stopHowlAnimation(mammutilation);
        if (mammutilation.getPose() == SpeciesPose.HOWLING.get()) {
            mammutilation.setPose(Pose.STANDING);
        }
        st.active = false;
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        states.remove(wearer);
    }

    private static void startHowlAnimation(LivingEntity disguise, int tickCount) {
        if (howlAnimationStateField == null) return;
        try {
            Object value = howlAnimationStateField.get(disguise);
            if (value instanceof AnimationState anim) anim.start(tickCount);
        } catch (IllegalAccessException ignored) {}
    }

    private static void stopHowlAnimation(LivingEntity disguise) {
        if (howlAnimationStateField == null) return;
        try {
            Object value = howlAnimationStateField.get(disguise);
            if (value instanceof AnimationState anim) anim.stop();
        } catch (IllegalAccessException ignored) {}
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (MammutilationSpecialActionBehavior.class) {
            if (reflectionInited) return;
            howlTimerField = ReflectionHelper.declaredFieldOfType(entityClass, "howlTimer", int.class);
            howlAnimationStateField = ReflectionHelper.declaredFieldOfType(
                    entityClass, "howlAnimationState", AnimationState.class);
            reflectionInited = true;
        }
    }
}
