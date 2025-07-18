package com.ninni.species.server.entity.mob.update_2;

import com.google.common.annotations.VisibleForTesting;
import com.ninni.species.Species;
import com.ninni.species.client.screen.ScreenShakeEvent;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.server.criterion.SpeciesCriterion;
import com.ninni.species.server.entity.ai.goal.TreeperPlantGoal;
import com.ninni.species.server.entity.ai.goal.TreeperUprootGoal;
import com.ninni.species.server.entity.mob.update_3.Quake;
import com.ninni.species.server.entity.util.SpeciesPose;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesTags;
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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class Treeper extends AgeableMob {
    private static final EntityDataAccessor<Integer> SAPLING_COOLDOWN = SynchedEntityData.defineId(Treeper.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(Treeper.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Boolean> PLANTED = SynchedEntityData.defineId(Treeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BURNED = SynchedEntityData.defineId(Treeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_MOB = SynchedEntityData.defineId(Treeper.class, EntityDataSerializers.BOOLEAN);
    public final AnimationState shakingSuccessAnimationState = new AnimationState();
    public final AnimationState shakingFailAnimationState = new AnimationState();
    public final AnimationState plantingAnimationState = new AnimationState();
    public final AnimationState uprootingAnimationState = new AnimationState();
    private final TreeperCanopy[] subEntities;
    public final TreeperCanopy trunk1;
    public final TreeperCanopy trunk2;
    public final TreeperCanopy canopyBase, canopyBase2, canopyBase3, canopyBase4;
    public final TreeperCanopy canopy8x8, canopy8x82, canopy8x83, canopy8x84, canopy8x85;
    public final TreeperCanopy canopy6x6, canopy6x62, canopy6x63, canopy6x64;
    public final TreeperCanopy canopy4x4, canopy4x42, canopy4x43, canopy4x44;
    public final TreeperCanopy canopy2x2;

    public Treeper(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1);
        this.lookControl = new TreeperLookControl(this);

        this.trunk1 = new TreeperCanopy(this, "trunk1", 2F, 4, 0,0,0);
        this.trunk2 = new TreeperCanopy(this, "trunk2", 2F, 3F,0,4,0);

        this.canopyBase = new TreeperCanopy(this, "canopyBase", 6F, 1, 1,7,0);
        this.canopyBase2 = new TreeperCanopy(this, "canopyBase2", 6F, 1, -1,7,0);
        this.canopyBase3 = new TreeperCanopy(this, "canopyBase3", 6F, 1, 0,7,1);
        this.canopyBase4 = new TreeperCanopy(this, "canopyBase4", 6F, 1, 0,7,-1);

        this.canopy8x8 = new TreeperCanopy(this, "canopy8x8", 4F, 2, 2,8,0);
        this.canopy8x82 = new TreeperCanopy(this, "canopy8x82", 4F, 2, -2,8,0);
        this.canopy8x83 = new TreeperCanopy(this, "canopy8x83", 4F, 2, 0,8,2);
        this.canopy8x84 = new TreeperCanopy(this, "canopy8x84", 4F, 2, 0,8,-2);
        this.canopy8x85 = new TreeperCanopy(this, "canopy8x85", 6F, 2, 0,8,0);

        this.canopy6x6 = new TreeperCanopy(this, "canopy6x6", 4F, 1, 1,10,0);
        this.canopy6x62 = new TreeperCanopy(this, "canopy6x62", 4F, 1, -1,10,0);
        this.canopy6x63 = new TreeperCanopy(this, "canopy6x63", 4F, 1, 0,10,1);
        this.canopy6x64 = new TreeperCanopy(this, "canopy6x64", 4F, 1, 0,10,-1);

        this.canopy4x4 = new TreeperCanopy(this, "canopy4x4", 2F, 1, 1,11,0);
        this.canopy4x42 = new TreeperCanopy(this, "canopy4x42", 2F, 1, -1,11,0);
        this.canopy4x43 = new TreeperCanopy(this, "canopy4x43", 2F, 1, 0,11,1);
        this.canopy4x44 = new TreeperCanopy(this, "canopy4x44", 2F, 1, 0,11,-1);
        this.canopy2x2 = new TreeperCanopy(this, "canopy2x2", 2F, 1, 0,12,0);

        this.subEntities = new TreeperCanopy[]{
                this.trunk1, this.trunk2,
                this.canopyBase, this.canopyBase2, this.canopyBase3, this.canopyBase4,
                this.canopy8x8, this.canopy8x82, this.canopy8x83, this.canopy8x84, this.canopy8x85,
                this.canopy6x6, this.canopy6x62, this.canopy6x63, this.canopy6x64,
                this.canopy4x4, this.canopy4x42, this.canopy4x43, this.canopy4x44,
                this.canopy2x2
        };
        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1);
    }

    public void setId(int i1) {
        super.setId(i1);
        for(int i = 0; i < this.subEntities.length; ++i) {
            this.subEntities[i].setId(i1 + i + 1);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setHasMob(this.random.nextInt(10) == 0);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TreeperPlantGoal(this));
        this.goalSelector.addGoal(0, new TreeperUprootGoal(this));
        this.goalSelector.addGoal(1, new TreeperLookGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.15);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(SpeciesTags.BURNS_TREEPER) && !this.isBurned()) {
            SoundEvent soundEvent = itemStack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundSource(), 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.TREEPER_BURN.get(), this.getSoundSource(), 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
            for (int l = 0; l < 30; ++l) {
                this.level().addParticle(ParticleTypes.FLAME, this.getRandomX(2), this.getY() + Math.random(), this.getRandomZ(2), 0.0, 0.0, 0.0);
            }
            this.setBurned(true);
            if (player instanceof ServerPlayer serverPlayer) SpeciesCriterion.BURN_TREEPER_INTO_PLACE.trigger(serverPlayer);
            if (!this.isPlanted()) this.plant();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (itemStack.is(SpeciesTags.EXTINGUISHES_TREEPER) && this.isBurned()) {
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRE_EXTINGUISH, this.getSoundSource(), 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
            this.setBurned(false);
            if (itemStack.is(Items.WATER_BUCKET)) {
                ItemStack itemstack1 = ItemUtils.createFilledResult(itemStack, player, Items.BUCKET.getDefaultInstance());
                if (!player.getAbilities().instabuild) player.setItemInHand(hand, itemstack1);
            }
            for (int l = 0; l < 30; ++l) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(2), this.getY() + Math.random(), this.getRandomZ(2), 0.0, 0.0, 0.0);
            }
            if (this.isPlanted() && this.level().isNight() && this.onGround() && !this.isInWater()) this.uproot();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isPlanted() || this.isInPoseTransition() || this.tickCount < 20) Arrays.stream(this.subEntities).forEach(this::tickParts);

        if (this.getSaplingCooldown() > 0) this.setSaplingCooldown(this.getSaplingCooldown() - 1);

        if (this.level().isClientSide) {

            if (this.getPoseTime() < 0L != this.isPlanted()) {
                this.uprootingAnimationState.stop();
                if (this.isPlanted() && this.getPoseTime() < 80L && this.getPoseTime() >= 0L) {
                    this.plantingAnimationState.startIfStopped(this.tickCount);
                }
            } else {
                this.plantingAnimationState.stop();
                this.uprootingAnimationState.animateWhen(this.isInPoseTransition() && this.getPoseTime() >= 0L, this.tickCount);
            }
        }

        if (this.isPlanted()) {
            this.setYRot(Math.round(this.getYRot() / 90.0) * 90);
            this.setYBodyRot(Math.round(this.yBodyRot / 90.0) * 90);
            this.setYHeadRot(Math.round(this.yHeadRot / 90.0) * 90);
            this.setPos(Math.floor(position().x) + 0.99F, this.getY(), Math.floor(position().z) + 0.99F);
        }
    }

    private void tickParts(TreeperCanopy canopy) {
        canopy.moveTo(this.getX() + canopy.getxOffset(), this.getY() + canopy.getyOffset() + (this.isPlanted() ? 0 : 0.95F), this.getZ() + canopy.getzOffset());
    }

    public boolean isInPoseTransition() {
        long l = this.getPoseTime();
        return l < (long)(this.isPlanted() ? 80 : 152);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == SpeciesPose.PLANTING.get()) this.plantingAnimationState.start(this.tickCount);
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }


    public void plant() {
        if (this.isPlanted()) return;
        this.playSound(SpeciesSoundEvents.TREEPER_PLANT.get(), 2.0f, 1.0f);
        this.setPose(SpeciesPose.PLANTING.get());
        this.setPlanted(true);
        this.resetLastPoseChangeTick(-(this.level()).getGameTime());
    }
    public void uproot() {
        if (!this.isPlanted()) return;
        this.playSound(SpeciesSoundEvents.TREEPER_UPROOT.get(), 1.0f, 1.0f);
        this.setPose(Pose.STANDING);
        this.setPlanted(false);
        this.resetLastPoseChangeTick((this.level()).getGameTime());
        Species.PROXY.screenShake(new ScreenShakeEvent(this.position(), 80, 0.75F, 10, true));
    }

    public long getPoseTime() {
        return (this.level()).getGameTime() - Math.abs(this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SAPLING_COOLDOWN, 0);
        this.entityData.define(LAST_POSE_CHANGE_TICK, 0L);
        this.entityData.define(PLANTED, false);
        this.entityData.define(BURNED, false);
        this.entityData.define(HAS_MOB, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
        compoundTag.putInt("SaplingCooldown", this.getSaplingCooldown());
        compoundTag.putBoolean("Planted", this.isPlanted());
        compoundTag.putBoolean("Burned", this.isBurned());
        compoundTag.putBoolean("HasMob", this.hasMob());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setSaplingCooldown(compoundTag.getInt("SaplingCooldown"));
        this.setPlanted(compoundTag.getBoolean("Planted"));
        this.setBurned(compoundTag.getBoolean("Burned"));
        this.setHasMob(compoundTag.getBoolean("HasMob"));
        long l = compoundTag.getLong("LastPoseTick");
        if (l < 0L) this.setPose(SpeciesPose.PLANTING.get());
        this.resetLastPoseChangeTick(l);
    }

    @VisibleForTesting
    public void resetLastPoseChangeTick(long l) {
        this.entityData.set(LAST_POSE_CHANGE_TICK, l);
    }


    public Optional<ItemStack> getStackInHand(Player player) {
        return Arrays.stream(InteractionHand.values()).filter(hand -> player.getItemInHand(hand).getItem() instanceof AxeItem).map(player::getItemInHand).findFirst();
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            this.playSound(SpeciesSoundEvents.TREEPER_HURT.get(), 1.0f, 1.0f);
            if (this.getStackInHand(player).isPresent() && this.getStackInHand(player).get().getItem() instanceof AxeItem && !this.isPlanted()) {
                if (this.getSaplingCooldown() == 0) {
                    this.spawnAtLocation(SpeciesItems.ANCIENT_PINECONE.get(), 7);
                    if (this.random.nextInt(3) == 0) this.spawnAtLocation(SpeciesItems.ANCIENT_PINECONE.get(), 7);
                    if (this.random.nextInt(3) == 0) this.spawnAtLocation(SpeciesItems.ANCIENT_PINECONE.get(), 7);
                    if (this.random.nextInt(3) == 0) this.spawnAtLocation(SpeciesItems.ANCIENT_PINECONE.get(), 7);
                    this.setSaplingCooldown(this.random.nextIntBetweenInclusive(60 * 20 * 2, 60 * 20 * 7));
                    this.shakingSuccessAnimationState.start(this.tickCount);
                    this.playSound(SpeciesSoundEvents.TREEPER_SHAKE_SUCCESS.get(), 1.0f, 1.0f);
                } else {
                    this.playSound(SpeciesSoundEvents.TREEPER_SHAKE_FAIL.get(), 0.5f, 1.0f);
                }

                if (this.hasMob() && this.level() instanceof ServerLevel serverLevel) {
                    this.setHasMob(false);
                    BlockPos pos = new BlockPos(this.blockPosition().getX(), this.blockPosition().getY() + 7, this.blockPosition().getZ());

                    if (random.nextBoolean()) {
                        EntityType.FOX.spawn(serverLevel, pos, MobSpawnType.NATURAL);
                    } else {
                        EntityType.CHICKEN.spawn(serverLevel, pos, MobSpawnType.NATURAL);
                    }
                }
            }
        }


        this.shakingFailAnimationState.start(this.tickCount);
        if (this.level() instanceof ServerLevel serverLevel && source.getEntity() instanceof Player) {
            for (int i = 0; i < this.random.nextInt(15) + 5; i++) {
                serverLevel.sendParticles(SpeciesParticles.TREEPER_LEAF.get(), this.getRandomX(1.8), this.getY() + 7, this.getRandomZ(1.8), 1,0, 0, 0, 0);
            }
        }

        return (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_LIGHTNING) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.getEntity() instanceof Quake)  && super.hurt(source, amount);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return pose == SpeciesPose.PLANTING.get() ? entityDimensions.height * 0.5F : entityDimensions.height * 0.65F;
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (this.isPlanted()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
            movementInput = movementInput.multiply(0.0, 1.0, 0.0);
        }
        super.travel(movementInput);
    }

    @Override
    public int getAmbientSoundInterval() {
        return this.isPlanted() ? 320 : 160;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SpeciesSoundEvents.TREEPER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isPlanted() && !this.isBurned() ? SpeciesSoundEvents.TREEPER_IDLE_PLANTED.get() : SpeciesSoundEvents.TREEPER_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.TREEPER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SpeciesSoundEvents.TREEPER_STEP.get(), 1f, 1);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    public int getSaplingCooldown() {
        return this.entityData.get(SAPLING_COOLDOWN);
    }

    public void setSaplingCooldown(int saplingCooldown) {
        this.entityData.set(SAPLING_COOLDOWN, saplingCooldown);
    }

    public boolean isPlanted() {
        return this.entityData.get(PLANTED);
    }

    public void setPlanted(boolean planted) {
        this.entityData.set(PLANTED, planted);
    }

    public boolean isBurned() {
        return this.entityData.get(BURNED);
    }

    public void setBurned(boolean burned) {
        this.entityData.set(BURNED, burned);
    }

    public boolean hasMob() {
        return this.entityData.get(HAS_MOB);
    }

    public void setHasMob(boolean bl) {
        this.entityData.set(HAS_MOB, bl);
    }
    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Treeper> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.below()).is(SpeciesTags.TREEPER_SPAWNABLE_ON);
    }

    public boolean isMultipartEntity() {
        return true;
    }

    public PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    static class TreeperLookControl extends LookControl {
        protected final Treeper mob;
        TreeperLookControl(Treeper mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (!this.mob.isPlanted()) {
                super.tick();
            }
        }
    }

    public static class TreeperLookGoal extends LookAtPlayerGoal {
        protected final Treeper mob;

        public TreeperLookGoal(Treeper mob, Class<? extends LivingEntity> class_, float f) {
            super(mob, class_, f);
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (this.mob.isPlanted()) return false;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.mob.isPlanted()) return false;
            return super.canContinueToUse();
        }
    }

}