package com.ninni.species.registry;

import com.mojang.serialization.Codec;
import com.ninni.species.Species;
import com.ninni.species.world.gen.structure.PaleontologyDigSiteStructure;
import com.ninni.species.world.gen.structure.WraptorCoopStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class SpeciesStructureTypes {
    public static final StructureType<WraptorCoopStructure> WRAPTOR_COOP = register("wraptor_coop", WraptorCoopStructure.CODEC);
    public static final StructureType<PaleontologyDigSiteStructure> PALEONTOLOGY_DIG_SITE = register("paleontology_dig_site", PaleontologyDigSiteStructure.CODEC);

    private static <S extends Structure> StructureType<S> register(String id, Codec<S> codec) {
        return Registry.register(BuiltInRegistries.STRUCTURE_TYPE, new ResourceLocation(Species.MOD_ID, id), () -> codec);
    }
}
