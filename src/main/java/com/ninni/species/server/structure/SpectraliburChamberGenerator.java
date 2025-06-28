package com.ninni.species.server.structure;

import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class SpectraliburChamberGenerator {

    public static void addPieces(StructureTemplateManager manager, BlockPos pos, StructurePieceAccessor holder) {
        holder.addPiece(new SpectraliburChamberGenerator.Piece(manager, new ResourceLocation(Species.MOD_ID, "spectralibur_chamber"), pos));
    }

    public static class Piece extends TemplateStructurePiece {

        public Piece(StructureTemplateManager manager, ResourceLocation id, BlockPos pos) {
            super(SpeciesStructurePieceTypes.SPECTRALIBUR_CHAMBER.get(), 20, manager, id, id.toString(), new StructurePlaceSettings(), pos);
        }

        public Piece(StructureTemplateManager manager, CompoundTag nbt) {
            super(SpeciesStructurePieceTypes.SPECTRALIBUR_CHAMBER.get(), nbt, manager, id -> new StructurePlaceSettings());
        }

        @Override
        public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            BlockPos blockPos1 = this.templatePosition;
            this.templatePosition = this.templatePosition.below(20);
            super.postProcess(world, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, blockPos);
            this.templatePosition = blockPos1;
        }

        @Override
        protected void handleDataMarker(String s, BlockPos blockPos, ServerLevelAccessor serverLevelAccessor, RandomSource randomSource, BoundingBox boundingBox) {
        }

    }
}
