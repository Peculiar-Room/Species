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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

public class GooberGooManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
    public static final Gson GSON_INSTANCE = (new GsonBuilder()).create();
    public static final List<GooberGooManager.GooberGooData> DATA = Lists.newArrayList();

    public GooberGooManager() {
        super(GSON_INSTANCE, "gameplay/goober_goo");
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Species.MOD_ID, "gameplay/goober_goo");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {   object.forEach((resourceLocation, jsonElement) -> {
        GooberGooManager.GooberGooData cruncherPelletData = GooberGooManager.GooberGooData.CODEC.parse(JsonOps.INSTANCE, jsonElement).result().orElseThrow();
        DATA.add(cruncherPelletData);
    });
    }

    public record GooberGooData(Block input, Block output) {
        public static final Codec<GooberGooManager.GooberGooData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("input").forGetter(GooberGooManager.GooberGooData::input),
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output").forGetter(GooberGooManager.GooberGooData::output)
                ).apply(instance, GooberGooManager.GooberGooData::new));
    }
}
