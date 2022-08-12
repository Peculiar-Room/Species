package com.ninni.species.world.gen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.ninni.species.structure.WraptorCoopGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class WraptorCoopStructure extends Structure {
    public static final Codec<WraptorCoopStructure> CODEC = WraptorCoopStructure.createCodec(WraptorCoopStructure::new);

    public WraptorCoopStructure(Config config) {
        super(config);
    }

    @Override
    public Optional<StructurePosition> getStructurePosition(Context context) {
        if (!WraptorCoopStructure.canGenerate(context, context.chunkPos().getCenterAtY(0))) {
            return Optional.empty();
        }
        return WraptorCoopStructure.getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, collector -> this.addPieces(collector, context));
    }

    public static boolean canGenerate(Context context, BlockPos blockPos) {
        boolean flag = false;
        VerticalBlockSample verticalBlockSample = context.chunkGenerator().getColumnSample(blockPos.getX(), blockPos.getZ(), context.world(), context.noiseConfig());
        for (int y = 127; y > context.chunkGenerator().getSeaLevel(); y--) {
            if (verticalBlockSample.getState(y - 1).isOpaque() && verticalBlockSample.getState(y).isAir() && verticalBlockSample.getState(y + 4).isAir()) {
                flag = true;
            }
        }
        return flag;
    }

    public void addPieces(StructurePiecesCollector collector, Structure.Context context) {
        ChunkPos chunkPos = context.chunkPos();
        ChunkRandom random = context.random();
        BlockPos pos = new BlockPos(chunkPos.getStartX(), this.getHeight(random), chunkPos.getStartZ());
        WraptorCoopGenerator.addPieces(context.structureTemplateManager(), pos, 0, BlockRotation.random(random), collector, random);
    }

    private int getHeight(ChunkRandom random) {
        return random.nextInt(60) + 40;
    }

    @Override
    public StructureType<?> getType() {
        return SpeciesStructureTypes.WRAPTOR_COOP;
    }
}
