package com.ninni.species.data;

import com.ninni.species.registry.SpeciesStructureSets;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class SpeciesStructureSetProvider extends FabricDynamicRegistryProvider {

    public SpeciesStructureSetProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        add(registries, entries, SpeciesStructureSets.WRAPTOR_COOPS);
        add(registries, entries, SpeciesStructureSets.PALEONTOLOGY_DIG_SITE);
    }

    private void add(HolderLookup.Provider registries, Entries entries, ResourceKey<StructureSet> resourceKey) {
        final HolderLookup.RegistryLookup<StructureSet> configuredFeatureRegistryLookup = registries.lookupOrThrow(Registries.STRUCTURE_SET);

        entries.add(resourceKey, configuredFeatureRegistryLookup.getOrThrow(resourceKey).value());
    }

    @Override
    public String getName() {
        return "worldgen/structure_set";
    }
}
