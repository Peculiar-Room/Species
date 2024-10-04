package com.ninni.species.entity;

import com.ninni.species.init.SpeciesBlocks;
import com.ninni.species.criterion.SpeciesCriterion;
import com.ninni.species.entity.ai.goal.WraptorSwoopAtTargetGoal;
import com.ninni.species.init.SpeciesSoundEvents;
import com.ninni.species.init.SpeciesTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


public class WraptorEntity extends Animal implements Enemy, Shearable {
    public static final EntityDataAccessor<Integer> FEATHER_STAGE = SynchedEntityData.defineId(WraptorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_BORN_FROM_EGG = SynchedEntityData.defineId(WraptorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(WraptorEntity.class, EntityDataSerializers.BOOLEAN);
    public static final String FEATHER_STAGE_KEY = "FeatherStage";
    public static final String TIME_SINCE_SHEARED_KEY = "TimeSinceSheared";
    private long timeSinceSheared;

    public WraptorEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 3;
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
    }

    public static AttributeSupplier.Builder createWraptorAttributes() {
        return createMobAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.2)
            .add(Attributes.MAX_HEALTH, 18.0D)
            .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random spawn bonus", serverLevelAccessor.getRandom().triangle(0.0, 0.11485000000000001), AttributeModifier.Operation.MULTIPLY_BASE));
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FEATHER_STAGE, 4);
        this.entityData.define(IS_BORN_FROM_EGG, false);
        this.entityData.define(HAS_EGG, false);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, WitherSkeleton.class, false));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WraptorSwoopAtTargetGoal(this, 0.5F));
        this.goalSelector.addGoal(2, new WraptorEntity.AttackGoal(1.2, false));
        this.goalSelector.addGoal(1, new WraptorEntity.MateGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new WraptorEntity.LayGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, Ingredient.of(SpeciesTags.WRAPTOR_BREED_ITEMS), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new HurtByTargetGoal(this));
    }

    public int getFeatherStage() {
        return this.entityData.get(FEATHER_STAGE);
    }
    public void setFeatherStage(int stage) {
        this.entityData.set(FEATHER_STAGE, stage);
    }

    @Override
    public boolean readyForShearing() {
        return this.getFeatherStage() > 0;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
        return mobEffectInstance.getEffect() != MobEffects.WITHER && super.canBeAffected(mobEffectInstance);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        long time = this.level().getGameTime();
        int stage = this.getFeatherStage();
        if (this.level().dimensionType().piglinSafe()) {
            if (stage < 4) {
                if (this.random.nextInt((int) (time - this.timeSinceSheared)) > 20 * 150) {
                    this.timeSinceSheared = time;
                    this.setFeatherStage(stage + 1);
                }
            }
        } else {
            if (this.readyForShearing() && random.nextInt(20 * 15) == 0) {
                this.setFeatherStage(stage - 1);
                this.playSound(SpeciesSoundEvents.ENTITY_WRAPTOR_FEATHER_LOSS.get(), 1.0f, 1.0f);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.isAlive() && this.age % 80 == 0 && this.isBaby() && this.getHealth() < this.getMaxHealth()) this.heal(1f);

        Vec3 vec3d = this.getDeltaMovement();
        if (!this.onGround() && vec3d.y < 0.0) {
            if (this.getFeatherStage() == 0 || this.isBaby()) this.setDeltaMovement(vec3d.multiply(1.0, 0.6, 1.0));
        }
    }

    @Override
    public boolean causeFallDamage(float f, float g, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return !this.isPersistenceRequired();
    }

    @Override
    public boolean shouldDropExperience() {
        return true;
    }

    @Override
    public int getExperienceReward() {
        return this.xpReward;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SHEARS) && !this.isBaby()) {
            if (!this.level().isClientSide() && this.readyForShearing()) {
                this.shear(SoundSource.PLAYERS);
                this.gameEvent(GameEvent.SHEAR, player);
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                if (this.getFeatherStage() == 0) {
                    if (player instanceof ServerPlayer serverPlayer) SpeciesCriterion.SHEAR_WRAPTOR_COMPLETELY.trigger(serverPlayer);
                    if (!player.isCreative() && !this.isBormFromEgg()) {
                        if (!this.isSilent())
                            this.level().playSound(null, this, SpeciesSoundEvents.ENTITY_WRAPTOR_AGGRO.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
                        this.setTarget(player);
                    }
                }
                this.setPersistenceRequired();
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        InteractionResult result = super.mobInteract(player, hand);
        if (result.consumesAction()) this.setPersistenceRequired();
        return result;
    }

    @Override
    public void shear(SoundSource category) {
        int stage = this.getFeatherStage();
        if (stage == 4) this.timeSinceSheared = this.level().getGameTime();
        this.level().playSound(null, this, SpeciesSoundEvents.ENTITY_WRAPTOR_SHEAR.get(), category, 1.0f, 1.0f);
        this.setFeatherStage(stage - 1);
        for (int i = 0, l = 2 + this.random.nextInt(5); i < l; i++) {
            ItemEntity itemEntity = this.spawnAtLocation(Items.FEATHER, 1);
            if (itemEntity == null) continue;
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1f, this.random.nextFloat() * 0.05f, (this.random.nextFloat() - this.random.nextFloat()) * 0.1f));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt(FEATHER_STAGE_KEY, this.getFeatherStage());
        nbt.putLong(TIME_SINCE_SHEARED_KEY, this.timeSinceSheared);
        nbt.putBoolean("IsBornFromEgg", this.isBormFromEgg());
        nbt.putBoolean("HasEgg", this.hasEgg());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setFeatherStage(nbt.getInt(FEATHER_STAGE_KEY));
        this.timeSinceSheared = nbt.getLong(TIME_SINCE_SHEARED_KEY);
        this.setBormFromEgg(nbt.getBoolean("IsBornFromEgg"));
        this.setHasEgg(nbt.getBoolean("HasEgg"));
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.95F;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.getFeatherStage() <= 2 || !this.level().dimensionType().piglinSafe()) return SpeciesSoundEvents.ENTITY_WRAPTOR_AGITATED.get();
        else return SpeciesSoundEvents.ENTITY_WRAPTOR_IDLE.get();
    }
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.ENTITY_WRAPTOR_HURT.get();
    }
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.ENTITY_WRAPTOR_DEATH.get();
    }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SpeciesSoundEvents.ENTITY_WRAPTOR_STEP.get(), 0.15f, 1.0f);
    }

    public boolean isBormFromEgg() {
        return this.entityData.get(IS_BORN_FROM_EGG);
    }

    public void setBormFromEgg(boolean bormFromEgg) {
        this.entityData.set(IS_BORN_FROM_EGG, bormFromEgg);
    }

    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    void setHasEgg(boolean hasEgg) {
        this.entityData.set(HAS_EGG, hasEgg);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType <WraptorEntity> entity, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random){
        return !world.getBlockState(pos.below()).is(Blocks.WARPED_WART_BLOCK);
    }
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(SpeciesTags.WRAPTOR_BREED_ITEMS);
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return this.isBormFromEgg();
    }

    static class MateGoal extends BreedGoal {
        private final WraptorEntity wraptor;

        MateGoal(WraptorEntity wraptor, double speed) {
            super(wraptor, speed);
            this.wraptor = wraptor;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.wraptor.hasEgg();
        }

        @Override
        public void stop() {
            super.stop();
            this.level.broadcastEntityEvent(wraptor, (byte) 18);
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, wraptor.getX(), wraptor.getY(), wraptor.getZ(), wraptor.getRandom().nextInt(7) + 1));
            }
        }

        @Override
        protected void breed() {
            if (this.partner != null) {
                ServerPlayer serverPlayerEntity = wraptor.getLoveCause();
                if (serverPlayerEntity == null && this.animal.getLoveCause() != null) {
                    serverPlayerEntity = this.animal.getLoveCause();
                }
                if (serverPlayerEntity != null) {
                    serverPlayerEntity.awardStat(Stats.ANIMALS_BRED);
                    CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayerEntity, wraptor, this.animal, null);
                }
                wraptor.setAge(6000);
                this.animal.setAge(6000);
                wraptor.resetLove();
                this.animal.resetLove();
                wraptor.setHasEgg(true);

            }
        }
    }

    static class LayGoal extends MoveToBlockGoal {
        private final WraptorEntity wraptor;

        LayGoal(WraptorEntity wraptor, double speed) {
            super(wraptor, speed, 16);
            this.wraptor = wraptor;
        }

        @Override
        public boolean canUse() {
            return this.wraptor.hasEgg() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.wraptor.hasEgg();
        }

        @Override
        public void stop() {
            super.stop();
            Level world = this.wraptor.level();
            world.playSound(null, this.wraptor.blockPosition(), SpeciesSoundEvents.ENTITY_WRAPTOR_EGG.get(), SoundSource.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
        }

        @Override
        public void tick() {
            super.tick();
            if (!this.wraptor.isInWater() && this.isReachedTarget() && this.wraptor.level().dimensionType().piglinSafe()) {
                this.wraptor.level().setBlock(this.blockPos.above(), SpeciesBlocks.WRAPTOR_EGG.get().defaultBlockState(), 3);
                this.wraptor.setHasEgg(false);
                this.wraptor.setInLoveTime(600);
            }
        }

        @Override
        protected boolean isValidTarget(LevelReader world, BlockPos pos) {
            return world.isEmptyBlock(pos.above()) && world.getBlockState(pos).is(SpeciesTags.WRAPTOR_NESTING_BLOCKS);
        }
    }

    private class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(double speed, boolean pauseWhenIdle) {
            super(WraptorEntity.this, speed, pauseWhenIdle);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
            double d = this.getAttackReachSqr(target);
            if (squaredDistance <= d && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(target);
                WraptorEntity.this.playSound(SpeciesSoundEvents.ENTITY_WRAPTOR_ATTACK.get(), 1.0F, 1.0F);
            }
        }

    }
}
