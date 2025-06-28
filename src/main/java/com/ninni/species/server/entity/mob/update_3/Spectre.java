package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.util.CustomDeathParticles;
import com.ninni.species.server.entity.util.SpeciesPose;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class Spectre extends Monster implements OwnableEntity, CustomDeathParticles, VariantHolder<Spectre.Type> {
    public static final EntityDataAccessor<Boolean> FROM_SWORD = SynchedEntityData.defineId(Spectre.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_TYPE_ID = SynchedEntityData.defineId(Spectre.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<Optional<UUID>> OWNERUUID_ID = SynchedEntityData.defineId(Spectre.class, EntityDataSerializers.OPTIONAL_UUID);
    public final AnimationState poofAnimationState = new AnimationState();
    public final AnimationState spawnAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState dashAnimationState = new AnimationState();
    public int poofTimer;
    public int spawnTimer;
    public int attackTimer;
    public int dashTimer;
    public int timeLeftToLive;
    public int flyingSoundCooldown;

    public Spectre(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
        if (levelAccessor instanceof ServerLevel) {
            if (this.getVariant() == Type.HULKING_SPECTRE) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(50);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10);
                this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(4);
                this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5F);
            }
            if (this.getVariant() == Type.JOUSTING_SPECTRE) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(15);
            }
            this.setHealth((float) this.getAttribute(Attributes.MAX_HEALTH).getValue());
        }
        return super.finalizeSpawn(levelAccessor, difficultyInstance, spawnType, spawnGroupData, tag);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(0, new OwnerHurtByTargetGoal());
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal());
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(Spectre.class));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 0, true, false, Turtle.BABY_ON_LAND_SELECTOR));

        this.goalSelector.addGoal(0, new SpectreMeleeAttackGoal(this.getVariant() == Type.HULKING_SPECTRE ? 0.45D : 0.55D, true));
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 0.5, 10, 4));
        this.goalSelector.addGoal(5, new DashTowardsTargetGoal(this));
        this.goalSelector.addGoal(6, new SpectreRandomLookAroundGoalGoal());
        this.goalSelector.addGoal(6, new SpectreWanderAroundGoal());
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.FLYING_SPEED, 0.23F);
    }

    @Override
    public void tick() {
        super.tick();

        //Cooldowns and poses
        if (attackTimer > 0) --attackTimer;
        if (attackTimer == 0 && this.getPose() == SpeciesPose.ATTACK.get()) this.setPose(Pose.STANDING);
        if (dashTimer > 0) --dashTimer;
        if (dashTimer == 0 && this.getPose() == SpeciesPose.DASH.get()) this.setPose(Pose.STANDING);
        if (poofTimer > 0) --poofTimer;
        if (poofTimer == 0 && this.getPose() == Pose.DYING) this.poofAnimationState.stop();
        if (spawnTimer > 0) --spawnTimer;
        if (attackTimer == 0 && this.getPose() == SpeciesPose.SPAWNING.get()) this.setPose(Pose.STANDING);
        if (dashTimer == 0 && this.getPose() == SpeciesPose.DASH.get()) this.setPose(Pose.STANDING);
        if (timeLeftToLive > 0) --timeLeftToLive;
        if (timeLeftToLive == 0 && this.tickCount % 20 == 0 && this.isFromSword()) this.hurt(this.damageSources().magic(), 4);

        if (!this.onGround()) {
            if (flyingSoundCooldown > 0) flyingSoundCooldown--;
            if (flyingSoundCooldown == 0) {
                this.playSound(SpeciesSoundEvents.SPECTRE_FLY.get(), 0.15F, 1);
                flyingSoundCooldown = 60 + random.nextInt(40);
            }
        }
    }

    public static void spawnSpectre(ServerLevel serverLevel, @Nullable Player player, BlockPos pos, Spectre.Type type, boolean fromSword) {
        Spectre spectre = new Spectre(SpeciesEntities.SPECTRE.get(), serverLevel);

        spectre.setPos(pos.getX() + 0.5 + (spectre.random.nextGaussian() * 0.5F), pos.getY(), pos.getZ() + 0.5 + (spectre.random.nextGaussian() * 0.5F));
        if (player != null) {
            if (fromSword) {
                spectre.getAttribute(Attributes.FLYING_SPEED).addPermanentModifier(new AttributeModifier("From sword speed bonus", 0.1, AttributeModifier.Operation.ADDITION));
                spectre.setFromSword(true);
                spectre.setOwnerUUID(player.getUUID());
                spectre.timeLeftToLive = 20 * 45;
                if (player.getMainHandItem().hasCustomHoverName())
                    spectre.setCustomName(Component.translatable("item.species.spectralibur.summon", player.getMainHandItem().getHoverName().getString()).withStyle(Style.EMPTY.withColor(0x44B4D1)));
                if (player.getLastHurtMob() != null && player.getLastHurtMob().isAlive())
                    spectre.setTarget(player.getLastHurtMob());
            } else {
                if (!player.isCreative() && !player.isSpectator()) {
                    spectre.getLookControl().setLookAt(player);
                    spectre.setTarget(player);
                }
            }
        }
        spectre.setVariant(type);
        spectre.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
        spectre.setPose(SpeciesPose.SPAWNING.get());
        serverLevel.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SpeciesSoundEvents.SPECTRE_SPAWN.get(), spectre.getSoundSource(), 1.0F, 1);
        serverLevel.addFreshEntity(spectre);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (this.isFromSword() && this.getOwner() != null && damageSource.getEntity() instanceof Player player && player.is(this.getOwner())) {
            return player.isSecondaryUseActive() && super.hurt(damageSource, amount);
        }
        return super.hurt(damageSource, amount);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 40 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
        if (this.deathTime == 1) {
            this.playSound(SpeciesSoundEvents.SPECTRE_DEATH.get(),1,1);
        }

        if (!this.isRemoved() && deathTime < 20) {
            this.addDeltaMovement(new Vec3(0,0.01F,0));
            if (this.random.nextInt(3) == 0) this.level().addParticle(SpeciesParticles.SPECTRE_SMOKE.get(), this.getRandomX(1), this.getRandomY(), this.getRandomZ(1), 0, 0, 0);
        }
    }

    @Override
    public void makeDeathParticles() {
        for(int i = 0; i < 10; ++i) {
            double d3 = this.random.nextGaussian() * 0.2;
            double d4 = this.random.nextGaussian() * 0.2;
            this.level().addParticle(SpeciesParticles.BROKEN_LINK.get(), this.getX(), this.getY() + this.getBbHeight()/2, this.getZ(), d3, 0, d4);
        }
        this.level().addParticle(SpeciesParticles.SPECTRE_POP.get(), this.getX(), this.getY() + this.getBbHeight()/2, this.getZ(), 0, 0, 0);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.setPose(SpeciesPose.ATTACK.get());

        if (entity instanceof Player player) {
            ItemStack stack = player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY;
            if (!stack.isEmpty() && stack.getItem() instanceof ShieldItem) {
                int amount;
                switch (this.getVariant()) {
                    case JOUSTING_SPECTRE -> amount = 5 * 20;
                    case HULKING_SPECTRE -> amount = 10 * 20;
                    default -> amount = 0;
                }
                player.getCooldowns().addCooldown(stack.getItem(), amount);
                player.stopUsingItem();
                player.level().broadcastEntityEvent(this, (byte)29);
                return true;
            }
        }

        if (this.getVariant() == Type.SPECTRE) {
            this.playSound(SpeciesSoundEvents.SPECTRE_ATTACK.get(), 1, 1F);
        }
        if (this.getVariant() == Type.JOUSTING_SPECTRE) {
            this.playSound(SpeciesSoundEvents.SPECTRE_JOUSTING_ATTACK.get(), 1, 1F);
        }
        if (this.getVariant() == Type.HULKING_SPECTRE) {
            this.playSound(SpeciesSoundEvents.SPECTRE_HULKING_ATTACK.get(), 1, 1F);
            boolean hurt = entity.hurt(this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            float f1 = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            if (hurt) {
                if (f1 > 0.0F && entity instanceof LivingEntity livingEntity) {
                    livingEntity.knockback((f1 * 0.5F), Mth.sin(this.getYRot() * 0.017453292F), (-Mth.cos(this.getYRot() * 0.017453292F)));
                    livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0.0, 0.4, 0.0));
                }
            }
            return hurt;
        }

        return super.doHurtTarget(entity);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == Pose.STANDING) {
                this.attackAnimationState.stop();
            }
            else if (this.getPose() == SpeciesPose.ATTACK.get()) {
                this.attackTimer = 15;
                this.attackAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == SpeciesPose.DASH.get()) {
                this.dashTimer = 40;
                this.dashAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == SpeciesPose.SPAWNING.get()) {
                spawnTimer = 25;
                this.spawnAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == Pose.DYING) {
                poofTimer = 40;
                this.poofAnimationState.start(this.tickCount);
                this.dashAnimationState.stop();
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        return levelReader.getBlockState(blockPos).isAir() ? 10.0F : 0.0F;
    }

    protected PathNavigation createNavigation(Level p_218342_) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_218342_);
        flyingpathnavigation.setCanOpenDoors(true);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    public void travel(Vec3 vec3) {
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    protected void playStepSound(BlockPos p_218364_, BlockState p_218365_) {
    }

    protected void checkFallDamage(double p_218316_, boolean p_218317_, BlockState p_218318_, BlockPos p_218319_) {
    }

    public SoundSource getSoundSource() {
        return this.isFromSword() ? SoundSource.PLAYERS : SoundSource.HOSTILE;
    }

    protected boolean shouldDropLoot() {
        return !this.isFromSword();
    }

    public boolean isPreventingPlayerRest(Player player) {
        return !this.isFromSword();
    }

    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return !this.isFromSword();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_SWORD, false);
        this.entityData.define(OWNERUUID_ID, Optional.empty());
        this.entityData.define(DATA_TYPE_ID, "spectre");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("FromSword", this.isFromSword());
        compoundTag.putString("Type", this.getVariant().getSerializedName());
        compoundTag.putInt("TimeLeftToLive", this.timeLeftToLive);

        if (this.getOwnerUUID() != null) {
            compoundTag.putUUID("Owner", this.getOwnerUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.hasUUID("Owner")) compoundTag.getUUID("Owner");
        else OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), compoundTag.getString("Owner"));

        this.timeLeftToLive = compoundTag.getInt("TimeLeftToLive");
        this.setVariant(Spectre.Type.byName(compoundTag.getString("Type")));
        this.setFromSword(compoundTag.getBoolean("FromSword"));
    }

    @Override
    public Spectre.Type getVariant() {
        return Spectre.Type.byName(this.entityData.get(DATA_TYPE_ID));
    }

    @Override
    public void setVariant(Spectre.Type type) {
        this.entityData.set(DATA_TYPE_ID, type.getSerializedName());
    }

    public boolean isFromSword() {
        return this.entityData.get(FROM_SWORD);
    }
    public void setFromSword(boolean fromSword) {
        this.entityData.set(FROM_SWORD, fromSword);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID p_21817_) {
        this.entityData.set(OWNERUUID_ID, Optional.ofNullable(p_21817_));
    }

    public boolean isAlliedTo(Entity entity) {
        if (this.isFromSword()) {
            LivingEntity livingentity = this.getOwner();
            if (entity == livingentity) return true;
            if (livingentity != null) return livingentity.isAlliedTo(entity);
        }
        return super.isAlliedTo(entity);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.getVariant() == Type.JOUSTING_SPECTRE ? SoundEvents.EMPTY : SpeciesSoundEvents.SPECTRE_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SpeciesSoundEvents.SPECTRE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EMPTY;
    }

    class SpectreMeleeAttackGoal extends MeleeAttackGoal {

        public SpectreMeleeAttackGoal(double p_25553_, boolean p_25554_) {
            super(Spectre.this, p_25553_, p_25554_);
        }

        @Override
        public boolean canUse() {
            if (Spectre.this.getVariant() == Type.JOUSTING_SPECTRE) return false;
            return super.canUse();
        }
    }

    class SpectreWanderAroundGoal extends Goal {

        SpectreWanderAroundGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (Spectre.this.getVariant() == Type.JOUSTING_SPECTRE) return false;
            return Spectre.this.navigation.isDone() && Spectre.this.random.nextInt(10) == 0 && Spectre.this.getTarget() == null;
        }

        @Override
        public boolean canContinueToUse() {
            if (Spectre.this.getVariant() == Type.JOUSTING_SPECTRE) return false;
            return Spectre.this.navigation.isInProgress() && Spectre.this.getTarget() == null;
        }

        @Override
        public void start() {
            Vec3 vec3d = this.getRandomLocation();
            if (vec3d != null) {
                Spectre.this.navigation.moveTo(Spectre.this.navigation.createPath(BlockPos.containing(vec3d), 1), Spectre.this.getVariant() == Type.HULKING_SPECTRE ? 0.2 : 0.5);
            }
        }

        @Nullable
        private Vec3 getRandomLocation() {
            Vec3 vec3d2 = Spectre.this.getViewVector(0.0F);
            Vec3 vec3d3 = HoverRandomPos.getPos(Spectre.this, 12, 5, vec3d2.x, vec3d2.z, 1.5707964F, 3, 1);
            return vec3d3 != null ? vec3d3 : AirAndWaterRandomPos.getPos(Spectre.this, 12, 2, -2, vec3d2.x, vec3d2.z, 1.5707963705062866);
        }
    }

    class SpectreRandomLookAroundGoalGoal extends RandomLookAroundGoal {

        SpectreRandomLookAroundGoalGoal() {
            super(Spectre.this);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Spectre.this.getTarget() == null;
        }
    }

    public static class DashTowardsTargetGoal extends Goal {
        protected final Spectre mob;
        private int timer;
        private boolean attacked;

        public DashTowardsTargetGoal(Spectre mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (mob.getTarget() == null || mob.isFromSword() && mob.getTarget().is(mob.getOwner()) || mob.getVariant() != Type.JOUSTING_SPECTRE) return false;
            return !(mob.getTarget() instanceof Player) || !mob.getTarget().isSpectator() && !((Player)mob.getTarget()).isCreative();
        }

        @Override
        public boolean canContinueToUse() {
            if (mob.getTarget() == null || timer <= 0 || mob.isFromSword() && mob.getTarget().is(mob.getOwner())) return false;
            return !(mob.getTarget() instanceof Player) || !mob.getTarget().isSpectator() && !((Player)mob.getTarget()).isCreative();
        }

        @Override
        public void start() {
            timer = 40;
            attacked = false;
            this.mob.playSound(SpeciesSoundEvents.SPECTRE_SPOT.get());
        }

        @Override
        public void tick() {
            if (timer > 0) timer--;


            if (mob.getTarget() != null) {
                if (timer > 20) this.mob.addDeltaMovement(new Vec3(0,0.01F,0));
                double d0 = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(mob.getTarget());
                checkAndPerformAttack(mob.getTarget(), d0);
                this.mob.getLookControl().setLookAt(mob.getTarget(), mob.getMaxHeadYRot(), mob.getMaxHeadXRot());
                this.mob.setYBodyRot(this.mob.getYHeadRot());
                this.mob.setYRot(this.mob.getYHeadRot());
            }

            if (timer == 30 && !attacked) {
                this.mob.setPose(SpeciesPose.DASH.get());
            }

            if (attacked && this.mob.dashAnimationState.isStarted()) this.mob.dashAnimationState.stop();

            if (timer == 20 && !attacked) {
                if (mob.getTarget() != null) {
                    mob.playSound(SpeciesSoundEvents.SPECTRE_DASH.get(), 1.5F, 1);
                    Vec3 direction = new Vec3(mob.getTarget().getX() - mob.getX(), mob.getTarget().getY() - mob.getY(), mob.getTarget().getZ() - mob.getZ()).normalize().scale(1.5);
                    mob.setDeltaMovement(new Vec3(direction.x, direction.y, direction.z));
                    mob.getNavigation().stop();
                }
            }
        }

        protected void checkAndPerformAttack(LivingEntity livingEntity, double distance) {
            double reach = this.getAttackReachSqr(livingEntity);
            if (distance <= reach && !attacked) {
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(livingEntity);
                if (livingEntity.getVehicle() != null) {
                    livingEntity.removeVehicle();
                    livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(new Vec3(0, (1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0)));
                }
                attacked = true;
            }

        }

        protected double getAttackReachSqr(LivingEntity target) {
            return this.mob.getBbWidth() * 3.0F * this.mob.getBbWidth() * 3.0F + target.getBbWidth();
        }

        @Override
        public void stop() {
            this.mob.setPose(Pose.STANDING);
        }
    }

    public class FollowOwnerGoal extends Goal {
        private final Spectre spectre;
        private LivingEntity owner;
        private final double speedModifier;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float stopDistance;
        private final float startDistance;

        public FollowOwnerGoal(Spectre spectre, double speed, float startDistance, float stopDistance) {
            this.spectre = spectre;
            this.speedModifier = speed;
            this.navigation = spectre.getNavigation();
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.spectre.getOwner();
            if (livingentity == null) return false;
            else if (livingentity.isSpectator()) return false;
            else if (this.unableToMove()) return false;
            else if (this.spectre.distanceToSqr(livingentity) < (double)(this.startDistance * this.startDistance)) return false;
            else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.unableToMove()) {
                return false;
            } else {
                return !(this.spectre.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
            }
        }

        private boolean unableToMove() {
            return this.spectre.isPassenger() || this.spectre.isLeashed();
        }

        public void start() {
            this.timeToRecalcPath = 0;
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
        }

        public void tick() {
            this.spectre.getLookControl().setLookAt(this.owner, 10.0F, (float)this.spectre.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                this.navigation.moveTo(this.owner, Spectre.this.getVariant() == Type.HULKING_SPECTRE ? this.speedModifier * 0.7 : this.speedModifier);
            }
        }
    }


    public class OwnerHurtTargetGoal extends TargetGoal {
        private final Spectre spectre;
        private LivingEntity ownerLastHurt;
        private int timestamp;

        public OwnerHurtTargetGoal() {
            super(Spectre.this, false);
            this.spectre = Spectre.this;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        public boolean canUse() {
            if (this.spectre.isFromSword() && this.spectre.getOwnerUUID() != null) {
                LivingEntity livingentity = this.spectre.getOwner();
                if (livingentity == null) return false;
                else {
                    this.ownerLastHurt = livingentity.getLastHurtMob();
                    int i = livingentity.getLastHurtMobTimestamp();
                    return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
                }
            } else return false;
        }

        public void start() {
            this.mob.setTarget(this.ownerLastHurt);
            LivingEntity livingentity = this.spectre.getOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getLastHurtMobTimestamp();
            }
            super.start();
        }
    }


    public class OwnerHurtByTargetGoal extends TargetGoal {
        private final Spectre spectre;
        private LivingEntity ownerLastHurtBy;
        private int timestamp;

        public OwnerHurtByTargetGoal() {
            super(Spectre.this, false);
            this.spectre = Spectre.this;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        public boolean canUse() {
            if (this.spectre.isFromSword() && this.spectre.getOwnerUUID() != null) {
                LivingEntity livingentity = this.spectre.getOwner();
                if (livingentity == null) {
                    return false;
                } else {
                    this.ownerLastHurtBy = livingentity.getLastHurtByMob();
                    int i = livingentity.getLastHurtByMobTimestamp();
                    return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
                }
            } else {
                return false;
            }
        }

        public void start() {
            this.mob.setTarget(this.ownerLastHurtBy);
            LivingEntity livingentity = this.spectre.getOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getLastHurtByMobTimestamp();
            }

            super.start();
        }
    }

    public enum Type implements StringRepresentable {
        SPECTRE("spectre"),
        JOUSTING_SPECTRE("jousting_spectre"),
        HULKING_SPECTRE("hulking_spectre");

        public static final StringRepresentable.EnumCodec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private final String name;

        Type(String name) {
            this.name = name;
        }

        public static Type byName(String name) {
            return CODEC.byName(name, SPECTRE);
        }

        public String getSerializedName() {
            return this.name;
        }
    }
}
