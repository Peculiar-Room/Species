package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.registry.*;
import com.ninni.species.server.criterion.SpeciesCriterion;
import com.ninni.species.server.entity.ai.goal.BewereagerAttackGoal;
import com.ninni.species.server.entity.ai.goal.CelebrateWithVillagersGoal;
import com.ninni.species.server.entity.util.SpeciesPose;
import com.ninni.species.mixin_util.WolfAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class Bewereager extends Monster implements OwnableEntity {
    public static final EntityDataAccessor<Integer> HOWL_COOLDOWN = SynchedEntityData.defineId(Bewereager.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ATTACK_SPEED = SynchedEntityData.defineId(Bewereager.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR = SynchedEntityData.defineId(Bewereager.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> FROM_WOLF = SynchedEntityData.defineId(Bewereager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SPLITTING_ID = SynchedEntityData.defineId(Bewereager.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(Bewereager.class, EntityDataSerializers.OPTIONAL_UUID);
    public final AnimationState howlAnimationState = new AnimationState();
    public final AnimationState biteAttackAnimationState = new AnimationState();
    public final AnimationState slashAttackAnimationState = new AnimationState();
    public final AnimationState shakeAnimationState = new AnimationState();
    public final AnimationState splitAnimationState = new AnimationState();
    public final AnimationState stunAnimationState = new AnimationState();
    public final AnimationState jumpAnimationState = new AnimationState();
    public int bewereagerSplittingTime;
    private int howlTime;
    private int stunTime;
    private int jumpTime;
    public int splitTime;
    public int shakeTime;
    public int attackCooldown;
    public boolean isWet;

    public Bewereager(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, (livingEntity) -> livingEntity.getType().is(SpeciesTags.ATTACKED_BY_BEWEREAGER) && !this.isSplitting()));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, !this.isSplitting()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, !this.isSplitting()));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new BewereagerAttackGoal(this));
        this.goalSelector.addGoal(1, new TransformDuringFullMoonGoal(this));
        this.goalSelector.addGoal(1, new CelebrateWithVillagersGoal(this, SpeciesSoundEvents.BEWEREAGER_CELEBRATE.get(), true));
        this.goalSelector.addGoal(1, new HowlGoal(this));
        this.goalSelector.addGoal(2, new JumpTowardsTargetGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
                .add(Attributes.MOVEMENT_SPEED, 0.23);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.isAlive() && this.isSplitting()) {
            int i = this.getSplittingProgress();
            this.bewereagerSplittingTime -= i;
            if (this.bewereagerSplittingTime == 0 && this.isSplitting() && this.getPose() != SpeciesPose.SPLITTING.get()) {
                this.playSound(SpeciesSoundEvents.BEWEREAGER_SPLIT.get(), 1, 1);
                this.setPose(SpeciesPose.SPLITTING.get());
            }
        }

        if (this.getHowlCooldown() > 0) this.setHowlCooldown(this.getHowlCooldown()-1);

        if (this.getTarget() == null && this.getAttackSpeed() != 0) this.setAttackSpeed(0);

        if (attackCooldown > 0) attackCooldown--;
        if (attackCooldown == 0 && (this.getPose() == SpeciesPose.SLASH_ATTACK.get() || this.getPose() == SpeciesPose.ATTACK.get())) this.setPose(Pose.STANDING);
        if (stunTime > 0) stunTime--;
        if (stunTime == 0 && this.getPose() == SpeciesPose.STUN.get()) this.setPose(Pose.STANDING);
        if (jumpTime > 0) jumpTime--;
        if (jumpTime == 0 && this.getPose() == Pose.LONG_JUMPING) this.setPose(Pose.STANDING);
        if (splitTime > 0) splitTime--;
        if (splitTime == 1) this.split();
        if (shakeTime > 0) {
            shakeTime--;
            this.level().addParticle(ParticleTypes.SPLASH, this.getRandomX(1.8D), this.getRandomY(), this.getRandomZ(1.8D), 0.0D, 0.0D, 0.0D);
        }
        if (shakeTime == 0 && this.getPose() == SpeciesPose.SHAKING.get()) this.setPose(Pose.STANDING);

        if (this.getAttackSpeed() > 0) {
            float amount = 1 - (6 - (float) this.getAttackSpeed()) / 6;
            if (this.level() instanceof ServerLevel serverLevel && this.random.nextFloat() < amount) {
                for (int i = 0; i < amount * 3; i++) {
                    serverLevel.sendParticles(
                            SpeciesParticles.BEWEREAGER_SPEED.get(),
                            this.getRandomX(0.35D),
                            this.getY(0.35D) + random.nextFloat(),
                            this.getRandomZ(0.35D),
                            1,
                            0.3, 0.3, 0.3,
                            1.0D
                    );
                }
            }
        }

        if (howlTime > 0) {
            howlTime--;
            if (howlTime < 60 && howlTime > 20) {
                final float angle = (0.0174532925F * this.yBodyRot);
                final double headX = 1F * getScale() * Mth.sin(Mth.PI + angle);
                final double headZ = 1F * getScale() * Mth.cos(angle);

                if (howlTime % 8 == 0) this.level().addParticle(SpeciesParticles.BEWEREAGER_HOWL.get(), this.getX() + headX, this.getY() + 2.25F, this.getZ() + headZ, 0.0D, 0.0D, 0.0D);
            }
        }
        if (howlTime == 0 && this.getPose() == Pose.ROARING) this.setPose(Pose.STANDING);
        if (howlTime == 1 && !this.hasEffect(MobEffects.DAMAGE_BOOST)) {
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 15));
            this.playSound(SpeciesSoundEvents.BEWEREAGER_HOWL_STRENGTH.get(), 4, 1);
            this.setHowlCooldown(20 * 30);
        }

        if (!this.isWet && this.isInWaterRainOrBubble()) this.isWet = true;

        if (this.level().isClientSide) {
            if (attackCooldown == 0 && (this.biteAttackAnimationState.isStarted() || this.slashAttackAnimationState.isStarted())) {
                this.biteAttackAnimationState.stop();
                this.slashAttackAnimationState.stop();
            }
            if (howlTime == 0 && this.howlAnimationState.isStarted())this.howlAnimationState.stop();
            if (stunTime == 0 && this.stunAnimationState.isStarted())this.stunAnimationState.stop();
            if (jumpTime == 0 && this.jumpAnimationState.isStarted())this.jumpAnimationState.stop();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.isWet && this.getPose() == Pose.STANDING && !this.isInWaterRainOrBubble() && !this.isPathFinding() && !this.level().isRaining() && this.onGround()) {
            this.setPose(SpeciesPose.SHAKING.get());
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        ListTag listtag = (new BannerPattern.Builder()).addPattern(BannerPatterns.RHOMBUS_MIDDLE, DyeColor.CYAN).addPattern(BannerPatterns.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY).addPattern(BannerPatterns.STRIPE_CENTER, DyeColor.GRAY).addPattern(BannerPatterns.BORDER, DyeColor.LIGHT_GRAY).addPattern(BannerPatterns.STRIPE_MIDDLE, DyeColor.BLACK).addPattern(BannerPatterns.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY).addPattern(BannerPatterns.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY).addPattern(BannerPatterns.BORDER, DyeColor.BLACK).toListTag();
        if (this.getFromWolf() && this.getOwner() != null && this.getOwner().is(player) && itemstack.is(Items.WHITE_BANNER) && BlockItem.getBlockEntityData(itemstack).contains("Patterns") && BlockItem.getBlockEntityData(itemstack).get("Patterns").equals(listtag)) {

            if (this.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) || this.hasEffect(SpeciesStatusEffects.STUCK.get())) {
                if (!player.getAbilities().instabuild) itemstack.shrink(1);
                if (!this.level().isClientSide) this.startSplitting(this.random.nextInt(401) + 400);
                this.playSound(SpeciesSoundEvents.BEWEREAGER_TRANSFORM_START.get());
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float v) {
        if (this.howlTime > 0) {
            this.howlTime = 0;
            this.setPose(Pose.STANDING);
            this.setHowlCooldown(20 * 30);
        }

        return super.hurt(damageSource, v);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (entity.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
            if (this.getAttackSpeed() == 6) {
                this.setPose(SpeciesPose.STUN.get());
                this.playSound(SpeciesSoundEvents.BEWEREAGER_STUN.get());

                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 20; i++) {
                        serverLevel.sendParticles(
                                SpeciesParticles.BEWEREAGER_SLOW.get(),
                                this.getX() + (random.nextGaussian() * 0.5),
                                this.getY(1F) + random.nextFloat(),
                                this.getZ() + (random.nextGaussian() * 0.5),
                                1,
                                0.3, 0.3, 0.3,
                                1.0D
                        );
                    }
                }
            }
            else this.setAttackSpeed(this.getAttackSpeed() + 1);
        }
        return super.doHurtTarget(entity);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HOWL_COOLDOWN, 0);
        this.entityData.define(ATTACK_SPEED, 0);
        this.entityData.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
        this.entityData.define(FROM_WOLF, false);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(DATA_SPLITTING_ID, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("HowlCooldown", this.getHowlCooldown());
        nbt.putInt("AttackSpeed", this.getAttackSpeed());
        nbt.putByte("CollarColor", (byte)this.getCollarColor().getId());
        nbt.putBoolean("FromWolf", this.getFromWolf());
        if (this.getOwnerUUID() != null) {
            nbt.putUUID("Owner", this.getOwnerUUID());
        }
        nbt.putInt("SplittingTime", this.isSplitting() ? this.bewereagerSplittingTime : -1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setHowlCooldown(nbt.getInt("HowlCooldown"));
        this.setAttackSpeed(nbt.getInt("AttackSpeed"));
        if (nbt.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(nbt.getInt("CollarColor")));
        }
        this.setFromWolf(nbt.getBoolean("FromWolf"));
        if (nbt.hasUUID("Owner")) {
            this.setOwnerUUID(nbt.getUUID("Owner"));
        }
        if (nbt.contains("SplittingTime", 99) && nbt.getInt("SplittingTime") > -1) {
            this.startSplitting(nbt.getInt("SplittingTime"));
        }
    }

    public int getAttackSpeed() {
        return this.entityData.get(ATTACK_SPEED);
    }
    public void setAttackSpeed(int speed) {
        this.entityData.set(ATTACK_SPEED, speed);
    }

    public int getHowlCooldown() {
        return this.entityData.get(HOWL_COOLDOWN);
    }
    public void setHowlCooldown(int cooldown) {
        this.entityData.set(HOWL_COOLDOWN, cooldown);
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
    }
    public void setCollarColor(DyeColor p_30398_) {
        this.entityData.set(DATA_COLLAR_COLOR, p_30398_.getId());
    }

    public boolean getFromWolf() {
        return this.entityData.get(FROM_WOLF);
    }
    public void setFromWolf(boolean fromWolf) {
        this.entityData.set(FROM_WOLF, fromWolf);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }
    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    public boolean isSplitting() {
        return this.getEntityData().get(DATA_SPLITTING_ID);
    }

    private void startSplitting(int i) {
        this.bewereagerSplittingTime = i;
        this.getEntityData().set(DATA_SPLITTING_ID, true);
        this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, i, 2));
    }

    private int getSplittingProgress() {
        int i = 1;
        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; ++k) {
                for(int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; ++l) {
                    for(int i1 = (int)this.getZ() - 4; i1 < (int)this.getZ() + 4 && j < 14; ++i1) {
                        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos.set(k, l, i1));
                        if (blockstate.is(Blocks.IRON_BARS) || blockstate.getBlock() instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3F) ++i;
                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.getPose() == Pose.ROARING || this.getPose() == SpeciesPose.STUN.get() || this.getPose() == SpeciesPose.SPLITTING.get();
    }

    protected int calculateFallDamage(float v, float v1) {
        return super.calculateFallDamage(v, v1) - 10;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == Pose.ROARING) {
                this.howlAnimationState.start(this.tickCount);
                this.howlTime = 70;
            }
            else if (this.getPose() == SpeciesPose.STUN.get()) {
                this.stunAnimationState.start(this.tickCount);
                this.setAttackSpeed(0);
                this.stunTime = 70;
            }
            else if (this.getPose() == SpeciesPose.ATTACK.get()) {
                this.attackCooldown = 5;
                this.biteAttackAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == SpeciesPose.SLASH_ATTACK.get()) {
                this.attackCooldown = 15;
                this.slashAttackAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == Pose.LONG_JUMPING) {
                this.jumpTime = 20;
                this.jumpAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == SpeciesPose.SPLITTING.get()) {
                this.splitTime = 45;
                this.splitAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == SpeciesPose.SHAKING.get()) {
                this.shakeTime = 25;
                this.playSound(SpeciesSoundEvents.BEWEREAGER_SHAKE.get(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.gameEvent(GameEvent.ENTITY_SHAKE);
                this.shakeAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == Pose.STANDING) {
                this.howlAnimationState.stop();
                this.stunAnimationState.stop();
                this.biteAttackAnimationState.stop();
                this.slashAttackAnimationState.stop();
                this.jumpAnimationState.stop();
                this.shakeAnimationState.stop();
                if (this.isWet) this.isWet = false;
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    public void transform() {
        if (this.getFromWolf()) {

            this.playSound(SpeciesSoundEvents.BEWEREAGER_TRANSFORM.get(), 1, 1);

            Wolf wolf = this.convertTo(EntityType.WOLF, false);
            wolf.setCollarColor(this.getCollarColor());
            if (this.getOwnerUUID() != null) {
                wolf.setOwnerUUID(this.getOwnerUUID());
                wolf.setTame(true);
                wolf.setOrderedToSit(true);
            }
            if (wolf instanceof WolfAccess wolfAccess) wolfAccess.setIsBewereager(true);
            this.addTransformingParticles();
            this.level().addFreshEntity(wolf);
        }

    }

    public void split() {
        if (this.getFromWolf()) {

            Villager villager = EntityType.VILLAGER.create(this.level());
            villager.setVillagerData(villager.getVillagerData().setType(SpeciesVillagerTypes.CURED_BEWEREAGER.get()).setProfession(VillagerProfession.NONE));
            if (this.level() instanceof ServerLevel serverLevel) {
                if (this.getOwnerUUID() != null) serverLevel.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, this.getOwner(), villager);
                villager.refreshBrain(serverLevel);
            }
            if (this.getOwner() instanceof ServerPlayer serverPlayer) SpeciesCriterion.CURE_BEWEREAGER.trigger(serverPlayer);
            if (this.hasCustomName()) villager.setCustomName(this.getCustomName());
            villager.teleportTo(this.getX(),this.getY(),this.getZ());
            villager.setXRot(this.getXRot());
            villager.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
            this.level().addFreshEntity(villager);

            Wolf wolf = this.convertTo(EntityType.WOLF, false);
            wolf.setCollarColor(this.getCollarColor());
            if (this.getOwnerUUID() != null) {
                wolf.setOwnerUUID(this.getOwnerUUID());
                wolf.setTame(true);
            }
            if (wolf instanceof WolfAccess wolfAccess) {
                wolfAccess.setIsBewereager(false);
                wolfAccess.setIsCuredBewereager(true);
            }
            this.addTransformingParticles();
            this.level().addFreshEntity(wolf);

            float v = 0.35F;
            Vec3 vec3 = new Vec3(v, v, v);
            Vec3 vec32 = new Vec3(-v, v, -v);
            villager.addDeltaMovement(vec3);
            wolf.addDeltaMovement(vec32);
        }

    }

    public void addTransformingParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY(0.6), this.getZ(),
                    1,
                    0.3, 0.3, 0.3,
                    1.0D
            );
        }
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return super.shouldDespawnInPeaceful() && !this.getFromWolf();
    }

    @Override
    public boolean removeWhenFarAway(double p_21542_) {
        return !this.getFromWolf();
    }

    protected void dropCustomDeathLoot(DamageSource p_34697_, int p_34698_, boolean p_34699_) {
        super.dropCustomDeathLoot(p_34697_, p_34698_, p_34699_);
        Entity entity = p_34697_.getEntity();
        if (entity instanceof Creeper creeper) {
            if (creeper.canDropMobsSkull()) {
                ItemStack itemstack = new ItemStack(SpeciesItems.BEWEREAGER_HEAD.get());
                creeper.increaseDroppedSkulls();
                this.spawnAtLocation(itemstack);
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SpeciesSoundEvents.BEWEREAGER_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SpeciesSoundEvents.BEWEREAGER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.BEWEREAGER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState state) {
        this.playSound(SpeciesSoundEvents.BEWEREAGER_STEP.get(), 0.15F, this.getVoicePitch());
    }

    public static class HowlGoal extends Goal {
        protected final Bewereager mob;

        public HowlGoal(Bewereager mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null && mob.getHowlCooldown() == 0 && !mob.hasEffect(MobEffects.DAMAGE_BOOST) && mob.getPose() == Pose.STANDING && mob.onGround() && !this.mob.isSplitting();
        }

        @Override
        public void start() {
            mob.setPose(Pose.ROARING);
            mob.playSound(SpeciesSoundEvents.BEWEREAGER_HOWL.get(), 4, 1);
        }
    }

    public static class JumpTowardsTargetGoal extends Goal {
        protected final Bewereager mob;

        public JumpTowardsTargetGoal(Bewereager mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null
                    && mob.distanceToSqr(mob.getTarget()) > 100
                    && mob.onGround()
                    && mob.getPose() == Pose.STANDING
                    && !this.mob.isSplitting()
                    && !this.mob.hasEffect(SpeciesStatusEffects.STUCK.get())
                    && !this.mob.hasEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }

        @Override
        public void start() {
            if (mob.getTarget() != null) {
                mob.setPose(Pose.LONG_JUMPING);
                mob.playSound(SpeciesSoundEvents.BEWEREAGER_JUMP.get(), 3, mob.getVoicePitch());
                Vec3 direction = new Vec3(mob.getTarget().getX() - mob.getX(), mob.getTarget().getY() - mob.getY(), mob.getTarget().getZ() - mob.getZ()).normalize().scale(2);
                mob.setDeltaMovement(new Vec3(direction.x, 0.5, direction.z));
                mob.getNavigation().stop();
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !mob.onGround() && !this.mob.isSplitting();
        }

        @Override
        public void stop() {
            mob.playSound(SpeciesSoundEvents.BEWEREAGER_LAND.get());
        }
    }

    public static class TransformDuringFullMoonGoal extends Goal {
        protected final Bewereager bewereager;

        public TransformDuringFullMoonGoal(Bewereager bewereager) {
            this.bewereager = bewereager;
        }

        @Override
        public boolean canUse() {
            if (!bewereager.getFromWolf() || this.bewereager.isSplitting() || this.bewereager.getPose() != Pose.STANDING) return false;
            return !bewereager.isInWater() && !bewereager.level().isNight() && bewereager.onGround() && !bewereager.hasEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }

        @Override
        public void start() {
            bewereager.transform();
        }

    }

    public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return pos.getY() > 60 && levelAccessor.getBrightness(LightLayer.SKY, pos) > 0 && levelAccessor.getLevel().isNight() && levelAccessor.getMoonBrightness() > 0.9F && levelAccessor.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(levelAccessor, pos, randomSource) && checkMobSpawnRules(entityType, levelAccessor, spawnType, pos, randomSource);
    }
}
