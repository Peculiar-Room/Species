package com.ninni.species.api.disguise;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

/** Declarative spec for cycling an integer-valued visual variant on the disguise (e.g. Wraptor
 *  feather coverage, slime size, any "N stages" indicator). Registered via
 *  {@link SpeciesAPI#registerIntStateCycle}. */
public record IntStateCycleSpec(
        ToIntFunction<LivingEntity> getter,
        BiConsumer<LivingEntity, Integer> setter,
        int min,
        int max,
        /** When true, the cycle counts down (max → min → max). Wraptor uses this so the
         *  natural visual ("disguise loses feathers, then re-grows") is the default direction. */
        boolean countDown
) {
    public IntStateCycleSpec {
        if (min > max) throw new IllegalArgumentException("min > max");
    }

    /** Convenience constructor: defaults to count-up direction. */
    public static IntStateCycleSpec ascending(ToIntFunction<LivingEntity> getter,
                                              BiConsumer<LivingEntity, Integer> setter,
                                              int min, int max) {
        return new IntStateCycleSpec(getter, setter, min, max, false);
    }

    /** Convenience constructor: count-down direction. */
    public static IntStateCycleSpec descending(ToIntFunction<LivingEntity> getter,
                                               BiConsumer<LivingEntity, Integer> setter,
                                               int min, int max) {
        return new IntStateCycleSpec(getter, setter, min, max, true);
    }

    /** Computes the next value in the cycle given the current value. */
    public int nextValue(int current) {
        if (countDown) return current <= min ? max : current - 1;
        return current >= max ? min : current + 1;
    }
}
