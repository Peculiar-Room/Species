package com.ninni.species.entity;

import com.ninni.species.entity.pose.SpeciesPose;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class Treeper extends AgeableMob {
    private static final EntityDataAccessor<Integer> SAPLING_COOLDOWN = SynchedEntityData.defineId(Treeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PLANTED = SynchedEntityData.defineId(Treeper.class, EntityDataSerializers.BOOLEAN);
    public final AnimationState shakingSuccessAnimationState = new AnimationState();
    public final AnimationState shakingFailAnimationState = new AnimationState();
    public final AnimationState plantingAnimationState = new AnimationState();

    public Treeper(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1);
        this.lookControl = new TreeperLookControl(this);
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

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TreeperLookGoal(this, Player.class, 6.0F));
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

        if (this.level().getDayTime() > 1000 && this.level().getDayTime() < 13000) {
            this.setPose(SpeciesPose.PLANTING.get());
            this.setPlanted(true);
        } else {
            this.setPose(Pose.STANDING);
            this.setPlanted(false);
        }

        if (this.isPlanted()) {
            this.yBodyRot = Math.round(this.yBodyRot / 90.0) * 90;
            this.setPos(Math.floor(position().x) + 0.99F, this.getY(), Math.floor(position().z) + 0.99F);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == SpeciesPose.PLANTING.get()) this.plantingAnimationState.start(this.tickCount);
            if (this.getPose() == SpeciesPose.SHAKE_SUCCESS.get()) this.shakingSuccessAnimationState.start(this.tickCount);
            if (this.getPose() == SpeciesPose.SHAKE_FAIL.get()) this.shakingFailAnimationState.start(this.tickCount);
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }


    @Override
    public boolean canBeCollidedWith() {
        return this.isPlanted();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SAPLING_COOLDOWN, 0);
        this.entityData.define(PLANTED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("saplingCooldown", this.getSaplingCooldown());
        compoundTag.putBoolean("planted", this.isPlanted());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setSaplingCooldown(compoundTag.getInt("saplingCooldown"));
        this.setPlanted(compoundTag.getBoolean("planted"));
    }

    public Optional<ItemStack> getStackInHand(Player player) {
        return Arrays.stream(InteractionHand.values()).filter(hand -> player.getItemInHand(hand).getItem() instanceof AxeItem).map(player::getItemInHand).findFirst();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && this.getStackInHand(player).isPresent() && this.getStackInHand(player).get().getItem() instanceof AxeItem && !this.isPlanted()) {
            if (this.getSaplingCooldown() == 0) {
                this.spawnAtLocation(SpeciesItems.ANCIENT_PINECONE, 7);
                if (this.random.nextInt(5) == 0) this.spawnAtLocation(SpeciesItems.ANCIENT_PINECONE, 7);
                if (this.random.nextInt(5) == 0) this.spawnAtLocation(SpeciesItems.ANCIENT_PINECONE, 7);
                this.setSaplingCooldown(this.random.nextIntBetweenInclusive(60 * 20 * 2, 60 * 20 * 7));
                this.setPose(SpeciesPose.SHAKE_SUCCESS.get());
            }else this.setPose(SpeciesPose.SHAKE_FAIL.get());

        }

        return (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_LIGHTNING) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) && super.hurt(source, amount);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.65F;
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

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Treeper> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.below()).is(SpeciesTags.TREEPER_SPAWNABLE_ON);
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
