package com.ninni.species.entity;

import com.google.common.annotations.VisibleForTesting;
import com.ninni.species.entity.ai.goal.GooberLayDownGoal;
import com.ninni.species.entity.ai.goal.GooberRearUpGoal;
import com.ninni.species.entity.ai.goal.GooberYawnGoal;
import com.ninni.species.entity.enums.GooberBehavior;
import com.ninni.species.entity.pose.SpeciesPose;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Goober extends Animal {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState layDownIdleAnimationState = new AnimationState();
    public final AnimationState layDownAnimationState = new AnimationState();
    public final AnimationState standUpAnimationState = new AnimationState();
    public final AnimationState yawnAnimationState = new AnimationState();
    public final AnimationState layDownYawnAnimationState = new AnimationState();
    public final AnimationState rearUpAnimationState = new AnimationState();
    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(Goober.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<String> BEHAVIOR = SynchedEntityData.defineId(Goober.class, EntityDataSerializers.STRING);
    private static final EntityDimensions SITTING_DIMENSIONS = EntityDimensions.scalable(2F, 1.4f);
    private int idleAnimationTimeout = 0;


    //TODO
    // they play yawn and rear up sounds when they change pose for some reason
    public Goober(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new GooberMoveControl();
        this.lookControl = new GooberLookControl(this);
        this.setMaxUpStep(1);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new Goober.GooberBodyRotationControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new GooberYawnGoal(this));
        this.goalSelector.addGoal(7, new GooberRearUpGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(9, new GooberLayDownGoal(this));
    }

    public static AttributeSupplier.Builder createGooberAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.15);
    }

    @Override
    public void tick() {
        super.tick();

        if ((this.level()).isClientSide()) {
            this.setupAnimationStates();
        }
        if (this.isGooberLayingDown() && this.isInWater()) {
            this.standUpInstantly();
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
        if (this.isGooberVisuallyLayingDown()) {
            this.standUpAnimationState.stop();
            this.yawnAnimationState.stop();
            this.rearUpAnimationState.stop();
            if (this.isVisuallyLayingDown()) {
                this.layDownAnimationState.startIfStopped(this.tickCount);
                this.layDownIdleAnimationState.stop();
            } else {
                this.layDownAnimationState.stop();
                //TODO fix animation not looping correctly
                this.layDownIdleAnimationState.startIfStopped(this.tickCount);
            }
        } else {
            this.layDownAnimationState.stop();
            this.layDownIdleAnimationState.stop();
            this.standUpAnimationState.animateWhen(this.isInPoseTransition() && this.getPoseTime() >= 0L, this.tickCount);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return (pose == SpeciesPose.LAYING_DOWN.get() || pose == SpeciesPose.YAWNING_LAYING_DOWN.get()) ? SITTING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(pose);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == SpeciesPose.YAWNING.get()) this.yawnAnimationState.start(this.tickCount);
            if (this.getPose() == SpeciesPose.YAWNING_LAYING_DOWN.get()) this.layDownYawnAnimationState.start(this.tickCount);
            if (this.getPose() == SpeciesPose.REARING_UP.get()) this.rearUpAnimationState.start(this.tickCount);
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isGooberLayingDown();
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.refuseToMove() && this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
            vec3 = vec3.multiply(0.0, 1.0, 0.0);
        }
        super.travel(vec3);
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float f) {
        this.standUpInstantly();
        super.actuallyHurt(damageSource, f);
    }

    public boolean refuseToMove() {
        return this.isGooberLayingDown() || this.isInPoseTransition() || this.getPose() == SpeciesPose.REARING_UP.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LAST_POSE_CHANGE_TICK, 0L);
        this.entityData.define(BEHAVIOR, GooberBehavior.IDLE.getName());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
        compoundTag.putString("Behavior", this.getBehavior());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        this.setBehavior(compoundTag.getString("Behavior"));
        long l = compoundTag.getLong("LastPoseTick");
        if (l < 0L) this.setPose(SpeciesPose.LAYING_DOWN.get());
        this.resetLastPoseChangeTick(l);
    }

    public String getBehavior() {
        return this.entityData.get(BEHAVIOR);
    }
    public void setBehavior(String behavior) {
        this.entityData.set(BEHAVIOR, behavior);
    }

    public boolean isGooberLayingDown() {
        return this.entityData.get(LAST_POSE_CHANGE_TICK) < 0L;
    }
    public boolean isGooberVisuallyLayingDown() {
        return this.getPoseTime() < 0L != this.isGooberLayingDown();
    }

    public boolean isInPoseTransition() {
        long l = this.getPoseTime();
        return l < (long)(this.isGooberLayingDown() ? 40 : 52);
    }

    private boolean isVisuallyLayingDown() {
        return this.isGooberLayingDown() && this.getPoseTime() < 40L && this.getPoseTime() >= 0L;
    }

    public void layDown() {
        if (this.isGooberLayingDown()) return;
        this.playSound(SpeciesSoundEvents.ENTITY_GOOBER_LAY_DOWN, 1.0f, 1.0f);
        this.setPose(SpeciesPose.LAYING_DOWN.get());
        this.resetLastPoseChangeTick(-(this.level()).getGameTime());
    }

    public void standUp() {
        if (!this.isGooberLayingDown()) {
            return;
        }
        this.setPose(Pose.STANDING);
        this.resetLastPoseChangeTick((this.level()).getGameTime());
    }

    public void standUpInstantly() {
        this.setPose(Pose.STANDING);
        this.resetLastPoseChangeTickToFullStand((this.level()).getGameTime());
    }

    @VisibleForTesting
    public void resetLastPoseChangeTick(long l) {
        this.entityData.set(LAST_POSE_CHANGE_TICK, l);
    }

    private void resetLastPoseChangeTickToFullStand(long l) {
        this.resetLastPoseChangeTick(Math.max(0L, l - 52L - 1L));
    }

    public long getPoseTime() {
        return (this.level()).getGameTime() - Math.abs(this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isGooberLayingDown() ? SpeciesSoundEvents.ENTITY_GOOBER_IDLE_RESTING : SpeciesSoundEvents.ENTITY_GOOBER_IDLE;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.ENTITY_GOOBER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.ENTITY_GOOBER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SpeciesSoundEvents.ENTITY_GOOBER_STEP, 0.15f, 1.0f);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    static class GooberLookControl extends LookControl {
        protected final Goober mob;
        GooberLookControl(Goober mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (!mob.refuseToMove()) super.tick();

        }
    }

    class GooberMoveControl
            extends MoveControl {
        public GooberMoveControl() {
            super(Goober.this);
        }

        @Override
        public void tick() {
            if (!Goober.this.refuseToMove()) {
                if (this.operation == MoveControl.Operation.MOVE_TO && !Goober.this.isLeashed() && Goober.this.isGooberLayingDown() && !Goober.this.isInPoseTransition()) {
                    Goober.this.standUp();
                }
                super.tick();
            }
        }
    }

    class GooberBodyRotationControl
            extends BodyRotationControl {
        public GooberBodyRotationControl(Goober goober) {
            super(goober);
        }

        @Override
        public void clientTick() {
            if (!Goober.this.refuseToMove()) super.clientTick();
        }
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Goober> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return false;
    }
}
