package com.ninni.species.entity;

import com.ninni.species.entity.enums.LimpetType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
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
    private static final EntityDataAccessor<Integer> SCARED_TICKS = SynchedEntityData.defineId(LimpetEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(LimpetEntity.class, EntityDataSerializers.INT);

    protected LimpetEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createLimpetAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SCARED_TICKS, 0);
        this.entityData.define(TYPE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("ScaredTicks", this.getScaredTicks());
        nbt.putInt("LimpetType", this.getLimpetType().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setScaredTicks(nbt.getInt("ScaredTicks"));
        this.setLimpetType(nbt.getInt("LimpetType"));
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
        this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(4D), this::isValidEntity).forEach(player -> this.setScaredTicks(100));
        if (this.getScaredTicks() > 0) {
            this.getNavigation().stop();
            this.setScaredTicks(this.getScaredTicks() - 1);
        }
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
                && !player.isShiftKeyDown()
                || (this.getLimpetType().getId() > 0
                && player.getItemInHand(player.getUsedItemHand()).getItem() instanceof PickaxeItem);
    }

    public boolean isValidEntityHoldingPickaxe(Player player) {
        return this.getLimpetType().getId() > 0 && player.getItemInHand(player.getUsedItemHand()).getItem() instanceof PickaxeItem;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        LimpetType type = this.getLimpetType();

        if (source.getEntity() instanceof Player player
                && player.getItemInHand(player.getUsedItemHand()).getItem() instanceof PickaxeItem pickaxe
                && type.getId() > 0
                && pickaxe.getTier().getLevel() >= type.getPickaxeLevel()) {

            ItemStack stack = player.getItemInHand(player.getUsedItemHand());

            this.spawnAtLocation(type.getItem(), 1);
            if (random.nextInt(2) == 1) this.spawnAtLocation(type.getItem(), 1);
            switch (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack)) {
                case 1 : if (random.nextInt(4) == 1) this.spawnAtLocation(type.getItem(), 1);
                case 2 : if (random.nextInt(2) == 1) this.spawnAtLocation(type.getItem(), 1);
                case 3 : this.spawnAtLocation(type.getItem(), 1);
            }

            playSound(type.getMiningSound(), 1, 1);

            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) != 0) {
                this.setLimpetType(1);
                return false;
            } else {
                this.setLimpetType(0);
                this.setScaredTicks(0);
            }
        } else if (source.getEntity() instanceof LivingEntity && amount < 12 && !this.level.isClientSide && type.getId() > 0) {

            playSound(SoundEvents.SHIELD_BLOCK, 1, 1);
            this.setScaredTicks(300);
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isScared()) {
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
