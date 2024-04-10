package com.ninni.species.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.ninni.species.data.CruncherPelletManager;
import com.ninni.species.entity.ai.CruncherAi;
import com.ninni.species.registry.*;
import com.ninni.species.world.CruncherBossEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

public class Cruncher extends Animal {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<CruncherState> CRUNCHER_STATE = SynchedEntityData.defineId(Cruncher.class, SpeciesEntityDataSerializers.CRUNCHER_STATE);
    private static final EntityDataAccessor<Integer> STUNNED_TICKS = SynchedEntityData.defineId(Cruncher.class, EntityDataSerializers.INT);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState roarAnimationState = new AnimationState();
    public final AnimationState spitAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState stunAnimationState = new AnimationState();
    private final ServerBossEvent bossEvent = new ServerBossEvent(Component.translatable("bar.species.cruncher" , this.getDisplayName().getString()), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_6);
    private final int maxHunger = 3;
    @Nullable
    private CruncherPelletManager.CruncherPelletData pelletData = null;
    private int hunger;
    private int idleAnimationTimeout = 0;
    private long day = -1L;

    public Cruncher(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 500.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.KNOCKBACK_RESISTANCE, 0.75).add(Attributes.ATTACK_DAMAGE, 10);
    }

    @Override
    protected Brain.Provider<Cruncher> brainProvider() {
        return Brain.provider(CruncherAi.MEMORY_TYPES, CruncherAi.SENSOR_TYPES);
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

        super.customServerAiStep();

        boolean flag = this.getHunger() <= 0;

        if (flag && !this.bossEvent.getPlayers().isEmpty()) {
            this.bossEvent.removeAllPlayers();
            this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }

        if (flag) return;

        this.bossEvent.setProgress((float) this.getHunger() / this.maxHunger);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CRUNCHER_STATE, CruncherState.IDLE);
        this.entityData.define(STUNNED_TICKS, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        if (this.hasCustomName()) {
            this.bossEvent.setName(Component.translatable("bar.species.cruncher" , this.getDisplayName().getString()));
        }

        this.bossEvent.setColor(this.getBarColor());

        if (compoundTag.contains("PelletData", 10)) {
            CruncherPelletManager.CruncherPelletData.CODEC.parse(
                    new Dynamic<>(NbtOps.INSTANCE, compoundTag.getCompound("PelletData"))
            ).resultOrPartial(LOGGER::error).ifPresent(this::setPelletData);
        }

        this.setHunger(compoundTag.getInt("Hunger"));
        this.setStunnedTicks(compoundTag.getInt("StunnedTicks"));
        this.setDay(compoundTag.getLong("Day"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);

        this.bossEvent.setName(Component.translatable("bar.species.cruncher" , this.getDisplayName().getString()));
        this.bossEvent.setColor(this.getBarColor());

        if (this.pelletData != null) {
            CruncherPelletManager.CruncherPelletData.CODEC
                    .encodeStart(NbtOps.INSTANCE, this.getPelletData())
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(tag -> compoundTag.put("PelletData", tag));
        }


        compoundTag.putInt("Hunger", this.getHunger());
        compoundTag.putInt("StunnedTicks", this.getStunnedTicks());
        compoundTag.putLong("Day", this.getDay());
    }

    public BossEvent.BossBarColor getBarColor() {
        return switch (this.getHunger()) {
            case 3 -> BossEvent.BossBarColor.BLUE;
            case 2 -> BossEvent.BossBarColor.PURPLE;
            case 1 -> BossEvent.BossBarColor.RED;
            default -> BossEvent.BossBarColor.BLUE;
        };
    }

    @Nullable
    public CruncherPelletManager.CruncherPelletData getPelletData() {
        return this.pelletData;
    }

    public void setPelletData(CruncherPelletManager.CruncherPelletData data) {
        this.pelletData = data;
    }

    public long getDay() {
        return this.day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public boolean canDisableShield() {
        return true;
    }

    public DamageSource crunch(LivingEntity livingEntity) {
        return this.damageSources().source(SpeciesDamageTypes.CRUNCH, livingEntity);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        if (this.getHunger() > 0) {
            this.bossEvent.addPlayer(serverPlayer);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.bossEvent.removePlayer(serverPlayer);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        InteractionResult interactionResult = super.mobInteract(player, interactionHand);
        for (CruncherPelletManager.CruncherPelletData data : CruncherPelletManager.DATA) {

            if (this.getPelletData() == data) continue;

            if (!this.level().isClientSide() && ItemStack.isSameItemSameTags(itemStack, data.item())) {
                this.setPelletData(data);

                if (!player.getAbilities().instabuild) itemStack.shrink(1);

                this.playSound(SoundEvents.GENERIC_EAT, 2.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        if (itemStack.is(SpeciesTags.CRUNCHER_EATS) && this.getStunnedTicks() > 0) {

            itemStack.shrink(1);

            this.setHunger(this.getHunger() - 1);
            this.bossEvent.setColor(this.getBarColor());

            if (this.getHunger() == 0) {
                this.bossEvent.setProgress(0.0f);
                this.bossEvent.setVisible(false);

                this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);

                this.level().broadcastEntityEvent(this, (byte)7);
            }

            this.transitionTo(CruncherState.IDLE);
            this.playSound(SoundEvents.GENERIC_EAT, 2.0F, 1.0F);
            this.setHealth(this.getMaxHealth());
            this.setStunnedTicks(0);
            return InteractionResult.SUCCESS;
        }
        return interactionResult;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.getState() == CruncherState.ROAR) {
            f = (f - 1.0f) / 2.0f;
        }
        return super.hurt(damageSource, f);
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float f) {
        boolean flag = this.getHealth() / this.getMaxHealth() <= 0.75;
        if (this.getHunger() > 0 && flag) {
            if (this.getState() != CruncherState.STUNNED) {
                //todo custom sound event
                this.playSound(SoundEvents.PLAYER_LEVELUP, 2.0F, 1.0F);
                this.transitionTo(CruncherState.STUNNED);
                this.setStunnedTicks(320);
            }
        }
        super.actuallyHurt(damageSource, f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        } else {
            if (this.getState() == CruncherState.STUNNED) {
                int ticks = this.getStunnedTicks();
                if (ticks > 0) {
                    this.setStunnedTicks(ticks - 1);
                    this.heal(1.0F);
                } else {
                    this.transitionTo(CruncherState.IDLE);
                }
            }
        }
    }

    public int getHunger() {
        return this.hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
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
                this.stunAnimationState.stop();
            }
            case ROAR -> {
                this.stunAnimationState.stop();
                this.attackAnimationState.stop();
                this.spitAnimationState.stop();
                this.roarAnimationState.startIfStopped(this.tickCount);
            }
            case SPIT -> {
                //TODO only plays the first time it spits
                this.stunAnimationState.stop();
                this.attackAnimationState.stop();
                this.roarAnimationState.stop();
                this.spitAnimationState.startIfStopped(this.tickCount);
            }
            case STOMP -> {
                this.stunAnimationState.stop();
                this.roarAnimationState.stop();
                this.spitAnimationState.stop();
                this.attackAnimationState.startIfStopped(this.tickCount);
            }
            case STUNNED -> this.stunAnimationState.startIfStopped(this.tickCount);
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
        if (this.getHunger() == 0) {
            return Optional.empty();
        }
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
            case IDLE -> this.setState(CruncherState.IDLE);
            case ROAR -> this.setState(CruncherState.ROAR);
            case SPIT -> this.setState(CruncherState.SPIT);
            case STOMP -> this.setState(CruncherState.STOMP);
            case STUNNED -> this.setState(CruncherState.STUNNED);
        }
    }

    public CruncherState getState() {
        return this.entityData.get(CRUNCHER_STATE);
    }

    public void setState(CruncherState cruncherState) {
        this.entityData.set(CRUNCHER_STATE, cruncherState);
    }

    public int getStunnedTicks() {
        return this.entityData.get(STUNNED_TICKS);
    }

    public void setStunnedTicks(int stunnedTicks) {
        this.entityData.set(STUNNED_TICKS, stunnedTicks);
    }

    public boolean cannotWalk() {
        CruncherState state = this.getState();
        boolean inState = state == CruncherState.ROAR || state == CruncherState.STOMP  || state == CruncherState.SPIT || state == CruncherState.STUNNED;
        return inState;
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

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setHunger(3);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public enum CruncherState implements StringRepresentable {
        IDLE("idle", SoundEvents.EMPTY, 0),
        ROAR("roar", SoundEvents.EMPTY, 80),
        SPIT("spit", SoundEvents.EMPTY, 30),
        STOMP("stomp", SoundEvents.EMPTY, 20),
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
