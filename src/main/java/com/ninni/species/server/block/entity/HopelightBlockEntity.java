package com.ninni.species.server.block.entity;

import com.ninni.species.server.block.HopelightBlock;
import com.ninni.species.registry.SpeciesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeBlockEntity;

public class HopelightBlockEntity extends SpectreLightBlockEntity implements IForgeBlockEntity {

    public HopelightBlockEntity(BlockPos pos, BlockState state) {
        super(SpeciesBlockEntities.HOPELIGHT.get(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = this.worldPosition;
        if (this.level.getBlockState(pos).getBlock() instanceof HopelightBlock) {
            if (this.level.getBlockState(pos).getValue(HopelightBlock.HANGING)) return new AABB(pos.getX(), pos.getY() - 4, pos.getZ(), pos.getX() + 1.0, pos.getY() + 1, pos.getZ() + 1.0);
            else return new AABB(pos.getX(), pos.getY() + 4, pos.getZ(), pos.getX() + 1.0, pos.getY() - 1, pos.getZ() + 1.0);
        } else return super.getRenderBoundingBox();
    }
}