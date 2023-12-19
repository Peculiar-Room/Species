package com.ninni.species.data;

import com.ninni.species.world.gen.features.SpeciesConfiguredFeatures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class SpeciesConfiguredFeaturesProvider extends FabricDynamicRegistryProvider {

    public SpeciesConfiguredFeaturesProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        add(registries, entries, SpeciesConfiguredFeatures.BIRTED_BIRCH);
        add(registries, entries, SpeciesConfiguredFeatures.BIRTED_BIRCH_TREE_FILTERED);
        add(registries, entries, SpeciesConfiguredFeatures.ORE_FROZEN_MEAT);
        add(registries, entries, SpeciesConfiguredFeatures.ORE_FROZEN_HAIR);
    }

    private void add(HolderLookup.Provider registries, Entries entries, ResourceKey<ConfiguredFeature<?, ?>> resourceKey) {
        final HolderLookup.RegistryLookup<ConfiguredFeature<?, ?>> configuredFeatureRegistryLookup = registries.lookupOrThrow(Registries.CONFIGURED_FEATURE);

        entries.add(resourceKey, configuredFeatureRegistryLookup.getOrThrow(resourceKey).value());
    }

    @Override
    public String getName() {
        return "worldgen/configured_feature";
    }
}
