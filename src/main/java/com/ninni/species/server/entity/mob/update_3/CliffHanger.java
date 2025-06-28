package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.Species;
import com.ninni.species.registry.*;
import com.ninni.species.server.criterion.SpeciesCriterion;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class CliffHanger extends Hanger {
    public static final EntityDataAccessor<Boolean> ATTACHED = SynchedEntityData.defineId(CliffHanger.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> GOING_UPWARDS = SynchedEntityData.defineId(CliffHanger.class, EntityDataSerializers.BOOLEAN);

    public CliffHanger(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0, true));
        this.goalSelector.addGoal(0, new SwitchGravityGoal(this));
        this.goalSelector.addGoal(0, new PullTowardsMouthGoal(this));
        this.goalSelector.addGoal(1, new CeilingTargetingGoal(this, 1.5,
                entity -> (entity.getType().is(SpeciesTags.CLIFF_HANGER_PREY) || entity instanceof Player) && this.canAttack(entity) && entity.isAlive() && !entity.isSpectator())
        );
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class, 4.0F, 1D, 1.25D,  livingEntity -> !this.isAttached()));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel level) {
            setAttached(verticalCollision && getDeltaMovement().y >= 0);

            if (!this.isAttached() && !isGoingUpwards() && this.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).getValue() < 0) {
                this.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(0.08D);
            }

            boolean flag = (this.random.nextFloat() < 0.02f && this.isAttached()) || (this.random.nextFloat() < 0.3f && this.isTongueOut());
            if (flag) {
                level.sendParticles(SpeciesParticles.FALLING_HANGER_SALIVA.get(), this.getRandomX(1.5), this.getY() + getTongueOffset(), this.getRandomZ(1.5) , 1,0, 0, 0, 0);
            }
        }
    }

    @Override
    public void unstuckTarget() {
        super.unstuckTarget();
        if ((this.isAttached() && this.getTargetPos().y > this.getY() + getTongueOffset() + 0.5F && this.isTongueOut() && !this.isGoingUpwards()) || this.getTarget() instanceof Axolotl axolotl && axolotl.isPlayingDead()) {
            deactivateTongue();
            this.setTarget(null);
            this.setCantAttackTicks(40);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof Projectile) {
            amount *= 3.0F;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(SpeciesParticles.HANGER_CRIT.get(),
                        this.getX(), (this.isAttached() ? this.getY() + this.getBbHeight() - 0.1F : this.getY() + 0.1F) - this.random.nextFloat()/100, this.getZ(),
                        1,
                        0, 0, 0, 0);
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isTongueOut() || this.isAttached()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0,1,0));
        }
        super.travel(vec3);
    }

    @Override
    public float getTongueOffset() {
        if (this.entityData.hasItem(ATTACHED)) return this.isAttached() ? 1.2F : super.getTongueOffset();
        return super.getTongueOffset();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACHED, false);
        this.entityData.define(GOING_UPWARDS, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("Attached", this.isAttached());
        compoundTag.putBoolean("GoingUpwards", this.isGoingUpwards());
        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setAttached(compoundTag.getBoolean("Attached"));
        this.setGoingUpwards(compoundTag.getBoolean("GoingUpwards"));
        super.readAdditionalSaveData(compoundTag);
    }

    public boolean isAttached() {
        return this.entityData.get(ATTACHED);
    }
    public void setAttached(boolean attached) {
        this.entityData.set(ATTACHED, attached);
    }

    public boolean isGoingUpwards() {
        return this.entityData.get(GOING_UPWARDS);
    }
    public void setGoingUpwards(boolean goingUpwards) {
        this.entityData.set(GOING_UPWARDS, goingUpwards);
    }

    @Override
    public boolean isPushable() {
        return !isAttached() && super.isPushable();
    }

    @Override
    public int getAmbientSoundInterval() {
        return this.isAttached() ? 400 : super.getAmbientSoundInterval();
    }

    public static class PullTowardsMouthGoal extends Goal {
        private final CliffHanger cliffHanger;

        public PullTowardsMouthGoal(CliffHanger cliffHanger) {
            this.cliffHanger = cliffHanger;
        }

        @Override
        public boolean canUse() {
            return cliffHanger.isAttached() && !cliffHanger.isGoingUpwards() && cliffHanger.getTarget() != null && !cliffHanger.getTarget().isPassenger() && cliffHanger.getCantAttackTicks() == 0 && cliffHanger.getTarget().onGround() && cliffHanger.getTarget().isAlive();
        }

        @Override
        public void start() {
            super.start();
            cliffHanger.setTongueOut(true);
            cliffHanger.level().playSound(null, cliffHanger.getTarget().getX(), cliffHanger.getTarget().getY(), cliffHanger.getTarget().getZ(), SpeciesSoundEvents.CLIFF_HANGER_SHOOT.get(), cliffHanger.getSoundSource(), 1, 1);
        }

        @Override
        public void tick() {
            LivingEntity target = cliffHanger.getTarget();
            if (!cliffHanger.level().isClientSide) {

                if (target.position().y < cliffHanger.position().y - 1F) {
                    AttributeInstance gravity = target.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
                    Vec3 diff = cliffHanger.position().add(0, cliffHanger.getTongueOffset(), 0).subtract(target.position());
                    diff = diff.multiply(0.05, 0, 0.05).add(0, 0.025, 0);

                    target.setDeltaMovement(target.getDeltaMovement().add(diff).add(0, gravity.getValue(), 0));
                    if (target.getDeltaMovement().y < 0.1) {
                        target.addDeltaMovement(new Vec3(0, 0.1, 0));
                    }

                    if (target instanceof Player player) {
                        player.setDeltaMovement(player.getDeltaMovement().add(diff.add(0, (0.08 / 4.0D), 0)));
                        player.hurtMarked = true;
                    }
                }

                if (target.tickCount % 30 == 0) {
                    target.playSound(SpeciesSoundEvents.HANGER_PULL.get(), 1, 1);
                }

                Vector3f visualTarget = target.position().add(0, target.getBbHeight(), 0).toVector3f();
                cliffHanger.setTargetPos(visualTarget);
                cliffHanger.setTongueTarget(visualTarget);
            }

        }

        @Override
        public boolean canContinueToUse() {
            return cliffHanger.isAttached() && !cliffHanger.isGoingUpwards() && cliffHanger.getTarget() != null && !cliffHanger.getTarget().isPassenger() && cliffHanger.getCantAttackTicks() == 0 && cliffHanger.getTarget().isAlive();
        }

        @Override
        public void stop() {
            cliffHanger.deactivateTongue();
        }
    }

    public static class SwitchGravityGoal extends Goal {
        private final CliffHanger cliffHanger;
        private BlockPos targetCeilingPos = null;
        private boolean navigatingToCeiling = false;
        private int stuckTicks;

        public SwitchGravityGoal(CliffHanger cliffHanger) {
            this.cliffHanger = cliffHanger;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !cliffHanger.isAttached() && !cliffHanger.isTongueOut() && cliffHanger.getNavigation().isDone() && cliffHanger.random.nextInt(20) == 0;
        }

        @Override
        public void start() {
            Vec3 mobCenter = cliffHanger.position();
            BlockHitResult hit = raycastCeiling(mobCenter);

            if (hit.getType() != HitResult.Type.BLOCK) {
                hit = searchForNearbyCeiling(mobCenter, 15);
            }

            if (hit.getType() == HitResult.Type.BLOCK) {
                targetCeilingPos = BlockPos.containing(hit.getLocation());
                navigatingToCeiling = true;

                BlockPos.MutableBlockPos walkTarget = targetCeilingPos.mutable();
                while (walkTarget.getY() > cliffHanger.level().getMinBuildHeight() && cliffHanger.level().getBlockState(walkTarget.below()).isAir()) {
                    walkTarget.move(Direction.DOWN);
                }

                cliffHanger.getNavigation().moveTo(walkTarget.getX() + 0.5, walkTarget.getY() + 1, walkTarget.getZ() + 0.5, 1.0);
            } else {
                Vec3 randomPos = mobCenter.add(cliffHanger.random.nextGaussian() * 5, 0, cliffHanger.random.nextGaussian() * 5);
                cliffHanger.getNavigation().moveTo(randomPos.x, randomPos.y, randomPos.z, 1.0);
                navigatingToCeiling = false;
            }
        }

        @Override
        public void tick() {

            if (navigatingToCeiling && cliffHanger.getDeltaMovement().horizontalDistanceSqr() < 0.1) stuckTicks++;
            if (stuckTicks > 100) cliffHanger.getNavigation().recomputePath();

            if (navigatingToCeiling && targetCeilingPos != null) {

                BlockPos.MutableBlockPos walkTarget = targetCeilingPos.mutable();
                while (walkTarget.getY() > cliffHanger.level().getMinBuildHeight() &&
                        cliffHanger.level().getBlockState(walkTarget.below()).isAir()) {
                    walkTarget.move(Direction.DOWN);
                }

                cliffHanger.getNavigation().moveTo(walkTarget.getX() + 0.5, walkTarget.getY() + 1, walkTarget.getZ() + 0.5, 1.0);

                double distanceSq = cliffHanger.distanceToSqr(Vec3.atCenterOf(targetCeilingPos).with(Direction.Axis.Y, cliffHanger.position().y));
                if (distanceSq < 2.0 && cliffHanger.getNavigation().isDone()) {
                    cliffHanger.setGoingUpwards(true);
                    cliffHanger.playSound(SpeciesSoundEvents.CLIFF_HANGER_SHOOT.get());
                    cliffHanger.activateTongue(Vec3.atBottomCenterOf(targetCeilingPos.above()).toVector3f());
                    navigatingToCeiling = false;
                }
            }

            if (cliffHanger.isGoingUpwards()) {
                if (cliffHanger.tickCount % 15 == 0) {
                    cliffHanger.playSound(SpeciesSoundEvents.HANGER_PULL.get(), 1,1);
                }
                cliffHanger.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(-0.04D);
                if (cliffHanger.getY() > cliffHanger.getTargetPos().y) {
                    cliffHanger.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(0.08D);
                    stop();
                }
            }
        }


        @Override
        public boolean canContinueToUse() {
            return (navigatingToCeiling || cliffHanger.isGoingUpwards()) && !cliffHanger.isAttached();
        }

        @Override
        public void stop() {
            if (cliffHanger.isGoingUpwards()) {
                cliffHanger.setGoingUpwards(false);
                cliffHanger.deactivateTongue();
                cliffHanger.playSound(SpeciesSoundEvents.CLIFF_HANGER_ATTACH.get());
            }
            navigatingToCeiling = false;
            targetCeilingPos = null;
        }

        private BlockHitResult raycastCeiling(Vec3 origin) {
            BlockHitResult clip = cliffHanger.level().clip(new ClipContext(
                    origin.add(0, cliffHanger.getBbHeight(), 0),
                    origin.add(0, 50.0, 0),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    cliffHanger
            ));

            if (clip.getBlockPos().getY() < cliffHanger.blockPosition().getY() + 10) return BlockHitResult.miss(origin, cliffHanger.getDirection(), BlockPos.containing(origin));
            return clip;
        }

        private BlockHitResult searchForNearbyCeiling(Vec3 center, double radius) {
            for (int angle = 0; angle < 360; angle += 15) {
                double radians = Math.toRadians(angle);
                double offsetX = Math.cos(radians) * radius;
                double offsetZ = Math.sin(radians) * radius;

                Vec3 searchPos = center.add(offsetX, 0, offsetZ);
                BlockHitResult result = raycastCeiling(searchPos);

                if (result.getType() == HitResult.Type.BLOCK) {
                    return result;
                }
            }

            return BlockHitResult.miss(center, cliffHanger.getDirection(), BlockPos.containing(center));
        }
    }


    public static class CeilingTargetingGoal extends Goal {
        private final CliffHanger cliffHanger;
        private final double horizontalRange;
        private final Predicate<LivingEntity> predicate;

        public CeilingTargetingGoal(CliffHanger cliffHanger, double horizontalRange, Predicate<LivingEntity> predicate) {
            this.cliffHanger = cliffHanger;
            this.horizontalRange = horizontalRange;
            this.predicate = predicate;
        }

        @Override
        public boolean canUse() {
            if (!cliffHanger.isAttached() || cliffHanger.getTarget() != null || cliffHanger.getCantAttackTicks() > 0) return false;

            BlockPos ceilingPos = cliffHanger.blockPosition();
            int maxDistance = 40;
            int distance = 0;

            BlockPos.MutableBlockPos checkPos = ceilingPos.mutable();
            while (distance < maxDistance && cliffHanger.level().getBlockState(checkPos).isAir()) {
                checkPos.move(0, -1, 0);
                distance++;
            }

            if (distance >= maxDistance) return false;

            double floorY = ceilingPos.getY() - distance + 1;

            AABB box = new AABB(
                    cliffHanger.getX() - horizontalRange, floorY,
                    cliffHanger.getZ() - horizontalRange,
                    cliffHanger.getX() + horizontalRange, cliffHanger.getY(),
                    cliffHanger.getZ() + horizontalRange
            );


            List<LivingEntity> potentialTargets = cliffHanger.level().getEntitiesOfClass(LivingEntity.class, box, predicate);

            if (!potentialTargets.isEmpty()) {
                cliffHanger.setTarget(potentialTargets.get(0));
                if (potentialTargets.get(0) instanceof Mob mob && (mob.getTarget() == null || !mob.getTarget().is(cliffHanger))) mob.setTarget(cliffHanger);

                if (potentialTargets.get(0) instanceof ServerPlayer serverPlayer) {
                    Advancement hangerAdvancement = serverPlayer.server.getAdvancements().getAdvancement(new ResourceLocation(Species.MOD_ID, "species/v3/fall_for_hanger"));

                    if (hangerAdvancement != null) {
                        if (serverPlayer.getAdvancements().getOrStartProgress(hangerAdvancement).isDone()) SpeciesCriterion.FALL_FOR_HANGER_TWICE.trigger(serverPlayer);
                        else SpeciesCriterion.FALL_FOR_HANGER.trigger(serverPlayer);
                    }
                }
                return true;
            }

            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return cliffHanger.getTarget() != null && cliffHanger.getTarget().isAlive() && cliffHanger.isAttached() && cliffHanger.getCantAttackTicks() == 0;
        }

        @Override
        public void stop() {
            cliffHanger.setTarget(null);
        }
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<? extends PathfinderMob> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource randomSource) {
        return levelAccessor.getBrightness(LightLayer.BLOCK, blockPos) == 0 && levelAccessor.getBrightness(LightLayer.SKY, blockPos) == 0 && levelAccessor.getBlockState(blockPos.below()).is(SpeciesTags.CLIFF_HANGER_SPAWNABLE_ON) && levelAccessor.getBlockState(blockPos.below()).isValidSpawn(levelAccessor, blockPos, entityType) && levelAccessor.getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.level().playSound(null, this.blockPosition(), SpeciesSoundEvents.CLIFF_HANGER_ATTACK.get(), this.getSoundSource(), 1.0F, 1.0F);
        return super.doHurtTarget(target);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.getTarget() != null && this.isTongueOut() ? SpeciesSoundEvents.CLIFF_HANGER_IDLE_PULLING.get() : SpeciesSoundEvents.CLIFF_HANGER_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SpeciesSoundEvents.CLIFF_HANGER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.CLIFF_HANGER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState state) {
        this.playSound(SpeciesSoundEvents.CLIFF_HANGER_STEP.get(), 0.15F, this.getVoicePitch());
    }

}
