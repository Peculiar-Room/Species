package com.ninni.species.entity;

import com.ninni.species.registry.*;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Springling extends Animal implements PlayerRideable {
    public static final EntityDataAccessor<Float> EXTENDED_AMOUNT = SynchedEntityData.defineId(Springling.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> RETRACTING = SynchedEntityData.defineId(Springling.class, EntityDataSerializers.BOOLEAN);
    private static int messageCooldown;
    public int maxExtendedAmount = 9;

    public Springling(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BreedGoal(this, 1));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2, Ingredient.of(SpeciesTags.GOOBER_BREED_ITEMS), false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        this.refreshDimensions();

        if (getExtendedAmount() > maxExtendedAmount) this.setExtendedAmount(maxExtendedAmount);

        if (this.isRetracting() && !this.level().isClientSide) {
            if (getExtendedAmount() > 0) {
                this.setExtendedAmount(getExtendedAmount() - 0.25f);
                this.playExtendingSound(this);
            }
            if (getExtendedAmount() < 0.5f) this.playSound(SpeciesSoundEvents.SPRINGLING_EXTEND_FINISH, 2f, 1f);
            if (getExtendedAmount() == 0) this.setRetracting(false);
        }

        if (messageCooldown > 0 && !this.level().isClientSide) messageCooldown--;
        if (messageCooldown == 1 && this.isVehicle()) {
            if (this.getFirstPassenger() instanceof ServerPlayer serverPlayer) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                ServerPlayNetworking.send(serverPlayer, SpeciesNetwork.SEND_SPRINGLING_MESSAGE, buf);
            }
        }

        if (getExtendedAmount() < 0) this.setExtendedAmount(0);
    }

    public void playExtendingSound(Entity entity) {
        if (getExtendedAmount() >= maxExtendedAmount - 0.1f) entity.playSound(SpeciesSoundEvents.SPRINGLING_EXTEND_FINISH, 2f, 1f);
        entity.playSound(SpeciesSoundEvents.SPRINGLING_EXTEND, 0.5f, getExtendedAmount()/10 + 0.5f);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if (this.isVehicle()) {
            return super.mobInteract(player, interactionHand);
        }

        if (this.isFood(itemStack)) {
            int i = this.getAge();
            if (!this.level().isClientSide && i == 0 && this.canFallInLove()) {
                this.usePlayerItem(player, interactionHand, itemStack);
                this.setInLove(player);
                this.level().playSound(null, this, SpeciesSoundEvents.SPRINGLING_EAT, SoundSource.NEUTRAL, 1.0f, this.isBaby() ? Mth.randomBetween(this.level().random, 0.8f, 1.2f) * 1.5f : Mth.randomBetween(this.level().random, 0.8f, 1.2f));
                return InteractionResult.SUCCESS;
            }
            if (this.isBaby()) {
                this.usePlayerItem(player, interactionHand, itemStack);
                this.ageUp(Animal.getSpeedUpSecondsWhenFeeding(-i), true);
                this.level().playSound(null, this, SpeciesSoundEvents.SPRINGLING_EAT, SoundSource.NEUTRAL, 1.0f, this.isBaby() ? Mth.randomBetween(this.level().random, 0.8f, 1.2f) * 1.5f : Mth.randomBetween(this.level().random, 0.8f, 1.2f));
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            }
        }

        if (!this.isBaby() && !this.isFood(itemStack)) {
            if (player.isShiftKeyDown() && !isRetracting() && getExtendedAmount() > 0 && !this.level().isClientSide) {
                this.setRetracting(true);
                return InteractionResult.SUCCESS;
            }
            if (!player.isShiftKeyDown()) {
                messageCooldown = 70;
                this.doPlayerRide(player);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, interactionHand);
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 4) {
            this.playExtendingSound(this.getFirstPassenger());
        } else {
            super.handleEntityEvent(b);
        }
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel serverLevel, Animal animal) {
        ItemStack itemStack = new ItemStack(SpeciesItems.SPRINGLING_EGG);
        ItemEntity itemEntity = new ItemEntity(serverLevel, this.position().x(), this.position().y(), this.position().z(), itemStack);
        itemEntity.setDefaultPickUpDelay();
        this.finalizeSpawnChildFromBreeding(serverLevel, animal, null);
        this.playSound(SpeciesSoundEvents.SPRINGLING_EGG_PLOP, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 0.5f);
        serverLevel.addFreshEntity(itemEntity);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(SpeciesTags.SPRINGLING_BREED_ITEMS);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(0.99F, 2.2F).scale(this.getScale(), this.getScale() + getExtendedAmount()/1.5f);
    }

    @Override
    public float getScale() {
        return this.isBaby() ? 0.25f : 1.0f;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EXTENDED_AMOUNT, 0f);
        this.entityData.define(RETRACTING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Retracting", this.isRetracting());
        compoundTag.putFloat("ExtendedAmount", getExtendedAmount());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setRetracting(compoundTag.getBoolean("Retracting"));
        this.setExtendedAmount(compoundTag.getFloat("ExtendedAmount"));
    }

    public float getExtendedAmount() {
        return this.entityData.get(EXTENDED_AMOUNT);
    }
    public void setExtendedAmount(float amount) {
        this.entityData.set(EXTENDED_AMOUNT, amount);
    }
    public boolean isRetracting() {
        return this.entityData.get(RETRACTING);
    }
    public void setRetracting(boolean bl) {
        this.entityData.set(RETRACTING, bl);
    }

    @Override
    public boolean causeFallDamage(float f, float g, DamageSource damageSource) {
        int i;
        if ((i = this.calculateFallDamage(f, g)) <= 0) {
            return false;
        }
        this.hurt(damageSource, i);
        if (this.isVehicle()) {
            for (Entity entity : this.getIndirectPassengers()) {
                entity.hurt(damageSource, i);
            }
        }
        this.playBlockFallSound();
        return true;
    }

    @Override
    public boolean isImmobile() {
        return (super.isImmobile() && this.isVehicle()) || (getExtendedAmount() > 0 && !this.isVehicle());
    }

    @Override
    protected int calculateFallDamage(float f, float g) {
        return Mth.ceil((f * 0.5f - 3.0f) * g);
    }

    protected void doPlayerRide(Player player) {
        if (!(this.level()).isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        Vec2 vec2 = this.getRiddenRotation(player);
        this.setRot(vec2.y, vec2.x);
        this.yBodyRot = this.yHeadRot = this.getYRot();
        this.yRotO = this.yHeadRot;
    }

    protected Vec2 getRiddenRotation(LivingEntity livingEntity) {
        return new Vec2(livingEntity.getXRot() * 0.5f, livingEntity.getYRot());
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
        float f = player.xxa * 0.5f;
        float g = player.zza;
        if (g <= 0.0f) {
            g *= 0.25f;
        }
        return new Vec3(f, 0.0, g);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SpeciesSoundEvents.SPRINGLING_IDLE;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.SPRINGLING_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.SPRINGLING_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (this.getFirstPassenger() != null) this.getFirstPassenger().playSound(SpeciesSoundEvents.SPRINGLING_STEP, 0.15f * getExtendedAmount()/10, 1.0f);
        this.playSound(SpeciesSoundEvents.SPRINGLING_STEP, 0.15f, 1.0f);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return SpeciesEntities.SPRINGLING.create(serverLevel);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) / 1.5f;
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height - 0.15f;
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getDimensions(Pose.STANDING).height - 0.25f;
    }

    @Override
    public boolean isPushable() {
        return !this.isVehicle() && getExtendedAmount() == 0;
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Mob mob) {
            return mob;
        }
        if (entity instanceof Player mob) {
            return mob;
        }
        return null;
    }


    @Nullable
    private Vec3 getDismountLocationInDirection(Vec3 vec3, LivingEntity livingEntity) {
        double d = this.getX() + vec3.x;
        double e = this.getBoundingBox().minY;
        double f = this.getZ() + vec3.z;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        block0: for (Pose pose : livingEntity.getDismountPoses()) {
            mutableBlockPos.set(d, e, f);
            double g = this.getBoundingBox().maxY + 0.75;
            do {
                if ((double)mutableBlockPos.getY() > g) continue block0;
                if (DismountHelper.isBlockFloorValid(0)) {
                    AABB aABB = livingEntity.getLocalBoundsForPose(pose);
                    Vec3 vec32 = new Vec3(d, g, f);

                    if (DismountHelper.canDismountTo((this.level()), livingEntity, aABB.move(vec32))) {
                        livingEntity.setPose(pose);
                        return vec32;
                    }
                }
                mutableBlockPos.move(Direction.UP);
            } while ((double)mutableBlockPos.getY() < g);
        }

        return null;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        Vec3 vec3 = getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), this.getYRot());
        Vec3 vec32 = this.getDismountLocationInDirection(vec3, livingEntity);
        if (vec32 != null) {
            return vec32;
        }
        return this.position().add(0,this.getEyeHeight(),0);
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Springling> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return false;
    }
}
