package com.ninni.species.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BirtEntity extends PassiveEntity implements Flutterer {
    public final AnimationState flyingAnimationState = new AnimationState();
    public int groundTicks;

    public BirtEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.fromTag(ItemTags.FLOWERS), false));
        this.goalSelector.add(8, new BirtWanderAroundGoal());
        this.goalSelector.add(9, new BirtLookAroundGoal());
    }

    public static DefaultAttributeContainer.Builder createBirtAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6f);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            EntityPose entityPose = this.getPose();
            if (entityPose == EntityPose.FALL_FLYING) {
                this.flyingAnimationState.start(this.age);
            } else {
                this.flyingAnimationState.stop();
            }
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        Vec3d vec3d = this.getVelocity();
        if (!this.onGround && vec3d.y < 0.0) {
            this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
        }
        if (this.isInAir()) {
            this.groundTicks = random.nextInt(100) + 20;
            this.setPose(EntityPose.FALL_FLYING);
        }
        else {
            this.groundTicks--;
            this.setPose(EntityPose.STANDING);
        }
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return world.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    public boolean isInAir() {
        return !this.onGround;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world) {
            @Override
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }
        };

        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(false);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<? extends PassiveEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        return false;
    }

    class BirtWanderAroundGoal extends Goal {

        BirtWanderAroundGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (BirtEntity.this.isOnGround() && BirtEntity.this.groundTicks < 0) return true;
            return BirtEntity.this.navigation.isIdle() && BirtEntity.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean shouldContinue() {
            if (BirtEntity.this.isOnGround() && BirtEntity.this.groundTicks < 0) return true;
            return BirtEntity.this.navigation.isFollowingPath();
        }

        @Override
        public void start() {
            Vec3d vec3d = this.getRandomLocation();
            if (vec3d != null) {
                BirtEntity.this.navigation.startMovingAlong(BirtEntity.this.navigation.findPathTo(new BlockPos(vec3d), 1), 1.0);
            }
        }

        @Nullable
        private Vec3d getRandomLocation() {
            Vec3d vec3d2 = BirtEntity.this.getRotationVec(0.0f);
            Vec3d vec3d3 = AboveGroundTargeting.find(BirtEntity.this, 16, 7, vec3d2.x, vec3d2.z, 1.5707964f, 3, 1);
            if (vec3d3 != null) {
                return vec3d3;
            }
            return NoPenaltySolidTargeting.find(BirtEntity.this, 16, 4, -2, vec3d2.x, vec3d2.z, 1.5707963705062866);
        }
    }

    class BirtLookAroundGoal extends LookAroundGoal {

        BirtLookAroundGoal() {
            super(BirtEntity.this);
        }

        @Override
        public boolean canStart() {
            return BirtEntity.this.isOnGround() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return BirtEntity.this.isOnGround() && super.shouldContinue();
        }
    }
}
