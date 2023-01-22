package com.ninni.species.world.gen.structure;

import com.ninni.species.tag.SpeciesTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

import java.util.Map;

public class SpeciesStructures {
    public static final Holder<Structure> WRAPTOR_COOP = register(SpeciesStructureKeys.WRAPTOR_COOP, new WraptorCoopStructure(createConfig(SpeciesTags.WRAPTOR_COOP_HAS_STRUCTURE, TerrainAdjustment.BEARD_BOX)));

    private static Holder<Structure> register(ResourceKey<Structure> key, Structure structure) {
        return BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, key, structure);
    }

    private static Structure.StructureSettings createConfig(TagKey<Biome> tag, Map<MobCategory, StructureSpawnOverride> spawns, GenerationStep.Decoration featureStep, TerrainAdjustment terrainAdaptation) {
        return new Structure.StructureSettings(getOrCreateBiomeTag(tag), spawns, featureStep, terrainAdaptation);
    }

    private static Structure.StructureSettings createConfig(TagKey<Biome> tag, GenerationStep.Decoration step, TerrainAdjustment adaptation) {
        return createConfig(tag, Map.of(), step, adaptation);
    }

    private static Structure.StructureSettings createConfig(TagKey<Biome> tag, TerrainAdjustment terrainAdaptation) {
        return createConfig(tag, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, terrainAdaptation);
    }

    private static HolderSet<Biome> getOrCreateBiomeTag(TagKey<Biome> key) {
        return BuiltinRegistries.BIOME.getOrCreateTag(key);
    }
}
