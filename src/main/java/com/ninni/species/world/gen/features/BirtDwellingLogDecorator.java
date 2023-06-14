package com.ninni.species.world.gen.features;

import com.mojang.serialization.Codec;
import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.block.entity.SpeciesBlockEntities;
import com.ninni.species.entity.SpeciesEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;

public class BirtDwellingLogDecorator extends TreeDecorator {

    public static final BirtDwellingLogDecorator INSTANCE = new BirtDwellingLogDecorator();
    public static final Codec<BirtDwellingLogDecorator> CODEC = Codec.unit(() -> INSTANCE);

    private boolean placeBirtDwelling(Context context, List<BlockPos> list, int index, RandomSource random) {
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos pos = list.get(index);
        if (!context.level().isStateAtPosition(pos.above(), this::isBirtDwelling) && !context.level().isStateAtPosition(pos, this::isBirtDwelling) && context.level().isStateAtPosition(pos.relative(direction), BlockBehaviour.BlockStateBase::isAir)) {
            context.setBlock(pos, SpeciesBlocks.BIRT_DWELLING.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(context.random())));
            context.level().getBlockEntity(pos, SpeciesBlockEntities.BIRT_DWELLING.get()).ifPresent((blockEntity) -> {
                int i = 2 + random.nextInt(2);
                for (int j = 0; j < i; ++j) {
                    CompoundTag nbtCompound = new CompoundTag();
                    nbtCompound.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(SpeciesEntities.BIRT.get()).toString());
                    blockEntity.addBirt(nbtCompound, random.nextInt(599));
                }

            });
            return true;
        } else {
            return index != 1 && this.placeBirtDwelling(context, list, index - 1, random);
        }
    }

    private boolean isBirtDwelling(BlockState blockState) {
        return blockState.is(SpeciesBlocks.BIRT_DWELLING.get());
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return SpeciesTreeDecorators.BIRT_DWELLING.get();
    }

    @Override
    public void place(Context context) {
        RandomSource random = context.random();
        int count = context.leaves().size() > 6 ? 3 : 2;
        for (int i = 0; i < count; i++) {
            this.placeBirtDwelling(context, context.logs(), Mth.nextInt(random, 3, context.logs().size() - 1), random);
        }
    }
}