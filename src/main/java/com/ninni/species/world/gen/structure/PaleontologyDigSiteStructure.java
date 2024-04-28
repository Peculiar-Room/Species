package com.ninni.species.world.gen.structure;

import com.mojang.serialization.Codec;
import com.ninni.species.registry.SpeciesStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class PaleontologyDigSiteStructure extends Structure {
    public static final Codec<PaleontologyDigSiteStructure> CODEC = PaleontologyDigSiteStructure.simpleCodec(PaleontologyDigSiteStructure::new);

    public PaleontologyDigSiteStructure(StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext generationContext) {
        WorldgenRandom worldgenRandom = generationContext.random();
        int x = generationContext.chunkPos().getMinBlockX() + worldgenRandom.nextInt(16);
        int z = generationContext.chunkPos().getMinBlockZ() + worldgenRandom.nextInt(16);
        int seaLevel = generationContext.chunkGenerator().getSeaLevel();

        int height = 128;

        NoiseColumn noiseColumn = generationContext.chunkGenerator().getBaseColumn(x, z, generationContext.heightAccessor(), generationContext.randomState());

        for (int y = height; y > seaLevel; y--) {

            if (!noiseColumn.getBlock(y).isAir() && noiseColumn.getBlock(y + 1).isAir()) break;

            height--;
        }

        if (height <= seaLevel) {
            return Optional.empty();
        }

        BlockPos blockPos = new BlockPos(x, height, z);

        return Optional.of(new Structure.GenerationStub(blockPos, structurePiecesBuilder -> PaleontologyDigSiteGenerator.addPieces(generationContext.structureTemplateManager(), blockPos, structurePiecesBuilder, worldgenRandom)));
    }

    @Override
    public StructureType<?> type() {
        return SpeciesStructureTypes.PALEONTOLOGY_DIG_SITE;
    }
}
