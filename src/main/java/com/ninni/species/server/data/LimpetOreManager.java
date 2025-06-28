package com.ninni.species.server.data;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ninni.species.Species;
import com.ninni.species.server.entity.mob.update_1.Limpet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class LimpetOreManager extends SimpleJsonResourceReloadListener {
    public static final Gson GSON_INSTANCE = (new GsonBuilder()).create();
    public static final List<LimpetOreData> DATA = Lists.newArrayList();
    public static final String DEFAULT_VARIANT_NAME = "species:shell";

    public LimpetOreManager() {
        super(GSON_INSTANCE, "gameplay/limpet_ores");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DATA.clear();

        object.forEach((resourceLocation, jsonElement) -> {
            LimpetOreData data = LimpetOreData.CODEC.parse(JsonOps.INSTANCE, jsonElement).result()
                    .orElseGet(() -> {
                        Species.LOGGER.error("Failed to read Limpet ore entry for resource {}", resourceLocation);
                        return null;
                    });

            if (data != null) DATA.add(data);
        });
        Species.LOGGER.info("Loaded {} Limpet ore entries", DATA.size());
    }

    public record LimpetOreData(ResourceLocation id, Item item, Block block,  Optional<Integer> maxCount, Optional<Location> location, Optional<Integer> spawnWeight, Optional<Integer> maxSpawnHeight, Optional<Integer> minSpawnHeight) {
        public static final Codec<Location> LOCATION_CODEC = Codec.either(ResourceKey.codec(Registries.BIOME), TagKey.hashedCodec(Registries.BIOME)).xmap(Location::new, Location::get);

        public static final Codec<LimpetOreData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("id").forGetter(LimpetOreData::id),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(LimpetOreData::item),
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(LimpetOreData::block),
                        Codec.INT.optionalFieldOf("maxCount").forGetter(LimpetOreData::maxCount),
                        LOCATION_CODEC.optionalFieldOf("location").forGetter(LimpetOreData::location),
                        Codec.INT.optionalFieldOf("spawnWeight").forGetter(LimpetOreData::spawnWeight),
                        Codec.INT.optionalFieldOf("maxSpawnHeight").forGetter(LimpetOreData::maxSpawnHeight),
                        Codec.INT.optionalFieldOf("minSpawnHeight").forGetter(LimpetOreData::minSpawnHeight)
                ).apply(instance, LimpetOreData::new));
    }


    public static void setOre(Limpet limpet) {
        Holder<Biome> holder = limpet.level().getBiome(limpet.blockPosition());
        int yLevel = limpet.blockPosition().getY();
        int maxYLevel = limpet.level().getMaxBuildHeight();
        int minYLevel = limpet.level().getMinBuildHeight();

        //Choosing the ore
        List<WeightedEntry> weightedEntries = buildWeightedEntries(data -> data.location().isPresent()
                && data.location().get().matchesBiome(holder)
                && yLevel <= data.maxSpawnHeight().orElse(maxYLevel)
                && yLevel > data.minSpawnHeight().orElse(minYLevel)
        );

        if (weightedEntries.isEmpty()) {
            weightedEntries = buildWeightedEntries(data -> data.location().isEmpty()
                    && yLevel <= data.maxSpawnHeight().orElse(maxYLevel)
                    && yLevel > data.minSpawnHeight().orElse(minYLevel)
            );
        }

        WeightedEntry variant = chooseWeightedVariant(weightedEntries, limpet);


        if (variant != null) {
            limpet.setOre(variant.id().toString());
            limpet.setMaxCount(variant.maxCount());
            limpet.setOreItemStack(variant.stack());
            limpet.setOreBlockState(variant.state());
        } else {
            setNoOre(limpet);
        }
    }

    public static void setNoOre(Limpet limpet) {
        limpet.setOre(DEFAULT_VARIANT_NAME);
        limpet.setMaxCount(0);
        limpet.setOreItemStack(Items.BONE_MEAL.getDefaultInstance());
        limpet.setOreBlockState(Blocks.STONE.defaultBlockState());
    }

    private static WeightedEntry chooseWeightedVariant(List<WeightedEntry> entries, Limpet limpet) {
        if (entries.isEmpty()) return null;

        int totalWeight = entries.stream().mapToInt(WeightedEntry::weight).sum();
        int randomWeight = limpet.level().random.nextInt(totalWeight);
        if (totalWeight <= 0) return null;

        int cumulativeWeight = 0;

        for (WeightedEntry entry : entries) {
            cumulativeWeight += entry.weight();
            if (randomWeight < cumulativeWeight) return entry;
        }
        return null;
    }

    private static List<WeightedEntry> buildWeightedEntries(Predicate<LimpetOreData> filter) {
        List<WeightedEntry> weightedEntries = new ArrayList<>();
        for (LimpetOreManager.LimpetOreData data : LimpetOreManager.DATA) {
            if (data.item != Items.AIR && data.block != Blocks.AIR) {
                if (filter.test(data)) {
                    int weight = data.spawnWeight().orElse(0);
                    ItemStack itemStack = data.item() != null ? data.item().getDefaultInstance() : Items.BONE_MEAL.getDefaultInstance();
                    weightedEntries.add(new WeightedEntry(data.id(), weight, data.maxCount().orElse(0), itemStack, data.block().defaultBlockState()));
                }
            }
        }
        return weightedEntries;
    }

    public record WeightedEntry(ResourceLocation id, int weight, int maxCount, ItemStack stack, BlockState state) {}

    public static class Location {
        private final Either<ResourceKey<Biome>, TagKey<Biome>> value;

        public Location(Either<ResourceKey<Biome>, TagKey<Biome>> value) {
            this.value = value;
        }

        public Either<ResourceKey<Biome>, TagKey<Biome>> get() {
            return value;
        }

        public boolean matchesBiome(Holder<Biome> biome) {
            if (value.left().isPresent()) return biome.is(value.left().get());
            return biome.is(value.right().get());
        }
    }
}