package com.ninni.species.init;

import com.ninni.species.Species;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class SpeciesPlacedFeatures {

    public static final ResourceKey<PlacedFeature> BIRTED_BIRCH_TREE_CHECKED = registerPlacedFeature("birted_birch");
    public static final ResourceKey<PlacedFeature> BIRTED_BIRCH_TREES = registerPlacedFeature("birted_birch_trees");
    public static final ResourceKey<PlacedFeature> MAMMUTILATION_REMNANT = registerPlacedFeature("mammutilation_remnant");

    public static void bootstrap(BootstapContext<PlacedFeature> bootstapContext) {
        HolderGetter<ConfiguredFeature<?, ?>> holderGetter = bootstapContext.lookup(Registries.CONFIGURED_FEATURE);
        PlacementUtils.register(bootstapContext, BIRTED_BIRCH_TREE_CHECKED, holderGetter.getOrThrow(SpeciesConfiguredFeatures.BIRTED_BIRCH), PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
        PlacementUtils.register(bootstapContext, BIRTED_BIRCH_TREES, holderGetter.getOrThrow(SpeciesConfiguredFeatures.BIRTED_BIRCH_TREE_FILTERED), VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(50)));
        PlacementUtils.register(bootstapContext, MAMMUTILATION_REMNANT, holderGetter.getOrThrow(SpeciesConfiguredFeatures.MAMMUTILATION_REMNANT), RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(20), VerticalAnchor.absolute(200)), BiomeFilter.biome());
    }

    public static ResourceKey<PlacedFeature> registerPlacedFeature(String id) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(Species.MOD_ID, id));
    }

}
