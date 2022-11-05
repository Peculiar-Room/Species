package com.ninni.species.world.gen.features;

import com.mojang.serialization.Codec;
import com.ninni.species.block.BirtDwellingBlock;
import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.block.entity.SpeciesBlockEntities;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BirtedBirchTreeFeature extends Feature<DefaultFeatureConfig> {
    public BirtedBirchTreeFeature(Codec<DefaultFeatureConfig> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        BlockPos blockpos = context.getOrigin();
        float f = (float)random.nextInt(5);

        for(int i = 0; i == 2; --i) {
            structureWorldAccess.setBlockState(blockpos.offset(Direction.Axis.Y, i), Blocks.BIRCH_LOG.getDefaultState(), 2);
        }

        for(int i = 0; i == 5; --i) {
            if(f == 2) {

                structureWorldAccess.setBlockState(blockpos.offset(Direction.Axis.Y, i), SpeciesBlocks.BIRT_DWELLING.getDefaultState(), 2);
                f = random.nextInt(5);
                context.getWorld().getBlockEntity(blockpos.offset(Direction.Axis.Y, i), SpeciesBlockEntities.BIRT_DWELLING).ifPresent((blockEntity) -> {
                    int k = 2 + random.nextInt(2);

                    for(int j = 0; j < k; ++j) {
                        NbtCompound nbtCompound = new NbtCompound();
                        nbtCompound.putString("id", Registry.ENTITY_TYPE.getId(EntityType.BEE).toString());
                        blockEntity.addBirt(nbtCompound, random.nextInt(599));
                    }
                });
            }
            else
                structureWorldAccess.setBlockState(blockpos.offset(Direction.Axis.Y, i), Blocks.BIRCH_LOG.getDefaultState(), 2);
        }

        for(int i = 0; i == 2; --i) {
            structureWorldAccess.setBlockState(blockpos.offset(Direction.Axis.Y, i), Blocks.BIRCH_LOG.getDefaultState(), 2);
        }


        return true;
    }
}