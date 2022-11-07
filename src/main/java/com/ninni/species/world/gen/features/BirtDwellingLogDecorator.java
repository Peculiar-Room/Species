package com.ninni.species.world.gen.features;

import com.mojang.serialization.Codec;
import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.block.entity.SpeciesBlockEntities;
import com.ninni.species.entity.SpeciesEntities;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.List;

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
        int count = generator.getLeavesPositions().size() > 6 ? 3 : 2;
        for (int i = 0; i < count; i++) {
            this.placeBirtDwelling(generator, generator.getLogPositions(), MathHelper.nextInt(random, 3, 6), random);
        }
    }

    private boolean placeBirtDwelling(Generator generator, List<BlockPos> list, int index, Random random) {
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        BlockPos pos = list.get(index);
        if (!generator.getWorld().testBlockState(pos.up(), this::isBirtDwelling) && !generator.getWorld().testBlockState(pos, this::isBirtDwelling) && generator.getWorld().testBlockState(pos.offset(direction), AbstractBlock.AbstractBlockState::isAir)) {
            generator.replace(pos, SpeciesBlocks.BIRT_DWELLING.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.Type.HORIZONTAL.random(generator.getRandom())));
            generator.getWorld().getBlockEntity(pos, SpeciesBlockEntities.BIRT_DWELLING).ifPresent((blockEntity) -> {
                int i = 2 + random.nextInt(2);
                for (int j = 0; j < i; ++j) {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putString("id", Registry.ENTITY_TYPE.getId(SpeciesEntities.BIRT).toString());
                    blockEntity.addBirt(nbtCompound, random.nextInt(599));
                }

            });
            return true;
        } else {
            return index != 1 && this.placeBirtDwelling(generator, list, index - 1, random);
        }
    }

    private boolean isBirtDwelling(BlockState blockState) {
        return blockState.isOf(SpeciesBlocks.BIRT_DWELLING);
    }

}