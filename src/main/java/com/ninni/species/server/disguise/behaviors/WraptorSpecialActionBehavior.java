package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.disguise.util.DisguiseSounds;
import com.ninni.species.server.entity.mob.update_1.Wraptor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

import java.lang.reflect.Field;

/** Wraptor disguise: plain keybind drives the ROARING pose; shift+keybind cycles
 *  {@code FEATHER_STAGE} (0-5, wraps). {@code onCreated} backfills {@code timeSinceSheared}
 *  so {@code random.nextInt((int)(gameTime - 0L))} doesn't overflow on long-running worlds. */
public final class WraptorSpecialActionBehavior implements DisguiseBehavior {

    public static final WraptorSpecialActionBehavior INSTANCE = new WraptorSpecialActionBehavior();

    /** Wraptor.defineSynchedData spawns at stage 5; range is [0, 5] inclusive. */
    private static final int FEATHER_STAGE_MAX = 5;

    private static volatile boolean reflectionInited;
    private static Field timeSinceShearedField;

    private WraptorSpecialActionBehavior() {}

    @Override
    public void onCreated(LivingEntity wearer, LivingEntity disguise) {
        initReflection(disguise.getClass());
        if (timeSinceShearedField == null) return;
        try {
            long current = timeSinceShearedField.getLong(disguise);
            if (current == 0L) timeSinceShearedField.setLong(disguise, disguise.level().getGameTime());
        } catch (IllegalAccessException ignored) {}
    }

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                com.ninni.species.api.disguise.ActionContext context) {
        if (context.shifted()) {
            cycleFeatherStage(wearer, disguise);
            return;
        }
        if (disguise.getPose() == Pose.ROARING) return;
        disguise.setPose(Pose.ROARING);
        DisguiseSounds.playServerBroadcast(wearer, SpeciesSoundEvents.WRAPTOR_AGGRO.get(), 3.0F);
    }

    private static void cycleFeatherStage(LivingEntity wearer, LivingEntity disguise) {
        if (!(disguise instanceof Wraptor wraptor)) return;
        // Cycles 5 → 4 → 3 → 2 → 1 → 0 → 5 — natural Wraptor visual is "loses cover, then regrows".
        int current = wraptor.getFeatherStage();
        int next = current <= 0 ? FEATHER_STAGE_MAX : current - 1;
        wraptor.setFeatherStage(next);
        // Mirror Wraptor.customServerAiStep: each natural decrement plays WRAPTOR_FEATHER_LOSS.
        DisguiseSounds.playServerBroadcast(wearer, SpeciesSoundEvents.WRAPTOR_FEATHER_LOSS.get(), 1.0F);
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (WraptorSpecialActionBehavior.class) {
            if (reflectionInited) return;
            timeSinceShearedField = ReflectionHelper.declaredFieldOfType(entityClass, "timeSinceSheared", long.class);
            reflectionInited = true;
        }
    }
}
