package com.ninni.species.server.disguise.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ninni.species.Species;
import com.ninni.species.api.disguise.SpeciesAPI;
import com.ninni.species.api.disguise.data.DisguiseData;
import com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Loads {@code data/<ns>/species_disguises/<entity_path>.json} on reload, mapping file path to {@link EntityType}.
 * Cosmetic axes feed {@link DisguiseDataRegistry}; topology axes go through {@link SpeciesAPI} and are tracked for clear-on-reload.
 */
public final class DisguiseDataReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();

    /** Topology entries applied on the last reload; cleared before each re-apply. */
    private final Set<EntityType<?>> trackedTopologyTypes = new HashSet<>();

    public DisguiseDataReloadListener() {
        super(GSON, "species_disguises");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        // Clear topology entries from prior reload (cosmetics layer is map-based; replaceAll handles it).
        for (EntityType<?> type : trackedTopologyTypes) {
            DisguiseTopologyRegistry.clearInventoryScale(type);
            DisguiseTopologyRegistry.clearInventoryYOffset(type);
            DisguiseTopologyRegistry.clearCameraSizeMinimum(type);
        }
        trackedTopologyTypes.clear();

        Map<EntityType<?>, DisguiseData> snapshot = new HashMap<>();

        files.forEach((id, json) -> {
            EntityType<?> target = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
            if (target == null) {
                Species.LOGGER.warn("species_disguises/{}.json: unknown entity type, skipping", id);
                return;
            }
            try {
                DisguiseData data = parse(json.getAsJsonObject(), id);
                snapshot.put(target, data);
                applyTopology(target, data);
            } catch (Throwable t) {
                Species.LOGGER.error("species_disguises/{}.json: parse failed: {}", id, t.toString());
            }
        });

        DisguiseDataRegistry.replaceAll(snapshot);
        Species.LOGGER.info("Loaded {} disguise data entries from datapack", snapshot.size());
    }

    private DisguiseData parse(JsonObject obj, ResourceLocation id) {
        ResourceLocation texture = optResource(obj, "texture", id);
        SoundEvent hurt = optSound(obj, "hurt_sound", id);
        SoundEvent death = optSound(obj, "death_sound", id);
        SoundEvent ambient = optSound(obj, "ambient_sound", id);
        Component name = obj.has("name_tag") ? Component.literal(obj.get("name_tag").getAsString()) : null;
        Float worldScale = obj.has("world_scale") ? obj.get("world_scale").getAsFloat() : null;
        Integer glow = obj.has("glow_color") ? parseColor(obj.get("glow_color").getAsString(), id) : null;
        Float invScale = obj.has("inventory_scale") ? obj.get("inventory_scale").getAsFloat() : null;
        Float invY = obj.has("inventory_y_offset") ? obj.get("inventory_y_offset").getAsFloat() : null;
        Double camMin = obj.has("camera_size_min") ? obj.get("camera_size_min").getAsDouble() : null;
        java.util.List<net.minecraft.world.effect.MobEffectInstance> effects = parseWearerEffects(obj, id);

        return new DisguiseData(texture, hurt, death, ambient, name, worldScale, glow, invScale, invY, camMin, effects);
    }

    private static java.util.List<net.minecraft.world.effect.MobEffectInstance> parseWearerEffects(JsonObject obj, ResourceLocation file) {
        if (!obj.has("wearer_effects")) return java.util.Collections.emptyList();
        if (!obj.get("wearer_effects").isJsonArray()) return java.util.Collections.emptyList();
        java.util.List<net.minecraft.world.effect.MobEffectInstance> list = new java.util.ArrayList<>();
        for (com.google.gson.JsonElement el : obj.getAsJsonArray("wearer_effects")) {
            if (!el.isJsonObject()) continue;
            JsonObject e = el.getAsJsonObject();
            if (!e.has("id")) continue;
            try {
                ResourceLocation rl = new ResourceLocation(e.get("id").getAsString());
                net.minecraft.world.effect.MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(rl);
                if (effect == null) {
                    Species.LOGGER.warn("species_disguises/{}.json: unknown effect '{}'", file, rl);
                    continue;
                }
                int amplifier = e.has("amplifier") ? e.get("amplifier").getAsInt() : 0;
                boolean ambient = e.has("ambient") && e.get("ambient").getAsBoolean();
                boolean visible = !e.has("visible") || e.get("visible").getAsBoolean();
                boolean showIcon = !e.has("show_icon") || e.get("show_icon").getAsBoolean();
                int duration = e.has("duration") ? e.get("duration").getAsInt()
                        : net.minecraft.world.effect.MobEffectInstance.INFINITE_DURATION;
                list.add(new net.minecraft.world.effect.MobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon));
            } catch (Throwable t) {
                Species.LOGGER.warn("species_disguises/{}.json: invalid wearer_effects entry: {}", file, t.toString());
            }
        }
        return list;
    }

    private void applyTopology(EntityType<?> type, DisguiseData data) {
        boolean any = false;
        if (data.inventoryScale() != null) {
            SpeciesAPI.setInventoryScale(type, data.inventoryScale());
            any = true;
        }
        if (data.inventoryYOffset() != null) {
            SpeciesAPI.setInventoryYOffset(type, data.inventoryYOffset());
            any = true;
        }
        if (data.cameraSizeMin() != null) {
            SpeciesAPI.setCameraSizeMinimum(type, data.cameraSizeMin());
            any = true;
        }
        if (any) trackedTopologyTypes.add(type);
    }

    @Nullable
    private static ResourceLocation optResource(JsonObject obj, String key, ResourceLocation file) {
        if (!obj.has(key)) return null;
        try {
            return new ResourceLocation(obj.get(key).getAsString());
        } catch (Throwable t) {
            Species.LOGGER.warn("species_disguises/{}.json: invalid resource location for '{}': {}", file, key, t.toString());
            return null;
        }
    }

    @Nullable
    private static SoundEvent optSound(JsonObject obj, String key, ResourceLocation file) {
        ResourceLocation rl = optResource(obj, key, file);
        if (rl == null) return null;
        SoundEvent ev = BuiltInRegistries.SOUND_EVENT.get(rl);
        if (ev == null) {
            Species.LOGGER.warn("species_disguises/{}.json: unknown sound '{}' for key '{}'", file, rl, key);
        }
        return ev;
    }

    @Nullable
    private static Integer parseColor(String s, ResourceLocation file) {
        try {
            String hex = s.startsWith("#") ? s.substring(1) : s;
            return (int) Long.parseLong(hex, 16);
        } catch (Throwable t) {
            Species.LOGGER.warn("species_disguises/{}.json: invalid glow_color '{}'", file, s);
            return null;
        }
    }
}
