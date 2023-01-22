package com.ninni.species.structure;

import com.ninni.species.Species;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class SpeciesStructurePieceTypes {
    public static final StructurePieceType WRAPTOR_COOP = register("wraptor_coop", WraptorCoopGenerator.Piece::new);

    private static StructurePieceType register(String id, StructurePieceType type) {
        return Registry.register(Registry.STRUCTURE_PIECE, new ResourceLocation(Species.MOD_ID, id), type);
    }

    private static StructurePieceType register(String id, StructurePieceType.StructureTemplateType type) {
        return register(id, (StructurePieceType) type);
    }
}
