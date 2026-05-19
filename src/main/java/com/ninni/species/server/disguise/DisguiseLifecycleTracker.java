package com.ninni.species.server.disguise;

import com.ninni.species.api.util.PerSideStateMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/** Lets a disguise survive a body rebuild (e.g. a dimension change recreates the {@code LocalPlayer}):
 *  snapshots the body NBT by UUID per side, replayed onto the rebuilt body to route {@code onBodyRebuilt}. */
public final class DisguiseLifecycleTracker {

    private record Carryover(String disguiseId, CompoundTag nbt) {}

    private static final PerSideStateMap<Carryover> CARRYOVER = new PerSideStateMap<>();

    private DisguiseLifecycleTracker() {}

    /** Snapshot the wearer's current disguise body so an imminent rebuild can resume its runtime
     *  state instead of resetting to the mask's capture-time state. */
    public static void snapshot(LivingEntity wearer, LivingEntity disguise) {
        if (wearer == null || disguise == null) return;
        try {
            String id = EntityType.getKey(disguise.getType()).toString();
            CompoundTag nbt = disguise.saveWithoutId(new CompoundTag());
            CARRYOVER.side(wearer).put(wearer.getUUID(), new Carryover(id, nbt));
        } catch (Throwable ignored) {}
    }

    /** Consume the carryover NBT iff it matches {@code disguiseId} (the same disguise is being
     *  rebuilt). Returns {@code null} for a fresh equip (no snapshot) or a different disguise id. */
    public static CompoundTag takeRebuildNbt(LivingEntity wearer, String disguiseId) {
        if (wearer == null) return null;
        Carryover c = CARRYOVER.side(wearer).remove(wearer.getUUID());
        return (c != null && c.disguiseId().equals(disguiseId)) ? c.nbt() : null;
    }

    /** Drop any pending carryover — call on a genuine unequip so a later re-equip is treated fresh. */
    public static void clear(LivingEntity wearer) {
        if (wearer == null) return;
        CARRYOVER.side(wearer).remove(wearer.getUUID());
    }
}
