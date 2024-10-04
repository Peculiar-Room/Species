package com.ninni.species.structure;

import com.ninni.species.Species;
import com.ninni.species.init.SpeciesEntities;
import com.ninni.species.init.SpeciesLootTables;
import com.ninni.species.init.SpeciesStructurePieceTypes;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import java.util.function.Function;

public class WraptorCoopGenerator {
    public static final int COOP_STRUCTURES = 7;
    private static final Function<Integer, ResourceLocation> TEMPLATES = Util.memoize(i -> new ResourceLocation(Species.MOD_ID, "wraptor_coop/coop" + i));

    public static void addPieces(StructureTemplateManager manager, BlockPos pos, int height, Rotation rotation, StructurePieceAccessor holder, RandomSource random) {
        holder.addPiece(new WraptorCoopGenerator.Piece(manager, TEMPLATES.apply(Mth.randomBetweenInclusive(random, 1, COOP_STRUCTURES)), pos, rotation));
    }

    public static class Piece extends TemplateStructurePiece {
        public static final String ROTATION_KEY = "Rotation";

        public Piece(StructureTemplateManager manager, ResourceLocation id, BlockPos pos, Rotation rotation) {
            super(SpeciesStructurePieceTypes.WRAPTOR_COOP.get(), 0, manager, id, id.toString(), createPlacementData(rotation), pos);
        }

        public Piece(StructureTemplateManager manager, CompoundTag nbt) {
            super(SpeciesStructurePieceTypes.WRAPTOR_COOP.get(), nbt, manager, id -> createPlacementData(Rotation.valueOf(nbt.getString(ROTATION_KEY))));
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
            if (world.getBlockState(this.templatePosition.below()).is(BlockTags.NYLIUM) || (world.getBlockState(this.templatePosition.below()).is(Blocks.NETHERRACK) && world.isEmptyBlock(this.templatePosition))) {
                this.templatePosition = this.templatePosition.below();
            }
            super.postProcess(world, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, blockPos);
        }

        @Override
        protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor world, RandomSource random, BoundingBox boundingBox) {
            if (metadata.startsWith("Chest")) {
                Rotation rotation = this.placeSettings.getRotation();
                this.createChest(world, boundingBox, random, pos, SpeciesLootTables.WRAPTOR_COOP_CHEST, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, rotation.rotate(
                        switch (metadata) {
                            default -> Direction.NORTH;
                            case "ChestEast" -> Direction.EAST;
                            case "ChestSouth" -> Direction.SOUTH;
                            case "ChestWest" -> Direction.WEST;
                        }
                )));
            } else {
                ArrayList<Mob> entities = new ArrayList<>();

                if ("Wraptor".equals(metadata)) entities.add(SpeciesEntities.WRAPTOR.get().create(world.getLevel()));

                for (Mob entity : entities) {
                    entity.setPersistenceRequired();
                    entity.moveTo(pos, 0.0f, 0.0f);
                    entity.finalizeSpawn(world, world.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.STRUCTURE, null, null);
                    world.addFreshEntityWithPassengers(entity);
                }
            }
        }

    }
}
