package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.world.gen.structure.PaleontologyDigSiteGenerator;
import com.ninni.species.world.gen.structure.WraptorCoopGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class SpeciesStructurePieceTypes {
    public static final StructurePieceType WRAPTOR_COOP = register("wraptor_coop", WraptorCoopGenerator.Piece::new);
    public static final StructurePieceType PALEONTOLOGY_DIG_SITE = register("paleontology_dig_site", PaleontologyDigSiteGenerator.Piece::new);

    private static StructurePieceType register(String id, StructurePieceType type) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, new ResourceLocation(Species.MOD_ID, id), type);
    }

    private static StructurePieceType register(String id, StructurePieceType.StructureTemplateType type) {
        return register(id, (StructurePieceType) type);
    }
}
