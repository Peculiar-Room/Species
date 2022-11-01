package com.ninni.species.entity;

import com.ninni.species.entity.ai.goal.BirtCommunicatingGoal;
import com.ninni.species.entity.ai.goal.SendMessageTicksGoal;
import com.ninni.species.sound.SpeciesSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class BirtEntity extends AnimalEntity implements Flutterer {
    public final AnimationState flyingAnimationState = new AnimationState();
    public float flapProgress;
    public float maxWingDeviation;
    public float prevMaxWingDeviation;
    public float prevFlapProgress;
    public float flap = 1;
    public int antennaTicks;
    private float flapSpeed = 1.0f;
    public int groundTicks;
    public int messageTicks = 0;

    public BirtEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.fromTag(ItemTags.FLOWERS), false));
        this.goalSelector.add(4, new SendMessageTicksGoal(this));
        this.goalSelector.add(5, new BirtCommunicatingGoal(this));
        this.goalSelector.add(8, new BirtWanderAroundGoal());
        this.goalSelector.add(9, new BirtLookAroundGoal());
    }

    public static DefaultAttributeContainer.Builder createBirtAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2);
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
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("canMessage", this.messageTicks);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.messageTicks = nbt.getInt("canMessage");
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        Vec3d vec3d = this.getVelocity();
        if (this.antennaTicks > 0) {
            this.antennaTicks--;
        }
        if (!this.onGround && vec3d.y < 0.0) {
            this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
        }
        if (this.isInAir()) {
            this.groundTicks = random.nextInt(300) + 20;
            this.setPose(EntityPose.FALL_FLYING);
        }
        else {
            this.groundTicks--;
            this.setPose(EntityPose.STANDING);
        }

        if (messageTicks > 0) this.messageTicks--;
        this.flapWings();
    }

    @Nullable
    public BirtEntity findReciever() {
        List<? extends BirtEntity> list = this.world.getTargets(BirtEntity.class, TargetPredicate.DEFAULT, this, this.getBoundingBox().expand(8.0));
        double d = Double.MAX_VALUE;
        BirtEntity birt = null;
        for (BirtEntity birt2 : list) {
            if (!(this.squaredDistanceTo(birt2) < d)) continue;
            birt = birt2;
            d = this.squaredDistanceTo(birt2);
        }
        return birt;
    }

    public boolean canSendMessage() {
        return this.messageTicks > 0;
    }

    public void setMessageTicks(int messageTicks) {
        this.messageTicks = messageTicks;
    }

    public void resetMessageTicks() {
        this.messageTicks = 0;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 10) {
            this.antennaTicks = 60;
        }
        else {
            super.handleStatus(status);
        }
    }

    public void sendMessage(ServerWorld world, BirtEntity other) {
        this.resetMessageTicks();
        other.resetMessageTicks();

        PositionSource positionSource = new PositionSource() {
            @Override
            public Optional<Vec3d> getPos(World world) {
                return Optional.of(new Vec3d(BirtEntity.this.getX(), BirtEntity.this.getY() + 0.75, BirtEntity.this.getZ()));
            }

            @Override
            public PositionSourceType<?> getType() {
                return PositionSourceType.ENTITY;
            }
        };

        world.playSound(null, other.getBlockPos(), SpeciesSoundEvents.ENTITY_BIRT_MESSAGE, SoundCategory.NEUTRAL, 1,  0.6f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        world.spawnParticles(new VibrationParticleEffect(positionSource, 20), other.getX(), other.getY() + 0.75, other.getZ(), 0, 0, 0, 0, 0);
    }

    private void flapWings() {
        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation += (float)(this.onGround || this.hasVehicle() ? -1 : 4) * 0.3f;
        this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0f, 1.0f);
        if (!this.onGround && this.flapSpeed < 1.0f) {
            this.flapSpeed = 1.0f;
        }
        this.flapSpeed *= 0.9f;
        Vec3d vec3d = this.getVelocity();
        if (!this.onGround && vec3d.y < 0.0) {
            this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
        }
        this.flapProgress += this.flapSpeed * 2.0f;
    }


    @Override
    protected boolean hasWings() {
        return this.speed > this.flap;
    }

    @Override
    protected void addFlapEffects() {
        this.playSound(SpeciesSoundEvents.ENTITY_BIRT_FLY, 0.15f, 1.0f);
        this.flap = this.speed + this.maxWingDeviation / 2.0f;
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
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SpeciesSoundEvents.ENTITY_BIRT_IDLE;
    }
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.ENTITY_BIRT_HURT;
    }
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.ENTITY_BIRT_DEATH;
    }

    class BirtWanderAroundGoal extends Goal {

        BirtWanderAroundGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (BirtEntity.this.groundTicks < 0) return true;
            else if (BirtEntity.this.isInAir()) return BirtEntity.this.navigation.isIdle() && BirtEntity.this.random.nextInt(10) == 0;
            return false;
        }

        @Override
        public boolean shouldContinue() {
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
            Vec3d vec3d3 = AboveGroundTargeting.find(BirtEntity.this, 12, 7, vec3d2.x, vec3d2.z, 1.5707964f, 3, 1);
            if (vec3d3 != null) {
                return vec3d3;
            }
            return NoPenaltySolidTargeting.find(BirtEntity.this, 12, 4, -2, vec3d2.x, vec3d2.z, 1.5707963705062866);
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
