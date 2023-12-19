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
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class SpeciesPlacedFeatures {

    public static final ResourceKey<PlacedFeature> BIRTED_BIRCH_TREE_CHECKED = registerPlacedFeature("birted_birch");
    public static final ResourceKey<PlacedFeature> BIRTED_BIRCH_TREES = registerPlacedFeature("birted_birch_trees");

    public static void bootstrap(BootstapContext<PlacedFeature> bootstapContext) {
        HolderGetter<ConfiguredFeature<?, ?>> holderGetter = bootstapContext.lookup(Registries.CONFIGURED_FEATURE);
        PlacementUtils.register(bootstapContext, BIRTED_BIRCH_TREE_CHECKED, holderGetter.getOrThrow(SpeciesConfiguredFeatures.BIRTED_BIRCH), PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
        PlacementUtils.register(bootstapContext, BIRTED_BIRCH_TREES, holderGetter.getOrThrow(SpeciesConfiguredFeatures.BIRTED_BIRCH_TREE_FILTERED), VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(75)));
    }

    public static ResourceKey<PlacedFeature> registerPlacedFeature(String id) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(Species.MOD_ID, id));
    }
}
