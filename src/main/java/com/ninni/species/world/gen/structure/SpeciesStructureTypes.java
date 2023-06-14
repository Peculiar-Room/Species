package com.ninni.species.world.gen.structure;

import com.mojang.serialization.Codec;
import com.ninni.species.Species;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class SpeciesStructureTypes {
    public static final StructureType<WraptorCoopStructure> WRAPTOR_COOP = register("wraptor_coop", WraptorCoopStructure.CODEC);

    private static <S extends Structure> StructureType<S> register(String id, Codec<S> codec) {
        return Registry.register(BuiltInRegistries.STRUCTURE_TYPE, new ResourceLocation(Species.MOD_ID, id), () -> codec);
    }
}
