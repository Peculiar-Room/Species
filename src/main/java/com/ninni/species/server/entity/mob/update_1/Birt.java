package com.ninni.species.server.entity.mob.update_1;

import com.google.common.collect.Lists;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.server.block.entity.BirtDwellingBlockEntity;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.server.entity.ai.goal.BirtCommunicatingGoal;
import com.ninni.species.server.entity.ai.goal.SendMessageTicksGoal;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesTags;
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
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Birt extends Animal implements NeutralMob, FlyingAnimal, VibrationSystem {
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
    private static final EntityDataAccessor<Byte> BIRT_FLAGS = SynchedEntityData.defineId(Birt.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANGER = SynchedEntityData.defineId(Birt.class, EntityDataSerializers.INT);
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID angryAt;
    private int cannotEnterDwellingTicks;
    int ticksLeftToFindDwelling;
    @Nullable
    BlockPos dwellingPos;
    Birt.MoveToDwellingGoal moveToDwellingGoal;
    private final VibrationUser vibrationUser;
    private final DynamicGameEventListener<LoudVibrationListener> loudVibrationListener;
    private VibrationSystem.Data vibrationData;

    public Birt(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new FlyingMoveControl(this, 20, false);
        this.vibrationUser = new VibrationUser();
        this.vibrationData = new VibrationSystem.Data();
        this.loudVibrationListener = new DynamicGameEventListener<>(new LoudVibrationListener(this.vibrationUser.getPositionSource(), GameEvent.JUKEBOX_PLAY.getNotificationRadius()));
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

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        if (this.level() instanceof ServerLevel serverLevel) {
            biConsumer.accept(this.loudVibrationListener, serverLevel);
        }
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

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            VibrationSystem.Ticker.tick(this.level(), this.vibrationData, this.vibrationUser);
        }
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
    public Birt findReciever() {
        List<? extends Birt> list = this.level().getNearbyEntities(Birt.class, TargetingConditions.DEFAULT, this, this.getBoundingBox().inflate(8.0));
        double d = Double.MAX_VALUE;
        Birt birt = null;
        for (Birt birt2 : list) {
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
        if (status == 10) this.antennaTicks = 60;
        else super.handleEntityEvent(status);
    }

    public void sendMessage(ServerLevel world, Birt other) {
        this.resetMessageTicks();
        other.resetMessageTicks();

        EntityPositionSource positionSource = new EntityPositionSource(this, 0.75F);

        world.playSound(null, other.blockPosition(), SpeciesSoundEvents.BIRT_MESSAGE.get(), SoundSource.NEUTRAL, 1,  0.6f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
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
        this.playSound(SpeciesSoundEvents.BIRT_FLY.get(), 0.15f, 1.0f);
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
        if (this.cannotEnterDwellingTicks <= 0 && this.getTarget() == null) return this.level().isDay();
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
        return SpeciesSoundEvents.BIRT_IDLE.get();
    }
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.BIRT_HURT.get();
    }
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.BIRT_DEATH.get();
    }

    @Override
    public Data getVibrationData() {
        return this.vibrationData;
    }
    @Override
    public User getVibrationUser() {
        return this.vibrationUser;
    }

    public class VibrationUser implements VibrationSystem.User {
        private static final int VIBRATION_EVENT_LISTENER_RANGE = 16;
        private final PositionSource positionSource;
        VibrationUser() {
            this.positionSource = new EntityPositionSource(Birt.this, Birt.this.getEyeHeight());
        }
        @Override
        public int getListenerRadius() {
            return 16;
        }
        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }
        @Override
        public boolean canReceiveVibration(ServerLevel serverLevel, BlockPos blockPos, GameEvent gameEvent, GameEvent.Context context) {
            if (Birt.this.isNoAi()) {
                return false;
            }
            return Birt.this.getTarget() != null;
        }
        @Override
        public void onReceiveVibration(ServerLevel serverLevel, BlockPos blockPos, GameEvent gameEvent, @Nullable Entity entity, @Nullable Entity entity2, float f) {
        }
        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.ALLAY_CAN_LISTEN;
        }
    }
    public class LoudVibrationListener implements GameEventListener {
        private final PositionSource listenerSource;
        private final int listenerRadius;
        public LoudVibrationListener(PositionSource positionSource, int i) {
            this.listenerSource = positionSource;
            this.listenerRadius = i;
        }
        @Override
        public PositionSource getListenerSource() {
            return this.listenerSource;
        }
        @Override
        public int getListenerRadius() {
            return this.listenerRadius;
        }
        @Override
        public boolean handleGameEvent(ServerLevel serverLevel, GameEvent gameEvent, GameEvent.Context context, Vec3 vec3) {
            BlockPos blockPos = BlockPos.containing(vec3);
            if (Birt.isLoudNoise(gameEvent, serverLevel, blockPos)) {
                Birt.this.setTarget(null);
                return true;
            }
            return false;
        }
    }
    public static boolean isLoudNoise(GameEvent gameEvent, ServerLevel serverLevel, BlockPos blockPos) {
        return gameEvent == GameEvent.EXPLODE || gameEvent == GameEvent.INSTRUMENT_PLAY || gameEvent == GameEvent.JUKEBOX_PLAY || (gameEvent == GameEvent.BLOCK_CHANGE && serverLevel.getBlockState(blockPos).is(Blocks.BELL));
    }

    class BirtWanderAroundGoal extends Goal {

        BirtWanderAroundGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (Birt.this.groundTicks < 0) return true;
            else if (Birt.this.isFlying()) return Birt.this.navigation.isDone() && Birt.this.random.nextInt(10) == 0;
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return Birt.this.navigation.isInProgress();
        }

        @Override
        public void start() {
            Vec3 vec3d = this.getRandomLocation();
            if (vec3d != null) {
                Birt.this.navigation.moveTo(Birt.this.navigation.createPath(BlockPos.containing(vec3d), 1), 1.0);
            }
        }
        
        @Nullable
        private Vec3 getRandomLocation() {
            Vec3 vec3d2;
            assert Birt.this.dwellingPos != null;
            if (Birt.this.isDwellingValid() && !Birt.this.isWithinDistance(Birt.this.dwellingPos, 22)) {
                Vec3 vec3d = Vec3.atCenterOf(Birt.this.dwellingPos);
                vec3d2 = vec3d.subtract(Birt.this.position()).normalize();
            } else {
                vec3d2 = Birt.this.getViewVector(0.0F);
            }

            Vec3 vec3d3 = HoverRandomPos.getPos(Birt.this, 12, 5, vec3d2.x, vec3d2.z, 1.5707964F, 3, 1);
            return vec3d3 != null ? vec3d3 : AirAndWaterRandomPos.getPos(Birt.this, 12, 2, -2, vec3d2.x, vec3d2.z, 1.5707963705062866);
        }
    }

    class BirtLookAroundGoal extends RandomLookAroundGoal {

        BirtLookAroundGoal() {
            super(Birt.this);
        }

        @Override
        public boolean canUse() {
            return Birt.this.onGround() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return Birt.this.onGround() && super.canContinueToUse();
        }
    }
    
    class EnterDwellingGoal extends Birt.NotAngryGoal {
        EnterDwellingGoal() {
            super();
        }

        @Override
        public boolean canBirtStart() {
            if (Birt.this.hasDwelling() && Birt.this.canEnterDwelling()) {
                assert Birt.this.dwellingPos != null;
                if (Birt.this.dwellingPos.closerToCenterThan(Birt.this.position(), 2.0)) {
                    BlockEntity blockEntity = Birt.this.level().getBlockEntity(Birt.this.dwellingPos);
                    if (blockEntity instanceof BirtDwellingBlockEntity blockEntity1) {
                        if (!blockEntity1.isFullOfBirts()) {
                            return true;
                        }

                        Birt.this.dwellingPos = null;
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
            BlockEntity blockEntity = Birt.this.level().getBlockEntity(Birt.this.dwellingPos);
            if (blockEntity instanceof BirtDwellingBlockEntity birtDwellingBlockEntity) {
                birtDwellingBlockEntity.tryEnterDwelling(Birt.this);
            }

        }
    }

    private class FindDwellingGoal extends Birt.NotAngryGoal {
        FindDwellingGoal() {
            super();
        }

        @Override
        public boolean canBirtStart() {
            return Birt.this.ticksLeftToFindDwelling == 0 && !Birt.this.hasDwelling() && Birt.this.canEnterDwelling();
        }

        @Override
        public boolean canBirtContinue() {
            return false;
        }

        @Override
        public void start() {
            Birt.this.ticksLeftToFindDwelling = 200;
            List<BlockPos> list = this.getNearbyFreeDwellings();
            if (!list.isEmpty()) {
                Iterator<BlockPos> var2 = list.iterator();

                BlockPos blockPos;
                do {
                    if (!var2.hasNext()) {
                        Birt.this.moveToDwellingGoal.clearPossibleDwellings();
                        Birt.this.dwellingPos = list.get(0);
                        return;
                    }

                    blockPos = var2.next();
                } while(Birt.this.moveToDwellingGoal.isPossibleDwelling(blockPos));

                Birt.this.dwellingPos = blockPos;
            }
        }

        private List<BlockPos> getNearbyFreeDwellings() {
            BlockPos blockPos = Birt.this.blockPosition();
            PoiManager pointOfInterestStorage = ((ServerLevel) Birt.this.level()).getPoiManager();
            Stream<PoiRecord> stream = pointOfInterestStorage.getInRange((poiType) -> poiType.is(SpeciesTags.BIRT_HOME), blockPos, 20, PoiManager.Occupancy.ANY);
            return stream.map(PoiRecord::getPos).filter(Birt.this::doesDwellingHaveSpace).sorted(Comparator.comparingDouble((blockPos2) -> blockPos2.distSqr(blockPos))).collect(Collectors.toList());
        }
    }

    @VisibleForDebug
    public class MoveToDwellingGoal extends Birt.NotAngryGoal {
        int ticks;
        final List<BlockPos> possibleDwellings;
        @Nullable
        private Path path;
        private int ticksUntilLost;

        MoveToDwellingGoal() {
            super();
            this.ticks = Birt.this.level().random.nextInt(10);
            this.possibleDwellings = Lists.newArrayList();
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canBirtStart() {
            return Birt.this.dwellingPos != null && !Birt.this.hasRestriction() && Birt.this.canEnterDwelling() && !this.isCloseEnough(Birt.this.dwellingPos) && Birt.this.level().getBlockState(Birt.this.dwellingPos).is(SpeciesBlocks.BIRT_DWELLING.get());
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
            Birt.this.navigation.stop();
            Birt.this.navigation.resetMaxVisitedNodesMultiplier();
        }

        @Override
        public void tick() {
            if (Birt.this.dwellingPos != null) {
                ++this.ticks;
                if (this.ticks > this.adjustedTickDelay(600)) {
                    this.makeChosenDwellingPossibleDwelling();
                } else if (!Birt.this.navigation.isInProgress()) {
                    if (!Birt.this.isWithinDistance(Birt.this.dwellingPos, 16)) {
                        if (Birt.this.isTooFar(Birt.this.dwellingPos)) {
                            this.setLost();
                        } else {
                            Birt.this.startMovingTo(Birt.this.dwellingPos);
                        }
                    } else {
                        boolean bl = this.startMovingToFar(Birt.this.dwellingPos);
                        if (!bl) {
                            this.makeChosenDwellingPossibleDwelling();
                        } else if (this.path != null && Objects.requireNonNull(Birt.this.navigation.getPath()).sameAs(this.path)) {
                            ++this.ticksUntilLost;
                            if (this.ticksUntilLost > 60) {
                                this.setLost();
                                this.ticksUntilLost = 0;
                            }
                        } else {
                            this.path = Birt.this.navigation.getPath();
                        }

                    }
                }
            }
        }

        private boolean startMovingToFar(BlockPos pos) {
            Birt.this.navigation.setMaxVisitedNodesMultiplier(10.0F);
            Birt.this.navigation.moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.0);
            return Birt.this.navigation.getPath() != null && Birt.this.navigation.getPath().canReach();
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
            if (Birt.this.dwellingPos != null) {
                this.addPossibleDwelling(Birt.this.dwellingPos);
            }

            this.setLost();
        }

        private void setLost() {
            Birt.this.dwellingPos = null;
            Birt.this.ticksLeftToFindDwelling = 200;
        }

        private boolean isCloseEnough(BlockPos pos) {
            if (Birt.this.isWithinDistance(pos, 2)) {
                return true;
            } else {
                Path path = Birt.this.navigation.getPath();
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
            return this.canBirtStart() && !Birt.this.isAngry();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canBirtContinue() && !Birt.this.isAngry();
        }
    }
    
}
