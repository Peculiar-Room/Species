package com.ninni.species.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class RoombugFollowOwnerGoal extends Goal {
    private final TamableAnimal tamable;
    private LivingEntity owner;
    private final LevelReader level;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;
    private final boolean canFly;

    public RoombugFollowOwnerGoal(TamableAnimal tamableAnimal, double d, float f, float g, boolean bl) {
        this.tamable = tamableAnimal;
        this.level = tamableAnimal.level();
        this.speedModifier = d;
        this.navigation = tamableAnimal.getNavigation();
        this.startDistance = f;
        this.stopDistance = g;
        this.canFly = bl;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(tamableAnimal.getNavigation() instanceof GroundPathNavigation) && !(tamableAnimal.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.tamable.getOwner();
        if (livingEntity == null) return false;
        else if (livingEntity.isSpectator()) return false;
        else if (this.tamable.isOrderedToSit()) return false;
        else if (this.tamable.distanceToSqr(livingEntity) < (double)(this.startDistance * this.startDistance)) return false;
        else {
            this.owner = livingEntity;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) return false;
        else if (this.tamable.isOrderedToSit()) return false;
        else return !(this.tamable.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                if (this.tamable.distanceToSqr(this.owner) >= 144.0) {
                    this.teleportToOwner();
                } else {
                    this.navigation.moveTo(this.owner, this.speedModifier);
                }

            }
        }
    }

    private void teleportToOwner() {
        BlockPos blockPos = this.owner.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean bl = this.maybeTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
            if (bl) {
                return;
            }
        }

    }

    private boolean maybeTeleportTo(int i, int j, int k) {
        if (Math.abs((double)i - this.owner.getX()) < 2.0 && Math.abs((double)k - this.owner.getZ()) < 2.0) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(i, j, k))) {
            return false;
        } else {
            this.tamable.moveTo((double)i + 0.5, j, (double)k + 0.5, this.tamable.getYRot(), this.tamable.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        BlockPathTypes blockPathTypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());

        BlockPos n = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
        BlockPos s = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
        BlockPos e = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
        BlockPos w = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
        BlockPos se = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ() - 1);
        BlockPos sw = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ() - 1);
        BlockPos ne = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
        BlockPos nw = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ() + 1);

        if (blockPathTypes != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.level.getBlockState(pos.below());
            if (!this.canFly && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos pos2 = pos.subtract(this.tamable.blockPosition());
                BlockPos pos3 = n.subtract(this.tamable.blockPosition());
                BlockPos pos4 = s.subtract(this.tamable.blockPosition());
                BlockPos pos5 = e.subtract(this.tamable.blockPosition());
                BlockPos pos6 = w.subtract(this.tamable.blockPosition());
                BlockPos pos7 = se.subtract(this.tamable.blockPosition());
                BlockPos pos8 = sw.subtract(this.tamable.blockPosition());
                BlockPos pos9 = ne.subtract(this.tamable.blockPosition());
                BlockPos pos10 = nw.subtract(this.tamable.blockPosition());

                return
                        this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(pos3))
                                && this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(pos4))
                                && this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(pos5))
                                && this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(pos6))
                                && this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(pos7))
                                && this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(pos8))
                                && this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(pos9))
                                && this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(pos10))
                                && this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(pos2));
            }
        }
    }

    private int randomIntInclusive(int i, int j) {
        return this.tamable.getRandom().nextInt(j - i + 1) + i;
    }
}
