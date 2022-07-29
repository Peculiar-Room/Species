package com.ninni.species.structure;

import com.ninni.species.Species;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SpeciesStructurePieceTypes {
    public static final StructurePieceType WRAPTOR_COOP = register("wraptor_coop", WraptorCoopGenerator.Piece::new);

    private static StructurePieceType register(String id, StructurePieceType type) {
        return Registry.register(Registry.STRUCTURE_PIECE, new Identifier(Species.MOD_ID, id), type);
    }

    private static StructurePieceType register(String id, StructurePieceType.ManagerAware type) {
        return register(id, (StructurePieceType) type);
    }
}
