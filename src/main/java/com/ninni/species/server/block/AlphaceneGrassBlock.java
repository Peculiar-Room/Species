package com.ninni.species.server.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;

public class AlphaceneGrassBlock extends SpreadingSnowyDirtBlock {
    public static final MapCodec<AlphaceneGrassBlock> CODEC = simpleCodec(AlphaceneGrassBlock::new);
    public AlphaceneGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends SpreadingSnowyDirtBlock> codec() {
        return CODEC;
    }
}