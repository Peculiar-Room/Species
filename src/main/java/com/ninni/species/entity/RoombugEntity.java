package com.ninni.species.entity;

import com.google.common.collect.Sets;
import com.ninni.species.entity.entity.ai.goal.RoombugFollowOwnerGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class RoombugEntity extends TameableEntity {
    private static final Set<Item> TAMING_INGREDIENTS = Sets.newHashSet(Items.HONEYCOMB);

    protected RoombugEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
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
        this.goalSelector.add(2, new RoombugFollowOwnerGoal(this, 1.5, 3.0f, 1.0f));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
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
                //TODO custom sounds
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PARROT_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }

            if (!this.world.isClient) {
                if (this.random.nextInt(10) == 0) {
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
                if (!this.world.isClient && this.isOwner(player)) this.setSitting(!this.isSitting());
            } else {
                if (!this.isSubmergedIn(FluidTags.WATER) && this.getPrimaryPassenger() == null) {
                    if (!this.world.isClient) {
                        return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
                    }
                }
            }

            return ActionResult.success(this.world.isClient);
        }

        return super.interactMob(player, hand);
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
        return this.getHeight() / 2;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return RoombugEntity.canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPrimaryPassenger() == null && !this.isSubmergedIn(FluidTags.WATER);
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
    @Nullable
    public Entity getPrimaryPassenger() {
        return this.getFirstPassenger();
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

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<RoombugEntity> roombugEntityEntityType, ServerWorldAccess serverWorldAccess, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return false;
    }
}
