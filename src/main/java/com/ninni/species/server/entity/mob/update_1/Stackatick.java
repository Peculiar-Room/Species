package com.ninni.species.server.entity.mob.update_1;

import com.ninni.species.registry.*;
import com.ninni.species.server.entity.ai.goal.StackatickFollowOwnerGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Stackatick extends TamableAnimal {
    public static final EntityDataAccessor<Boolean> DYED = SynchedEntityData.defineId(Stackatick.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(Stackatick.class, EntityDataSerializers.INT);
    int snoringTicks = 0;
    int sittingAnimTicks = 0;
    int cooldownAnimTicks = 0;
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState standUpAnimationState = new AnimationState();
    private static final byte EVENT_PLAY_SIT_ANIMATION = 8;
    private static final byte EVENT_PLAY_STAND_ANIMATION = 9;


    public Stackatick(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        this.lookControl = new StackatickLookControl(this);
        this.refreshDimensions();
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
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
        this.goalSelector.addGoal(1, new BreedGoal(this, 1));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1, Ingredient.of(SpeciesTags.STACKATICK_TEMPT_ITEMS), false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(4, new StackatickFollowOwnerGoal(this, 1.25, 5.0f, 2.0f, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 0.225);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (this.isFood(itemStack)) {
            if (!this.isSilent()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.STACKATICK_EAT.get(), this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }
        }

        if (!this.isBaby() && !itemStack.is(SpeciesItems.WICKED_MASK.get())) {
            if (!this.isTame() && itemStack.is(SpeciesTags.STACKATICK_TAME_ITEMS)) {
                if (!player.getAbilities().instabuild) itemStack.shrink(1);
                if (!this.isSilent()) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.STACKATICK_EAT.get(), this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
                }
                if (!this.level().isClientSide) {
                    if (this.random.nextInt(2) == 0) {
                        this.tame(player);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else this.level().broadcastEntityEvent(this, (byte) 6);
                }
                return InteractionResult.SUCCESS;
            }

            if (player.isSecondaryUseActive()) {
                if ((this.isOwnedBy(player) || !this.isTame()) && itemStack.getItem() instanceof DyeItem dyeItem && ((this.isDyed() && this.getColor() != dyeItem.getDyeColor()) || !this.isDyed())) {
                    if (!this.isDyed()) this.setDyed(true);
                    this.setColor(dyeItem.getDyeColor());
                    this.playSound(SoundEvents.DYE_USE);
                    if (!player.getAbilities().instabuild) itemStack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
                if ((this.isOwnedBy(player) || !this.isTame()) && itemStack.getItem() == Items.WATER_BUCKET && this.isDyed()) {
                    this.setDyed(false);
                    ItemStack filledResult = ItemUtils.createFilledResult(itemStack, player, Items.BUCKET.getDefaultInstance());
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                                ParticleTypes.FALLING_WATER,
                                this.getX(), this.getY(0.6), this.getZ(),
                                20,
                                0.3, 0.3, 0.3,
                                1.0D
                        );
                    }
                    this.playSound(SoundEvents.BUCKET_EMPTY);
                    if (!player.getAbilities().instabuild) player.setItemInHand(hand, filledResult);
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (!this.isEyeInFluid(FluidTags.WATER) && this.getControllingPassenger() == null && !this.level().getBlockState(this.getOnPos()).isAir() && !this.level().isClientSide) {
                    return player.startRiding(this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
                }
            }


            if (this.isTame()) {
                if (player.isSecondaryUseActive()) {
                    if (this.isOwnedBy(player) && itemStack.getItem() != Items.HONEY_BOTTLE && itemStack.getItem() != Items.WATER_BUCKET) {
                        if (sittingAnimTicks == 0 && cooldownAnimTicks == 0) {
                            if (this.isInSittingPose()) {
                                this.setOrderedToSit(false);
                                this.setInSittingPose(false);
                                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.STACKATICK_STAND_UP.get(), this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
                                if (!this.level().isClientSide) {
                                    this.level().broadcastEntityEvent(this, EVENT_PLAY_STAND_ANIMATION);
                                }
                                this.cooldownAnimTicks = 20;
                            } else {
                                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.STACKATICK_SIT.get(), this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
                                this.setPos(Math.floor(position().x) + 0.5F, this.getY(), Math.floor(position().z) + 0.5F);
                                if (!this.level().isClientSide) {
                                    this.level().broadcastEntityEvent(this, EVENT_PLAY_SIT_ANIMATION);
                                }
                                this.sittingAnimTicks = 15;
                            }
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_PLAY_SIT_ANIMATION) {
            this.standUpAnimationState.stop();
            this.sitAnimationState.start(this.tickCount);
            return;
        }
        if (id == EVENT_PLAY_STAND_ANIMATION) {
            this.sitAnimationState.stop();
            this.standUpAnimationState.start(this.tickCount);
            return;
        }
        super.handleEntityEvent(id);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return pose == Pose.CROUCHING ? EntityDimensions.scalable(1F, 0.5625F) : super.getDimensions(pose);
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public void aiStep() {
        if (this.isInSittingPose() && !this.isComfy()) {
            if (snoringTicks == 0) {
                this.snoringTicks = 30;
                this.level().addParticle(SpeciesParticles.SNORING.get(), this.getX(), this.getY() + 0.375F, this.getZ(), 0f, 0f, 0f);
            }
            if (snoringTicks > 0) this.snoringTicks--;
        }

        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().move(0,-0.03,0), entity -> entity instanceof Stackatick stackatick && stackatick.isOrderedToSit());

        this.setPose(this.isInSittingPose() ? Pose.CROUCHING : Pose.STANDING);
        this.setNoGravity(this.isOrderedToSit() && !list.isEmpty());
        if (this.isOrderedToSit()) {
            this.setYRot(Math.round(this.getYRot() / 90.0) * 90);
            this.setYBodyRot(Math.round(this.yBodyRot / 90.0) * 90);
            this.setYHeadRot(Math.round(this.yHeadRot / 90.0) * 90);
        }

        if (cooldownAnimTicks > 0) this.cooldownAnimTicks--;

        if (sittingAnimTicks > 0) {
            this.sittingAnimTicks--;
            if (sittingAnimTicks == 6 && cooldownAnimTicks == 0) {
                this.setOrderedToSit(true);
                this.setInSittingPose(true);
            }
        }


        super.aiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getFirstPassenger() != null) {
            List<Entity> list = this.level().getEntities(this.getFirstPassenger(), this.getFirstPassenger().getBoundingBox(), entity -> entity instanceof Stackatick stackatick && stackatick.isOrderedToSit() && !stackatick.is(this));
            if (!this.level().isClientSide && !list.isEmpty()) {
                this.ejectPassengers();
            }
        }
        if (!this.level().isClientSide && this.isEyeInFluid(FluidTags.WATER)) {
            this.ejectPassengers();
        }
        List<Entity> list1 = this.level().getEntities(this, this.getBoundingBox().inflate(0.2f, -0.01f, 0.2f), EntitySelector.pushableBy(this));
        if (!list1.isEmpty() && this.isOrderedToSit() && !this.level().getBlockState(this.getOnPos()).isAir()) {
            for (Entity entity : list1) {
                if (entity.hasPassenger(this)) continue;
                if (!this.level().isClientSide && this.getPassengers().isEmpty() && !entity.isPassenger() && entity.getBbWidth() < (this.getBbWidth() * 2) && entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player) && !(entity instanceof Stackatick)) {
                    entity.startRiding(this);
                    continue;
                }
                this.push(entity);
            }
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DYED, false);
        this.entityData.define(COLOR, DyeColor.WHITE.getId());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setDyed(tag.getBoolean("Dyed"));
        if (tag.contains("Color", 99)) this.setColor(DyeColor.byId(tag.getInt("Color")));
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Dyed", this.isDyed());
        tag.putInt("Color", this.getColor().getId());
    }

    public boolean isDyed() {
        return this.entityData.get(DYED);
    }
    public void setDyed(boolean dyed) {
        this.entityData.set(DYED, dyed);
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(COLOR));
    }
    public void setColor(DyeColor color) {
        this.entityData.set(COLOR, color.getId());
    }

    public boolean isComfy() {
        return this.level().getBlockState( this.getOnPos()).is(SpeciesTags.STACKATICK_IS_COMFY_ON) || this.isDyed();
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.isInSittingPose() ? 0.4235F : 0.7351F;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return !this.isVehicle() && !this.isEyeInFluid(FluidTags.WATER);
    }

    @Override
    public boolean canBeLeashed(Player p_21813_) {
        return false;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi() && this.isTame()) {
            if (this.getFirstPassenger() instanceof Player player && this.isOwnedBy(player)) return player;
        }
        return null;
    }

    public boolean onClimbable() {
        return false;
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
        if (this.isOrderedToSit()) {
            if (entity instanceof Stackatick) {
                if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                    super.push(entity);
                }
            } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
                super.push(entity);
            }
        } else {
            super.push(entity);
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return entity.canBeCollidedWith() && !this.isPassengerOfSameVehicle(entity) && this.isOrderedToSit();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return SpeciesEntities.STACKATICK.get().create(serverLevel);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(SpeciesTags.STACKATICK_BREED_ITEMS);
    }

    static class StackatickLookControl extends LookControl {
        protected final Stackatick mob;
        StackatickLookControl(Stackatick mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (!this.mob.isOrderedToSit()) {
                super.tick();
            }
        }
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (this.getPose() != Pose.CROUCHING) {
            Vec2 vec2 = new Vec2(player.getXRot() * 0.5F, player.getYRot());
            this.setRot(vec2.y, vec2.x);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
        if ((this.getPose() != Pose.CROUCHING)) {
            float f = player.xxa * 0.5F;
            float f1 = player.zza;
            if (f1 <= 0.0F) {
                f1 *= 0.25F;
            }
            return new Vec3(f, 0.0, f1);
        }
        return Vec3.ZERO;
    }

    @Override
    public void travel(Vec3 movementInput) {
        LivingEntity livingentity = this.getControllingPassenger();

        if (this.getPose() == Pose.CROUCHING || this.sittingAnimTicks > 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
            movementInput = movementInput.multiply(0.0, 1.0, 0.0);
        } else {
            if (this.isVehicle() && livingentity instanceof Player && this.isOwnedBy(livingentity)) {
                float f = livingentity.xxa * 0.5F;
                float f1 = livingentity.zza;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }
                double moveY = movementInput.y;
                this.setSpeed(0.04F);
                Vec3 vec = new Vec3(f, moveY, f1);
                super.travel(vec);

            }
        }
        super.travel(movementInput);
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Stackatick> entitty, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && Animal.isBrightEnoughToSpawn(world, pos);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isOrderedToSit() && !this.isComfy()) return SpeciesSoundEvents.STACKATICK_SNORING.get();
        return this.isOrderedToSit() ? SoundEvents.EMPTY : SpeciesSoundEvents.STACKATICK_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.STACKATICK_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.STACKATICK_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (this.getName().getString().equalsIgnoreCase("goofy aah") || this.getName().getString().equalsIgnoreCase("goofy ah") || this.getName().getString().equalsIgnoreCase("goofy ahh")) {
            this.playSound(SpeciesSoundEvents.STACKATICK_GOOFY_AAH_STEP.get(), 1, 1);
        } else this.playSound(SpeciesSoundEvents.STACKATICK_STEP.get(), 0.5f, 1.0f);
    }

}
