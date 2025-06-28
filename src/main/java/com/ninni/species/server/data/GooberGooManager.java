package com.ninni.species.server.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.server.packet.GooberGooSyncPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class GooberGooManager extends SimpleJsonResourceReloadListener {
    public static final Gson GSON_INSTANCE = (new GsonBuilder()).create();
    public static final List<GooberGooManager.GooberGooData> DATA = Lists.newArrayList();

    public GooberGooManager() {
        super(GSON_INSTANCE, "gameplay/goober_goo");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DATA.clear();

        object.forEach((resourceLocation, jsonElement) -> {
            GooberGooManager.GooberGooData data = GooberGooManager.GooberGooData.CODEC.parse(JsonOps.INSTANCE, jsonElement).result()
                    .orElseGet(() -> {
                        Species.LOGGER.error("Failed to read Goober goo recipe for resource {}", resourceLocation);
                        return null;
                    });

            if (data != null && data.input != Blocks.AIR && data.output != Blocks.AIR) DATA.add(data);
        });

        Species.LOGGER.info("Loaded {} Goober goo recipes", DATA.size());
    }

    public void onDatapackSync(@Nullable ServerPlayer player) {
        if (DATA.isEmpty()) return;

        BiMap<ResourceLocation, GooberGooData> registryMap = HashBiMap.create();
        for (GooberGooData data : DATA) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(data.input());
            if (id != null) {
                registryMap.put(id, data);
            }
        }

        if (player == null) {
            SpeciesNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new GooberGooSyncPacket(registryMap));
        } else {
            SpeciesNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new GooberGooSyncPacket(registryMap));
        }
    }


    public void synchronizeRegistryForClient(BiMap<ResourceLocation, GooberGooData> newData) {
        DATA.clear();
        DATA.addAll(newData.values());
    }

    public record GooberGooData(Block input, Block output) {
        public static final Codec<GooberGooManager.GooberGooData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("input").forGetter(GooberGooManager.GooberGooData::input),
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output").forGetter(GooberGooManager.GooberGooData::output)
                ).apply(instance, GooberGooManager.GooberGooData::new));


        public static GooberGooData fromNetwork(FriendlyByteBuf buf) {
            Block input = buf.readById(BuiltInRegistries.BLOCK);
            Block output = buf.readById(BuiltInRegistries.BLOCK);
            return new GooberGooData(input, output);
        }

        public void toNetwork(FriendlyByteBuf buf) {
            buf.writeId(BuiltInRegistries.BLOCK, this.input);
            buf.writeId(BuiltInRegistries.BLOCK, this.output);
        }
    }

}