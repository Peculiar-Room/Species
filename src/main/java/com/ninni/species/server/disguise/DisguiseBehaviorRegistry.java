package com.ninni.species.server.disguise;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.disguise.dsl.CompositeDisguiseBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-{@link EntityType} {@link DisguiseBehavior} lookup, falling back to {@link DefaultDisguiseBehavior}.
 * Registration happens during mod setup. {@link #register} replaces; {@link #compose} stacks.
 */
public final class DisguiseBehaviorRegistry {

    private static final Map<EntityType<?>, DisguiseBehavior> BY_TYPE = new HashMap<>();

    /** Behaviors run for every disguise; each self-gates on applicability. */
    private static final java.util.List<DisguiseBehavior> GLOBAL_BEHAVIORS = new java.util.ArrayList<>();

    /** Composed result per type. Invalidated on every registration mutation. */
    private static final Map<EntityType<?>, DisguiseBehavior> COMPOSED_CACHE = new ConcurrentHashMap<>();

    private DisguiseBehaviorRegistry() {}

    private static void invalidateCache() { COMPOSED_CACHE.clear(); }

    /** Replace any existing entry. Use {@link #compose} to stack instead. */
    public static void register(EntityType<?> type, DisguiseBehavior behavior) {
        if (type == null || behavior == null) return;
        BY_TYPE.put(type, behavior);
        invalidateCache();
    }

    /** Stack on top of any existing entry (existing runs first). Equivalent to {@link #register} when none exists. */
    public static void compose(EntityType<?> type, DisguiseBehavior behavior) {
        if (type == null || behavior == null) return;
        DisguiseBehavior existing = BY_TYPE.get(type);
        if (existing == null) {
            BY_TYPE.put(type, behavior);
        } else {
            BY_TYPE.put(type, CompositeDisguiseBehavior.of(existing, behavior));
        }
        invalidateCache();
    }

    /** Add to {@link #GLOBAL_BEHAVIORS}. */
    public static void registerGlobal(DisguiseBehavior behavior) {
        if (behavior == null) return;
        GLOBAL_BEHAVIORS.add(behavior);
        invalidateCache();
    }

    /** Composed result for the disguise's type, never null. */
    public static DisguiseBehavior get(LivingEntity disguise) {
        if (disguise == null) return DefaultDisguiseBehavior.INSTANCE;
        return get(disguise.getType());
    }

    /** Composed result for an entity type, never null. */
    public static DisguiseBehavior get(EntityType<?> type) {
        if (type == null) return DefaultDisguiseBehavior.INSTANCE;
        return COMPOSED_CACHE.computeIfAbsent(type, DisguiseBehaviorRegistry::compute);
    }

    private static DisguiseBehavior compute(EntityType<?> type) {
        DisguiseBehavior perType = BY_TYPE.get(type);
        if (GLOBAL_BEHAVIORS.isEmpty()) {
            return perType != null ? perType : DefaultDisguiseBehavior.INSTANCE;
        }
        // Globals run first so per-type entries can override auto-detected defaults.
        int size = GLOBAL_BEHAVIORS.size() + (perType != null ? 1 : 0);
        DisguiseBehavior[] all = new DisguiseBehavior[size];
        int i = 0;
        for (DisguiseBehavior g : GLOBAL_BEHAVIORS) all[i++] = g;
        if (perType != null) all[i++] = perType;
        return CompositeDisguiseBehavior.of(all);
    }

    public static boolean hasOverride(EntityType<?> type) {
        return type != null && BY_TYPE.containsKey(type);
    }
}
