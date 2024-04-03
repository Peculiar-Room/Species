package com.ninni.species.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.ninni.species.entity.ai.CruncherAi;
import com.ninni.species.registry.SpeciesEntityDataSerializers;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesMemoryModuleTypes;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSensorTypes;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Cruncher extends Animal {
    protected static final ImmutableList<SensorType<? extends Sensor<? super Cruncher>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SpeciesSensorTypes.CRUNCHER_ATTACK_ENTITY_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.IS_PANICKING, MemoryModuleType.AVOID_TARGET, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_ATTACKABLE, SpeciesMemoryModuleTypes.ROAR_CHARGING, SpeciesMemoryModuleTypes.STOMP_CHARGING);
    private static final EntityDataAccessor<CruncherState> CRUNCHER_STATE = SynchedEntityData.defineId(Cruncher.class, SpeciesEntityDataSerializers.CRUNCHER_STATE);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState roarAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    private int hunger = 3;
    private int idleAnimationTimeout = 0;
    private int stunnedTicks = 0;
    private final int maxHunger = 3;
    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    public Cruncher(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 300.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_DAMAGE, 10);
    }

    @Override
    protected Brain.Provider<Cruncher> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return CruncherAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public Brain<Cruncher> getBrain() {
        return (Brain<Cruncher>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("cruncherBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("cruncherActivityUpdate");
        CruncherAi.updateActivity(this);
        this.level().getProfiler().pop();

        if (this.hunger <= 0 && !this.bossEvent.getPlayers().isEmpty()) {
            this.bossEvent.removeAllPlayers();
        }

        this.bossEvent.setProgress((float) this.hunger / this.maxHunger);

        super.customServerAiStep();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CRUNCHER_STATE, CruncherState.IDLE);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
        this.hunger = compoundTag.getInt("Hunger");
        this.stunnedTicks = compoundTag.getInt("StunnedTicks");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.bossEvent.setName(this.getDisplayName());
        compoundTag.putInt("Hunger", this.hunger);
        compoundTag.putInt("StunnedTicks", this.stunnedTicks);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        this.bossEvent.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.bossEvent.removePlayer(serverPlayer);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.is(SpeciesItems.FROZEN_MEAT) && this.getState() == CruncherState.STUNNED) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            this.hunger--;
            if (this.hunger == 0) {
                this.bossEvent.setProgress(0.0f);
                this.bossEvent.setVisible(false);
            }
            this.setHealth(this.getMaxHealth());
            this.transitionTo(CruncherState.IDLE);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, interactionHand);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        this.handleStunnedEffect();
        return super.hurt(damageSource, f);
    }

    private void handleStunnedEffect() {
        if (this.getHealth() / this.getMaxHealth() <= 0.6) {
            this.transitionTo(CruncherState.STUNNED);
            this.stunnedTicks = 180;
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.getState() != CruncherState.IDLE) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0, 1, 0));
            vec3 = vec3.multiply(0, 1, 0);
        }
        super.travel(vec3);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        } else {
            if (this.getState() == CruncherState.STUNNED && this.stunnedTicks > 0) {
                this.stunnedTicks--;
                this.heal(1.0F);
                if (this.stunnedTicks == 0) {
                    this.transitionTo(CruncherState.IDLE);
                }
            }
        }
    }

    public int getHunger() {
        return this.hunger;
    }

    private void setupAnimationStates() {
        switch (this.getState()) {
            case IDLE -> {
                if (this.idleAnimationTimeout <= 0) {
                    this.idleAnimationTimeout = 80;
                    this.idleAnimationState.start(this.tickCount);
                } else {
                    --this.idleAnimationTimeout;
                }
                this.roarAnimationState.stop();
                this.attackAnimationState.stop();
            }
            case ROAR -> {
                this.attackAnimationState.stop();
                this.roarAnimationState.startIfStopped(this.tickCount);
            }
            case STOMP -> {
                this.roarAnimationState.stop();
                this.attackAnimationState.startIfStopped(this.tickCount);
            }
            case STUNNED -> {
            }
        }
    }

    public boolean canAttack() {
        CruncherState state = this.getState();
        return this.getHunger() > 0 && state == CruncherState.IDLE;
    }

    public boolean isTargetClose() {
        return this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get().distanceTo(this) <= 9;
    }

    public Optional<LivingEntity> getHurtBy() {
        return this.getBrain().getMemory(MemoryModuleType.HURT_BY).map(DamageSource::getEntity).filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SpeciesSoundEvents.CRUNCHER_IDLE;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.CRUNCHER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.CRUNCHER_DEATH;
    }

    public void transitionTo(CruncherState cruncherState) {
        switch (cruncherState) {
            case IDLE -> {
                this.setState(CruncherState.IDLE);
            }
            case ROAR -> {
                this.setState(CruncherState.ROAR);
            }
            case STOMP -> {
                this.setState(CruncherState.STOMP);
            }
            case STUNNED -> {
                this.setState(CruncherState.STUNNED);
            }
        }
    }

    public CruncherState getState() {
        return this.entityData.get(CRUNCHER_STATE);
    }

    public void setState(CruncherState cruncherState) {
        this.entityData.set(CRUNCHER_STATE, cruncherState);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Cruncher> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return false;
    }

    public enum CruncherState implements StringRepresentable {
        IDLE("idle", SoundEvents.EMPTY, 0),
        ROAR("roar", SoundEvents.EMPTY, 160),
        STOMP("stomp", SoundEvents.EMPTY, 60),
        STUNNED("stunned", SoundEvents.EMPTY, 0);

        private final String name;
        private final SoundEvent soundEvent;
        private final int duration;

        CruncherState(String name, SoundEvent soundEvents, int duration) {
            this.name = name;
            this.soundEvent = soundEvents;
            this.duration = duration;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public SoundEvent getSoundEvent() {
            return this.soundEvent;
        }

        public int getDuration() {
            return this.duration;
        }
    }
}
