package com.ninni.species.api.disguise;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

/** Public facade for the disguise system. Hard-dep mods call directly; soft-dep mods use Forge IMC (see {@code imc.SpeciesIMCKeys}). */
public final class SpeciesAPI {

    private SpeciesAPI() {}

    // ---- Behavior ------------------------------------------------------

    /** Register a behavior for an entity type, replacing any prior entry. */
    public static void registerBehavior(EntityType<?> type, DisguiseBehavior behavior) {
        com.ninni.species.server.disguise.DisguiseBehaviorRegistry.register(type, behavior);
    }

    /** Compose a behavior with any existing entry for the type (existing runs first). */
    public static void composeBehavior(EntityType<?> type, DisguiseBehavior behavior) {
        com.ninni.species.server.disguise.DisguiseBehaviorRegistry.compose(type, behavior);
    }

    /** Register a behavior applied to every disguise. The behavior should self-gate on applicability. */
    public static void registerGlobalBehavior(DisguiseBehavior behavior) {
        com.ninni.species.server.disguise.DisguiseBehaviorRegistry.registerGlobal(behavior);
    }

    /** Composed behavior for the type (per-type entry stacked over globals). Never null —
     *  returns the default no-op when nothing is registered. */
    public static DisguiseBehavior getBehavior(EntityType<?> type) {
        return com.ninni.species.server.disguise.DisguiseBehaviorRegistry.get(type);
    }

    /** Whether a per-type behavior is registered (does not count globals). */
    public static boolean hasBehavior(EntityType<?> type) {
        return com.ninni.species.server.disguise.DisguiseBehaviorRegistry.hasOverride(type);
    }

    // ---- Cosmetics -----------------------------------------------------

    /** Register cosmetics for an entity type, replacing any prior entry. */
    public static void registerCosmetics(EntityType<?> type, DisguiseCosmetics cosmetics) {
        com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry.register(type, cosmetics);
    }

    /** Compose cosmetics on top of any existing registration via first-non-null-wins precedence. */
    public static void composeCosmetics(EntityType<?> type, DisguiseCosmetics cosmetics) {
        com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry.compose(type, cosmetics);
    }

    /** Composed cosmetics for the type. Never null — returns the default no-op when nothing is registered. */
    public static DisguiseCosmetics getCosmetics(EntityType<?> type) {
        return com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry.get(type);
    }

    /** Whether per-type cosmetics are registered (does not count globals). */
    public static boolean hasCosmetics(EntityType<?> type) {
        return com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry.hasOverride(type);
    }

    // ---- Wearer / disguise lookup --------------------------------------

    /** Resolve the disguise body the wearer currently has, if any. */
    public static Optional<LivingEntity> getDisguiseBody(LivingEntity wearer) {
        if (!(wearer instanceof com.ninni.species.mixin_util.LivingEntityAccess access)) return Optional.empty();
        return Optional.ofNullable(access.getDisguisedEntity());
    }

    // ---- Topology ------------------------------------------------------

    /** Register a sub-entity provider for an entity type (independent client-side entities). */
    public static void registerSubEntityProvider(EntityType<?> type, SubEntityProvider provider) {
        com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry.registerSubEntities(type, provider);
    }

    /** Register a global sub-entity provider that self-gates on applicability per disguise. */
    public static void registerGlobalSubEntityProvider(SubEntityProvider provider) {
        com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry.registerGlobalSubEntityProvider(provider);
    }

    /** Override the inventory-preview scale (multiplied on top of auto-fit). */
    public static void setInventoryScale(EntityType<?> type, float scale) {
        com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry.setInventoryScale(type, scale);
    }

    /** Pin the camera visual-size floor for streaming-segment disguises. */
    public static void setCameraSizeMinimum(EntityType<?> type, double minVisualSize) {
        com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry.setCameraSizeMinimum(type, minVisualSize);
    }

    /** Vertical translate (world units, pre-scale) applied during inventory render. Negative shifts down. */
    public static void setInventoryYOffset(EntityType<?> type, float yOffset) {
        com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry.setInventoryYOffset(type, yOffset);
    }

    /** Vertical translate (world units) applied during in-world render. Use to lift small/flat creatures off the wearer's feet. */
    public static void setWorldYOffset(EntityType<?> type, float yOffset) {
        com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry.setWorldYOffset(type, yOffset);
    }

    /** Override the disguise's shadow radius (default copies the renderer's intrinsic).
     *  Use {@code 0F} to suppress, or a tuned value when the intrinsic doesn't fit. */
    public static void setShadowRadius(EntityType<?> type, float radius) {
        com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry.setShadowRadius(type, radius);
    }

    /** Override the disguise's shadow distance-fade strength (vanilla default {@code 1.0F}). */
    public static void setShadowStrength(EntityType<?> type, float strength) {
        com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry.setShadowStrength(type, strength);
    }

    /** Add an extra render-pass layer for the given type. Multiple layers run in registration order. */
    public static void registerRenderLayer(EntityType<?> type, DisguiseRenderLayer layer) {
        com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry.registerRenderLayer(type, layer);
    }

