package com.ninni.species.mixin;

import com.google.common.collect.Lists;
import com.ninni.species.entity.Mammutilation;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LightningRodBlock.class)
public class LightningRodMixin {


    @Inject(at = @At("TAIL"), method = "onLightningStrike")
    private void s$onLightningStrike(BlockState blockState, Level level, BlockPos blockPos, CallbackInfo ci) {

        Direction rodFacing = blockState.getValue(LightningRodBlock.FACING);
        List<BlockPos> pos = Lists.newArrayList();
        BlockPos otherRodPos = blockPos.relative(rodFacing.getOpposite(), 4);

        if (rodFacing.getAxis() != Direction.Axis.Y && level.getBlockState(blockPos.relative(rodFacing.getOpposite())).is(SpeciesTags.MAMMUTILATION_BODY_BLOCKS)) {
            //Check for the other Lightning Rod
            if (level.getBlockState(otherRodPos).is(Blocks.LIGHTNING_ROD)) {
                Direction otherRodFacing = level.getBlockState(otherRodPos).getValue(LightningRodBlock.FACING);
                if (otherRodFacing == rodFacing.getOpposite()) {

                    //Where the pumpkins could be
                    BlockPos pumpkin1 = blockPos.relative(rodFacing.getOpposite(), 2).offset(0,-1,0).relative(rodFacing.getClockWise());
                    BlockPos pumpkin2 = blockPos.relative(rodFacing.getOpposite(), 2).offset(0,-1,0).relative(rodFacing.getCounterClockWise());

                    //Checks for the pumpkin's facing and position
                    boolean bl1 = level.getBlockState(pumpkin1).is(Blocks.CARVED_PUMPKIN) && level.getBlockState(pumpkin2).is(SpeciesTags.MAMMUTILATION_BODY_BLOCKS) && level.getBlockState(pumpkin1).getValue(CarvedPumpkinBlock.FACING) == rodFacing.getClockWise();
                    boolean bl2 = level.getBlockState(pumpkin2).is(Blocks.CARVED_PUMPKIN) && level.getBlockState(pumpkin1).is(SpeciesTags.MAMMUTILATION_BODY_BLOCKS) && level.getBlockState(pumpkin2).getValue(CarvedPumpkinBlock.FACING) == rodFacing.getCounterClockWise();
                    if (bl1 || bl2) {
                        //Check for the other mammutilation block
                        for (int y = -1; y <= 1; y++) {
                            for (int x = -1; x <= 1; x++) {
                                for (int z = -1; z <= 1; z++) {
                                    BlockPos initialPos = blockPos.relative(rodFacing.getOpposite(), 2).offset(0,-1,0);
                                    BlockPos position = BlockPos.containing(initialPos.getX() + x, initialPos.getY() + y, initialPos.getZ() + z);
                                    if (!level.getBlockState(position).is(Blocks.CARVED_PUMPKIN)) {
                                        if (level.getBlockState(position).is(SpeciesTags.MAMMUTILATION_BODY_BLOCKS)) {
                                            pos.add(position);
                                        }
                                    }
                                }
                            }
                        }
                        //Check for Bone Blocks
                        List<BlockPos> bonePos = Lists.newArrayList();
                        if (bl1) {
                            bonePos.add(blockPos.offset(0,-1,0).relative(rodFacing.getClockWise()));
                            bonePos.add(blockPos.offset(0,-1,0).relative(rodFacing.getClockWise(), 2));
                            bonePos.add(otherRodPos.offset(0,-1,0).relative(otherRodFacing.getCounterClockWise()));
                            bonePos.add(otherRodPos.offset(0,-1,0).relative(otherRodFacing.getCounterClockWise(), 2));
                        }
                        if (bl2) {
                            bonePos.add(blockPos.offset(0,-1,0).relative(rodFacing.getCounterClockWise()));
                            bonePos.add(blockPos.offset(0,-1,0).relative(rodFacing.getCounterClockWise(), 2));
                            bonePos.add(otherRodPos.offset(0,-1,0).relative(otherRodFacing.getClockWise()));
                            bonePos.add(otherRodPos.offset(0,-1,0).relative(otherRodFacing.getClockWise(), 2));
                        }

                        for(BlockPos bonePos1 : bonePos) {
                            if (level.getBlockState(bonePos1).is(Blocks.BONE_BLOCK)) pos.add(bonePos1);
                        }


                        //Add the blocks
                        pos.add(blockPos);
                        pos.add(otherRodPos);
                        pos.add(bl1 ? pumpkin1 : pumpkin2);

                        if (pos.size() == 33) {
                            pos.forEach(blockPos1 -> {
                                level.setBlock(blockPos1, Blocks.AIR.defaultBlockState(), 2);
                                level.levelEvent(2001, blockPos1, Block.getId(level.getBlockState(blockPos1)));
                                level.blockUpdated(blockPos1, Blocks.AIR);
                            });
                            Mammutilation mammutilation = SpeciesEntities.MAMMUTILATION.get().create(level);
                            float x = 0.0F;
                            float y = 0.0F;
                            mammutilation.moveTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.05, (double)blockPos.getZ() + 0.5, Mth.wrapDegrees(x), Mth.wrapDegrees(y));
                            level.addFreshEntity(mammutilation);
                            for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, mammutilation.getBoundingBox().inflate(5.0))) {
                                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, mammutilation);
                            }
                            mammutilation.clearFire();
                        }

                    }
                }
            }
        }

    }

}
