package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.Species;
import com.ninni.species.client.screen.ScreenShakeEvent;
import com.ninni.species.registry.*;
import com.ninni.species.server.criterion.SpeciesCriterion;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.world.entity.EntitySelector.NO_CREATIVE_OR_SPECTATOR;

public class Quake extends Monster {
    public static final EntityDataAccessor<Float> STORED_DAMAGE = SynchedEntityData.defineId(Quake.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> ATTACK_TIMER = SynchedEntityData.defineId(Quake.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RECHARGE_TIMER = SynchedEntityData.defineId(Quake.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ATTACK_COOLDOWN = SynchedEntityData.defineId(Quake.class, EntityDataSerializers.INT);
    public final AnimationState attackAnimationState = new AnimationState();

    public Quake(EntityType<? extends Monster> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
        this.setMaxUpStep(1);
        this.xpReward = 15;
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1, false));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.FOLLOW_RANGE, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15);
    }


    @Override
    public void tick() {
        super.tick();

        //Particles when core is in function
        if ((this.getAttackTimer() > 0) || (this.getRechargeTimer() > 0)) {
            this.level().addParticle(DustParticleOptions.REDSTONE, this.getRandomX(0.8D), this.getRandomY(), this.getRandomZ(0.8D), 0.0D, 0.0D, 0.0D);
        }

        //Tick cooldowns
        if (this.getAttackTimer() > 0) this.setAttackTimer(this.getAttackTimer()-1);
        if (this.getRechargeTimer() > 0) this.setRechargeTimer(this.getRechargeTimer()-1);
        if (this.getAttackCooldown() > 0) this.setAttackCooldown(this.getAttackCooldown()-1);

        //Release stored damage
        if (this.getAttackTimer() == 10) {
            damage(this.getStoredDamage());
        }

        //Reset poses and set timers accordingly
        if (this.getAttackTimer() == 1) this.setRechargeTimer(121);
        if (this.getRechargeTimer() == 1) this.setAttackCooldown(201);
        if (this.getAttackTimer() == 0 && this.getPose() == SpeciesPose.ATTACK.get()) {
            this.setPose(SpeciesPose.RECHARGE.get());
            this.setRechargeTimer(120);
        }
        if (this.getAttackTimer() == 0 && this.getRechargeTimer() == 0 && (this.getPose() == SpeciesPose.ATTACK.get() || this.getPose() == SpeciesPose.RECHARGE.get())) {
            this.setPose(Pose.STANDING);
        }

        //Animations and sounds
        if ((this.level()).isClientSide()) {
            if (this.getAttackTimer() == 0 && this.getRechargeTimer() == 0 && this.attackAnimationState.isStarted()) this.attackAnimationState.stop();
        }
        if (this.getAttackCooldown() == 20) this.playSound(SpeciesSoundEvents.QUAKE_RECHARGE.get(), 1, 1);
        if (this.getAttackTimer() == 20) this.playSound(SpeciesSoundEvents.QUAKE_ATTACK.get(), 3, 1);
        if (this.getRechargeTimer() == 90) this.playSound(SpeciesSoundEvents.QUAKE_UNSCREWS.get(), 2, 1);
        if (this.getRechargeTimer() == 40) this.playSound(SpeciesSoundEvents.QUAKE_FINISHES_UNSCREWING.get(), 1, 1);
    }

