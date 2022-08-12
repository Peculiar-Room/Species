package com.ninni.species.structure;

import com.ninni.species.Species;
import com.ninni.species.entity.SpeciesEntities;
import com.ninni.species.loot.SpeciesLootTables;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.ArrayList;
import java.util.function.Function;

public class WraptorCoopGenerator {
    public static final int COOP_STRUCTURES = 7;
    private static final Function<Integer, Identifier> TEMPLATES = Util.memoize(i -> new Identifier(Species.MOD_ID, "wraptor_coop/coop" + i));

    public static void addPieces(StructureTemplateManager manager, BlockPos pos, int height, BlockRotation rotation, StructurePiecesHolder holder, Random random) {
        holder.addPiece(new WraptorCoopGenerator.Piece(manager, TEMPLATES.apply(MathHelper.nextBetween(random, 1, COOP_STRUCTURES)), pos, rotation));
    }

    public static class Piece extends SimpleStructurePiece {
        public static final String ROTATION_KEY = "Rotation";

        public Piece(StructureTemplateManager manager, Identifier id, BlockPos pos, BlockRotation rotation) {
            super(SpeciesStructurePieceTypes.WRAPTOR_COOP, 0, manager, id, id.toString(), createPlacementData(rotation), pos);
        }

        public Piece(StructureTemplateManager manager, NbtCompound nbt) {
            super(SpeciesStructurePieceTypes.WRAPTOR_COOP, nbt, manager, id -> createPlacementData(BlockRotation.valueOf(nbt.getString(ROTATION_KEY))));
        }

        private static StructurePlacementData createPlacementData(BlockRotation rotation) {
            return new StructurePlacementData().setRotation(rotation).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            super.writeNbt(context, nbt);
            nbt.putString(ROTATION_KEY, this.placementData.getRotation().name());
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            if (world.getBlockState(this.pos.down()).isIn(BlockTags.NYLIUM) || (world.getBlockState(this.pos.down()).isOf(Blocks.NETHERRACK) && world.isAir(this.pos))) {
                this.pos = this.pos.down();
            }
            super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
        }

        @Override
        protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
            if (metadata.startsWith("Chest")) {
                BlockRotation rotation = this.placementData.getRotation();
                this.addChest(world, boundingBox, random, pos, SpeciesLootTables.WRAPTOR_COOP_CHEST, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, rotation.rotate(
                    switch (metadata) {
                        default -> Direction.NORTH;
                        case "ChestEast" -> Direction.EAST;
                        case "ChestSouth" -> Direction.SOUTH;
                        case "ChestWest" -> Direction.WEST;
                    }
                )));
            } else {
                ArrayList<MobEntity> entities = new ArrayList<>();

                if ("Wraptor".equals(metadata)) entities.add(SpeciesEntities.WRAPTOR.create(world.toServerWorld()));

                for (MobEntity entity : entities) {
                    entity.setPersistent();
                    entity.refreshPositionAndAngles(pos, 0.0f, 0.0f);
                    entity.initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.STRUCTURE, null, null);
                    world.spawnEntityAndPassengers(entity);
                }
            }
        }
    }
}
