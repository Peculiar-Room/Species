package com.ninni.species.world.gen.features;

import com.ninni.species.Species;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.util.List;

public class SpeciesFeatures {
    public static void init() { }

    public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> BIRTED_BIRCH = register("birted_birch", Feature.TREE, birtedBirch().build());
    public static final RegistryEntry<PlacedFeature> BIRTED_BIRCH_TREE_CHECKED = register("birted_birch", BIRTED_BIRCH, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
    public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> BIRTED_BIRCH_TREE_FILTERED = register("birted_birch_tree_filtered", Feature.RANDOM_SELECTOR, new RandomFeatureConfig(List.of(new RandomFeatureEntry(BIRTED_BIRCH_TREE_CHECKED, 0.0F)), BIRTED_BIRCH_TREE_CHECKED));
    public static final RegistryEntry<PlacedFeature> BIRTED_BIRCH_TREES = register("birted_birch_trees", BIRTED_BIRCH_TREE_FILTERED, VegetationPlacedFeatures.modifiers(RarityFilterPlacementModifier.of(50)));

    private static TreeFeatureConfig.Builder builder(Block log, Block leaves, int baseHeight, int firstRandomHeight, int secondRandomHeight, int radius) {
        return new TreeFeatureConfig.Builder(BlockStateProvider.of(log), new StraightTrunkPlacer(baseHeight, firstRandomHeight, secondRandomHeight), BlockStateProvider.of(leaves), new BlobFoliagePlacer(ConstantIntProvider.create(radius), ConstantIntProvider.create(0), 3), new TwoLayersFeatureSize(1, 0, 1));
    }

    private static TreeFeatureConfig.Builder birtedBirch() {
        return builder(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 4, 2, 5, 2).decorators(ImmutableList.of(BirtDwellingLogDecorator.INSTANCE)).ignoreVines();
    }

    public static RegistryEntry<PlacedFeature> register(String id, RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, PlacementModifier... modifiers) {
        return register(id, registryEntry, List.of(modifiers));
    }

    public static RegistryEntry<PlacedFeature> register(String id, RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, List<PlacementModifier> modifiers) {
        return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, new Identifier(Species.MOD_ID, id), new PlacedFeature(RegistryEntry.upcast(registryEntry), List.copyOf(modifiers)));
    }

    public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<FC, ?>> register(String id, F feature, FC config) {
        return BuiltinRegistries.addCasted(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Species.MOD_ID, id).toString(), new ConfiguredFeature<>(feature, config));
    }

}
