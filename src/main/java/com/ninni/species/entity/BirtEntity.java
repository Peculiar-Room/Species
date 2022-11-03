package com.ninni.species.entity;

import com.google.common.collect.Lists;
import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.block.entity.BirtDwellingBlockEntity;
import com.ninni.species.block.entity.SpeciesBlockEntities;
import com.ninni.species.entity.ai.goal.BirtCommunicatingGoal;
import com.ninni.species.entity.ai.goal.SendMessageTicksGoal;
import com.ninni.species.sound.SpeciesSoundEvents;
import com.ninni.species.tag.SpeciesTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.NoWaterTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BirtEntity extends AnimalEntity implements Angerable, Flutterer {
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
    private static final TrackedData<Byte> BIRT_FLAGS = DataTracker.registerData(BirtEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> ANGER = DataTracker.registerData(BirtEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    @Nullable
    private UUID angryAt;
    private int cannotEnterDwellingTicks;
    int ticksLeftToFindDwelling;
    @Nullable
    BlockPos dwellingPos;
    BirtEntity.MoveToDwellingGoal moveToDwellingGoal;

    public BirtEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new MeleeAttackGoal(this, 1, false));
        this.goalSelector.add(0, new FindDwellingGoal());
        this.goalSelector.add(1, new EnterDwellingGoal());
        this.moveToDwellingGoal = new MoveToDwellingGoal();
        this.goalSelector.add(2, this.moveToDwellingGoal);
        this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.fromTag(ItemTags.FLOWERS), false));
        this.goalSelector.add(4, new SendMessageTicksGoal(this));
        this.goalSelector.add(5, new BirtCommunicatingGoal(this));
        this.goalSelector.add(8, new BirtWanderAroundGoal());
        this.goalSelector.add(9, new BirtLookAroundGoal());
    }

    public static DefaultAttributeContainer.Builder createBirtAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BIRT_FLAGS, (byte)0);
        this.dataTracker.startTracking(ANGER, 0);
    }
    
    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            EntityPose entityPose = this.getPose();
            if (entityPose == EntityPose.FALL_FLYING) {
                this.flyingAnimationState.start(this.age);
            } else {
                this.flyingAnimationState.stop();
            }
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("canMessage", this.messageTicks);
        nbt.putInt("CannotEnterDwellingTicks", this.cannotEnterDwellingTicks);
        if (this.hasDwelling()) {
            assert this.getDwellingPos() != null;
            nbt.put("DwellingPos", NbtHelper.fromBlockPos(this.getDwellingPos()));
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.messageTicks = nbt.getInt("canMessage");
        this.cannotEnterDwellingTicks = nbt.getInt("CannotEnterDwellingTicks");
        if (nbt.contains("DwellingPos")) {
            this.dwellingPos = NbtHelper.toBlockPos(nbt.getCompound("DwellingPos"));
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        Vec3d vec3d = this.getVelocity();
        if (this.antennaTicks > 0) {
            this.antennaTicks--;
        }
        if (!this.onGround && vec3d.y < 0.0 && this.getTarget() == null) {
            this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
        }
        if (this.isInAir()) {
            this.groundTicks = random.nextInt(300) + 20;
            this.setPose(EntityPose.FALL_FLYING);
        }
        else {
            this.groundTicks--;
            this.setPose(EntityPose.STANDING);
        }
        if (this.cannotEnterDwellingTicks > 0) {
            --this.cannotEnterDwellingTicks;
        }

        if (this.ticksLeftToFindDwelling > 0) {
            --this.ticksLeftToFindDwelling;
        }
        boolean bl = this.hasAngerTime() && this.getTarget() != null && this.getTarget().squaredDistanceTo(this) < 4.0;
        this.setNearTarget(bl);
        if (this.age % 20 == 0 && !this.isDwellingValid()) {
            this.dwellingPos = null;
        }
        
        if (messageTicks > 0) this.messageTicks--;
        this.flapWings();
    }

    void startMovingTo(BlockPos pos) {
        Vec3d vec3d = Vec3d.ofBottomCenter(pos);
        int i = 0;
        BlockPos blockPos = this.getBlockPos();
        int j = (int)vec3d.y - blockPos.getY();
        if (j > 2) {
            i = 4;
        } else if (j < -2) {
            i = -4;
        }

        int k = 6;
        int l = 8;
        int m = blockPos.getManhattanDistance(pos);
        if (m < 15) {
            k = m / 2;
            l = m / 2;
        }

        Vec3d vec3d2 = NoWaterTargeting.find(this, k, l, i, vec3d, 0.3141592741012573);
        if (vec3d2 != null) {
            this.navigation.setRangeMultiplier(0.5F);
            this.navigation.startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, 1.0);
        }
    }
    
    @Nullable
    public BirtEntity findReciever() {
        List<? extends BirtEntity> list = this.world.getTargets(BirtEntity.class, TargetPredicate.DEFAULT, this, this.getBoundingBox().expand(8.0));
        double d = Double.MAX_VALUE;
        BirtEntity birt = null;
        for (BirtEntity birt2 : list) {
            if (!(this.squaredDistanceTo(birt2) < d)) continue;
            birt = birt2;
            d = this.squaredDistanceTo(birt2);
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
    public void handleStatus(byte status) {
        if (status == 10) {
            this.antennaTicks = 60;
        }
        else {
            super.handleStatus(status);
        }
    }

    public void sendMessage(ServerWorld world, BirtEntity other) {
        this.resetMessageTicks();
        other.resetMessageTicks();

        PositionSource positionSource = new PositionSource() {
            @Override
            public Optional<Vec3d> getPos(World world) {
                return Optional.of(new Vec3d(BirtEntity.this.getX(), BirtEntity.this.getY() + 0.75, BirtEntity.this.getZ()));
            }

            @Override
            public PositionSourceType<?> getType() {
                return PositionSourceType.ENTITY;
            }
        };

        world.playSound(null, other.getBlockPos(), SpeciesSoundEvents.ENTITY_BIRT_MESSAGE, SoundCategory.NEUTRAL, 1,  0.6f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        world.spawnParticles(new VibrationParticleEffect(positionSource, 20), other.getX(), other.getY() + 0.75, other.getZ(), 0, 0, 0, 0, 0);
    }

    private void flapWings() {
        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation += (float)(this.onGround || this.hasVehicle() ? -1 : 4) * 0.3f;
        this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0f, 1.0f);
        if (!this.onGround && this.flapSpeed < 1.0f) {
            this.flapSpeed = 1.0f;
        }
        this.flapSpeed *= 0.9f;
        Vec3d vec3d = this.getVelocity();
        if (!this.onGround && vec3d.y < 0.0) {
            this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
        }
        this.flapProgress += this.flapSpeed * 2.0f;
    }

    @Override
    protected boolean hasWings() {
        return this.speed > this.flap;
    }

    @Override
    protected void addFlapEffects() {
        this.playSound(SpeciesSoundEvents.ENTITY_BIRT_FLY, 0.15f, 1.0f);
        this.flap = this.speed + this.maxWingDeviation / 2.0f;
    }

    @Override
    public int getAngerTime() {
        return this.dataTracker.get(ANGER);
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.dataTracker.set(ANGER, angerTime);
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }
    
    boolean canEnterDwelling() {
        if (this.cannotEnterDwellingTicks <= 0 && this.getTarget() == null) return this.world.isRaining() || this.world.isNight();
        else return false;
    }

    public void setCannotEnterDwellingTicks(int cannotEnterDwellingTicks) {
        this.cannotEnterDwellingTicks = cannotEnterDwellingTicks;
    }

    private boolean doesDwellingHaveSpace(BlockPos pos) {
        BlockEntity blockEntity = this.world.getBlockEntity(pos);
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
            BlockEntity blockEntity = this.world.getBlockEntity(this.dwellingPos);
            return blockEntity != null && blockEntity.getType() == SpeciesBlockEntities.BIRT_DWELLING;
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
            this.dataTracker.set(BIRT_FLAGS, (byte)(this.dataTracker.get(BIRT_FLAGS) | bit));
        } else {
            this.dataTracker.set(BIRT_FLAGS, (byte)(this.dataTracker.get(BIRT_FLAGS) & ~bit));
        }

    }

    @Debug
    public boolean hasDwelling() {
        return this.dwellingPos != null;
    }

    @Nullable
    @Debug
    public BlockPos getDwellingPos() {
        return this.dwellingPos;
    }

    boolean isWithinDistance(BlockPos pos, int distance) {
        return pos.isWithinDistance(this.getBlockPos(), distance);
    }
    
    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return world.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    public boolean isInAir() {
        return !this.onGround;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }
    
    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world) {
            @Override
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }
        };

        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(false);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<? extends PassiveEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        return false;
    }
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SpeciesSoundEvents.ENTITY_BIRT_IDLE;
    }
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.ENTITY_BIRT_HURT;
    }
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.ENTITY_BIRT_DEATH;
    }

    class BirtWanderAroundGoal extends Goal {

        BirtWanderAroundGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (BirtEntity.this.groundTicks < 0) return true;
            else if (BirtEntity.this.isInAir()) return BirtEntity.this.navigation.isIdle() && BirtEntity.this.random.nextInt(10) == 0;
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return BirtEntity.this.navigation.isFollowingPath();
        }

        @Override
        public void start() {
            Vec3d vec3d = this.getRandomLocation();
            if (vec3d != null) {
                BirtEntity.this.navigation.startMovingAlong(BirtEntity.this.navigation.findPathTo(new BlockPos(vec3d), 1), 1.0);
            }
        }
        
        @Nullable
        private Vec3d getRandomLocation() {
            Vec3d vec3d2;
            assert BirtEntity.this.dwellingPos != null;
            if (BirtEntity.this.isDwellingValid() && !BirtEntity.this.isWithinDistance(BirtEntity.this.dwellingPos, 22)) {
                Vec3d vec3d = Vec3d.ofCenter(BirtEntity.this.dwellingPos);
                vec3d2 = vec3d.subtract(BirtEntity.this.getPos()).normalize();
            } else {
                vec3d2 = BirtEntity.this.getRotationVec(0.0F);
            }

            Vec3d vec3d3 = AboveGroundTargeting.find(BirtEntity.this, 12, 5, vec3d2.x, vec3d2.z, 1.5707964F, 3, 1);
            return vec3d3 != null ? vec3d3 : NoPenaltySolidTargeting.find(BirtEntity.this, 12, 2, -2, vec3d2.x, vec3d2.z, 1.5707963705062866);
        }
    }

    class BirtLookAroundGoal extends LookAroundGoal {

        BirtLookAroundGoal() {
            super(BirtEntity.this);
        }

        @Override
        public boolean canStart() {
            return BirtEntity.this.isOnGround() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return BirtEntity.this.isOnGround() && super.shouldContinue();
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
                if (BirtEntity.this.dwellingPos.isWithinDistance(BirtEntity.this.getPos(), 2.0)) {
                    BlockEntity blockEntity = BirtEntity.this.world.getBlockEntity(BirtEntity.this.dwellingPos);
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
            BlockEntity blockEntity = BirtEntity.this.world.getBlockEntity(BirtEntity.this.dwellingPos);
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
            BlockPos blockPos = BirtEntity.this.getBlockPos();
            PointOfInterestStorage pointOfInterestStorage = ((ServerWorld)BirtEntity.this.world).getPointOfInterestStorage();
            Stream<PointOfInterest> stream = pointOfInterestStorage.getInCircle((poiType) -> poiType.isIn(SpeciesTags.BIRT_HOME), blockPos, 20, PointOfInterestStorage.OccupationStatus.ANY);
            return stream.map(PointOfInterest::getPos).filter(BirtEntity.this::doesDwellingHaveSpace).sorted(Comparator.comparingDouble((blockPos2) -> blockPos2.getSquaredDistance(blockPos))).collect(Collectors.toList());
        }
    }

    @Debug
    public class MoveToDwellingGoal extends BirtEntity.NotAngryGoal {
        int ticks;
        final List<BlockPos> possibleDwellings;
        @Nullable
        private Path path;
        private int ticksUntilLost;

        MoveToDwellingGoal() {
            super();
            this.ticks = BirtEntity.this.world.random.nextInt(10);
            this.possibleDwellings = Lists.newArrayList();
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canBirtStart() {
            return BirtEntity.this.dwellingPos != null && !BirtEntity.this.hasPositionTarget() && BirtEntity.this.canEnterDwelling() && !this.isCloseEnough(BirtEntity.this.dwellingPos) && BirtEntity.this.world.getBlockState(BirtEntity.this.dwellingPos).isOf(SpeciesBlocks.BIRT_DWELLING);
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
            BirtEntity.this.navigation.resetRangeMultiplier();
        }

        @Override
        public void tick() {
            if (BirtEntity.this.dwellingPos != null) {
                ++this.ticks;
                if (this.ticks > this.getTickCount(600)) {
                    this.makeChosenDwellingPossibleDwelling();
                } else if (!BirtEntity.this.navigation.isFollowingPath()) {
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
                        } else if (this.path != null && Objects.requireNonNull(BirtEntity.this.navigation.getCurrentPath()).equalsPath(this.path)) {
                            ++this.ticksUntilLost;
                            if (this.ticksUntilLost > 60) {
                                this.setLost();
                                this.ticksUntilLost = 0;
                            }
                        } else {
                            this.path = BirtEntity.this.navigation.getCurrentPath();
                        }

                    }
                }
            }
        }

        private boolean startMovingToFar(BlockPos pos) {
            BirtEntity.this.navigation.setRangeMultiplier(10.0F);
            BirtEntity.this.navigation.startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 1.0);
            return BirtEntity.this.navigation.getCurrentPath() != null && BirtEntity.this.navigation.getCurrentPath().reachesTarget();
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
                Path path = BirtEntity.this.navigation.getCurrentPath();
                return path != null && path.getTarget().equals(pos) && path.reachesTarget() && path.isFinished();
            }
        }
    }

    private abstract class NotAngryGoal extends Goal {
        NotAngryGoal() {
        }

        public abstract boolean canBirtStart();

        public abstract boolean canBirtContinue();

        @Override
        public boolean canStart() {
            return this.canBirtStart() && !BirtEntity.this.hasAngerTime();
        }

        @Override
        public boolean shouldContinue() {
            return this.canBirtContinue() && !BirtEntity.this.hasAngerTime();
        }
    }
    
}
