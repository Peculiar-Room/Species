package com.ninni.species.world.gen.structure;

import com.mojang.serialization.Codec;
import com.ninni.species.structure.WraptorCoopGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class WraptorCoopStructure extends Structure {
    public static final Codec<WraptorCoopStructure> CODEC = WraptorCoopStructure.createCodec(WraptorCoopStructure::new);

    public WraptorCoopStructure(Config config) {
        super(config);
    }

    @Override
    public Optional<StructurePosition> getStructurePosition(Context context) {
        return WraptorCoopStructure.getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, collector -> this.addPieces(collector, context));
    }

    public void addPieces(StructurePiecesCollector collector, Structure.Context context) {
        ChunkPos chunkPos = context.chunkPos();
        ChunkRandom random = context.random();
        BlockPos pos = new BlockPos(chunkPos.getStartX(), random.nextInt(60) + 40, chunkPos.getStartZ());
        WraptorCoopGenerator.addPieces(context.structureTemplateManager(), pos, 0, BlockRotation.random(random), collector, random);
    }

    @Override
    public StructureType<?> getType() {
        return SpeciesStructureTypes.WRAPTOR_COOP;
    }
}
