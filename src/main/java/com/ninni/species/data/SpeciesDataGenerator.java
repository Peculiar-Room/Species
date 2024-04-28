package com.ninni.species.data;

import com.ninni.species.registry.SpeciesStructureSets;
import com.ninni.species.world.gen.features.SpeciesConfiguredFeatures;
import com.ninni.species.world.gen.features.SpeciesPlacedFeatures;
import com.ninni.species.registry.SpeciesStructures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class SpeciesDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(SpeciesConfiguredFeaturesProvider::new);
        pack.addProvider(SpeciesPlacedFeaturesProvider::new);
        pack.addProvider(SpeciesStructureProvider::new);
        pack.addProvider(SpeciesStructureSetProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder
                .add(Registries.CONFIGURED_FEATURE, SpeciesConfiguredFeatures::bootstrap)
                .add(Registries.PLACED_FEATURE, SpeciesPlacedFeatures::bootstrap)
                .add(Registries.STRUCTURE, SpeciesStructures::bootstrap)
                .add(Registries.STRUCTURE_SET, SpeciesStructureSets::bootstrap);
    }
}
