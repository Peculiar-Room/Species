package com.ninni.species.api.disguise;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Per-entity-type behavior hook for the Wicked Mask disguise system; all hooks default to no-ops.
 * Third-party mods register via {@link SpeciesAPI} or Forge IMC.
 */
public interface DisguiseBehavior {

    /** Called once at disguise creation; one-time setup that survives across ticks. */
    default void onCreated(LivingEntity wearer, LivingEntity disguise) {}

    /**
     * Called once when the disguise body is discarded (mask removed, swap, logout,
     * dimension change). Symmetric to {@link #onCreated}.
     */
    default void onDestroyed(LivingEntity wearer, LivingEntity disguise) {}

    /** Called instead of {@link #onCreated} when the body is rebuilt (not freshly equipped) — e.g. a
     *  dimension change; NBT state is already restored, so override only for transient render state. */
    default void onBodyRebuilt(LivingEntity wearer, LivingEntity disguise) {}

    /**
     * Called each tick before {@code disguise.tick()}. Position/rotation already synced.
     * Use for state that must be in place before the disguise's own tick runs.
     */
    default void preTick(LivingEntity wearer, LivingEntity disguise) {}

    /** Called each tick after {@code disguise.tick()} and default realignment. */
    default void postTick(LivingEntity wearer, LivingEntity disguise) {}

    /** Called each frame before {@code renderer.render(disguise, …)}. */
    default void preRender(LivingEntity wearer, LivingEntity disguise, float partialTick, boolean inInventory) {}

    /** Called each frame after the renderer returns. Restore any state overridden in {@link #preRender}. */
    default void postRender(LivingEntity wearer, LivingEntity disguise, float partialTick, boolean inInventory) {}

    /**
     * Fired only for the inventory preview render, immediately before the renderer.
     * Prefer this over branching on {@code inInventory} in {@link #preRender} when work is inventory-only.
     */
    default void preInventoryPose(LivingEntity wearer, LivingEntity disguise, float partialTick) {}

    /** Symmetric to {@link #preInventoryPose}. */
    default void postInventoryPose(LivingEntity wearer, LivingEntity disguise, float partialTick) {}

    /**
     * Yaw offset (degrees) added to every rotation field (yRot, yBodyRot, yHeadRot)
     * when syncing wearer→disguise. EnderDragon's body convention is reversed; its behavior returns 180.
     */
    default float yawOffset(LivingEntity disguise) {
        return 0.0F;
    }

    /**
     * Whether the wearer's view pitch propagates to the disguise. Default: only while flying or swimming.
     */
    default boolean shouldApplyXRot(LivingEntity wearer, LivingEntity disguise, boolean inInventory) {
        if (inInventory) return true;
        return wearer.isFallFlying()
                || wearer.isSwimming()
                || (wearer instanceof Player p && p.getAbilities().flying);
    }

    /**
     * Whether to preserve the rotation delta that {@code LivingEntity.aiStep()} would zero.
     * Some mobs need it for animation drivers (Citadel ChainBuffer); default true.
     */
    default boolean preserveRotationDeltaInAiStep() {
        return true;
    }

    /** Whether body yaw tracks the wearer's camera yaw instead of body yaw. Snake/worm chains
     *  that natively pin {@code yBodyRot = getYRot()} need this so segments don't swing past a
     *  stuck head; receives {@code disguise} so global behaviors can scope by type. */
    default boolean bodyYawTracksCamera(LivingEntity disguise) {
        return false;
    }

    /** Per-frame Y offset (blocks) added to the disguise's render position. For impulse-driven
     *  locomotion (jumps, hops) that the pinned-in-place pipeline can't reproduce. */
    default float renderYOffset(LivingEntity wearer, LivingEntity disguise, float partialTick) {
        return 0F;
    }

    /** Fired on the special-action keybind; switch on {@code context} for per-scenario animations
     *  or ignore it for context-agnostic behavior. */
    default void onSpecialAction(LivingEntity wearer, LivingEntity disguise, ActionContext context) {}

    /** Lambda-friendly factory for behaviors that only need the special-action hook. */
    static DisguiseBehavior specialAction(SpecialActionHandler handler) {
        return new DisguiseBehavior() {
            @Override
            public void onSpecialAction(LivingEntity wearer, LivingEntity disguise, ActionContext context) {
                handler.handle(wearer, disguise, context);
            }
        };
    }
}