    private void damage(float amount) {
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(11D), NO_CREATIVE_OR_SPECTATOR);
        List<ServerPlayer> advancementList = level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(25D), player -> true);
        Set<Mob> killedMobs = new HashSet<>();

        //Particle
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(SpeciesParticles.KINETIC_ENERGY.get(), this.position().x, this.position().y + 0.01, this.position().z, 1, 0, 0, 0, 0.5F);
        }
        this.addDeltaMovement(new Vec3(0,0.35,0));

        for (LivingEntity target : list) {
            if (target != this) {
                //Handle knockback
                Vec3 enemyPos = target.position().subtract(this.position());
                Vec3 normalizedDirection = enemyPos.normalize();

                double knockbackXZ = 2.5 * (1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                double knockbackY = 0.5 * (1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

                target.push(normalizedDirection.x() * knockbackXZ, normalizedDirection.y() * knockbackY, normalizedDirection.z() * knockbackXZ);

                //Scale damage
                double distanceFromEnemy = target.position().distanceTo(this.position());
                float scalingFactor;

                if (distanceFromEnemy <= 4) scalingFactor = 1F;
                else if (distanceFromEnemy <= 7) scalingFactor = 0.75F;
                else if (distanceFromEnemy <= 10) scalingFactor = 0.5F;
                else scalingFactor = 0.25F;

                amount *= scalingFactor;

                //Damage
                if (target.getUUID().equals(UUID.fromString("3e11a940-8aff-449c-80d1-fc0f427f4876")) || target.getUUID().equals(UUID.fromString("053de6af-392b-4385-90c5-34395b7e8757"))) {
                    target.hurt(this.damageSources().source(DamageTypes.BAD_RESPAWN_POINT, this), 999999999);
                } else {
                   if (target instanceof Creeper creeper && creeper.isPowered() && amount > creeper.getHealth()) creeper.spawnAtLocation(SpeciesItems.MUSIC_DISK_SPAWNER.get());
                   target.hurt(this.kinetic(this), amount);
                }

                this.doHurtTarget(target);

                if (!target.isAlive() && target instanceof Mob mob) killedMobs.add(mob);
            }
        }


        if (!advancementList.isEmpty()) {
            for (ServerPlayer serverPlayer : advancementList) {
                if (killedMobs.size() >= 10) SpeciesCriterion.KILL_TEN_MOBS_WITH_QUAKE.trigger(serverPlayer);

                HashSet<EntityType<?>> killedMobTypes = killedMobs.stream().map(Mob::getType).collect(Collectors.toCollection(HashSet::new));
                Set<EntityType<?>> requiredMobTypes = BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> type.is(SpeciesTags.PREHISTORIC)).collect(Collectors.toSet());

                System.out.println(requiredMobTypes);

                if (killedMobTypes.containsAll(requiredMobTypes)) SpeciesCriterion.KILL_ALL_PREHISTORIC_MOBS_WITH_QUAKE.trigger(serverPlayer);
            }
        }


        Species.PROXY.screenShake(new ScreenShakeEvent(this.position(), Math.min(80, (int)this.getStoredDamage()), 0.75F, 40, true));

        this.setStoredDamage(0);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || damageSource.getEntity() == null) return super.hurt(damageSource, amount);

        //Shield or initiate attack
        if (this.getPose() != SpeciesPose.ATTACK.get() && this.getPose() != SpeciesPose.RECHARGE.get()) {
            if (this.getAttackCooldown() == 0) {
                this.setPose(SpeciesPose.ATTACK.get());
                this.addStoredDamage(amount);
                return false;
            }
            else {
                this.playSound(SpeciesSoundEvents.QUAKE_SHIELD.get(), 1,1);
                return true;
            }
        }

        //Absorb stored damage
        if (this.getPose() == SpeciesPose.ATTACK.get() && this.getAttackTimer() > 0) {
            this.addStoredDamage(amount);
            this.playSound(SpeciesSoundEvents.QUAKE_ABSORB.get(), 4,this.getStoredDamage() * 0.015F);
            return true;
        }

        return super.hurt(damageSource, amount);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == Pose.STANDING) {
                this.attackAnimationState.stop();
            }
            else if (this.getPose() == SpeciesPose.ATTACK.get()) {
                this.setStoredDamage(0);
                this.setAttackTimer(190);
                this.playSound(SpeciesSoundEvents.QUAKE_CHARGE.get(), 3, 1);
                this.attackAnimationState.start(this.tickCount);
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STORED_DAMAGE, 0F);
        this.entityData.define(ATTACK_COOLDOWN, 0);
        this.entityData.define(RECHARGE_TIMER, 0);
        this.entityData.define(ATTACK_TIMER, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putFloat("StoredDamage", this.getStoredDamage());
        compoundTag.putInt("AttackCooldown", this.getAttackCooldown());
        compoundTag.putInt("RechargeTimer", this.getRechargeTimer());
        compoundTag.putInt("AttackTimer", this.getAttackTimer());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setStoredDamage(compoundTag.getFloat("StoredDamage"));
        this.setAttackCooldown(compoundTag.getInt("AttackCooldown"));
        this.setRechargeTimer(compoundTag.getInt("RechargeTimer"));
        this.setAttackTimer(compoundTag.getInt("AttackTimer"));
    }

    public float getStoredDamage() {
        return this.entityData.get(STORED_DAMAGE);
    }
    public void setStoredDamage(float amount) {
        this.entityData.set(STORED_DAMAGE, amount);
    }
    public void addStoredDamage(float amount) {
        this.setStoredDamage(this.getStoredDamage() + amount);
    }

    public int getAttackCooldown() {
        return this.entityData.get(ATTACK_COOLDOWN);
    }
    public void setAttackCooldown(int cooldown) {
        this.entityData.set(ATTACK_COOLDOWN, cooldown);
    }

    public int getRechargeTimer() {
        return this.entityData.get(RECHARGE_TIMER);
    }
    public void setRechargeTimer(int timer) {
        this.entityData.set(RECHARGE_TIMER, timer);
    }

    public int getAttackTimer() {
        return this.entityData.get(ATTACK_TIMER);
    }
    public void setAttackTimer(int timer) {
        this.entityData.set(ATTACK_TIMER, timer);
    }


    public DamageSource kinetic(LivingEntity livingEntity) {
        return this.damageSources().source(SpeciesDamageTypes.KINETIC, livingEntity);
    }

    public float getWalkTargetValue(BlockPos p_33013_, LevelReader p_33014_) {
        return 0.0F;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.QUAKE_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return SpeciesSoundEvents.QUAKE_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(SpeciesSoundEvents.QUAKE_STEP.get(), 1F, this.getVoicePitch());
    }

    protected void dropCustomDeathLoot(DamageSource p_34697_, int p_34698_, boolean p_34699_) {
        super.dropCustomDeathLoot(p_34697_, p_34698_, p_34699_);
        Entity entity = p_34697_.getEntity();
        if (entity instanceof Creeper creeper) {
            if (creeper.canDropMobsSkull()) {
                ItemStack itemstack = new ItemStack(SpeciesItems.QUAKE_HEAD.get());
                creeper.increaseDroppedSkulls();
                this.spawnAtLocation(itemstack);
            }
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.getAttackTimer() > 0 || this.getRechargeTimer() > 0;
    }
}
