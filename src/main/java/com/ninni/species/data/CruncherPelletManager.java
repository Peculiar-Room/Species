package com.ninni.species.data;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ninni.species.Species;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CruncherPelletManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
    public static final Gson GSON_INSTANCE = (new GsonBuilder()).create();
    public static final List<CruncherPelletData> DATA = Lists.newArrayList();

    public CruncherPelletManager() {
        super(GSON_INSTANCE, "gameplay/cruncher_pellets");
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Species.MOD_ID, "gameplay/cruncher_pellets");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        object.forEach((resourceLocation, jsonElement) -> {
            CruncherPelletData cruncherPelletData = CruncherPelletData.CODEC.parse(JsonOps.INSTANCE, jsonElement).result().orElseThrow();
            DATA.add(cruncherPelletData);
        });
    }

    public record CruncherPelletData(EntityType<?> entityType, ItemStack item) {
        public static final Codec<CruncherPelletData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(CruncherPelletData::entityType),
                        net.minecraft.world.item.ItemStack.CODEC.fieldOf("item").forGetter(CruncherPelletData::item)
                ).apply(instance, CruncherPelletData::new));
    }

}
