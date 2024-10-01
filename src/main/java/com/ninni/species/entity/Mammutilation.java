package com.ninni.species.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ninni.species.entity.pose.SpeciesPose;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Mammutilation extends PathfinderMob {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState coughAnimationState = new AnimationState();
    public final AnimationState howlAnimationState = new AnimationState();
    public static final EntityDataAccessor<Integer> COUGH_COOLDOWN = SynchedEntityData.defineId(Mammutilation.class, EntityDataSerializers.INT);
    private static final Map<Block, SoundEvent> SOUNDS_BY_EGG = Util.make(Maps.newHashMap(), map -> {
        map.put(Blocks.TURTLE_EGG, SoundEvents.TURTLE_EGG_CRACK);
        map.put(Blocks.SNIFFER_EGG, SoundEvents.SNIFFER_EGG_CRACK);
        map.put(SpeciesBlocks.WRAPTOR_EGG, SpeciesSoundEvents.WRAPTOR_EGG_CRACK);
        map.put(SpeciesBlocks.PETRIFIED_EGG, SpeciesSoundEvents.PETRIFIED_EGG_CRACK);
        map.put(SpeciesBlocks.SPRINGLING_EGG, SpeciesSoundEvents.SPRINGLING_EGG_CRACK);
    });
    private int coughTimer;
    private int hatchCooldown;
    private int howlCooldown;
    private int howlTimer;
    private int idleAnimationTimeout = 0;

    public Mammutilation(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.75D));
        this.goalSelector.addGoal(2, new CoughGoal(this));
        this.goalSelector.addGoal(2, new HowlAtMoonGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.15).add(Attributes.KNOCKBACK_RESISTANCE, 0.6).add(Attributes.ATTACK_DAMAGE, 0.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.hatchCooldown > 0) {
                this.hatchCooldown--;
            }
            if (this.coughTimer > 0) {
                this.coughTimer--;

                if (this.coughTimer == 15) {
                    BlockPos blockPos = this.blockPosition();
                    final float angle = (0.0174532925F * this.yBodyRot);
                    final double headX = 3F * this.getScale() * Mth.sin(Mth.PI + angle);
                    final double headZ = 3F * this.getScale() * Mth.cos(angle);

                    Vec3 shootingVec = this.getLookAngle().scale(2).multiply(0.05D, 1.0D, 0.05D);

                    MammutilationIchor ichor = new MammutilationIchor(this.level(), (double) blockPos.getX() + headX, blockPos.getY() + this.getEyeHeight() + 0.35f, (double) blockPos.getZ() + headZ);
                    double d = shootingVec.x();
                    double e = shootingVec.y();
                    double g = shootingVec.z();
                    double h = Math.sqrt(d * d + g * g);
                    ichor.shoot(d, e + h * (double)0.1f, g, 0.8f, 14 - this.level().getDifficulty().getId() * 4);
                    this.level().addFreshEntity(ichor);
                    this.addDeltaMovement(new Vec3(0, 0.25D, 0));
                    this.addDeltaMovement(this.getLookAngle().scale(2.0D).multiply(-0.5D, 0, -0.5D));

                }
            } else {
                if (this.getPose() == SpeciesPose.COUGHING.get()) this.setPose(Pose.STANDING);
            }
            if (this.howlCooldown > 0) {
                this.howlCooldown--;
            }
            if (this.howlTimer > 0) {
                this.howlTimer--;
            } else {
                if (this.getPose() == SpeciesPose.HOWLING.get()) this.setPose(Pose.STANDING);
            }
            if (this.hatchCooldown == 0) {
                this.getAllEggPositions().stream().filter(blockPos -> this.level().getBlockState(blockPos).hasProperty(BlockStateProperties.HATCH)).forEach(this::handleEggHatching);
            }
        }
    }

    @Override
    public int getMaxHeadXRot() {
        return 20;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COUGH_COOLDOWN, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.hatchCooldown = compoundTag.getInt("HatchCooldown");
        this.howlCooldown = compoundTag.getInt("HowlCooldown");
        this.coughTimer = compoundTag.getInt("CoughTimer");
        this.setCoughCooldown(compoundTag.getInt("CoughCooldown"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("HatchCooldown", this.hatchCooldown);
        compoundTag.putInt("HowlCooldown", this.howlCooldown);
        compoundTag.putInt("CoughTimer", this.coughTimer);
        compoundTag.putInt("CoughCooldown", this.getCoughCooldown());
    }

    @Override
    public int getAmbientSoundInterval() {
        return 20 * 10;
    }

    public int getCoughCooldown() {
        return this.entityData.get(COUGH_COOLDOWN);
    }
    public void setCoughCooldown(int cooldown) {
        this.entityData.set(COUGH_COOLDOWN, cooldown);
    }
    public void coughCooldown() {
        this.entityData.set(COUGH_COOLDOWN, 30 * 20 + random.nextInt(60 * 2 * 20));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getCoughCooldown() > 0) this.setCoughCooldown(this.getCoughCooldown()-1);
        if ((this.level()).isClientSide()) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout == 0) {
            this.idleAnimationTimeout = 160;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == SpeciesPose.COUGHING.get()) this.coughAnimationState.start(this.tickCount);
            if (this.getPose() == SpeciesPose.HOWLING.get()) this.howlAnimationState.start(this.tickCount);
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    private void handleEggHatching(BlockPos blockPos) {
        BlockState blockState = this.level().getBlockState(blockPos);
        int hatch = blockState.getValue(BlockStateProperties.HATCH);
        this.level().setBlock(blockPos, blockState.setValue(BlockStateProperties.HATCH, hatch + 1), 2);
        if (SOUNDS_BY_EGG.containsKey(blockState.getBlock())) {
            this.level().playSound(null, blockPos, SOUNDS_BY_EGG.get(blockState.getBlock()), SoundSource.BLOCKS, 0.7f, 0.9f + this.getRandom().nextFloat() * 0.2f);
        }
        this.level().levelEvent(3009, blockPos, 0);
        this.hatchCooldown = UniformInt.of(6000, 12000).sample(this.getRandom());
    }

    public List<BlockPos> getAllEggPositions() {
        List<BlockPos> poses = Lists.newArrayList();
        int radius = 8;
        int yRange = radius / 2;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -yRange; y <= yRange; y++) {
                    BlockPos blockPos = BlockPos.containing(this.getX() + x, this.getY() + y, this.getZ() + z);
                    BlockState blockState = this.level().getBlockState(blockPos);
                    if (blockState.hasProperty(BlockStateProperties.HATCH) && blockState.getValue(BlockStateProperties.HATCH) < 2) {
                        poses.add(blockPos);
                        this.handleTallEggs(blockState, blockPos, poses);
                    }
                }
            }
        }
        return poses;
    }

    private void handleTallEggs(BlockState blockState, BlockPos blockPos, List<BlockPos> poses) {
        boolean isDoubleBlockTall = blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
        Arrays.stream(Direction.values()).filter(direction -> direction.getAxis() == Direction.Axis.Y).forEach(direction -> {
            BlockState relativeState = this.level().getBlockState(blockPos.relative(direction));
            DoubleBlockHalf doubleBlockHalf = direction == Direction.DOWN ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER;
            boolean hasDoubleBlockInRelativeState = relativeState.is(blockState.getBlock()) && relativeState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == doubleBlockHalf && isDoubleBlockTall;
            if (hasDoubleBlockInRelativeState) {
                poses.add(blockPos.relative(direction));
            }
        });
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.hatchCooldown = UniformInt.of(6000, 12000).sample(this.getRandom());
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.MAMMUTILATION_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SpeciesSoundEvents.MAMMUTILATION_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.getName().getString().equalsIgnoreCase("mammutiful")) {
            return SpeciesSoundEvents.MAMMUTIFUL_IDLE;
        } else return SpeciesSoundEvents.MAMMUTILATION_IDLE;
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Mammutilation> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return false;
    }

    public static class CoughGoal extends Goal{
        protected final Mammutilation mammutilation;

        public CoughGoal(Mammutilation mammutilation) {
            this.mammutilation = mammutilation;
        }

        @Override
        public void start() {
            this.mammutilation.coughCooldown();
            this.mammutilation.coughTimer = 25;
            this.mammutilation.setPose(SpeciesPose.COUGHING.get());
            this.mammutilation.playSound(SpeciesSoundEvents.MAMMUTILATION_COUGH, 1,1);
        }

        @Override
        public boolean canUse() {
            return this.mammutilation.getCoughCooldown() == 0 && this.mammutilation.getPose() != SpeciesPose.HOWLING.get();
        }
    }

    public static class HowlAtMoonGoal extends Goal{
        protected final Mammutilation mammutilation;

        public HowlAtMoonGoal(Mammutilation mammutilation) {
            this.mammutilation = mammutilation;
        }

        @Override
        public void start() {
            if (this.mammutilation.getName().getString().equalsIgnoreCase("mammutiful")) {
                this.mammutilation.playSound(SpeciesSoundEvents.MAMMUTIFUL_HOWL);
            } else this.mammutilation.playSound(SpeciesSoundEvents.MAMMUTILATION_HOWL);
            this.mammutilation.howlCooldown = 1000;
            this.mammutilation.howlTimer = 20 * 4;
            this.mammutilation.setPose(SpeciesPose.HOWLING.get());
        }

        @Override
        public boolean canUse() {
            return this.mammutilation.level().isNight() && this.mammutilation.howlCooldown == 0 && this.mammutilation.getPose() != SpeciesPose.COUGHING.get();
        }
    }
}
