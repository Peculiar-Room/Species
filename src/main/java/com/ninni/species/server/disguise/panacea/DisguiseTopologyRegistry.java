package com.ninni.species.server.disguise.panacea;

import com.ninni.species.api.disguise.DisguiseRenderLayer;
import com.ninni.species.api.disguise.SubEntityProvider;
import com.ninni.species.server.disguise.BoundroidPairManager;
import com.ninni.species.server.disguise.MineGuardianAnchorManager;
import com.ninni.species.server.disguise.StackatickStackManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

/** Per-{@link EntityType} topology overrides (sub-entities, scales, offsets, layers) read by
 *  {@link DisguiseTopology}. Register at or before {@code FMLCommonSetupEvent}. */
public final class DisguiseTopologyRegistry {

    private static final Map<EntityType<?>, SubEntityProvider> SUB_ENTITY_PROVIDERS = new HashMap<>();
    private static final Map<EntityType<?>, Float> INVENTORY_SCALE_OVERRIDES = new HashMap<>();
    private static final Map<EntityType<?>, Double> CAMERA_SIZE_OVERRIDES = new HashMap<>();
    private static final Map<EntityType<?>, Double> CAMERA_SIZE_FACTORS = new HashMap<>();
    private static final Map<EntityType<?>, Float> INVENTORY_Y_OFFSETS = new HashMap<>();
    private static final Map<EntityType<?>, Float> WORLD_Y_OFFSETS = new HashMap<>();
    private static final Map<EntityType<?>, Float> SHADOW_RADIUS_OVERRIDES = new HashMap<>();
    private static final Map<EntityType<?>, Float> SHADOW_STRENGTH_OVERRIDES = new HashMap<>();
    private static final Map<EntityType<?>, java.util.List<DisguiseRenderLayer>> RENDER_LAYERS = new HashMap<>();

    private static final java.util.List<SubEntityProvider> GLOBAL_SUB_ENTITY_PROVIDERS = new java.util.ArrayList<>();
    private static final Map<EntityType<?>, SubEntityProvider> COMPOSED_PROVIDER_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    private DisguiseTopologyRegistry() {}

    private static void invalidateProviderCache() { COMPOSED_PROVIDER_CACHE.clear(); }

    // --------------------------------------------------------------------
    // Public registration API
    // --------------------------------------------------------------------

    public static void registerSubEntities(EntityType<?> type, SubEntityProvider provider) {
        if (type == null || provider == null) return;
        SUB_ENTITY_PROVIDERS.put(type, provider);
        invalidateProviderCache();
    }

    /** Provider consulted for every disguise; self-gates on applicability. */
    public static void registerGlobalSubEntityProvider(SubEntityProvider provider) {
        if (provider == null) return;
        GLOBAL_SUB_ENTITY_PROVIDERS.add(provider);
        invalidateProviderCache();
    }

    /** Inventory-preview scale (multiplied on top of auto-fit). {@code 1.0F} doesn't clear — use {@link #clearInventoryScale}. */
    public static void setInventoryScale(EntityType<?> type, float scale) {
        if (type == null) return;
        INVENTORY_SCALE_OVERRIDES.put(type, scale);
    }

    public static void clearInventoryScale(EntityType<?> type) {
        if (type == null) return;
        INVENTORY_SCALE_OVERRIDES.remove(type);
    }

    /** Camera distance floor (max of computed union and this). For chains whose union starts small. */
    public static void setCameraSizeMinimum(EntityType<?> type, double minVisualSize) {
        if (type == null) return;
        CAMERA_SIZE_OVERRIDES.put(type, minVisualSize);
    }

    public static void clearCameraSizeMinimum(EntityType<?> type) {
        if (type == null) return;
        CAMERA_SIZE_OVERRIDES.remove(type);
    }

    /** Multiplier on the intrinsic AABB for camera zoom (default {@code 1.0}). Use when the visual
     *  extent overshoots the bbox (wings, sweeping tails). */
    public static void setCameraSizeFactor(EntityType<?> type, double factor) {
        if (type == null) return;
        CAMERA_SIZE_FACTORS.put(type, factor);
    }

    /** Pre-scale vertical translate for inventory render. Negative shifts down to recentre the model. */
    public static void setInventoryYOffset(EntityType<?> type, float yOffset) {
        if (type == null) return;
        INVENTORY_Y_OFFSETS.put(type, yOffset);
    }

    public static void clearInventoryYOffset(EntityType<?> type) {
        if (type == null) return;
        INVENTORY_Y_OFFSETS.remove(type);
    }

