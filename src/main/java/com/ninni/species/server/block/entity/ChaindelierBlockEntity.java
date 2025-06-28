package com.ninni.species.server.block.entity;

import com.ninni.species.registry.SpeciesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeBlockEntity;

public class ChaindelierBlockEntity extends BlockEntity implements IForgeBlockEntity {

    public ChaindelierBlockEntity(BlockPos pos, BlockState state) {
        super(SpeciesBlockEntities.CHAINDELIER.get(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = this.worldPosition;
        return new AABB(pos.getX(), pos.getY() - 4, pos.getZ(), pos.getX() + 1.0, pos.getY() + 1, pos.getZ() + 1.0);
    }
}