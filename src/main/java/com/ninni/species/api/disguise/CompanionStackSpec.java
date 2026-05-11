package com.ninni.species.api.disguise;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/** Declarative spec for a stacked-companion cosmetic — cycles 0..{@code maxLevel} visual-only
 *  companions above the wearer (Stackatick pattern, but generic). Registered via
 *  {@link SpeciesAPI#registerCompanionStack}. */
public record CompanionStackSpec<C extends LivingEntity>(
        EntityType<C> companionType,
        int maxLevel,
        /** Vertical spacing per level — typically the companion's bb height, or smaller to
         *  visually overlap. */
        double verticalSpacing,
        /** Initial configuration applied once when the companion is spawned (pose, NBT, etc.). */
        Configurer<C> initializer,
        /** Re-applied each tick to sync wearer-driven state (color, body yaw, etc.). */
        TickConfigurer<C> tickConfigurer,
        /** Resolves the Y offset (above wearer.getY()) where the bottom companion's feet sit.
         *  {@code null} = default {@code disguise.getDimensions(wearer.getPose()).height}.
         *  Override when the model's visual top diverges from its bbox. */
        HeightResolver heightResolver
) {
    public CompanionStackSpec {
        if (maxLevel < 1) throw new IllegalArgumentException("maxLevel must be >= 1");
        if (initializer == null) initializer = (c, w, d, i) -> {};
        if (tickConfigurer == null) tickConfigurer = (c, w, d, i) -> {};
    }

    /** Convenience constructor without HeightResolver — uses pose-aware bbHeight as the anchor. */
    public CompanionStackSpec(EntityType<C> companionType, int maxLevel, double verticalSpacing,
                              Configurer<C> initializer, TickConfigurer<C> tickConfigurer) {
        this(companionType, maxLevel, verticalSpacing, initializer, tickConfigurer, null);
    }

    @FunctionalInterface
    public interface Configurer<C extends LivingEntity> {
        void configure(C companion, LivingEntity wearer, LivingEntity disguise, int stackIndex);
    }

    @FunctionalInterface
    public interface TickConfigurer<C extends LivingEntity> {
        void tick(C companion, LivingEntity wearer, LivingEntity disguise, int stackIndex);
    }

    @FunctionalInterface
    public interface HeightResolver {
        double resolve(LivingEntity wearer, LivingEntity disguise);
    }
}