    /** In-world vertical translate. Lifts ground-level models (e.g. AM Triops at Y≈0) into normal sightlines. */
    public static void setWorldYOffset(EntityType<?> type, float yOffset) {
        if (type == null) return;
        WORLD_Y_OFFSETS.put(type, yOffset);
    }

    public static void clearWorldYOffset(EntityType<?> type) {
        if (type == null) return;
        WORLD_Y_OFFSETS.remove(type);
    }

    /** Override the renderer's intrinsic shadow radius. {@code 0F} suppresses the shadow. */
    public static void setShadowRadius(EntityType<?> type, float radius) {
        if (type == null) return;
        SHADOW_RADIUS_OVERRIDES.put(type, radius);
    }

    public static void clearShadowRadius(EntityType<?> type) {
        if (type == null) return;
        SHADOW_RADIUS_OVERRIDES.remove(type);
    }

    /** Override the shadow's distance-fade strength (vanilla default {@code 1.0F}). Rarely needed. */
    public static void setShadowStrength(EntityType<?> type, float strength) {
        if (type == null) return;
        SHADOW_STRENGTH_OVERRIDES.put(type, strength);
    }

    public static void clearShadowStrength(EntityType<?> type) {
        if (type == null) return;
        SHADOW_STRENGTH_OVERRIDES.remove(type);
    }

    /** Extra render-pass layer; multiple invocations stack in registration order. */
    public static void registerRenderLayer(EntityType<?> type, DisguiseRenderLayer layer) {
        if (type == null || layer == null) return;
        RENDER_LAYERS.computeIfAbsent(type, k -> new java.util.ArrayList<>()).add(layer);
    }

    public static java.util.List<DisguiseRenderLayer> getRenderLayers(LivingEntity disguise) {
        if (disguise == null) return java.util.Collections.emptyList();
        java.util.List<DisguiseRenderLayer> layers = RENDER_LAYERS.get(disguise.getType());
        return layers != null ? layers : java.util.Collections.emptyList();
    }

    // --------------------------------------------------------------------
    // Lookup API consumed by DisguiseTopology
    // --------------------------------------------------------------------

    public static SubEntityProvider getSubEntityProvider(LivingEntity disguise) {
        if (disguise == null) return SubEntityProvider.EMPTY;
        return COMPOSED_PROVIDER_CACHE.computeIfAbsent(disguise.getType(), DisguiseTopologyRegistry::composeProvider);
    }

    private static SubEntityProvider composeProvider(EntityType<?> type) {
        SubEntityProvider perType = SUB_ENTITY_PROVIDERS.get(type);
        if (GLOBAL_SUB_ENTITY_PROVIDERS.isEmpty()) {
            return perType != null ? perType : SubEntityProvider.EMPTY;
        }
        // Capture immutable snapshots once so the cached lambda doesn't iterate the live globals list.
        SubEntityProvider[] globals = GLOBAL_SUB_ENTITY_PROVIDERS.toArray(new SubEntityProvider[0]);
        return (wearer, dis) -> {
            java.util.List<Entity> combined = null;
            if (perType != null) {
                for (Entity e : perType.getSubEntities(wearer, dis)) {
                    if (combined == null) combined = new java.util.ArrayList<>(4);
                    combined.add(e);
                }
            }
            for (SubEntityProvider g : globals) {
                for (Entity e : g.getSubEntities(wearer, dis)) {
                    if (combined == null) combined = new java.util.ArrayList<>(4);
                    combined.add(e);
                }
            }
            return combined != null ? combined : java.util.Collections.emptyList();
        };
    }

    public static boolean hasSubEntityProvider(LivingEntity disguise) {
        return disguise != null && SUB_ENTITY_PROVIDERS.containsKey(disguise.getType());
    }

    public static boolean hasOverride(EntityType<?> type) {
        if (type == null) return false;
        return SUB_ENTITY_PROVIDERS.containsKey(type)
                || INVENTORY_SCALE_OVERRIDES.containsKey(type)
                || CAMERA_SIZE_OVERRIDES.containsKey(type)
                || INVENTORY_Y_OFFSETS.containsKey(type)
                || WORLD_Y_OFFSETS.containsKey(type)
                || SHADOW_RADIUS_OVERRIDES.containsKey(type)
                || SHADOW_STRENGTH_OVERRIDES.containsKey(type)
                || RENDER_LAYERS.containsKey(type);
    }

    public static boolean hasAnyOverride(LivingEntity disguise) {
        return disguise != null && hasOverride(disguise.getType());
    }

