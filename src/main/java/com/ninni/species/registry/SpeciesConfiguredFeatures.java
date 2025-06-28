package com.ninni.species.registry;

import com.google.common.collect.ImmutableList;
import com.ninni.species.Species;
import com.ninni.species.server.world.gen.features.BirtDwellingLogDecorator;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public class SpeciesConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> BIRTED_BIRCH = registerConfiguredFeature("birted_birch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIRTED_BIRCH_TREE_FILTERED = registerConfiguredFeature("birted_birch_tree_filtered");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MAMMUTILATION_REMNANT = registerConfiguredFeature("mammutilation_remnant");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ALPHACENE_MUSHROOM = registerConfiguredFeature("alphacene_mushroom");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> bootstapContext) {
        HolderGetter<PlacedFeature> holderGetter2 = bootstapContext.lookup(Registries.PLACED_FEATURE);
        FeatureUtils.register(bootstapContext, BIRTED_BIRCH, Feature.TREE, birtedBirch().build());
        FeatureUtils.register(bootstapContext, BIRTED_BIRCH_TREE_FILTERED, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(holderGetter2.getOrThrow(SpeciesPlacedFeatures.BIRTED_BIRCH_TREE_CHECKED), 0.0F)), holderGetter2.getOrThrow(SpeciesPlacedFeatures.BIRTED_BIRCH_TREE_CHECKED)));
        FeatureUtils.register(bootstapContext, MAMMUTILATION_REMNANT, Feature.GEODE, new GeodeConfiguration(
                new GeodeBlockSettings(
                        BlockStateProvider.simple(SpeciesBlocks.FROZEN_MEAT.get()),
                        BlockStateProvider.simple(SpeciesBlocks.FROZEN_MEAT.get()),
                        BlockStateProvider.simple(SpeciesBlocks.FROZEN_HAIR.get()),
                        BlockStateProvider.simple(SpeciesBlocks.FROZEN_HAIR.get()),
                        BlockStateProvider.simple(Blocks.PACKED_ICE),
                        List.of(Blocks.AIR.defaultBlockState()),
                        BlockTags.FEATURES_CANNOT_REPLACE,
                        SpeciesTags.MAMMUTILATION_REMNANT_INVALID_BLOCKS
                ),
                new GeodeLayerSettings(0.7D, 1.2D, 2.2D, 3.2D),
                new GeodeCrackSettings(0.95D, 2.0D, 2),
                0.0,
                0.35,
                true,
                UniformInt.of(4, 6),
                UniformInt.of(3, 4),
                UniformInt.of(1, 2),
                -16,
                16,
                0.05D,
                1
        ));
        FeatureUtils.register(bootstapContext, ALPHACENE_MUSHROOM, SpeciesFeatures.ALPHACENE_MUSHROOM.get(), FeatureConfiguration.NONE);
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
