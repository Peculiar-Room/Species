package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.structure.LibraGenerator;
import com.ninni.species.server.structure.PaleontologyDigSiteGenerator;
import com.ninni.species.server.structure.SpectraliburChamberGenerator;
import com.ninni.species.server.structure.WraptorCoopGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesStructurePieceTypes {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, Species.MOD_ID);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> WRAPTOR_COOP = register("wraptor_coop", WraptorCoopGenerator.Piece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> PALEONTOLOGY_DIG_SITE = register("paleontology_dig_site", PaleontologyDigSiteGenerator.Piece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> LIBRA = register("libra", LibraGenerator.Piece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> SPECTRALIBUR_CHAMBER = register("spectralibur_chamber", SpectraliburChamberGenerator.Piece::new);

    private static DeferredHolder<StructurePieceType, StructurePieceType> register(String id, StructurePieceType type) {
        return STRUCTURE_PIECE_TYPES.register(id, () -> type);
    }

    private static DeferredHolder<StructurePieceType, StructurePieceType> register(String id, StructurePieceType.StructureTemplateType type) {
        return register(id, (StructurePieceType) type);
    }
}
