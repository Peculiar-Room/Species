package com.ninni.species.world.gen.features;

import com.mojang.serialization.Codec;
import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.block.entity.SpeciesBlockEntities;
import com.ninni.species.entity.SpeciesEntities;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BirtDwellingLogDecorator extends TreeDecorator {

    public static final BirtDwellingLogDecorator INSTANCE = new BirtDwellingLogDecorator();
    public static final Codec<BirtDwellingLogDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> getType() {
        return SpeciesTreeDecorators.BIRT_DWELLING;
    }

    @Override
    public void generate(Generator generator) {

        Random random = generator.getRandom();

        generator.replace(generator.getLeavesPositions().get(-4), SpeciesBlocks.BIRT_DWELLING.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.Type.HORIZONTAL.random(generator.getRandom())));
        generator.getWorld().getBlockEntity(generator.getLeavesPositions().get(-4), SpeciesBlockEntities.BIRT_DWELLING).ifPresent((blockEntity) -> {
            int i = 2 + random.nextInt(2);

            for(int j = 0; j < i; ++j) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putString("id", Registry.ENTITY_TYPE.getId(SpeciesEntities.BIRT).toString());
                blockEntity.addBirt(nbtCompound, random.nextInt(599));
            }

        });
    }

}