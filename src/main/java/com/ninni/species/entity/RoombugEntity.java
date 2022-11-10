package com.ninni.species.entity;

import com.google.common.collect.Sets;
import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.sound.SpeciesSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class RoombugEntity extends TameableEntity {
    private static final Set<Item> TAMING_INGREDIENTS = Sets.newHashSet(Items.HONEYCOMB);
    int snoringTicks = 0;

    protected RoombugEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1;
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (entityData == null) {
            entityData = new PassiveEntity.PassiveData(false);
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new WanderAroundGoal(this, 1));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.25, 5.0f, 2.0f, false));
        this.goalSelector.add(3, new RoombugLookAtEntityGoal(this, PlayerEntity.class, 8.0f));
    }

    public static DefaultAttributeContainer.Builder createRoombugAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.225);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!this.isTamed() && TAMING_INGREDIENTS.contains(itemStack.getItem())) {

            if (!player.getAbilities().creativeMode) itemStack.decrement(1);
            if (!this.isSilent()) {
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.ENTITY_ROOMBUG_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }

            if (!this.world.isClient) {
                if (this.random.nextInt(2) == 0) {
                    this.setOwner(player);
                    this.world.sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
                } else {
                    this.world.sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
                }
            }

            return ActionResult.success(this.world.isClient);
        }

        if (this.isTamed()) {
            if (player.shouldCancelInteraction()) {
                if (this.isOwner(player) && itemStack.getItem() != Items.HONEY_BOTTLE) {
                    if (!this.world.isClient) this.setSitting(!this.isSitting());
                    return ActionResult.success(this.world.isClient);
                } else if (itemStack.getItem() == Items.HONEY_BOTTLE && this.getHealth() < this.getMaxHealth()) {
                    if (!this.world.isClient) {
                        if (!player.getAbilities().creativeMode) itemStack.decrement(1);
                        if (!player.getInventory().insertStack(Items.GLASS_BOTTLE.getDefaultStack())) {
                            player.dropItem(Items.GLASS_BOTTLE.getDefaultStack(), false);
                        }
                        this.heal(6);
                        this.world.sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
                        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_HONEY_BOTTLE_DRINK, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
                    }
                    return ActionResult.success(this.world.isClient);
                }
            }
            if (!this.isSubmergedIn(FluidTags.WATER) && this.getPrimaryPassenger() == null) {
                return player.startRiding(this) ? ActionResult.SUCCESS : ActionResult.PASS;
            }
        }

        return super.interactMob(player, hand);
    }

    @Override
    public boolean hurtByWater() {
        return true;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    @Override
    public void tickMovement() {
        if (this.isInSittingPose()) {
            if (snoringTicks == 0) {
                this.snoringTicks = 30;
                this.world.addParticle(SpeciesParticles.SNORING, this.getX(), this.getY() + 0.375F, this.getZ(), 0f, 0f, 0f);
            }
            if (snoringTicks > 0) this.snoringTicks--;
        }
        super.tickMovement();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && this.isSubmergedIn(FluidTags.WATER)) {
            this.removeAllPassengers();
        }
        List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox().expand(0.2f, -0.01f, 0.2f), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty() && this.isSitting()) {
            boolean bl = !this.world.isClient && !(this.getPrimaryPassenger() instanceof PlayerEntity);
            for (Entity entity : list) {
                if (entity.hasPassenger(this)) continue;
                if (bl && this.getPassengerList().size() < 1 && !entity.hasVehicle() && entity.getWidth() < this.getWidth() && entity instanceof LivingEntity && !(entity instanceof WaterCreatureEntity) && !(entity instanceof PlayerEntity)) {
                    entity.startRiding(this);
                    continue;
                }
                this.pushAwayFrom(entity);
            }
        }
    }

    @Override
    public double getMountedHeightOffset() {
        return this.isInSittingPose() ? 0.225F : 0.275F;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return !this.hasPassengers() && !this.isSubmergedIn(FluidTags.WATER);
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        return null;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (entity instanceof BoatEntity) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.pushAwayFrom(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.pushAwayFrom(entity);
        }
    }

    @Override
    public boolean collidesWith(Entity other) {
        return (other.isCollidable()) && !this.isConnectedThroughVehicle(other);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isSitting()) {
            this.setVelocity(this.getVelocity().multiply(0.0, 1.0, 0.0));
            movementInput = movementInput.multiply(0.0, 1.0, 0.0);
        }
        super.travel(movementInput);
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<RoombugEntity> entitty, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getBlockState(pos.down()).isIn(BlockTags.ANIMALS_SPAWNABLE_ON) && AnimalEntity.isLightLevelValidForNaturalSpawn(world, pos);
    }

    static class RoombugLookAtEntityGoal extends LookAtEntityGoal{
        private final RoombugEntity bug;

        public RoombugLookAtEntityGoal(RoombugEntity mob, Class<? extends LivingEntity> targetType, float range) {
            super(mob, targetType, range);
            this.bug = mob;
        }

        @Override
        public boolean canStart() {
            if (this.bug.isSitting()) return false;
            return super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            if (this.bug.isSitting()) return false;
            return super.shouldContinue();
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isSitting() ? SpeciesSoundEvents.ENTITY_ROOMBUG_SNORING : SpeciesSoundEvents.ENTITY_ROOMBUG_IDLE;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.ENTITY_ROOMBUG_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.ENTITY_ROOMBUG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if ("Goofy Ahh".equals(Formatting.strip(this.getName().getString()))) {
            this.playSound(SpeciesSoundEvents.ENTITY_ROOMBUG_GOOFY_AAH_STEP, 1, 1);
        } else this.playSound(SpeciesSoundEvents.ENTITY_ROOMBUG_STEP, 0.5f, 1.0f);
    }
}
