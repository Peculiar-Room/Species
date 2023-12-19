package com.ninni.species.world.gen.features;

import com.ninni.species.Species;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class SpeciesPlacedFeatures {

    public static final ResourceKey<PlacedFeature> BIRTED_BIRCH_TREE_CHECKED = registerPlacedFeature("birted_birch");
    public static final ResourceKey<PlacedFeature> BIRTED_BIRCH_TREES = registerPlacedFeature("birted_birch_trees");
    public static final ResourceKey<PlacedFeature> ORE_FROZEN_MEAT = registerPlacedFeature("ore_frozen_hair");
    public static final ResourceKey<PlacedFeature> ORE_FROZEN_HAIR = registerPlacedFeature("ore_frozen_meat");

    public static void bootstrap(BootstapContext<PlacedFeature> bootstapContext) {
        HolderGetter<ConfiguredFeature<?, ?>> holderGetter = bootstapContext.lookup(Registries.CONFIGURED_FEATURE);
        PlacementUtils.register(bootstapContext, BIRTED_BIRCH_TREE_CHECKED, holderGetter.getOrThrow(SpeciesConfiguredFeatures.BIRTED_BIRCH), PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
        PlacementUtils.register(bootstapContext, BIRTED_BIRCH_TREES, holderGetter.getOrThrow(SpeciesConfiguredFeatures.BIRTED_BIRCH_TREE_FILTERED), VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(75)));
        PlacementUtils.register(bootstapContext, ORE_FROZEN_MEAT, holderGetter.getOrThrow(SpeciesConfiguredFeatures.ORE_FROZEN_MEAT), CountPlacement.of(80), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());
        PlacementUtils.register(bootstapContext, ORE_FROZEN_HAIR, holderGetter.getOrThrow(SpeciesConfiguredFeatures.ORE_FROZEN_HAIR), CountPlacement.of(80), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());
    }

    public static ResourceKey<PlacedFeature> registerPlacedFeature(String id) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(Species.MOD_ID, id));
    }
}