    public static java.util.Set<EntityType<?>> getRegisteredTypes() {
        java.util.Set<EntityType<?>> all = new java.util.HashSet<>();
        all.addAll(SUB_ENTITY_PROVIDERS.keySet());
        all.addAll(INVENTORY_SCALE_OVERRIDES.keySet());
        all.addAll(CAMERA_SIZE_OVERRIDES.keySet());
        all.addAll(INVENTORY_Y_OFFSETS.keySet());
        all.addAll(WORLD_Y_OFFSETS.keySet());
        all.addAll(SHADOW_RADIUS_OVERRIDES.keySet());
        all.addAll(SHADOW_STRENGTH_OVERRIDES.keySet());
        all.addAll(RENDER_LAYERS.keySet());
        return java.util.Collections.unmodifiableSet(all);
    }

    @javax.annotation.Nullable
    public static Float getInventoryScaleOverride(LivingEntity disguise) {
        if (disguise == null) return null;
        return INVENTORY_SCALE_OVERRIDES.get(disguise.getType());
    }

    public static double getCameraSizeMinimum(LivingEntity disguise) {
        if (disguise == null) return 0.0;
        Double v = CAMERA_SIZE_OVERRIDES.get(disguise.getType());
        return v != null ? v : 0.0;
    }

    public static double getCameraSizeFactor(LivingEntity disguise) {
        if (disguise == null) return 1.0;
        Double v = CAMERA_SIZE_FACTORS.get(disguise.getType());
        return v != null ? v : 1.0;
    }

    @javax.annotation.Nullable
    public static Float getInventoryYOffsetOverride(LivingEntity disguise) {
        if (disguise == null) return null;
        return INVENTORY_Y_OFFSETS.get(disguise.getType());
    }

    public static float getWorldYOffset(LivingEntity disguise) {
        if (disguise == null) return 0.0F;
        Float v = WORLD_Y_OFFSETS.get(disguise.getType());
        return v != null ? v : 0.0F;
    }

    /** Per-type shadow-radius override; {@code null} means fall back to the disguise renderer's intrinsic. */
    @javax.annotation.Nullable
    public static Float getShadowRadiusOverride(LivingEntity disguise) {
        if (disguise == null) return null;
        return SHADOW_RADIUS_OVERRIDES.get(disguise.getType());
    }

    /** Per-type shadow-strength override; {@code null} means fall back to the disguise renderer's intrinsic. */
    @javax.annotation.Nullable
    public static Float getShadowStrengthOverride(LivingEntity disguise) {
        if (disguise == null) return null;
        return SHADOW_STRENGTH_OVERRIDES.get(disguise.getType());
    }

    // --------------------------------------------------------------------
    // Built-in defaults (Species' first-party disguise customisations)
    // --------------------------------------------------------------------

    /** First-party topology overrides; soft-dep entries no-op if the mod is absent. */
    public static void registerDefaults() {
        // Force-load companion managers so their static initializers register global providers.
        try {
            Class.forName(BoundroidPairManager.class.getName());
            Class.forName(MineGuardianAnchorManager.class.getName());
            Class.forName(StackatickStackManager.class.getName());
        } catch (ClassNotFoundException ignored) {}

        // Hullback: ~17-block Z span (nose +6 to fluke -11), bbox only covers a few.
        ifPresent("whaleborne", "hullback", type -> {
            setInventoryScale(type, 0.5F);
            setCameraSizeMinimum(type, 17.0);
        });

        // Forsaken: model reaches ~5 blocks vs 3.5-block bbHeight.
        ifPresent("alexscaves", "forsaken", type -> setInventoryScale(type, 0.7F));

        // IaF flyers: wingspan extends well past the bbox; factor scales the dynamic AABB.
        ifPresent("iceandfire", "fire_dragon", type -> setCameraSizeFactor(type, 2.5));
        ifPresent("iceandfire", "ice_dragon", type -> setCameraSizeFactor(type, 2.5));
        ifPresent("iceandfire", "lightning_dragon", type -> setCameraSizeFactor(type, 2.5));
        ifPresent("iceandfire", "amphithere", type -> setCameraSizeFactor(type, 2.0));
    }

    @FunctionalInterface
    private interface TypeConsumer { void accept(EntityType<?> type); }

    private static void ifPresent(String namespace, String path, TypeConsumer action) {
        ResourceLocation id = new ResourceLocation(namespace, path);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        if (type != null) action.accept(type);
    }
}
