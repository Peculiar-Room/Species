package com.ninni.species.world.gen.structure;

import com.ninni.species.SpeciesTags;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.structure.Structure;

import java.util.Map;

public class SpeciesStructures {
    public static final RegistryEntry<Structure> WRAPTOR_COOP = register(SpecieseStructureKeys.WRAPTOR_COOP, new WraptorCoopStructure(createConfig(SpeciesTags.WRAPTOR_COOP_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_BOX)));

    private static RegistryEntry<Structure> register(RegistryKey<Structure> key, Structure structure) {
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE, key, structure);
    }

    private static Structure.Config createConfig(TagKey<Biome> tag, Map<SpawnGroup, StructureSpawns> spawns, GenerationStep.Feature featureStep, StructureTerrainAdaptation terrainAdaptation) {
        return new Structure.Config(getOrCreateBiomeTag(tag), spawns, featureStep, terrainAdaptation);
    }

    private static Structure.Config createConfig(TagKey<Biome> tag, GenerationStep.Feature step, StructureTerrainAdaptation adaptation) {
        return createConfig(tag, Map.of(), step, adaptation);
    }

    private static Structure.Config createConfig(TagKey<Biome> tag, StructureTerrainAdaptation terrainAdaptation) {
        return createConfig(tag, Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, terrainAdaptation);
    }

    private static RegistryEntryList<Biome> getOrCreateBiomeTag(TagKey<Biome> key) {
        return BuiltinRegistries.BIOME.getOrCreateEntryList(key);
    }
}
