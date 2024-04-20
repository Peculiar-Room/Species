package com.ninni.species.world.gen.features;

import com.mojang.serialization.Codec;
import com.ninni.species.registry.SpeciesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.DripstoneUtils;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AlphaceneMushroomFeature extends Feature<NoneFeatureConfiguration> {

    public AlphaceneMushroomFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext) {
        WorldGenLevel world = featurePlaceContext.level();
        BlockPos blockPos = featurePlaceContext.origin();
        RandomSource random = featurePlaceContext.random();
        BlockState stemBlock = Blocks.MUSHROOM_STEM.defaultBlockState();
        BlockState mushroomBlock = SpeciesBlocks.ALPHACENE_MUSHROOM_BLOCK.defaultBlockState();

        boolean initialFlag = world.getBlockState(blockPos.below()).is(BlockTags.DIRT);

        if (!initialFlag) {
            return false;
        } else {
            Direction initialDirection = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos initialSprialPos = blockPos.above(2).relative(initialDirection);

            int secondLayerCount = UniformInt.of(6, 16).sample(random);
            int rotatingIndex = 3;
            int baseSize = 2;
            int elevateIndex = 2;
            int primaryStemLength = 8;

            this.placeStem(primaryStemLength, secondLayerCount, world, blockPos, stemBlock, mushroomBlock);

            this.placeBaseRoof(baseSize, blockPos, world, mushroomBlock);

            this.placeSpiral(initialSprialPos, primaryStemLength, initialDirection, world, mushroomBlock, elevateIndex, secondLayerCount, rotatingIndex);

            return true;
        }
    }

    private void placeSpiral(BlockPos initialSprialPos, int primaryStemLength, Direction initialDirection, WorldGenLevel world, BlockState mushroomBlock, int elevateIndex, int secondLayerCount, int rotatingIndex) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos().set(initialSprialPos);

        for (int i = 0; i < primaryStemLength; i++) {
            if (i % 2 != 0) {
                initialDirection = initialDirection.getClockWise();
            }
            world.setBlock(mutableBlockPos, mushroomBlock, 2);
            if (i == 7) {
                world.setBlock(mutableBlockPos.above(), mushroomBlock, 2);
            }
            mutableBlockPos.move(initialDirection.getClockWise());
            elevateIndex++;
            if (elevateIndex % 4 == 0) {
                world.setBlock(mutableBlockPos, mushroomBlock, 2);
                mutableBlockPos.move(Direction.UP);
            }
        }

        mutableBlockPos.move(Direction.UP);

        for (int i = 0; i <= secondLayerCount; i++) {
            if (rotatingIndex % 4 == 0) {
                initialDirection = initialDirection.getClockWise();
                rotatingIndex = 2;
            }
            if (i % 2 == 0 && i >= 2) {
                world.setBlock(mutableBlockPos, mushroomBlock, 2);
                mutableBlockPos.move(Direction.UP);
            }
            world.setBlock(mutableBlockPos, mushroomBlock, 2);
            mutableBlockPos.move(initialDirection.getClockWise());
            rotatingIndex++;
        }
    }

    private void placeBaseRoof(int baseSize, BlockPos blockPos, WorldGenLevel world, BlockState mushroomBlock) {
        for (int x = -baseSize; x <= baseSize; x++) {
            for (int z = -baseSize; z <= baseSize; z++) {
                BlockPos basePos = blockPos.above().offset(x, 0, z);
                boolean xCorner = x == -baseSize || x == baseSize;
                boolean zCorner = z == -baseSize || z == baseSize;

                boolean invalid = xCorner && zCorner || !world.isStateAtPosition(basePos, DripstoneUtils::isEmptyOrWater);

                if (invalid) continue;

                world.setBlock(basePos, mushroomBlock, 2);
            }
        }
    }

    private void placeStem(int primaryStemLength, int secondLayerCount, WorldGenLevel world, BlockPos blockPos, BlockState stemBlock, BlockState mushroomBlock) {
        BlockState placeState = stemBlock;
        int length = (primaryStemLength / 2) + (secondLayerCount / 2);
        for (int i = 0; i < length; i++) {
            if (i == length - 1) {
                placeState = mushroomBlock;
            }
            world.setBlock(blockPos.above(i), placeState, 2);
        }
    }

}
