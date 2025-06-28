package com.ninni.species.server.block.entity;

import com.ninni.species.registry.SpeciesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeBlockEntity;

public class SpeclightBlockEntity extends SpectreLightBlockEntity implements IForgeBlockEntity {

    public SpeclightBlockEntity(BlockPos pos, BlockState state) {
        super(SpeciesBlockEntities.SPECLIGHT.get(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = this.worldPosition;
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
    }
}