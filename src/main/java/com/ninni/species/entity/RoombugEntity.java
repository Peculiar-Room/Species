package com.ninni.species.entity;

import com.google.common.collect.Sets;
import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.entity.ai.goal.RoombugFollowOwnerGoal;
import com.ninni.species.sound.SpeciesSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class RoombugEntity extends TamableAnimal {
    private static final Set<Item> TAMING_INGREDIENTS = Sets.newHashSet(Items.HONEYCOMB);
    int snoringTicks = 0;

    protected RoombugEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        this.maxUpStep = 1;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        if (spawnGroupData == null) {
            spawnGroupData = new AgeableMob.AgeableMobGroupData(false);
        }
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(2, new RoombugFollowOwnerGoal(this, 1.25, 5.0f, 2.0f, false));
        this.goalSelector.addGoal(3, new RoombugLookAtEntityGoal(this, Player.class, 8.0f));
    }

    public static AttributeSupplier.Builder createRoombugAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 0.225);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!this.isTame() && TAMING_INGREDIENTS.contains(itemStack.getItem())) {

            if (!player.getAbilities().instabuild) itemStack.shrink(1);
            if (!this.isSilent()) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.ENTITY_ROOMBUG_EAT.get(), this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }

            if (!this.level.isClientSide) {
                if (this.random.nextInt(2) == 0) {
                    this.tame(player);
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }
            }

            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }

        if (this.isTame()) {
            if (player.isSecondaryUseActive()) {
                if (this.isOwnedBy(player) && itemStack.getItem() != Items.HONEY_BOTTLE) {
                    if (!this.level.isClientSide) this.setOrderedToSit(!this.isOrderedToSit());
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                } else if (itemStack.getItem() == Items.HONEY_BOTTLE && this.getHealth() < this.getMaxHealth()) {
                    if (!this.level.isClientSide) {
                        if (!player.getAbilities().instabuild) itemStack.shrink(1);
                        if (!player.getInventory().add(Items.GLASS_BOTTLE.getDefaultInstance())) {
                            player.drop(Items.GLASS_BOTTLE.getDefaultInstance(), false);
                        }
                        this.heal(6);
                        this.level.broadcastEntityEvent(this, (byte) 7);
                        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.HONEY_DRINK, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
                    }
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                }
            }
            if (!this.isEyeInFluid(FluidTags.WATER) && this.getControllingPassenger() == null) {
                return player.startRiding(this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public void aiStep() {
        if (this.isInSittingPose()) {
            if (snoringTicks == 0) {
                this.snoringTicks = 30;
                this.level.addParticle(SpeciesParticles.SNORING.get(), this.getX(), this.getY() + 0.375F, this.getZ(), 0f, 0f, 0f);
            }
            if (snoringTicks > 0) this.snoringTicks--;
        }
        super.aiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && this.isEyeInFluid(FluidTags.WATER)) {
            this.ejectPassengers();
        }
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.2f, -0.01f, 0.2f), EntitySelector.pushableBy(this));
        if (!list.isEmpty() && this.isOrderedToSit()) {
            boolean bl = !this.level.isClientSide && !(this.getControllingPassenger() instanceof Player);
            for (Entity entity : list) {
                if (entity.hasPassenger(this)) continue;
                if (bl && this.getPassengers().size() < 1 && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() && entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player) && !(entity instanceof RoombugEntity)) {
                    entity.startRiding(this);
                    continue;
                }
                this.push(entity);
            }
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.isInSittingPose() ? 0.225F : 0.275F;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return !this.isVehicle() && !this.isEyeInFluid(FluidTags.WATER);
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        return null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void push(Entity entity) {
        if (entity instanceof Boat) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entity);
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return entity.canBeCollidedWith() && !this.isPassengerOfSameVehicle(entity);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canMate(Animal other) {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (this.isOrderedToSit()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
            movementInput = movementInput.multiply(0.0, 1.0, 0.0);
        }
        super.travel(movementInput);
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<RoombugEntity> entitty, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && Animal.isBrightEnoughToSpawn(world, pos);
    }

    static class RoombugLookAtEntityGoal extends LookAtPlayerGoal {
        private final RoombugEntity bug;

        public RoombugLookAtEntityGoal(RoombugEntity mob, Class<? extends LivingEntity> targetType, float range) {
            super(mob, targetType, range);
            this.bug = mob;
        }

        @Override
        public boolean canUse() {
            if (this.bug.isOrderedToSit()) return false;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.bug.isOrderedToSit()) return false;
            return super.canContinueToUse();
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isOrderedToSit() ? SpeciesSoundEvents.ENTITY_ROOMBUG_SNORING.get() : SpeciesSoundEvents.ENTITY_ROOMBUG_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.ENTITY_ROOMBUG_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.ENTITY_ROOMBUG_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if ("Goofy Ahh".equals(ChatFormatting.stripFormatting(this.getName().getString()))) {
            this.playSound(SpeciesSoundEvents.ENTITY_ROOMBUG_GOOFY_AAH_STEP.get(), 1, 1);
        } else this.playSound(SpeciesSoundEvents.ENTITY_ROOMBUG_STEP.get(), 0.5f, 1.0f);
    }

}
