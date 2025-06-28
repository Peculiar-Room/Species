package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class Hanger extends Monster {
    public static final EntityDataAccessor<Vector3f> CURRENT_TONGUE_POS = SynchedEntityData.defineId(Hanger.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Integer> JAW_SNAP_TICKS = SynchedEntityData.defineId(Hanger.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CANT_ATTACK_TICKS = SynchedEntityData.defineId(Hanger.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Vector3f> TARGET_POS = SynchedEntityData.defineId(Hanger.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Boolean> IS_TONGUE_OUT = SynchedEntityData.defineId(Hanger.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> RARE = SynchedEntityData.defineId(Hanger.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState lookAroundAnimationState = new AnimationState();
    public final AnimationState shiftAnimationState = new AnimationState();
    public final AnimationState eyeTwitchAnimationState = new AnimationState();
    private int eyeTwitchCoooldown = this.random.nextInt(5 * 10) + (10 * 10);
    private int shiftCoooldown = this.random.nextInt(120 * 10) + (60 * 10);
    private int lookAroundCoooldown = this.random.nextInt(20 * 10) + (20 * 10);

    protected Vector3f tongueTarget;
    protected int targetStuckTicks;
    public float mouthOpenProgress = 0.0F;

    protected Hanger(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new EyeTwitchingAnimationGoal(this));
        this.goalSelector.addGoal(1, new ShiftingAnimationGoal(this));
        this.goalSelector.addGoal(1, new LookAroundAnimationGoal(this));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        this.setRare(random.nextInt(10) == 0);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, spawnType, groupData, tag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 50.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_DAMAGE, 8);
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getPose() != SpeciesPose.READJUSTING.get() && shiftCoooldown > 0) shiftCoooldown--;
        if (this.getPose() != SpeciesPose.TWITCHING.get() && eyeTwitchCoooldown > 0) eyeTwitchCoooldown--;
        if (this.getPose() != SpeciesPose.LOOKING_AROUND.get() && lookAroundCoooldown > 0) lookAroundCoooldown--;

        if (this.tickCount > 2) {
            updateTongue();
        }

        if (getJawSnapTicks() > 0) {
            setJawSnapTicks(getJawSnapTicks()-1);
            if (getJawSnapTicks() > 6) mouthOpenProgress = 0;
            else mouthOpenProgress = ((10 - getJawSnapTicks()) / 10.0F - 0.4F);
        } else {
            if (isTongueOut()) mouthOpenProgress = Math.min(1, mouthOpenProgress + 0.01f);
            else mouthOpenProgress = Math.max(0, mouthOpenProgress - 0.25f);
        }

        unstuckTarget();
    }

    public void unstuckTarget() {
        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            if (this.getCantAttackTicks() > 0) this.setCantAttackTicks(this.getCantAttackTicks() - 1);

            if (target != null && !(target instanceof Player)) {
                double dx = target.getX() - target.xo;
                double dy = target.getY() - target.yo;
                double dz = target.getZ() - target.zo;
                double motionSq = dx * dx + dy * dy + dz * dz;
                if (motionSq < 0.01 && !target.onGround()) targetStuckTicks++;

                if (targetStuckTicks > 200) {
                    this.setTarget(null);
                    this.setCantAttackTicks(200);
                }
                if (this.getCantAttackTicks() > 0) this.setTarget(null);
            } else {
                if (targetStuckTicks > 0) targetStuckTicks = 0;
            }
        }
    }

    private void updateTongue() {
        if (tongueTarget != null) {
            Vector3f target = new Vector3f(
                    tongueTarget.x - (float)this.getX(),
                    tongueTarget.y - (float)this.getY(),
                    tongueTarget.z - (float)this.getZ()
            );

            float lerp = Mth.clamp(this.getCurrentTonguePos().distance(target) * 0.3F, 0.05F, 1.0F);

            this.setCurrentTonguePos(new Vector3f(
                    Mth.lerp(lerp, this.getCurrentTonguePos().x, target.x),
                    Mth.lerp(lerp, this.getCurrentTonguePos().y, target.y),
                    Mth.lerp(lerp, this.getCurrentTonguePos().z, target.z)
            ));
        } else {
            tongueTarget = new Vector3f((float) this.getX(), (float) (this.getY() + getTongueOffset()), (float) this.getZ());
        }

        if (this.getCurrentTonguePos().distance(new Vector3f(0,getTongueOffset(),0)) > 40 || !this.isTongueOut()) deactivateTongue();

        this.setTongueTarget(this.getTargetPos());
    }

    public void activateTongue(Vector3f target) {
        Vector3f origin = new Vector3f(0, this.getTongueOffset(), 0);
        this.setCurrentTonguePos(origin);
        this.setTargetPos(target);
        this.setTongueTarget(target);
        this.setTongueOut(true);
    }

    public void deactivateTongue() {
        Vector3f fallback = new Vector3f((float)this.getX(), (float)(this.getY() + this.getTongueOffset()), (float)this.getZ());
        this.setTargetPos(fallback);
        this.setTongueTarget(fallback);
        this.setTongueOut(false);
    }

    public float getTongueOffset() {
        return 0.35F;
    }

    public void setTongueTarget(Vector3f tongueTarget) {
        this.tongueTarget = tongueTarget;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.setJawSnapTicks(10);
        return super.doHurtTarget(target);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int lootingMultiplier, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, lootingMultiplier, recentlyHit);

        if (this.isTongueOut()) {
            if (this.random.nextFloat() < 0.8F) {
                int count = 1;

                if (this.random.nextFloat() < 0.3F + (0.05F * lootingMultiplier)) {
                    count += 1 + this.random.nextInt(2);
                }
                for (int i = 0; i < count; i++) {
                    this.spawnAtLocation(SpeciesItems.COIL.get());
                }
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == SpeciesPose.TWITCHING.get()) this.eyeTwitchAnimationState.start(this.tickCount);
            else if (this.getPose() == SpeciesPose.LOOKING_AROUND.get()) this.lookAroundAnimationState.start(this.tickCount);
            else if (this.getPose() == SpeciesPose.READJUSTING.get()) this.shiftAnimationState.start(this.tickCount);
            else if (this.getPose() == Pose.STANDING) {
                this.eyeTwitchAnimationState.stop();
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CURRENT_TONGUE_POS, new Vector3f((float) this.getX(), (float) (this.getY() + getTongueOffset()), (float) this.getZ()));
        this.entityData.define(JAW_SNAP_TICKS, 0);
        this.entityData.define(CANT_ATTACK_TICKS, 0);
        this.entityData.define(TARGET_POS, new Vector3f((float) this.getX(), (float) this.getY() + getTongueOffset(), (float) this.getZ()));
        this.entityData.define(IS_TONGUE_OUT, false);
        this.entityData.define(RARE, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putFloat("CurrentTonguePosX", this.getCurrentTonguePos().x);
        compoundTag.putFloat("CurrentTonguePosY", this.getCurrentTonguePos().y);
        compoundTag.putFloat("CurrentTonguePosZ", this.getCurrentTonguePos().z);
        compoundTag.putFloat("TargetPosX", this.getTargetPos().x);
        compoundTag.putFloat("TargetPosY", this.getTargetPos().y);
        compoundTag.putFloat("TargetPosZ", this.getTargetPos().z);
        compoundTag.putInt("JawSnapTicks", this.getJawSnapTicks());
        compoundTag.putInt("CantAttackTicks", this.getCantAttackTicks());
        compoundTag.putBoolean("IsTongueOut", this.isTongueOut());
        compoundTag.putBoolean("Rare", this.isRare());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setCurrentTonguePos( new Vector3f(
                compoundTag.getFloat("CurrentTonguePosX"),
                compoundTag.getFloat("CurrentTonguePosY"),
                compoundTag.getFloat("CurrentTonguePosZ")
                ));
        this.setTargetPos( new Vector3f(
                compoundTag.getFloat("TargetPosX"),
                compoundTag.getFloat("TargetPosY"),
                compoundTag.getFloat("TargetPosZ")
                ));
        this.setJawSnapTicks(compoundTag.getInt("JawSnapTicks"));
        this.setCantAttackTicks(compoundTag.getInt("CantAttackTicks"));
        this.setTongueOut(compoundTag.getBoolean("IsTongueOut"));
        this.setRare(compoundTag.getBoolean("Rare"));
    }

    public Vector3f getCurrentTonguePos() {
        return this.entityData.get(CURRENT_TONGUE_POS);
    }
    public void setCurrentTonguePos(Vector3f vec) {
        this.entityData.set(CURRENT_TONGUE_POS, vec);
    }

    public void setJawSnapTicks(int ticks) {
        this.entityData.set(JAW_SNAP_TICKS, ticks);
    }
    public int getJawSnapTicks() {
        return this.entityData.get(JAW_SNAP_TICKS);
    }

    public void setCantAttackTicks(int ticks) {
        this.entityData.set(CANT_ATTACK_TICKS, ticks);
    }
    public int getCantAttackTicks() {
        return this.entityData.get(CANT_ATTACK_TICKS);
    }

    public Vector3f getTargetPos() {
        return this.entityData.get(TARGET_POS);
    }
    public void setTargetPos(Vector3f vec) {
        this.entityData.set(TARGET_POS, vec);
    }

    public boolean isTongueOut() {
        return this.entityData.get(IS_TONGUE_OUT);
    }
    public void setTongueOut(boolean tongueOut) {
        this.entityData.set(IS_TONGUE_OUT, tongueOut);
    }

    public boolean isRare() {
        return this.entityData.get(RARE);
    }
    public void setRare(boolean rare) {
        this.entityData.set(RARE, rare);
    }

    @Override
    public boolean isPushable() {
        return !isTongueOut() && super.isPushable();
    }

    public static class EyeTwitchingAnimationGoal extends Goal {
        public Hanger hanger;
        public int timer;

        public EyeTwitchingAnimationGoal(Hanger hanger) {
            this.hanger = hanger;
        }

        @Override
        public boolean canUse() {
            return hanger.eyeTwitchCoooldown == 0 && hanger.getPose() == Pose.STANDING;
        }

        @Override
        public void start() {
            this.timer = 30;
            this.hanger.getNavigation().stop();
            this.hanger.setPose(SpeciesPose.TWITCHING.get());
        }

        @Override
        public boolean canContinueToUse() {
            return timer > 0 && hanger.getPose() == SpeciesPose.TWITCHING.get();
        }

        @Override
        public void tick() {
            this.timer--;
        }

        @Override
        public void stop() {
            hanger.eyeTwitchCoooldown = hanger.random.nextInt(5 * 10) + (10 * 10);
            this.hanger.setPose(Pose.STANDING);
        }
    }

    public static class ShiftingAnimationGoal extends Goal {
        public Hanger hanger;
        public int timer;

        public ShiftingAnimationGoal(Hanger hanger) {
            this.hanger = hanger;
        }

        @Override
        public boolean canUse() {
            return hanger.shiftCoooldown == 0 && hanger.getPose() == Pose.STANDING;
        }

        @Override
        public void start() {
            this.timer = 13 * 20;
            this.hanger.getNavigation().stop();
            this.hanger.setPose(SpeciesPose.READJUSTING.get());
        }

        @Override
        public boolean canContinueToUse() {
            return timer > 0 && hanger.getPose() == SpeciesPose.READJUSTING.get();
        }

        @Override
        public void tick() {
            this.timer--;
        }

        @Override
        public void stop() {
            hanger.shiftCoooldown = hanger.random.nextInt(120 * 10) + (60 * 10);
            this.hanger.setPose(Pose.STANDING);
        }
    }

    public static class LookAroundAnimationGoal extends Goal {
        public Hanger hanger;
        public int timer;

        public LookAroundAnimationGoal(Hanger hanger) {
            this.hanger = hanger;
        }

        @Override
        public boolean canUse() {
            return hanger.lookAroundCoooldown == 0 && hanger.getPose() == Pose.STANDING;
        }

        @Override
        public void start() {
            this.timer = 8 * 20;
            this.hanger.getNavigation().stop();
            this.hanger.setPose(SpeciesPose.LOOKING_AROUND.get());
        }

        @Override
        public boolean canContinueToUse() {
            return timer > 0 && hanger.getPose() == SpeciesPose.LOOKING_AROUND.get();
        }

        @Override
        public void tick() {
            this.timer--;
        }

        @Override
        public void stop() {
            hanger.lookAroundCoooldown = hanger.random.nextInt(20 * 10) + (20 * 10);
            this.hanger.setPose(Pose.STANDING);
        }
    }
}
