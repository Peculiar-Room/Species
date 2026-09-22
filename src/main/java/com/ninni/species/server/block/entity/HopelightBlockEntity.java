package com.ninni.species.server.block.entity;

import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.server.block.HopelightBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;

public class HopelightBlockEntity extends SpectreLightBlockEntity implements IBlockEntityExtension {

    public HopelightBlockEntity(BlockPos pos, BlockState state) {
        super(SpeciesBlockEntities.HOPELIGHT.get(), pos, state);
    }
}