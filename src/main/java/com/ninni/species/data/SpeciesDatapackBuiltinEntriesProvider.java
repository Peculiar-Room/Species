package com.ninni.species.data;

import com.ninni.species.Species;
import com.ninni.species.structure.SpeciesStructureSets;
import com.ninni.species.world.gen.features.SpeciesBiomeModifier;
import com.ninni.species.world.gen.features.SpeciesConfiguredFeatures;
import com.ninni.species.world.gen.features.SpeciesPlacedFeatures;
import com.ninni.species.world.gen.structure.SpeciesStructures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SpeciesDatapackBuiltinEntriesProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, SpeciesConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, SpeciesPlacedFeatures::bootstrap)
            .add(Registries.STRUCTURE_SET, SpeciesStructureSets::bootstrap)
            .add(Registries.STRUCTURE, SpeciesStructures::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, SpeciesBiomeModifier::bootstrap);

    public SpeciesDatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Species.MOD_ID));
    }

}
