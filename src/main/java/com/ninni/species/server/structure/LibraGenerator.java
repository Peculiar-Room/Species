package com.ninni.species.server.structure;

import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesLootTables;
import com.ninni.species.registry.SpeciesStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;

public class LibraGenerator {

    public static void addPieces(StructureTemplateManager manager, BlockPos pos, Rotation rotation, StructurePieceAccessor holder) {
        holder.addPiece(new LibraGenerator.Piece(manager, new ResourceLocation(Species.MOD_ID, "libra"), pos, rotation));
    }

    public static class Piece extends TemplateStructurePiece {
        public static final String ROTATION_KEY = "Rotation";

        public Piece(StructureTemplateManager manager, ResourceLocation id, BlockPos pos, Rotation rotation) {
            super(SpeciesStructurePieceTypes.LIBRA.get(), 64, manager, id, id.toString(), createPlacementData(rotation), pos);
        }

        public Piece(StructureTemplateManager manager, CompoundTag nbt) {
            super(SpeciesStructurePieceTypes.LIBRA.get(), nbt, manager, id -> createPlacementData(Rotation.valueOf(nbt.getString(ROTATION_KEY))));
        }

        private static StructurePlaceSettings createPlacementData(Rotation rotation) {
            return new StructurePlaceSettings().setRotation(rotation).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
            super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
            compoundTag.putString(ROTATION_KEY, this.placeSettings.getRotation().name());
        }

        @Override
        public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {

            super.postProcess(world, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, blockPos);
        }

        @Override
        protected void handleDataMarker(String metadata, BlockPos blockPos, ServerLevelAccessor serverLevelAccessor, RandomSource random, BoundingBox boundingBox) {
            if ("CHEST".equals(metadata)) {
                Rotation rotation = this.placeSettings.getRotation();
                this.createChest(serverLevelAccessor, boundingBox, random, blockPos, SpeciesLootTables.LIBRA_CHEST, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, rotation.rotate(Direction.SOUTH)));
            } else {
                ArrayList<Mob> entities = new ArrayList<>();

                if ("WOLF".equals(metadata)) {
                    entities.add(EntityType.WOLF.create(serverLevelAccessor.getLevel()));
                    serverLevelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
                }

                for (Mob entity : entities) {
                    entity.setPersistenceRequired();
                    entity.moveTo(blockPos, 0.0f, 0.0f);
                    entity.finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.STRUCTURE, null, null);
                    serverLevelAccessor.addFreshEntityWithPassengers(entity);
                }
            }
        }

    }
}