    // ---- Chain disguises -----------------------------------------------

    /** Register a segmented-chain disguise (head + linked body parts) from a {@code ChainSpec}.
     *  Equivalent to {@code new SegmentChainManager(spec).register()}. */
    public static void registerChainDisguise(com.ninni.species.server.disguise.panacea.spec.ChainSpec spec) {
        new com.ninni.species.server.disguise.panacea.SegmentChainManager(spec).register();
    }

    /** Map chain segment types to their head so imprinting a segment can redirect to the head when
     *  the server config flag is on. {@code SegmentChainManager.register()} populates this from the
     *  ChainSpec automatically — call directly only for hardcoded chains that bypass the spec API. */
    public static void registerChainHeadMapping(EntityType<?> head, EntityType<?>... segments) {
        com.ninni.species.server.disguise.ChainHeadRegistry.register(head, segments);
    }

    // ---- Phase cycle (shift+keybind alt action) -----------------------

    /** Register a phase-cycle handler for the type — invoked when the wearer presses
     *  sneak+special-action. Composes onto any existing behavior; plain keybind is unaffected. */
    public static void registerPhaseCycle(EntityType<?> type, PhaseCycleHandler handler) {
        if (type == null || handler == null) return;
        composeBehavior(type, new DisguiseBehavior() {
            @Override
            public void onSpecialAction(net.minecraft.world.entity.LivingEntity wearer,
                                        net.minecraft.world.entity.LivingEntity disguise,
                                        ActionContext context) {
                if (context.shifted()) handler.cycleNext(disguise);
            }
        });
    }

    /** Sugar for the Wraptor-style "cycle through N integer visual states" pattern. Reads via
     *  {@code spec.getter()}, writes via {@code spec.setter()}, wraps at {@code min}/{@code max}.
     *  Driven by sneak+special-action like {@link #registerPhaseCycle}. */
    public static void registerIntStateCycle(EntityType<?> type, IntStateCycleSpec spec) {
        if (type == null || spec == null) return;
        composeBehavior(type, new DisguiseBehavior() {
            @Override
            public void onSpecialAction(net.minecraft.world.entity.LivingEntity wearer,
                                        net.minecraft.world.entity.LivingEntity disguise,
                                        ActionContext context) {
                if (!context.shifted()) return;
                int current = spec.getter().applyAsInt(disguise);
                spec.setter().accept(disguise, spec.nextValue(current));
            }
        });
    }

    /** Sugar for the Stackatick-style "stacked companions above the wearer" pattern. The spec
     *  declares the companion type, max level, vertical spacing, and per-companion init + tick
     *  callbacks. Drives sub-entity rendering automatically via the generic stack manager. */
    public static <C extends net.minecraft.world.entity.LivingEntity> void registerCompanionStack(
            EntityType<?> type, CompanionStackSpec<C> spec) {
        if (type == null || spec == null) return;
        com.ninni.species.server.disguise.GenericCompanionStackManager.register(type, spec);
        composeBehavior(type, new DisguiseBehavior() {
            @Override
            public void onSpecialAction(net.minecraft.world.entity.LivingEntity wearer,
                                        net.minecraft.world.entity.LivingEntity disguise,
                                        ActionContext context) {
                if (!context.shifted()) return;
                com.ninni.species.server.disguise.GenericCompanionStackManager.cycle(wearer, disguise);
            }

            @Override
            public void postTick(net.minecraft.world.entity.LivingEntity wearer,
                                 net.minecraft.world.entity.LivingEntity disguise) {
                com.ninni.species.server.disguise.GenericCompanionStackManager.tickStack(wearer, disguise);
            }

            @Override
            public void onDestroyed(net.minecraft.world.entity.LivingEntity wearer,
                                    net.minecraft.world.entity.LivingEntity disguise) {
                com.ninni.species.server.disguise.GenericCompanionStackManager.clear(wearer);
            }
        });
    }

    // ---- Animated transitions (phase-change choreography) ---------------

    /** Starts a timed transition on the wearer's disguise. The driver walks the spec's stages
     *  at their declared ticks and (optionally) invokes the per-tick callback every frame
     *  until {@code durationTicks}. Calling again replaces any in-flight transition. */
    public static void startAnimatedTransition(net.minecraft.world.entity.LivingEntity wearer,
                                               net.minecraft.world.entity.LivingEntity disguise,
                                               AnimatedTransitionSpec spec) {
        com.ninni.species.server.disguise.AnimatedTransitionDriver.start(wearer, disguise, spec);
    }

    /** Whether the wearer has an in-flight animated transition on their tick side. */
    public static boolean hasAnimatedTransition(net.minecraft.world.entity.LivingEntity wearer) {
        return com.ninni.species.server.disguise.AnimatedTransitionDriver.isActive(wearer);
    }

    /** Cancels the wearer's in-flight animated transition on both sides. */
    public static void cancelAnimatedTransition(net.minecraft.world.entity.LivingEntity wearer) {
        com.ninni.species.server.disguise.AnimatedTransitionDriver.cancel(wearer);
    }
}
