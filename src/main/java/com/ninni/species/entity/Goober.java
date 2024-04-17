package com.ninni.species.entity;

import com.google.common.annotations.VisibleForTesting;
import com.ninni.species.entity.ai.goal.GooberLayDownGoal;
import com.ninni.species.entity.ai.goal.GooberRearUpGoal;
import com.ninni.species.entity.ai.goal.GooberYawnGoal;
import com.ninni.species.entity.enums.GooberBehavior;
import com.ninni.species.entity.pose.SpeciesPose;
import com.ninni.species.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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
    public final AnimationState sneezeAnimationState = new AnimationState();
    public final AnimationState layDownSneezeAnimationState = new AnimationState();
    public final AnimationState rearUpAnimationState = new AnimationState();
    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(Goober.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<String> BEHAVIOR = SynchedEntityData.defineId(Goober.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> LAY_DOWN_COOLDOWN = SynchedEntityData.defineId(Goober.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> YAWN_COOLDOWN = SynchedEntityData.defineId(Goober.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> REAR_UP_COOLDOWN = SynchedEntityData.defineId(Goober.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SNEEZE_COOLDOWN = SynchedEntityData.defineId(Goober.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SNEEZE_TIMER = SynchedEntityData.defineId(Goober.class, EntityDataSerializers.INT);
    private static final EntityDimensions SITTING_DIMENSIONS = EntityDimensions.scalable(2F, 1.4f);
    private int idleAnimationTimeout = 0;


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
        this.goalSelector.addGoal(1, new PanicGoal(this, 2));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2, Ingredient.of(SpeciesTags.GOOBER_BREED_ITEMS), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new GooberYawnGoal(this));
        this.goalSelector.addGoal(7, new GooberRearUpGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(9, new GooberLayDownGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.15);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        boolean bl = this.isFood(itemStack);
        InteractionResult interactionResult = super.mobInteract(player, interactionHand);
        if (interactionResult.consumesAction() && bl) {
            if (this.isGooberLayingDown()) this.standUp();
            this.level().playSound(null, this, SpeciesSoundEvents.GOOBER_EAT, SoundSource.NEUTRAL, 1.0f, Mth.randomBetween(this.level().random, 0.8f, 1.2f));
        }

        if ((itemStack.is(Items.FEATHER) || itemStack.is(Items.BRUSH)) && this.getSneezeCooldown() == 0 && this.getBehavior().equals(GooberBehavior.IDLE.getName())) {
            this.setSneezeTimer(GooberBehavior.SNEEZING.getLength());
            this.setBehavior(GooberBehavior.SNEEZING.getName());
            this.setPose(this.isGooberLayingDown() ? SpeciesPose.SNEEZING_LAYING_DOWN.get() : SpeciesPose.SNEEZING.get());
            this.sneezeCooldown();
            this.playSound(SpeciesSoundEvents.GOOBER_SNEEZE, 2, 1);
            return InteractionResult.SUCCESS;
        }

        return interactionResult;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel serverLevel, Animal animal) {
        ItemStack itemStack = new ItemStack(SpeciesItems.PETRIFIED_EGG);
        ItemEntity itemEntity = new ItemEntity(serverLevel, this.position().x(), this.position().y(), this.position().z(), itemStack);
        itemEntity.setDefaultPickUpDelay();
        this.finalizeSpawnChildFromBreeding(serverLevel, animal, null);
        this.playSound(SpeciesSoundEvents.PETRIFIED_EGG_PLOP, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 0.5f);
        serverLevel.addFreshEntity(itemEntity);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(SpeciesTags.GOOBER_BREED_ITEMS);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getLayDownCooldown() > 0) this.setLayDownCooldown(this.getLayDownCooldown()-1);
        if (this.getRearUpCooldown() > 0) {
            if (this.getBehavior().equals(GooberBehavior.REAR_UP.getName())) this.setBehavior(GooberBehavior.IDLE.getName());
            this.setRearUpCooldown(this.getRearUpCooldown()-1);
        }
        if (this.getYawnCooldown() > 0) {
            if (this.getBehavior().equals(GooberBehavior.YAWN.getName())) this.setBehavior(GooberBehavior.IDLE.getName());
            this.setYawnCooldown(this.getYawnCooldown()-1);
        }
        if (this.getSneezeCooldown() > 0) {
            if (this.getBehavior().equals(GooberBehavior.SNEEZING.getName())) this.setBehavior(GooberBehavior.IDLE.getName());
            this.setSneezeCooldown(this.getSneezeCooldown()-1);
        }

        if (this.getSneezeTimer() > 0)  {
            this.setSneezeTimer(this.getSneezeTimer() - 1);

            if (this.getSneezeTimer() == 35) {
                BlockPos blockPos = this.blockPosition();
                final float angle = (0.0174532925F * this.yBodyRot);
                final double headX = 3F * this.getScale() * Mth.sin(Mth.PI + angle);
                final double headZ = 3F * this.getScale() * Mth.cos(angle);

                Vec3 shootingVec = this.getLookAngle().scale(2.0D).multiply(0.25D, 1.0D, 0.25D);

                GooberGoo goo = new GooberGoo(this.level(), (double) blockPos.getX() + headX, blockPos.getY() + this.getEyeHeight() + (this.isGooberLayingDown() ? 0 : 0.35f), (double) blockPos.getZ() + headZ);
                double d = shootingVec.x();
                double e = shootingVec.y();
                double g = shootingVec.z();
                double h = Math.sqrt(d * d + g * g);
                goo.shoot(d, e + h * (double)0.1f, g, 0.8f, 14 - this.level().getDifficulty().getId() * 4);
                this.level().addFreshEntity(goo);

                if (!isGooberLayingDown()) {
                    this.addDeltaMovement(new Vec3(0, 0.25D, 0));
                    this.addDeltaMovement(this.getLookAngle().scale(2.0D).multiply(-0.5D, 0, -0.5D));
                }
          }

            if (this.getSneezeTimer() == 1) {
                if (this.isGooberLayingDown()) this.setPose(SpeciesPose.LAYING_DOWN.get());
                else this.setPose(Pose.STANDING);

                this.setBehavior(GooberBehavior.IDLE.getName());
            }
        }


        if ((this.level()).isClientSide()) {
            this.setupAnimationStates();
        }
        if (this.isGooberLayingDown() && this.isInWater()) {
            this.standUpInstantly();
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout == 0) {
            this.idleAnimationTimeout = 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
        if (this.isGooberVisuallyLayingDown()) {
            this.standUpAnimationState.stop();
            this.yawnAnimationState.stop();
            this.sneezeAnimationState.stop();
            this.rearUpAnimationState.stop();
            if (this.isVisuallyLayingDown()) {
                this.layDownAnimationState.startIfStopped(this.tickCount);
                this.layDownIdleAnimationState.stop();
            } else {
                this.layDownAnimationState.stop();
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
        return (pose == SpeciesPose.LAYING_DOWN.get() || pose == SpeciesPose.YAWNING_LAYING_DOWN.get() || pose == SpeciesPose.SNEEZING_LAYING_DOWN.get()) ? SITTING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(pose);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == SpeciesPose.SNEEZING.get()) this.sneezeAnimationState.start(this.tickCount);
            if (this.getPose() == SpeciesPose.SNEEZING_LAYING_DOWN.get()) this.layDownSneezeAnimationState.start(this.tickCount);
            if (this.getPose() == SpeciesPose.YAWNING.get()) this.yawnAnimationState.start(this.tickCount);
            if (this.getPose() == SpeciesPose.YAWNING_LAYING_DOWN.get()) this.layDownYawnAnimationState.start(this.tickCount);
            if (this.getPose() == SpeciesPose.REARING_UP.get()) this.rearUpAnimationState.start(this.tickCount);
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public boolean fireImmune() {
        return this.isBaby();
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
        return this.isGooberLayingDown() || this.isInPoseTransition();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LAST_POSE_CHANGE_TICK, 0L);
        this.entityData.define(BEHAVIOR, GooberBehavior.IDLE.getName());
        this.entityData.define(LAY_DOWN_COOLDOWN, 12 * 20 + random.nextInt(30 * 20));
        this.entityData.define(YAWN_COOLDOWN, 2 * 20 + random.nextInt(12 * 20));
        this.entityData.define(REAR_UP_COOLDOWN, 60 * 20 + random.nextInt(60 * 4 * 20));
        this.entityData.define(SNEEZE_TIMER, 0);
        this.entityData.define(SNEEZE_COOLDOWN,
                0
                //60 * 20 + random.nextInt(60 * 4 * 20)
        );
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
        compoundTag.putString("Behavior", this.getBehavior());
        compoundTag.putInt("LayDownCooldown", this.getLayDownCooldown());
        compoundTag.putInt("YawnCooldown", this.getYawnCooldown());
        compoundTag.putInt("RearUpCooldown", this.getRearUpCooldown());
        compoundTag.putInt("SneezeTimer", this.getSneezeTimer());
        compoundTag.putInt("SneezeCooldown", this.getSneezeCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        this.setLayDownCooldown(compoundTag.getInt("LayDownCooldown"));
        this.setLayDownCooldown(compoundTag.getInt("LayDownCooldown"));
        this.setYawnCooldown(compoundTag.getInt("YawnCooldown"));
        this.setRearUpCooldown(compoundTag.getInt("RearUpCooldown"));
        this.setSneezeTimer(compoundTag.getInt("SneezeTimer"));
        this.setSneezeCooldown(compoundTag.getInt("SneezeCooldown"));
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

    public int getLayDownCooldown() {
        return this.entityData.get(LAY_DOWN_COOLDOWN);
    }
    public void setLayDownCooldown(int cooldown) {
        this.entityData.set(LAY_DOWN_COOLDOWN, cooldown);
    }
    public void layDownCooldown() {
        this.entityData.set(LAY_DOWN_COOLDOWN, 30 * 20 + random.nextInt(60 * 2 * 20));
    }
    public void standUpCooldown() {
        this.entityData.set(LAY_DOWN_COOLDOWN, 60 * 20 + random.nextInt(60 * 2 * 20));
    }

    public int getYawnCooldown() {
        return this.entityData.get(YAWN_COOLDOWN);
    }
    public void setYawnCooldown(int cooldown) {
        this.entityData.set(YAWN_COOLDOWN, cooldown);
    }
    public void yawnCooldown() {
        this.entityData.set(YAWN_COOLDOWN, 6 * 20 + random.nextInt(60 * 2 * 20));
    }

    public int getSneezeTimer() {
        return this.entityData.get(SNEEZE_TIMER);
    }
    public void setSneezeTimer(int timer) {
        this.entityData.set(SNEEZE_TIMER, timer);
    }

    public int getSneezeCooldown() {
        return this.entityData.get(SNEEZE_COOLDOWN);
    }
    public void setSneezeCooldown(int cooldown) {
        this.entityData.set(SNEEZE_COOLDOWN, cooldown);
    }
    public void sneezeCooldown() {
        this.entityData.set(SNEEZE_COOLDOWN,
                0
                //60 * 2 * 20 + random.nextInt(60 * 8 * 20)
        );
    }

    public int getRearUpCooldown() {
        return this.entityData.get(REAR_UP_COOLDOWN);
    }
    public void setRearUpCooldown(int cooldown) {
        this.entityData.set(REAR_UP_COOLDOWN, cooldown);
    }
    public void rearUpCooldown() {
        this.entityData.set(REAR_UP_COOLDOWN, 60 * 2 * 20 + random.nextInt(60 * 8 * 20));
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
        this.playSound(SpeciesSoundEvents.GOOBER_LAY_DOWN, 1.0f, 1.0f);
        this.setPose(SpeciesPose.LAYING_DOWN.get());
        this.resetLastPoseChangeTick(-(this.level()).getGameTime());
        this.refreshDimensions();
    }

    public void standUp() {
        if (!this.isGooberLayingDown()) {
            return;
        }
        this.setPose(Pose.STANDING);
        this.resetLastPoseChangeTick((this.level()).getGameTime());
        this.refreshDimensions();
    }

    public void standUpInstantly() {
        this.setPose(Pose.STANDING);
        this.resetLastPoseChangeTickToFullStand((this.level()).getGameTime());
        this.refreshDimensions();
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
        return this.getBehavior().equals(GooberBehavior.IDLE.getName())  ? this.isGooberLayingDown() ? SpeciesSoundEvents.GOOBER_IDLE_RESTING : SpeciesSoundEvents.GOOBER_IDLE : SoundEvents.EMPTY;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.GOOBER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.GOOBER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SpeciesSoundEvents.GOOBER_STEP, 0.35f, 1.0f);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return SpeciesEntities.GOOBER.create(serverLevel);
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

    class GooberMoveControl extends MoveControl {
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
