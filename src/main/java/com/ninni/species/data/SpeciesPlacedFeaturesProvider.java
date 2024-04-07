package com.ninni.species.data;

import com.ninni.species.world.gen.features.SpeciesPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class SpeciesPlacedFeaturesProvider extends FabricDynamicRegistryProvider {

    public SpeciesPlacedFeaturesProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        add(registries, entries, SpeciesPlacedFeatures.BIRTED_BIRCH_TREE_CHECKED);
        add(registries, entries, SpeciesPlacedFeatures.BIRTED_BIRCH_TREES);
        add(registries, entries, SpeciesPlacedFeatures.MAMMUTILATION_REMNANT);
    }

    private void add(HolderLookup.Provider registries, Entries entries, ResourceKey<PlacedFeature> resourceKey) {
        final HolderLookup.RegistryLookup<PlacedFeature> configuredFeatureRegistryLookup = registries.lookupOrThrow(Registries.PLACED_FEATURE);

        entries.add(resourceKey, configuredFeatureRegistryLookup.getOrThrow(resourceKey).value());
    }

    @Override
    public String getName() {
        return "worldgen/placed_features";
    }
}
