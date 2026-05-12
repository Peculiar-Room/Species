package com.ninni.species.api.disguise;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Consumer;

/** Declarative spec for a timed transition on a disguise — e.g. a phase-change animation with
 *  a state write at the keyframe tick and cleanup at the end. Started via
 *  {@link SpeciesAPI#startAnimatedTransition} and driven by the global animation driver. */
public record AnimatedTransitionSpec(
        int durationTicks,
        /** One-shot stages sorted by {@code atTick}, ascending. Drivers fire each stage when
         *  {@code elapsed >= atTick}, exactly once per transition. */
        List<Stage> stages,
        /** Optional per-tick callback receiving {@code (disguise, elapsed)} — used for
         *  animation systems that need an externally-driven tick (Citadel / LionfishAPI).
         *  May be {@code null} for systems that self-tick (Mojang {@code AnimationState}). */
        TickAction onEachTick
) {
    public AnimatedTransitionSpec {
        if (durationTicks < 1) throw new IllegalArgumentException("durationTicks must be >= 1");
        if (stages == null) stages = List.of();
        // Defensive copy + ascending atTick validation.
        stages = List.copyOf(stages);
        for (int i = 1; i < stages.size(); i++) {
            if (stages.get(i).atTick() < stages.get(i - 1).atTick()) {
                throw new IllegalArgumentException("Stages must be sorted by atTick ascending");
            }
        }
    }

    /** A single timed action fired once at {@code atTick} into the transition. */
    public record Stage(int atTick, Consumer<LivingEntity> action) {
        public Stage {
            if (atTick < 0) throw new IllegalArgumentException("atTick must be >= 0");
            if (action == null) throw new NullPointerException("Stage action cannot be null");
        }
    }

    @FunctionalInterface
    public interface TickAction {
        void run(LivingEntity disguise, int elapsed);
    }
}
