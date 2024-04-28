package com.ninni.species.data;

import com.ninni.species.registry.SpeciesStructureKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class SpeciesStructureProvider extends FabricDynamicRegistryProvider {

    public SpeciesStructureProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        add(registries, entries, SpeciesStructureKeys.WRAPTOR_COOP);
        add(registries, entries, SpeciesStructureKeys.PALEONTOLOGY_DIG_SITE);
    }

    private void add(HolderLookup.Provider registries, Entries entries, ResourceKey<Structure> resourceKey) {
        final HolderLookup.RegistryLookup<Structure> configuredFeatureRegistryLookup = registries.lookupOrThrow(Registries.STRUCTURE);

        entries.add(resourceKey, configuredFeatureRegistryLookup.getOrThrow(resourceKey).value());
    }

    @Override
    public String getName() {
        return "worldgen/structure";
    }
}
