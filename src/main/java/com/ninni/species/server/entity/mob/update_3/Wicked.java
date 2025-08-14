package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.server.criterion.SpeciesCriterion;
import com.ninni.species.server.entity.util.SpeciesPose;
import com.ninni.species.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

import static net.minecraft.world.entity.EntitySelector.NO_CREATIVE_OR_SPECTATOR;

public class Wicked extends Monster implements RangedAttackMob {
    private boolean isHaunting = false;
    protected static final EntityDataAccessor<Optional<UUID>> HAUNTED_TARGET_UUID = SynchedEntityData.defineId(Wicked.class, EntityDataSerializers.OPTIONAL_UUID);
    @Nullable
    private Mob hauntedTarget;
    public static final EntityDataAccessor<Integer> MANA_AMOUNT = SynchedEntityData.defineId(Wicked.class, EntityDataSerializers.INT);
    public final AnimationState spotAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    private int spotTimer = 0;
    private int attackTimer = 0;
    private int spotCooldown = 0;
    private int fallCooldown = 0;

    public Wicked(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setInvisible(false);
        this.setMaxUpStep(1);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new HauntMobGoal(this));
        this.goalSelector.addGoal(1, new RunAwayGoal<>(this, LivingEntity.class, this::isSuitableForRunningAway, 4.0F, 1.25D, 1.5D));
        this.goalSelector.addGoal(2, new ShootGoal(this, 1.25D,30, 30, 30.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1D));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22F);
    }

    public void haunt(Mob target, boolean addEffects) {
        if (addEffects && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getRandomX(1), this.getRandomY(), this.getRandomZ(1), 20, this.getBbWidth() / 8, 0, this.getBbWidth() / 8, 0.2F);
            for (int i = 0; i < 20; i++) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getRandomX(2), this.getRandomY(), this.getRandomZ(2), 0, this.getBbWidth() / 8, 0, this.getBbWidth() / 8, 0.1F);
            }
        }
        this.isHaunting = true;
        this.hauntedTarget = target;
        this.setHauntedTargetUUID(target.getUUID());
        this.hauntedTarget.setTarget(this.getTarget());
        this.setInvisible(true);
        this.setPose(SpeciesPose.HAUNTING.get());
        if (addEffects) {
            this.playSound(SpeciesSoundEvents.WICKED_HAUNT.get(), 2, 1);
            this.addWickedEffects(target);
        }
    }

    public void stopHaunting() {
        spotCooldown += 10;
        fallCooldown += 20 * 3;
        if (hauntedTarget != null) {
            this.removeWickedEffect(hauntedTarget);
            this.setPos(hauntedTarget.getX(),hauntedTarget.getY() + 1,hauntedTarget.getZ());
            this.hauntedTarget.setTarget(null);
        }
        this.isHaunting = false;
        this.hauntedTarget = null;
        this.setHauntedTargetUUID(null);

        this.setInvisible(false);
        this.setPose(Pose.STANDING);
        this.playSound(SpeciesSoundEvents.WICKED_STOP_HAUNTING.get());
        this.setMana(Math.max(0, this.getMana() - 1));

        this.setDeltaMovement(new Vec3(this.random.nextFloat() * 0.25F, 1.0f, this.random.nextFloat() * 0.25F));
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(SpeciesParticles.POOF.get(), Wicked.this.position().x, Wicked.this.position().y + 0.01, Wicked.this.position().z, 1, 0, 0, 0, 0.5F);
        }
        List<ServerPlayer> list = level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(10D), player -> true);
        for (ServerPlayer serverPlayer : list) SpeciesCriterion.WICKED_STOP_HAUNTING.trigger(serverPlayer);
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return !this.isHaunting;
    }

    public void addWickedEffects(Mob target) {
        int i = this.random.nextInt(4);
        MobEffect effect;
        int amplifier = 0;
        switch (i) {
            default -> effect = SpeciesStatusEffects.COMBUSTION.get();
            case 1 -> effect = SpeciesStatusEffects.IRON_WILL.get();
            case 2 -> effect = SpeciesStatusEffects.TANKED.get();
            case 3 -> effect = SpeciesStatusEffects.SNATCHED.get();
        }
        if (target.hasEffect(effect)) amplifier = target.getEffect(effect).getAmplifier() + 1;
        if (this.level().getDifficulty() == Difficulty.HARD) amplifier += 1;
        target.addEffect(new MobEffectInstance(effect, -1, amplifier));
    }

    public void removeWickedEffect(Mob target) {
        target.removeEffect(SpeciesStatusEffects.COMBUSTION.get());
        target.removeEffect(SpeciesStatusEffects.IRON_WILL.get());
        target.removeEffect(SpeciesStatusEffects.TANKED.get());
        target.removeEffect(SpeciesStatusEffects.SNATCHED.get());
    }

    @Override
    public void tick() {
        super.tick();

        //Sync hauntedTarget with the getHauntedTargetUUID nbt
        if (this.getHauntedTargetUUID() != null && this.hauntedTarget == null){
            this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(20), mob -> this.isSuitableForHaunting(mob) && mob.getUUID().equals(this.getHauntedTargetUUID())).forEach(mob -> this.haunt(mob, false));
        }

        //Cooldowns
        if (attackTimer > 0) --attackTimer;
        if (attackTimer == 0 && this.getPose() == SpeciesPose.ATTACK.get()) this.setPose(Pose.STANDING);
        if (spotTimer > 0) --spotTimer;
        if (spotTimer == 0 && this.getPose() == SpeciesPose.SPOT.get()) this.setPose(Pose.STANDING);
        if (attackTimer == 0 && this.attackAnimationState.isStarted()) this.attackAnimationState.stop();
        if (spotTimer == 0 && this.spotAnimationState.isStarted()) this.spotAnimationState.stop();
        if (spotCooldown > 0) --spotCooldown;
        if (fallCooldown > 0) --fallCooldown;

        //Slow falling
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < 0.0D && fallCooldown == 0) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
        }

        //Particles
        if (this.level() instanceof ServerLevel serverLevel && this.tickCount % 2 == 0) {
            if (isHaunting && hauntedTarget != null && this.random.nextFloat() < (float)this.getMana()/10) {
                // Particles for the haunted mob
                serverLevel.sendParticles(
                        SpeciesParticles.WICKED_FLAME.get(),
                        hauntedTarget.getRandomX(hauntedTarget.getBbWidth()),
                        hauntedTarget.getRandomY(),
                        hauntedTarget.getRandomZ(hauntedTarget.getBbWidth()),
                        1, hauntedTarget.getBbWidth() / 8, 0.1, hauntedTarget.getBbWidth() / 8, 0.02
                );
            }
            if (this.getMana() > 0) {
                int particleAmount;
                // Particles on top of wicked/haunted mob
                switch (this.getMana()) {
                    case 2 -> particleAmount = 15;
                    case 3 -> particleAmount = 10;
                    case 4 -> particleAmount = 8;
                    case 5 -> particleAmount = 3;
                    default -> particleAmount = 30;
                }
                if (isHaunting && hauntedTarget != null) particleAmount = 2;
                if (this.tickCount % particleAmount == 0) {
                    if (isHaunting && hauntedTarget != null) {

                        this.spawnParticles(serverLevel, hauntedTarget);
                    } else {
                        this.spawnParticles(serverLevel, this);
                    }
                }
            }
        }

        //Make it stop haunting a mob
        if (this.isHaunting && (!hauntedTarget.isAlive() || !this.isSuitableForHaunting(hauntedTarget) || hauntedTarget == null)) {
            this.stopHaunting();
        }

        if (this.hauntedTarget != null && this.hauntedTarget instanceof Creeper creeper) {
            if (creeper.getSwellDir() == 1) {
                spotCooldown = 6 * 20;
                this.stopHaunting();
            }
        }
    }

    private void spawnParticles(ServerLevel serverLevel, Mob mob) {
        float heightAdjustment = mob.hasEffect(SpeciesStatusEffects.SNATCHED.get()) || mob.hasEffect(SpeciesStatusEffects.TANKED.get()) ? 0.5F : 0.0F;

        serverLevel.sendParticles(
                SpeciesParticles.WICKED_EMBER.get(),
                mob.getRandomX(0.2F),
                mob.getY() + mob.getBbHeight() + 0.35F + heightAdjustment,
                mob.getRandomZ(0.2F),
                1,
                mob.getBbWidth() / 8,
                0,
                mob.getBbWidth() / 8,
                0.5F
        );
        serverLevel.sendParticles(
                ParticleTypes.SMOKE,
                mob.getRandomX(0.2F),
                mob.getY() + mob.getBbHeight() + 0.35F + heightAdjustment,
                mob.getRandomZ(0.2F),
                1,
                0,
                0,
                0,
                0
        );
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float v) {
        double d0 = livingEntity.getX() - this.getX();
        double d1 = livingEntity.getY(livingEntity.getBbHeight() * 0.4F) - this.getY(this.getBbHeight() * 0.4F);
        double d2 = livingEntity.getZ() - this.getZ();

        WickedFireball fireball = new WickedFireball(this, d0, d1, d2, this.level(), 5);
        this.setPose(SpeciesPose.ATTACK.get());
        if (!this.isSilent()) this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.WICKED_SHOOT.get(), this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        fireball.setPos(fireball.getX(), this.getY(this.getBbHeight() * 0.5F), fireball.getZ());
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, fireball.getRandomX(0.6F), fireball.getRandomY(), fireball.getRandomZ(0.6F), 2, 0, 0, 0, 0);
        }

        this.level().addFreshEntity(fireball);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        //Refilling the flame
        if (itemstack.is(Items.FLINT_AND_STEEL) && this.getMana() < 5) {
            this.setMana(Math.min(5, this.getMana() + 1));
            itemstack.hurtAndBreak(1, player, (player1) -> player1.broadcastBreakEvent(hand));
            this.playSound(SoundEvents.FLINTANDSTEEL_USE);
            this.setPersistenceRequired();
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public boolean isSuitableForHaunting(LivingEntity livingEntity) {
        return !(livingEntity instanceof Wicked)
                && (((livingEntity instanceof Enemy) || (livingEntity instanceof NeutralMob))
                && !livingEntity.getType().is(SpeciesTags.CANT_BE_HAUNTED)
                && !livingEntity.hasCustomName()
                && livingEntity.getMobType() != MobType.WATER)
                || (livingEntity.getType().is(SpeciesTags.CAN_BE_HAUNTED_EXTRAS));
    }
    public boolean isSuitableForRunningAway(LivingEntity livingEntity) {
        if (this.getTarget() != null) return livingEntity.is(this.getTarget());
        return (livingEntity instanceof Creeper creeper && creeper.getSwellDir() == 1) || livingEntity.getMobType() == MobType.WATER;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == Pose.STANDING) {
                this.attackAnimationState.stop();
                this.spotAnimationState.stop();
                this.refreshDimensions();
            }
            else if (this.getPose() == SpeciesPose.ATTACK.get()) {
                this.attackTimer = 15;
                this.attackAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == SpeciesPose.SPOT.get()) {
                this.spotTimer = 20;
                this.spotAnimationState.start(this.tickCount);
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MANA_AMOUNT, 5);
        this.entityData.define(HAUNTED_TARGET_UUID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Mana", this.getMana());
        if (this.getHauntedTargetUUID() != null) {
            compoundTag.putUUID("HauntedTarget", this.getHauntedTargetUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setMana(compoundTag.getInt("Mana"));
        if (compoundTag.hasUUID("HauntedTarget")) {
            this.setHauntedTargetUUID(compoundTag.getUUID("HauntedTarget"));
        }
    }

    @Nullable
    public UUID getHauntedTargetUUID() {
        return this.entityData.get(HAUNTED_TARGET_UUID).orElse(null);
    }
    public void setHauntedTargetUUID(@Nullable UUID uuid) {
        this.entityData.set(HAUNTED_TARGET_UUID, Optional.ofNullable(uuid));
    }

    public int getMana() {
        return this.entityData.get(MANA_AMOUNT);
    }
    public void setMana(int cooldown) {
        this.entityData.set(MANA_AMOUNT, cooldown);
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return pose == SpeciesPose.HAUNTING.get() ? EntityDimensions.scalable(0.0F, 0.0F) : super.getDimensions(pose);
    }

    @Override
    public void setPose(Pose p_20125_) {
        super.setPose(p_20125_);
    }

    @Override
    protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (this.isHaunting) return false;
        return super.hurt(damageSource, amount);
    }

    @Override
    public boolean canBeSeenByAnyone() {
        if (this.isHaunting) return false;
        return super.canBeSeenByAnyone();
    }

    @Override
    public boolean isAttackable() {
        return !this.isHaunting;
    }

    @Override
    public boolean isFree(double p_20230_, double p_20231_, double p_20232_) {
        if (this.isHaunting) return true;
        return super.isFree(p_20230_, p_20231_, p_20232_);
    }

    @Override
    public boolean isPushable() {
        return !this.isHaunting && super.isPushable();
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean dampensVibrations() {
        return !this.isHaunting;
    }

    @Override
    public boolean startRiding(Entity p_20330_) {
        if (this.isHaunting) return false;
        return super.startRiding(p_20330_);
    }

    @Override
    public void travel(Vec3 p_250068_) {
        if (spotTimer > 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
            p_250068_ = p_250068_.multiply(0.0D, 1.0D, 0.0D);
        }
        super.travel(p_250068_);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isHaunting ? SpeciesSoundEvents.WICKED_IDLE_HAUNTING.get() : SpeciesSoundEvents.WICKED_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SpeciesSoundEvents.WICKED_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.WICKED_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
    }

    protected void dropCustomDeathLoot(DamageSource p_34697_, int p_34698_, boolean p_34699_) {
        super.dropCustomDeathLoot(p_34697_, p_34698_, p_34699_);
        Entity entity = p_34697_.getEntity();
        if (entity instanceof Creeper creeper) {
            if (creeper.canDropMobsSkull()) {
                ItemStack itemstack = new ItemStack(SpeciesItems.WICKED_CANDLE.get());
                creeper.increaseDroppedSkulls();
                this.spawnAtLocation(itemstack);
            }
        }
    }

    @Override
    public int getExperienceReward() {
        int expBasedOnMana;
        switch (this.getMana()) {
            case 0 -> expBasedOnMana = 5;
            case 1 -> expBasedOnMana = 4;
            case 2 -> expBasedOnMana = 3;
            case 3 -> expBasedOnMana = 2;
            case 4 -> expBasedOnMana = 1;
            default -> expBasedOnMana = 0;
        }
        return super.getExperienceReward() + expBasedOnMana;
    }

    class ShootGoal extends RangedAttackGoal {
        private final Wicked wicked;

        public ShootGoal(RangedAttackMob p_25773_, double p_25774_, int p_25775_, int p_25776_, float p_25777_) {
            super(p_25773_, p_25774_, p_25775_, p_25776_, p_25777_);
            this.wicked = Wicked.this;
        }

        @Override
        public boolean canUse() {
            if (wicked.isHaunting) return false;
            else {
                if (wicked.getMana() > 0) {
                    LivingEntity target = wicked.level().getNearestEntity(Mob.class, TargetingConditions.forNonCombat().range(20.0D).selector(wicked::isSuitableForHaunting), wicked, wicked.getX(), wicked.getY(), wicked.getZ(), wicked.getBoundingBox().inflate(10.0D));
                    return target == null && super.canUse();
                } else return super.canUse();
            }
        }
    }

    static class RunAwayGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final Wicked wicked;

        public RunAwayGoal(Wicked mob, Class aClass, Predicate<LivingEntity> predicate, float v, double v1, double v2) {
            super(mob, aClass, predicate, v, v1, v2, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
            wicked = mob;
        }

        @Override
        public boolean canUse() {

            if (wicked.isHaunting) return false;
            else {
                if (wicked.getMana() > 0) {
                    LivingEntity target = wicked.level().getNearestEntity(Mob.class, TargetingConditions.forNonCombat().range(20.0D).selector(wicked::isSuitableForHaunting), wicked, wicked.getX(), wicked.getY(), wicked.getZ(), wicked.getBoundingBox().inflate(10.0D));
                    return target == null && super.canUse();
                } else return super.canUse();
            }
        }
    }

    static class HauntMobGoal extends Goal {
        private final Wicked wicked;
        private Mob target;
        private boolean sound = false;

        public HauntMobGoal(Wicked wicked) {
            this.wicked = wicked;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.wicked.getTarget() == null || wicked.getMana() == 0 || !wicked.onGround() || wicked.spotCooldown > 0) return false;

            target = wicked.level().getNearestEntity(
                    Mob.class,
                    TargetingConditions.forNonCombat().range(30.0D).selector(wicked::isSuitableForHaunting),
                    wicked,
                    wicked.getX(), wicked.getY(), wicked.getZ(),
                    wicked.getBoundingBox().inflate(30.0D)
            );

            return target != wicked.getTarget() && target != null && !wicked.isHaunting && target != wicked;
        }

        @Override
        public void start() {
            if (target != wicked.getTarget() && target != null) {
                if (!sound && wicked.onGround() && wicked.spotCooldown == 0){
                    wicked.playSound(SpeciesSoundEvents.WICKED_SPOT.get());
                    wicked.setPose(SpeciesPose.SPOT.get());
                    sound = true;
                }
                wicked.getNavigation().moveTo(target, 2);
            }
        }

        @Override
        public void tick() {
            if (target != wicked.getTarget() && target != null) {
                double distanceToTarget = wicked.distanceTo(target);

                if (wicked.isHaunting) {
                    wicked.getNavigation().moveTo(target, 1);
                } else {
                    if (distanceToTarget <= 1.5) {
                        wicked.haunt(target, true);
                        sound = false;
                    }
                    else wicked.getNavigation().moveTo(target, 2);
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return target != wicked.getTarget() && wicked.isHaunting && wicked.hauntedTarget != null && wicked.hauntedTarget.isAlive();
        }
    }


    public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> p_219014_, ServerLevelAccessor p_219015_, MobSpawnType p_219016_, BlockPos p_219017_, RandomSource p_219018_) {
        return !p_219015_.getLevel().isRaining() && p_219015_.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(p_219015_, p_219017_, p_219018_) && checkMobSpawnRules(p_219014_, p_219015_, p_219016_, p_219017_, p_219018_);
    }
}
