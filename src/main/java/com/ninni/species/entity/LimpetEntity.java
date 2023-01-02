package com.ninni.species.entity;

import com.ninni.species.entity.enums.LimpetType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class LimpetEntity extends AnimalEntity {
    private static final TrackedData<Integer> SCARED_TICKS = DataTracker.registerData(LimpetEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TYPE = DataTracker.registerData(LimpetEntity.class, TrackedDataHandlerRegistry.INTEGER);

    protected LimpetEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(5, new EscapeDangerGoal(this, 1.5));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createLimpetAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SCARED_TICKS, 0);
        this.dataTracker.startTracking(TYPE, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("ScaredTicks", this.getScaredTicks());
        nbt.putInt("LimpetType", this.getLimpetType().getId());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setScaredTicks(nbt.getInt("ScaredTicks"));
        this.setLimpetType(nbt.getInt("LimpetType"));
    }

    public LimpetType getLimpetType() { return LimpetType.TYPES[this.dataTracker.get(TYPE)]; }
    public void setLimpetType(int id) { this.dataTracker.set(TYPE, id); }
    public int getScaredTicks() {
        return this.dataTracker.get(SCARED_TICKS);
    }
    public void setScaredTicks(int scaredTicks) {
        this.dataTracker.set(SCARED_TICKS, scaredTicks);
    }
    public boolean isScared() {
        return this.getScaredTicks() > 0;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.world.getEntitiesByClass(PlayerEntity.class, this.getBoundingBox().expand(4D), this::isValidEntity).forEach(player -> this.setScaredTicks(100));
        if (this.getScaredTicks() > 0) {
            this.getNavigation().stop();
            this.setScaredTicks(this.getScaredTicks() - 1);
        }
    }

    @Override
    public boolean isPushable() {
        return !this.isScared();
    }

    public boolean isValidEntity(PlayerEntity player) {
        return this.getLimpetType().getId() > 0
                && !player.isSpectator()
                && player.isAlive()
                && !player.getAbilities().creativeMode
                && !player.isSneaking()
                || (this.getLimpetType().getId() > 0
                && player.getStackInHand(player.getActiveHand()).getItem() instanceof PickaxeItem);
    }

    public boolean isValidEntityHoldingPickaxe(PlayerEntity player) {
        return this.getLimpetType().getId() > 0 && player.getStackInHand(player.getActiveHand()).getItem() instanceof PickaxeItem;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        LimpetType type = this.getLimpetType();

        if (source.getAttacker() instanceof PlayerEntity player
                && player.getStackInHand(player.getActiveHand()).getItem() instanceof PickaxeItem pickaxe
                && type.getId() > 0
                && pickaxe.getMaterial().getMiningLevel() >= type.getPickaxeLevel()) {

            ItemStack stack = player.getStackInHand(player.getActiveHand());

            this.dropItem(type.getItem(), 1);
            if (random.nextInt(2) == 1) this.dropItem(type.getItem(), 1);
            switch (EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack)) {
                case 1 : if (random.nextInt(4) == 1) this.dropItem(type.getItem(), 1);
                case 2 : if (random.nextInt(2) == 1) this.dropItem(type.getItem(), 1);
                case 3 : this.dropItem(type.getItem(), 1);
            }

            playSound(type.getMiningSound(), 1, 1);

            if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) != 0) {
                this.setLimpetType(1);
                return false;
            } else {
                this.setLimpetType(0);
                this.setScaredTicks(0);
            }
        } else if (source.getAttacker() instanceof LivingEntity && amount < 12 && !world.isClient() && type.getId() > 0) {

            playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1, 1);
            this.setScaredTicks(300);
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isScared()) {
            this.setVelocity(this.getVelocity().multiply(0, 1, 0));
            movementInput = movementInput.multiply(0, 1, 0);
        }
        super.travel(movementInput);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<? extends PassiveEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        return false;
    }
}
