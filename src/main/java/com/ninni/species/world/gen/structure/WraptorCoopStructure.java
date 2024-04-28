package com.ninni.species.world.gen.structure;

import com.mojang.serialization.Codec;
import com.ninni.species.registry.SpeciesStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class WraptorCoopStructure extends Structure {
    public static final Codec<WraptorCoopStructure> CODEC = WraptorCoopStructure.simpleCodec(WraptorCoopStructure::new);

    public WraptorCoopStructure(StructureSettings config) {
        super(config);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!WraptorCoopStructure.canGenerate(context, context.chunkPos().getMiddleBlockPosition(0))) {
            return Optional.empty();
        }
        return WraptorCoopStructure.onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, collector -> this.addPieces(collector, context));
    }

    public static boolean canGenerate(GenerationContext context, BlockPos blockPos) {
        boolean flag = false;
        NoiseColumn verticalBlockSample = context.chunkGenerator().getBaseColumn(blockPos.getX(), blockPos.getZ(), context.heightAccessor(), context.randomState());
        for (int y = 127; y > context.chunkGenerator().getSeaLevel(); y--) {
            if (verticalBlockSample.getBlock(y - 1).canOcclude() && verticalBlockSample.getBlock(y).isAir() && verticalBlockSample.getBlock(y + 4).isAir()) {
                flag = true;
            }
        }
        return flag;
    }

    public void addPieces(StructurePiecesBuilder collector, Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        WorldgenRandom random = context.random();
        BlockPos pos = new BlockPos(chunkPos.getMinBlockX(), this.getHeight(random), chunkPos.getMinBlockZ());
        WraptorCoopGenerator.addPieces(context.structureTemplateManager(), pos, 0, Rotation.getRandom(random), collector, random);
    }

    private int getHeight(WorldgenRandom random) {
        return random.nextInt(60) + 40;
    }

    @Override
    public StructureType<?> type() {
        return SpeciesStructureTypes.WRAPTOR_COOP;
    }

}
