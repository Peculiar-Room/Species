package com.ninni.species.server.block.entity;

import com.ninni.species.registry.SpeciesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;

public class SpeclightBlockEntity extends SpectreLightBlockEntity implements IBlockEntityExtension {

    public SpeclightBlockEntity(BlockPos pos, BlockState state) {
        super(SpeciesBlockEntities.SPECLIGHT.get(), pos, state);
    }
}