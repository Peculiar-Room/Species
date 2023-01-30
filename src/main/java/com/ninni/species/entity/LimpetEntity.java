package com.ninni.species.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.ninni.species.entity.ai.LimpetAi;
import com.ninni.species.entity.enums.LimpetType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LimpetEntity extends Animal {
    protected static final ImmutableList<SensorType<? extends Sensor<? super LimpetEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.IS_PANICKING, MemoryModuleType.AVOID_TARGET);
    private static final EntityDataAccessor<Integer> SCARED_TICKS = SynchedEntityData.defineId(LimpetEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(LimpetEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CRACKED_STAGE = SynchedEntityData.defineId(LimpetEntity.class, EntityDataSerializers.INT);
    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);

    protected LimpetEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected Brain.Provider<LimpetEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    public Brain<LimpetEntity> getBrain() {
        return (Brain<LimpetEntity>) super.getBrain();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return LimpetAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("limpetBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("limpetActivityUpdate");
        LimpetAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    public static AttributeSupplier.Builder createLimpetAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SCARED_TICKS, 0);
        this.entityData.define(TYPE, 0);
        this.entityData.define(CRACKED_STAGE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("ScaredTicks", this.getScaredTicks());
        nbt.putInt("LimpetType", this.getLimpetType().getId());
        nbt.putInt("CrackedStage", this.getCrackedStage());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setScaredTicks(nbt.getInt("ScaredTicks"));
        this.setLimpetType(nbt.getInt("LimpetType"));
        this.setCrackedStage(nbt.getInt("CrackedStage"));
    }

    public int getCrackedStage() {
        return this.entityData.get(CRACKED_STAGE);
    }
    public void setCrackedStage(int crackedStage) {
        this.entityData.set(CRACKED_STAGE, crackedStage);
    }
    public LimpetType getLimpetType() { return LimpetType.TYPES[this.entityData.get(TYPE)]; }
    public void setLimpetType(int id) { this.entityData.set(TYPE, id); }
    public int getScaredTicks() {
        return this.entityData.get(SCARED_TICKS);
    }
    public void setScaredTicks(int scaredTicks) {
        this.entityData.set(SCARED_TICKS, scaredTicks);
    }
    public boolean isScared() {
        return this.getScaredTicks() > 0;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            if (!this.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET)) {
                this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(4D), this::isValidEntity).forEach(player -> this.setScaredTicks(100));
            }
            if (this.getScaredTicks() > 0) {
                int scaredTicks = this.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET) ? 0 : this.getScaredTicks() - 1;
                this.getNavigation().stop();
                this.setScaredTicks(scaredTicks);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        LimpetType type = this.getLimpetType();
        if (this.getCrackedStage() > 0 && type.getId() > 0 && player.getItemInHand(player.getUsedItemHand()).getItem() == type.getItem() && !this.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET)) {
            this.setCrackedStage(this.getCrackedStage() - 1);
            this.playSound(type.getPlacingSound(), 1, 1);
            if (!player.getAbilities().instabuild) player.getItemInHand(player.getUsedItemHand()).shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, interactionHand);
    }

    @Override
    public boolean isPushable() {
        return !this.isScared();
    }

    public boolean isValidEntity(Player player) {
        return this.getLimpetType().getId() > 0
                && !player.isSpectator()
                && player.isAlive()
                && !player.getAbilities().instabuild
                && !player.isShiftKeyDown() || (this.getLimpetType().getId() > 0
                && player.getItemInHand(player.getUsedItemHand()).getItem() instanceof PickaxeItem);
    }

    public boolean isValidEntityHoldingPickaxe(Player player) {
        return this.getLimpetType().getId() > 0 && player.getItemInHand(player.getUsedItemHand()).getItem() instanceof PickaxeItem;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        LimpetType type = this.getLimpetType();
        if (source.getEntity() instanceof Player player
                && type.getId() > 0
                && player.getItemInHand(player.getUsedItemHand()).getItem() instanceof PickaxeItem pickaxe
                && pickaxe.getTier().getLevel() >= type.getPickaxeLevel()
                && !player.getCooldowns().isOnCooldown(pickaxe)) {

            ItemStack stack = player.getItemInHand(player.getUsedItemHand());
            if (this.getCrackedStage() < 3) {
                this.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, player, RETREAT_DURATION.sample(this.level.random));
                this.setCrackedStage(this.getCrackedStage() + 1);
                this.playSound(type.getMiningSound(), 1, 1);
                this.setScaredTicks(0);
                player.getCooldowns().addCooldown(stack.getItem(), 80);
                return false;
            } else {
                this.spawnAtLocation(type.getItem(), 1);
                if (random.nextInt(2) == 1) this.spawnAtLocation(type.getItem(), 1);
                switch (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack)) {
                    case 1:
                        if (random.nextInt(4) == 1) this.spawnAtLocation(type.getItem(), 1);
                    case 2:
                        if (random.nextInt(2) == 1) this.spawnAtLocation(type.getItem(), 1);
                    case 3:
                        this.spawnAtLocation(type.getItem(), 1);
                }
                this.setCrackedStage(0);
                this.playSound(type.getMiningSound(), 1, 1);
                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) != 0) {
                    this.setLimpetType(1);
                    return false;
                } else {
                    this.setLimpetType(0);
                    this.setScaredTicks(0);
                }
            }

        } else if (source.getEntity() instanceof LivingEntity && amount < 12 && !this.level.isClientSide && type.getId() > 0) {
            this.playSound(SoundEvents.SHIELD_BLOCK, 1, 1);
            if (!this.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET)) this.setScaredTicks(300);
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void travel(Vec3 vec3) {
        if (!this.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET) && this.isScared()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0, 1, 0));
            vec3 = vec3.multiply(0, 1, 0);
        }
        super.travel(vec3);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<? extends AgeableMob> type, LevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return false;
    }
}
