package com.ninni.species.entity;

import com.google.common.collect.Lists;
import com.ninni.species.init.SpeciesBlocks;
import com.ninni.species.block.entity.BirtDwellingBlockEntity;
import com.ninni.species.init.SpeciesBlockEntities;
import com.ninni.species.entity.ai.goal.BirtCommunicatingGoal;
import com.ninni.species.entity.ai.goal.SendMessageTicksGoal;
import com.ninni.species.init.SpeciesSoundEvents;
import com.ninni.species.init.SpeciesTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BirtEntity extends Animal implements NeutralMob, FlyingAnimal {
    public final AnimationState flyingAnimationState = new AnimationState();
    public float flapProgress;
    public float maxWingDeviation;
    public float prevMaxWingDeviation;
    public float prevFlapProgress;
    public float flap = 1;
    public int antennaTicks;
    private float flapSpeed = 1.0f;
    public int groundTicks;
    public int messageTicks = 0;
    private static final EntityDataAccessor<Byte> BIRT_FLAGS = SynchedEntityData.defineId(BirtEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANGER = SynchedEntityData.defineId(BirtEntity.class, EntityDataSerializers.INT);
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID angryAt;
    private int cannotEnterDwellingTicks;
    int ticksLeftToFindDwelling;
    @Nullable
    BlockPos dwellingPos;
    BirtEntity.MoveToDwellingGoal moveToDwellingGoal;

    public BirtEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new FlyingMoveControl(this, 20, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1, false));
        this.goalSelector.addGoal(0, new FindDwellingGoal());
        this.goalSelector.addGoal(1, new EnterDwellingGoal());
        this.moveToDwellingGoal = new MoveToDwellingGoal();
        this.goalSelector.addGoal(2, this.moveToDwellingGoal);
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, Ingredient.of(ItemTags.FLOWERS), false));
        this.goalSelector.addGoal(4, new SendMessageTicksGoal(this));
        this.goalSelector.addGoal(5, new BirtCommunicatingGoal(this));
        this.goalSelector.addGoal(8, new BirtWanderAroundGoal());
        this.goalSelector.addGoal(9, new BirtLookAroundGoal());
    }

    public static AttributeSupplier.Builder createBirtAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.FLYING_SPEED, 0.6f)
                .add(Attributes.ATTACK_DAMAGE, 2);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BIRT_FLAGS, (byte)0);
        this.entityData.define(ANGER, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            Pose entityPose = this.getPose();
            if (entityPose == Pose.FALL_FLYING) {
                this.flyingAnimationState.start(this.age);
            } else {
                this.flyingAnimationState.stop();
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("canMessage", this.messageTicks);
        compoundTag.putInt("CannotEnterDwellingTicks", this.cannotEnterDwellingTicks);
        if (this.hasDwelling()) {
            assert this.getDwellingPos() != null;
            compoundTag.put("DwellingPos", NbtUtils.writeBlockPos(this.getDwellingPos()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.messageTicks = compoundTag.getInt("canMessage");
        this.cannotEnterDwellingTicks = compoundTag.getInt("CannotEnterDwellingTicks");
        if (compoundTag.contains("DwellingPos")) {
            this.dwellingPos = NbtUtils.readBlockPos(compoundTag.getCompound("DwellingPos"));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vec3d = this.getDeltaMovement();
        if (this.antennaTicks > 0) {
            this.antennaTicks--;
        }
        if (!this.onGround() && vec3d.y < 0.0 && this.getTarget() == null) {
            this.setDeltaMovement(vec3d.multiply(1.0, 0.6, 1.0));
        }
        if (this.isFlying()) {
            this.groundTicks = random.nextInt(300) + 20;
            this.setPose(Pose.FALL_FLYING);
        }
        else {
            this.groundTicks--;
            this.setPose(Pose.STANDING);
        }
        if (this.cannotEnterDwellingTicks > 0) {
            --this.cannotEnterDwellingTicks;
        }

        if (this.ticksLeftToFindDwelling > 0) {
            --this.ticksLeftToFindDwelling;
        }
        boolean bl = this.isAngry() && this.getTarget() != null && this.getTarget().distanceToSqr(this) < 4.0;
        this.setNearTarget(bl);
        if (this.age % 20 == 0 && !this.isDwellingValid()) {
            this.dwellingPos = null;
        }

        if (messageTicks > 0) this.messageTicks--;
        this.flapWings();
    }

    void startMovingTo(BlockPos pos) {
        Vec3 vec3d = Vec3.atBottomCenterOf(pos);
        int i = 0;
        BlockPos blockPos = this.blockPosition();
        int j = (int)vec3d.y - blockPos.getY();
        if (j > 2) {
            i = 4;
        } else if (j < -2) {
            i = -4;
        }

        int k = 6;
        int l = 8;
        int m = blockPos.distManhattan(pos);
        if (m < 15) {
            k = m / 2;
            l = m / 2;
        }

        Vec3 vec3d2 = AirRandomPos.getPosTowards(this, k, l, i, vec3d, 0.3141592741012573);
        if (vec3d2 != null) {
            this.navigation.setMaxVisitedNodesMultiplier(0.5F);
            this.navigation.moveTo(vec3d2.x, vec3d2.y, vec3d2.z, 1.0);
        }
    }
    
    @Nullable
    public BirtEntity findReciever() {
        List<? extends BirtEntity> list = this.level().getNearbyEntities(BirtEntity.class, TargetingConditions.DEFAULT, this, this.getBoundingBox().inflate(8.0));
        double d = Double.MAX_VALUE;
        BirtEntity birt = null;
        for (BirtEntity birt2 : list) {
            if (!(this.distanceToSqr(birt2) < d)) continue;
            birt = birt2;
            d = this.distanceToSqr(birt2);
        }
        return birt;
    }

    public boolean canSendMessage() {
        return this.messageTicks > 0 && this.getTarget() == null;
    }

    public void setMessageTicks(int messageTicks) {
        this.messageTicks = messageTicks;
    }

    public void resetMessageTicks() {
        this.messageTicks = 0;
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 10) {
            this.antennaTicks = 60;
        } else {
            super.handleEntityEvent(status);
        }
    }

    public void sendMessage(ServerLevel world, BirtEntity other) {
        this.resetMessageTicks();
        other.resetMessageTicks();

        EntityPositionSource positionSource = new EntityPositionSource(this, 0.75F);

        world.playSound(null, other.blockPosition(), SpeciesSoundEvents.ENTITY_BIRT_MESSAGE.get(), SoundSource.NEUTRAL, 1,  0.6f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        world.sendParticles(new VibrationParticleOption(positionSource, 20), other.getX(), other.getY() + 0.75, other.getZ(), 0, 0, 0, 0, 0);
    }

    private void flapWings() {
        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation += (float)(this.onGround() || this.isPassenger() ? -1 : 4) * 0.3f;
        this.maxWingDeviation = Mth.clamp(this.maxWingDeviation, 0.0f, 1.0f);
        if (!this.onGround() && this.flapSpeed < 1.0f) {
            this.flapSpeed = 1.0f;
        }
        this.flapSpeed *= 0.9f;
        Vec3 vec3d = this.getDeltaMovement();
        if (!this.onGround() && vec3d.y < 0.0) {
            this.setDeltaMovement(vec3d.multiply(1.0, 0.6, 1.0));
        }
        this.flapProgress += this.flapSpeed * 2.0f;
    }



    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.flap;
    }

    @Override
    protected void onFlap() {
        this.playSound(SpeciesSoundEvents.ENTITY_BIRT_FLY.get(), 0.15f, 1.0f);
        this.flap = this.flyDist + this.maxWingDeviation / 2.0f;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(ANGER);
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.entityData.set(ANGER, angerTime);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.angryAt;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }
    
    boolean canEnterDwelling() {
        if (this.cannotEnterDwellingTicks <= 0 && this.getTarget() == null) return this.level().isRaining() || this.level().isNight();
        else return false;
    }

    public void setCannotEnterDwellingTicks(int cannotEnterDwellingTicks) {
        this.cannotEnterDwellingTicks = cannotEnterDwellingTicks;
    }

    private boolean doesDwellingHaveSpace(BlockPos pos) {
        BlockEntity blockEntity = this.level().getBlockEntity(pos);
        if (blockEntity instanceof BirtDwellingBlockEntity) {
            return !((BirtDwellingBlockEntity)blockEntity).isFullOfBirts();
        } else {
            return false;
        }
    }

    boolean isDwellingValid() {
        if (!this.hasDwelling()) {
            return false;
        } else {
            BlockEntity blockEntity = this.level().getBlockEntity(this.dwellingPos);
            return blockEntity != null && blockEntity.getType() == SpeciesBlockEntities.BIRT_DWELLING.get();
        }
    }

    private void setNearTarget(boolean nearTarget) {
        this.setBirtFlag(2, nearTarget);
    }

    boolean isTooFar(BlockPos pos) {
        return !this.isWithinDistance(pos, 32);
    }

    private void setBirtFlag(int bit, boolean value) {
        if (value) {
            this.entityData.set(BIRT_FLAGS, (byte)(this.entityData.get(BIRT_FLAGS) | bit));
        } else {
            this.entityData.set(BIRT_FLAGS, (byte)(this.entityData.get(BIRT_FLAGS) & ~bit));
        }

    }

    @VisibleForDebug
    public boolean hasDwelling() {
        return this.dwellingPos != null;
    }

    @Nullable
    @VisibleForDebug
    public BlockPos getDwellingPos() {
        return this.dwellingPos;
    }

    boolean isWithinDistance(BlockPos pos, int distance) {
        return pos.closerThan(this.blockPosition(), distance);
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        return levelReader.getBlockState(blockPos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public boolean causeFallDamage(float f, float g, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingPathNavigation = new FlyingPathNavigation(this, level){
            @Override
            public boolean isStableDestination(BlockPos blockPos) {
                return !level().getBlockState(blockPos.below()).isAir();
            }
        };
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(false);
        flyingPathNavigation.setCanPassDoors(true);
        return flyingPathNavigation;
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<? extends Animal> type, LevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return false;
    }
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SpeciesSoundEvents.ENTITY_BIRT_IDLE.get();
    }
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.ENTITY_BIRT_HURT.get();
    }
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.ENTITY_BIRT_DEATH.get();
    }

    class BirtWanderAroundGoal extends Goal {

        BirtWanderAroundGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (BirtEntity.this.groundTicks < 0) return true;
            else if (BirtEntity.this.isFlying()) return BirtEntity.this.navigation.isDone() && BirtEntity.this.random.nextInt(10) == 0;
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return BirtEntity.this.navigation.isInProgress();
        }

        @Override
        public void start() {
            Vec3 vec3d = this.getRandomLocation();
            if (vec3d != null) {
                BirtEntity.this.navigation.moveTo(BirtEntity.this.navigation.createPath(BlockPos.containing(vec3d), 1), 1.0);
            }
        }
        
        @Nullable
        private Vec3 getRandomLocation() {
            Vec3 vec3d2;
            assert BirtEntity.this.dwellingPos != null;
            if (BirtEntity.this.isDwellingValid() && !BirtEntity.this.isWithinDistance(BirtEntity.this.dwellingPos, 22)) {
                Vec3 vec3d = Vec3.atCenterOf(BirtEntity.this.dwellingPos);
                vec3d2 = vec3d.subtract(BirtEntity.this.position()).normalize();
            } else {
                vec3d2 = BirtEntity.this.getViewVector(0.0F);
            }

            Vec3 vec3d3 = HoverRandomPos.getPos(BirtEntity.this, 12, 5, vec3d2.x, vec3d2.z, 1.5707964F, 3, 1);
            return vec3d3 != null ? vec3d3 : AirAndWaterRandomPos.getPos(BirtEntity.this, 12, 2, -2, vec3d2.x, vec3d2.z, 1.5707963705062866);
        }
    }

    class BirtLookAroundGoal extends RandomLookAroundGoal {

        BirtLookAroundGoal() {
            super(BirtEntity.this);
        }

        @Override
        public boolean canUse() {
            return BirtEntity.this.onGround() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return BirtEntity.this.onGround() && super.canContinueToUse();
        }
    }
    
    class EnterDwellingGoal extends BirtEntity.NotAngryGoal {
        EnterDwellingGoal() {
            super();
        }

        @Override
        public boolean canBirtStart() {
            if (BirtEntity.this.hasDwelling() && BirtEntity.this.canEnterDwelling()) {
                assert BirtEntity.this.dwellingPos != null;
                if (BirtEntity.this.dwellingPos.closerToCenterThan(BirtEntity.this.position(), 2.0)) {
                    BlockEntity blockEntity = BirtEntity.this.level().getBlockEntity(BirtEntity.this.dwellingPos);
                    if (blockEntity instanceof BirtDwellingBlockEntity blockEntity1) {
                        if (!blockEntity1.isFullOfBirts()) {
                            return true;
                        }

                        BirtEntity.this.dwellingPos = null;
                    }
                }
            }

            return false;
        }

        @Override
        public boolean canBirtContinue() {
            return false;
        }

        @Override
        public void start() {
            BlockEntity blockEntity = BirtEntity.this.level().getBlockEntity(BirtEntity.this.dwellingPos);
            if (blockEntity instanceof BirtDwellingBlockEntity birtDwellingBlockEntity) {
                birtDwellingBlockEntity.tryEnterDwelling(BirtEntity.this);
            }

        }
    }

    private class FindDwellingGoal extends BirtEntity.NotAngryGoal {
        FindDwellingGoal() {
            super();
        }

        @Override
        public boolean canBirtStart() {
            return BirtEntity.this.ticksLeftToFindDwelling == 0 && !BirtEntity.this.hasDwelling() && BirtEntity.this.canEnterDwelling();
        }

        @Override
        public boolean canBirtContinue() {
            return false;
        }

        @Override
        public void start() {
            BirtEntity.this.ticksLeftToFindDwelling = 200;
            List<BlockPos> list = this.getNearbyFreeDwellings();
            if (!list.isEmpty()) {
                Iterator<BlockPos> var2 = list.iterator();

                BlockPos blockPos;
                do {
                    if (!var2.hasNext()) {
                        BirtEntity.this.moveToDwellingGoal.clearPossibleDwellings();
                        BirtEntity.this.dwellingPos = list.get(0);
                        return;
                    }

                    blockPos = var2.next();
                } while(BirtEntity.this.moveToDwellingGoal.isPossibleDwelling(blockPos));

                BirtEntity.this.dwellingPos = blockPos;
            }
        }

        private List<BlockPos> getNearbyFreeDwellings() {
            BlockPos blockPos = BirtEntity.this.blockPosition();
            PoiManager pointOfInterestStorage = ((ServerLevel)BirtEntity.this.level()).getPoiManager();
            Stream<PoiRecord> stream = pointOfInterestStorage.getInRange((poiType) -> poiType.is(SpeciesTags.BIRT_HOME), blockPos, 20, PoiManager.Occupancy.ANY);
            return stream.map(PoiRecord::getPos).filter(BirtEntity.this::doesDwellingHaveSpace).sorted(Comparator.comparingDouble((blockPos2) -> blockPos2.distSqr(blockPos))).collect(Collectors.toList());
        }
    }

    @VisibleForDebug
    public class MoveToDwellingGoal extends BirtEntity.NotAngryGoal {
        int ticks;
        final List<BlockPos> possibleDwellings;
        @Nullable
        private Path path;
        private int ticksUntilLost;

        MoveToDwellingGoal() {
            super();
            this.ticks = BirtEntity.this.level().random.nextInt(10);
            this.possibleDwellings = Lists.newArrayList();
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canBirtStart() {
            return BirtEntity.this.dwellingPos != null && !BirtEntity.this.hasRestriction() && BirtEntity.this.canEnterDwelling() && !this.isCloseEnough(BirtEntity.this.dwellingPos) && BirtEntity.this.level().getBlockState(BirtEntity.this.dwellingPos).is(SpeciesBlocks.BIRT_DWELLING.get());
        }

        @Override
        public boolean canBirtContinue() {
            return this.canBirtStart();
        }

        @Override
        public void start() {
            this.ticks = 0;
            this.ticksUntilLost = 0;
            super.start();
        }

        @Override
        public void stop() {
            this.ticks = 0;
            this.ticksUntilLost = 0;
            BirtEntity.this.navigation.stop();
            BirtEntity.this.navigation.resetMaxVisitedNodesMultiplier();
        }

        @Override
        public void tick() {
            if (BirtEntity.this.dwellingPos != null) {
                ++this.ticks;
                if (this.ticks > this.adjustedTickDelay(600)) {
                    this.makeChosenDwellingPossibleDwelling();
                } else if (!BirtEntity.this.navigation.isInProgress()) {
                    if (!BirtEntity.this.isWithinDistance(BirtEntity.this.dwellingPos, 16)) {
                        if (BirtEntity.this.isTooFar(BirtEntity.this.dwellingPos)) {
                            this.setLost();
                        } else {
                            BirtEntity.this.startMovingTo(BirtEntity.this.dwellingPos);
                        }
                    } else {
                        boolean bl = this.startMovingToFar(BirtEntity.this.dwellingPos);
                        if (!bl) {
                            this.makeChosenDwellingPossibleDwelling();
                        } else if (this.path != null && Objects.requireNonNull(BirtEntity.this.navigation.getPath()).sameAs(this.path)) {
                            ++this.ticksUntilLost;
                            if (this.ticksUntilLost > 60) {
                                this.setLost();
                                this.ticksUntilLost = 0;
                            }
                        } else {
                            this.path = BirtEntity.this.navigation.getPath();
                        }

                    }
                }
            }
        }

        private boolean startMovingToFar(BlockPos pos) {
            BirtEntity.this.navigation.setMaxVisitedNodesMultiplier(10.0F);
            BirtEntity.this.navigation.moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.0);
            return BirtEntity.this.navigation.getPath() != null && BirtEntity.this.navigation.getPath().canReach();
        }

        boolean isPossibleDwelling(BlockPos pos) {
            return this.possibleDwellings.contains(pos);
        }

        private void addPossibleDwelling(BlockPos pos) {
            this.possibleDwellings.add(pos);

            while(this.possibleDwellings.size() > 3) {
                this.possibleDwellings.remove(0);
            }

        }

        void clearPossibleDwellings() {
            this.possibleDwellings.clear();
        }

        private void makeChosenDwellingPossibleDwelling() {
            if (BirtEntity.this.dwellingPos != null) {
                this.addPossibleDwelling(BirtEntity.this.dwellingPos);
            }

            this.setLost();
        }

        private void setLost() {
            BirtEntity.this.dwellingPos = null;
            BirtEntity.this.ticksLeftToFindDwelling = 200;
        }

        private boolean isCloseEnough(BlockPos pos) {
            if (BirtEntity.this.isWithinDistance(pos, 2)) {
                return true;
            } else {
                Path path = BirtEntity.this.navigation.getPath();
                return path != null && path.getTarget().equals(pos) && path.canReach() && path.isDone();
            }
        }
    }

    private abstract class NotAngryGoal extends Goal {
        NotAngryGoal() {
        }

        public abstract boolean canBirtStart();

        public abstract boolean canBirtContinue();

        @Override
        public boolean canUse() {
            return this.canBirtStart() && !BirtEntity.this.isAngry();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canBirtContinue() && !BirtEntity.this.isAngry();
        }
    }
    
}
