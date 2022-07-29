package com.ninni.species.world.gen.structure;

import com.mojang.serialization.Codec;
import com.ninni.species.Species;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class SpeciesStructureTypes {
    public static final StructureType<WraptorCoopStructure> WRAPTOR_COOP = register("wraptor_coop", WraptorCoopStructure.CODEC);

    private static <S extends Structure> StructureType<S> register(String id, Codec<S> codec) {
        return Registry.register(Registry.STRUCTURE_TYPE, new Identifier(Species.MOD_ID, id), () -> codec);
    }
}
