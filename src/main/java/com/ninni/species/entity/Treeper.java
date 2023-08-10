package com.ninni.species.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class Treeper extends AgeableMob {
    private static final EntityDataAccessor<Integer> SAPLING_COOLDOWN = SynchedEntityData.defineId(Treeper.class, EntityDataSerializers.INT);
    public final AnimationState shakingSuccessAnimationState = new AnimationState();
    public final AnimationState shakingFailAnimationState = new AnimationState();
    public final AnimationState plantingAnimationState = new AnimationState();

    public Treeper(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1));
    }


    public static AttributeSupplier.Builder createTreeperAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.15);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSaplingCooldown() > 0) this.setSaplingCooldown(this.getSaplingCooldown() - 1);

        if (this.getPose() != Pose.ROARING && this.getPose() != Pose.SNIFFING) {
            if (this.level().getDayTime() > 1000 && this.level().getDayTime() < 11000) {
                this.setPose(Pose.DIGGING);
            } else this.setPose(Pose.STANDING);
        }
//I AM USING POSES WHICH IS SHIT BECAUSE THEY ARE FOR ANIMATION AND CLIENT SIDE, WILL SWITCH SOON TO SYNCHED DATA
        if (this.getPose() == Pose.DIGGING) {
            setPos(Math.floor(position().x) + 0.5F, this.getY(), Math.floor(position().z) + 0.5F);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            Pose entityPose = this.getPose();
            if (entityPose == Pose.ROARING) {
                this.shakingFailAnimationState.start(this.age);
            } else {
                this.shakingFailAnimationState.stop();
            }
            if (entityPose == Pose.SNIFFING) {
                this.shakingSuccessAnimationState.start(this.age);
            } else {
                this.shakingSuccessAnimationState.stop();
            }
            if (entityPose == Pose.DIGGING) {
                this.plantingAnimationState.start(this.age);
            } else {
                this.plantingAnimationState.stop();
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SAPLING_COOLDOWN, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("saplingCooldown", this.getSaplingCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setSaplingCooldown(compoundTag.getInt("saplingCooldown"));
    }

    public Optional<ItemStack> getStackInHand(Player player) {
        return Arrays.stream(InteractionHand.values()).filter(hand -> player.getItemInHand(hand).getItem() instanceof AxeItem).map(player::getItemInHand).findFirst();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && this.getStackInHand(player).isPresent() && this.getStackInHand(player).get().getItem() instanceof AxeItem) {
            if (this.random.nextInt(10) == 0 && this.getSaplingCooldown() == 0) {
                this.spawnAtLocation(Items.SPRUCE_SAPLING, this.random.nextInt(2) + 1);
                this.setSaplingCooldown(this.random.nextIntBetweenInclusive(60 * 20 * 10, 60 * 20 * 15));
                this.setPose(Pose.SNIFFING);
            } else this.setPose(Pose.ROARING);
        }

        return (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_LIGHTNING) || source.isCreativePlayer()) && super.hurt(source, amount);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.65F;
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (this.getPose() == Pose.DIGGING) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
            movementInput = movementInput.multiply(0.0, 1.0, 0.0);
        }
        super.travel(movementInput);
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

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Treeper> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && world.getRawBrightness(pos, 0) > 8;
    }

}
