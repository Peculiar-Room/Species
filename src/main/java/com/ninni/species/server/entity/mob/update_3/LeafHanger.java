package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesTags;
import com.ninni.species.server.criterion.SpeciesCriterion;
import com.ninni.species.server.entity.mob.update_1.Limpet;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class LeafHanger extends Hanger {
    public static final EntityDataAccessor<Boolean> IS_PULLING_TARGET = SynchedEntityData.defineId(LeafHanger.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<BlockState> BAIT_BLOCK_STATE = SynchedEntityData.defineId(LeafHanger.class, EntityDataSerializers.BLOCK_STATE);

    public LeafHanger(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0, true));
        this.goalSelector.addGoal(0, new PullTowardsMouthGoal(this));
        this.goalSelector.addGoal(0, new BaitTargetingGoal(this,
                entity -> {
                    boolean flag = (entity.getType().is(SpeciesTags.LEAF_HANGER_PREY) || entity instanceof Player) && this.canAttack(entity) && entity.isAlive() && !entity.isSpectator();
                    if (entity instanceof Axolotl axolotl && axolotl.isPlayingDead()) return false;
                    return flag;
                }
        ));
        this.goalSelector.addGoal(1, new CastBaitGoal(this));
        this.goalSelector.addGoal(2, new FindUnderWaterSpotGoal(this, 1.25));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, LivingEntity.class, 4.0F, 1D, 1.25D, livingEntity -> !this.isInWaterOrBubble() && !(livingEntity instanceof LeafHanger)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_DAMAGE, 2);
    }

    @Override
    public @org.jetbrains.annotations.Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType spawnType, @org.jetbrains.annotations.Nullable SpawnGroupData groupData, @org.jetbrains.annotations.Nullable CompoundTag tag) {
        Holder<Biome> holder = serverLevelAccessor.getBiome(this.blockPosition());

        if (holder.is(SpeciesTags.LEAF_HANGER_HAS_DRIPLEAF)) {
            this.setBaitBlockState(Blocks.BIG_DRIPLEAF.defaultBlockState());
        }
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, spawnType, groupData, tag);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level() instanceof ServerLevel level) {

            if (this.random.nextInt(50) == 0) {
                level.sendParticles(ParticleTypes.SPLASH, this.getRandomX(1), this.getY() + this.getCurrentTonguePos().y, this.getRandomZ(1) , 1,0, 0, 0, 0);
            }
            if (this.random.nextFloat() < 0.3f && this.isPullingTarget()) {
                level.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, this.getRandomX(1.5), this.getY() + getTongueOffset(), this.getRandomZ(1.5) , 1,0, 0, 0, 0);
            }
        }
    }

    @Override
    public void unstuckTarget() {
        super.unstuckTarget();
        if ((this.getTargetPos().y < this.getY() - 3 && this.isTongueOut()) || this.getTarget() instanceof Axolotl axolotl && axolotl.isPlayingDead()) {
            deactivateTongue();
            this.setTarget(null);
            this.setCantAttackTicks(40);
        }
    }

    public void travel(Vec3 vec3) {
        if (this.isTongueOut()) this.setDeltaMovement(this.getDeltaMovement().multiply(0,1,0));

        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(0.01F, vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(1D).add(0,-0.2,0));
        } else {
            super.travel(vec3);
        }
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_PULLING_TARGET, false);
        this.entityData.define(BAIT_BLOCK_STATE, Blocks.LILY_PAD.defaultBlockState());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("IsPullingTarget", this.isPullingTarget());
        compoundTag.put("BaitBlockState", NbtUtils.writeBlockState(this.getBaitBlockState()));
        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setPullingTarget(compoundTag.getBoolean("IsPullingTarget"));
        this.setBaitBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compoundTag.getCompound("BaitBlockState")));
        super.readAdditionalSaveData(compoundTag);
    }

    public boolean isPullingTarget() {
        return this.entityData.get(IS_PULLING_TARGET);
    }
    public void setPullingTarget(boolean bl) {
        this.entityData.set(IS_PULLING_TARGET, bl);
    }

    public BlockState getBaitBlockState() {
        return this.entityData.get(BAIT_BLOCK_STATE);
    }
    public void setBaitBlockState(BlockState state) {
        this.entityData.set(BAIT_BLOCK_STATE, state);
    }


    public static class PullTowardsMouthGoal extends Goal {
        private final LeafHanger leafHanger;

        public PullTowardsMouthGoal(LeafHanger leafHanger) {
            this.leafHanger = leafHanger;
        }

        @Override
        public boolean canUse() {
            return leafHanger.isUnderWater() && leafHanger.getTarget() != null && (!leafHanger.getTarget().isPassenger() || leafHanger.getTarget().getVehicle() instanceof Boat) && leafHanger.getCantAttackTicks() == 0 && leafHanger.getTarget().isAlive();
        }

        @Override
        public void start() {
            super.start();
            leafHanger.setPullingTarget(true);
        }

        @Override
        public void tick() {
            if (!leafHanger.level().isClientSide) {
                LivingEntity target = leafHanger.getTarget();

                if (target.position().y > leafHanger.position().y - 3) {
                    Vec3 diff = leafHanger.position().add(0, leafHanger.getTongueOffset(), 0).subtract(target.position());
                    double diffY = Math.abs(diff.y);
                    diff = diff.multiply(0.15, 0, 0.15).add(0, (diffY * -0.02) - 0.01, 0);

                    target.setDeltaMovement(target.getDeltaMovement().add(diff));

                    if (target.getVehicle() instanceof Boat boat) {
                        leafHanger.setTarget(null);
                        leafHanger.setCantAttackTicks(200);
                        boat.setDeltaMovement(boat.getDeltaMovement().add(0.0D, -0.7D, 0.0D));
                        boat.ejectPassengers();
                        stop();
                    }

                    if (target instanceof Player player) {
                        player.hurtMarked = true;
                    }
                }

                if (leafHanger.tickCount % 10 == 0) {
                    leafHanger.level().playSound(null, leafHanger.getX(), leafHanger.getY(), leafHanger.getZ(), SpeciesSoundEvents.HANGER_PULL.get(), leafHanger.getSoundSource(), 1.0F, 1.0F);
                }
                
                Vector3f visualTarget = target.getBoundingBox().getCenter().toVector3f();
                leafHanger.setTargetPos(visualTarget);
                leafHanger.setTongueTarget(visualTarget);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return leafHanger.isUnderWater() && leafHanger.getTarget() != null && (!leafHanger.getTarget().isPassenger() || leafHanger.getTarget().getVehicle() instanceof Boat) && leafHanger.getCantAttackTicks() == 0 && leafHanger.getTarget().isAlive();
        }

        @Override
        public void stop() {
            leafHanger.setPullingTarget(false);
            leafHanger.deactivateTongue();
        }
    }

    public static class BaitTargetingGoal extends Goal {
        private final LeafHanger leafHanger;
        private final Predicate<LivingEntity> predicate;

        public BaitTargetingGoal(LeafHanger leafHanger, Predicate<LivingEntity> predicate) {
            this.leafHanger = leafHanger;
            this.predicate = predicate;
        }

        @Override
        public boolean canUse() {
            if (!leafHanger.isUnderWater() || !leafHanger.isTongueOut() || leafHanger.getTarget() != null || leafHanger.getCantAttackTicks() > 0) return false;

            BlockPos lilyPadPos = BlockPos.containing(new Vec3(leafHanger.getTargetPos()));

            AABB box = new AABB(
                    lilyPadPos.getX(),
                    lilyPadPos.getY() - 1,
                    lilyPadPos.getZ(),
                    lilyPadPos.getX() + 1,
                    lilyPadPos.getY() + 2,
                    lilyPadPos.getZ() + 1
            );


            List<LivingEntity> potentialTargets = leafHanger.level().getEntitiesOfClass(LivingEntity.class, box, predicate);

            if (!potentialTargets.isEmpty()) {
                leafHanger.setTarget(potentialTargets.get(0));
                potentialTargets.get(0).hurt(leafHanger.damageSources().mobAttack(leafHanger), 0);
                leafHanger.level().playSound(null, potentialTargets.get(0).blockPosition(), SpeciesSoundEvents.LEAF_HANGER_CATCH.get(), leafHanger.getSoundSource(), 2, 1);

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
            return leafHanger.getTarget() != null && leafHanger.getTarget().isAlive() && leafHanger.getCantAttackTicks() == 0;
        }

        @Override
        public void stop() {
            leafHanger.setTarget(null);
        }
    }

    public static class CastBaitGoal extends Goal {
        private final LeafHanger leafHanger;

        public CastBaitGoal(LeafHanger leafHanger) {
            this.leafHanger = leafHanger;
        }

        @Override
        public boolean canUse() {
            return !leafHanger.isTongueOut() && leafHanger.isUnderWater() && leafHanger.getNavigation().isDone() && leafHanger.getCantAttackTicks() == 0;
        }

        @Override
        public void tick() {
            Vec3 mobCenter = leafHanger.position().add(0, leafHanger.getTongueOffset(), 0);
            BlockPos currentPos = BlockPos.containing(mobCenter);

            while (leafHanger.level().getBlockState(currentPos).getFluidState().is(Fluids.WATER) && leafHanger.level().getBlockState(currentPos).getFluidState().isSource()) {
                currentPos = currentPos.below();
            }

            BlockPos searchPos = currentPos.above();
            while (leafHanger.level().getBlockState(searchPos).getFluidState().is(Fluids.WATER) && leafHanger.level().getBlockState(searchPos).getFluidState().isSource()) {
                searchPos = searchPos.above();
            }

            Vec3 target = Vec3.atBottomCenterOf(searchPos);

            if (mobCenter.distanceTo(target) < 40 && leafHanger.level().getBlockState(searchPos).isAir()) {
                leafHanger.playSound(SpeciesSoundEvents.LEAF_HANGER_SHOOT.get());
                leafHanger.activateTongue(target.toVector3f());
            } else {
                this.stop();
            }
        }
    }

    public static class FindUnderWaterSpotGoal extends Goal {
        private final LeafHanger leafHanger;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        private final double speedModifier;
        private final Level level;

        public FindUnderWaterSpotGoal(LeafHanger leafHanger, double speed) {
            this.leafHanger = leafHanger;
            this.speedModifier = speed;
            this.level = leafHanger.level();
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.leafHanger.isUnderWater()) {
                return !leafHanger.isTongueOut() && leafHanger.getNavigation().isDone();
            } else {
                Vec3 vec3 = this.getWaterPos();
                if (vec3 == null) {
                    return false;
                } else {
                    this.wantedX = vec3.x;
                    this.wantedY = vec3.y;
                    this.wantedZ = vec3.z;
                    return true;
                }
            }
        }

        public boolean canContinueToUse() {
            return !this.leafHanger.getNavigation().isDone();
        }

        public void start() {
            this.leafHanger.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Nullable
        private Vec3 getWaterPos() {
            RandomSource randomsource = this.leafHanger.getRandom();
            BlockPos blockpos = this.leafHanger.blockPosition();

            for(int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.offset(randomsource.nextInt(20) - 10, 2 - randomsource.nextInt(20), randomsource.nextInt(20) - 10);
                if (this.level.getBlockState(blockpos1).is(Blocks.WATER)) {
                    return Vec3.atBottomCenterOf(blockpos1);
                }
            }

            return null;
        }
    }

    public boolean checkSpawnObstruction(LevelReader p_30348_) {
        return p_30348_.isUnobstructed(this);
    }

    public static boolean canSpawn(EntityType<? extends Hanger> type, LevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return world.getFluidState(pos).is(Fluids.WATER) && world.getFluidState(pos.above()).is(Fluids.WATER) && world.getDifficulty() != Difficulty.PEACEFUL && pos.getY() < world.getSeaLevel() - 5;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.level().playSound(null, this.blockPosition(), SpeciesSoundEvents.LEAF_HANGER_ATTACK.get(), this.getSoundSource(), 1.0F, 1.0F);
        return super.doHurtTarget(target);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.getTarget() != null && this.isTongueOut() ? SpeciesSoundEvents.LEAF_HANGER_IDLE_PULLING.get() : SpeciesSoundEvents.LEAF_HANGER_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SpeciesSoundEvents.LEAF_HANGER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.LEAF_HANGER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState state) {
        this.playSound(SpeciesSoundEvents.LEAF_HANGER_STEP.get(), 0.15F, this.getVoicePitch());
    }
}
