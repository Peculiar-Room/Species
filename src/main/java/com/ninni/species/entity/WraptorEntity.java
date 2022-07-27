package com.ninni.species.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;


public class WraptorEntity extends AnimalEntity implements Shearable {
    public static final TrackedData<Integer> FEATHER_STAGE = DataTracker.registerData(WraptorEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final String FEATHER_STAGE_KEY = "FeatherStage";
    public static final String TIME_SINCE_SHEARED_KEY = "TimeSinceSheared";
    private long timeSinceSheared;

    public WraptorEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createWraptorAttributes() {
        return createMobAttributes()
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 18.0D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FEATHER_STAGE, 4);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
    }

    public int getFeatherStage() {
        return this.dataTracker.get(FEATHER_STAGE);
    }
    public void setFeatherStage(int stage) {
        this.dataTracker.set(FEATHER_STAGE, stage);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        int stage = this.getFeatherStage();
        if (stage < 4) {
            long time = this.world.getTime();
            if (this.random.nextInt((int)(time - this.timeSinceSheared)) > 20 * 90) {
                this.timeSinceSheared = time;
                this.setFeatherStage(stage + 1);
            }
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(Items.SHEARS)) {
            if (!this.world.isClient && this.isShearable()) {
                this.sheared(SoundCategory.PLAYERS);
                this.emitGameEvent(GameEvent.SHEAR, player);
                stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }
        return super.interactMob(player, hand);
    }

    @Override
    public void sheared(SoundCategory category) {
        int stage = this.getFeatherStage();
        if (stage == 4) this.timeSinceSheared = this.world.getTime();
        this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, category, 1.0f, 1.0f);
        this.setFeatherStage(stage - 1);
        for (int i = 0, l = 2 + this.random.nextInt(5); i < l; i++) {
            ItemEntity itemEntity = this.dropItem(Items.FEATHER, 1);
            if (itemEntity == null) continue;
            itemEntity.setVelocity(itemEntity.getVelocity().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1f, this.random.nextFloat() * 0.05f, (this.random.nextFloat() - this.random.nextFloat()) * 0.1f));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt(FEATHER_STAGE_KEY, this.getFeatherStage());
        nbt.putLong(TIME_SINCE_SHEARED_KEY, this.timeSinceSheared);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setFeatherStage(nbt.getInt(FEATHER_STAGE_KEY));
        this.timeSinceSheared = nbt.getLong(TIME_SINCE_SHEARED_KEY);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.95F;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType <WraptorEntity> entity, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random){
        return world.getBlockState(pos.down()).isOf(Blocks.GRASS_BLOCK) && world.getBaseLightLevel(pos, 0) > 8;
    }

    @Override
    public boolean isShearable() {
        return this.getFeatherStage() > 0;
    }
}
