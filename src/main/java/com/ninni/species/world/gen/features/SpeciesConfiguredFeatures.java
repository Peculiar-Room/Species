package com.ninni.species.world.gen.features;

import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.util.List;

public class SpeciesConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> BIRTED_BIRCH = registerConfiguredFeature("birted_birch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIRTED_BIRCH_TREE_FILTERED = registerConfiguredFeature("birted_birch_tree_filtered");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> bootstapContext) {
        HolderGetter<PlacedFeature> holderGetter2 = bootstapContext.lookup(Registries.PLACED_FEATURE);
        FeatureUtils.register(bootstapContext, BIRTED_BIRCH, Feature.TREE, birtedBirch().build());
        FeatureUtils.register(bootstapContext, BIRTED_BIRCH_TREE_FILTERED, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(holderGetter2.getOrThrow(SpeciesPlacedFeatures.BIRTED_BIRCH_TREE_CHECKED), 0.0F)), holderGetter2.getOrThrow(SpeciesPlacedFeatures.BIRTED_BIRCH_TREE_CHECKED)));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerConfiguredFeature(String id) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Species.MOD_ID, id));
    }

    private static TreeConfiguration.TreeConfigurationBuilder builder(Block log, Block leaves, int baseHeight, int firstRandomHeight, int secondRandomHeight, int radius) {
        return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(log), new StraightTrunkPlacer(baseHeight, firstRandomHeight, secondRandomHeight), BlockStateProvider.simple(leaves), new BlobFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1));
    }

    private static TreeConfiguration.TreeConfigurationBuilder birtedBirch() {
        return builder(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 4, 2, 5, 2).decorators(ImmutableList.of(BirtDwellingLogDecorator.INSTANCE)).ignoreVines();
    }

}
