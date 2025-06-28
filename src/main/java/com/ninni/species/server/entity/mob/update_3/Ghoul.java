package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.registry.*;
import com.ninni.species.server.criterion.SpeciesCriterion;
import com.ninni.species.server.entity.mob.update_1.Birt;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Ghoul extends Monster implements VibrationSystem {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState confusedAnimationState = new AnimationState();
    public final AnimationState searchAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    public static final EntityDataAccessor<Integer> SEARCH_COOLDOWN = SynchedEntityData.defineId(Ghoul.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> IS_CRAWLING = SynchedEntityData.defineId(Ghoul.class, EntityDataSerializers.BOOLEAN);
    private int searchTimer = 0;
    private int waveDelay = 0;
    private int attackTimer = 0;
    private int confusedTimer = 0;
    private int crawlingCooldown = 0;
    private static final EntityDimensions CRAWLING_DIMENSIONS = EntityDimensions.scalable(0.8F, 0.8F);
    private final VibrationUser vibrationUser;
    private final DynamicGameEventListener<LoudVibrationListener> loudVibrationListener;
    private VibrationSystem.Data vibrationData;

    public Ghoul(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
        this.moveControl = new GhoulMoveControl();
        this.refreshDimensions();
        this.setMaxUpStep(1);
        this.vibrationUser = new VibrationUser();
        this.vibrationData = new VibrationSystem.Data();
        this.loudVibrationListener = new DynamicGameEventListener<>(new LoudVibrationListener(this.vibrationUser.getPositionSource(), GameEvent.JUKEBOX_PLAY.getNotificationRadius()));
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GhoulCrawlMeleeAttackGhoul());
        this.goalSelector.addGoal(1, new GhoulMeleeAttackGhoul());
        this.goalSelector.addGoal(2, new SetCrawling());
        this.goalSelector.addGoal(3, new GhoulAvoidEntityGoal<>(this, Birt.class, 24.0F, 1D, 1D));
        this.goalSelector.addGoal(4, new SearchGoal());
        this.goalSelector.addGoal(5, new RestrictSunGoal(this));
        this.goalSelector.addGoal(6, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.15);
    }

    @Override
    public void tick() {
        super.tick();

        //Confused
        if (this.getTarget() != null && this.getTarget().isAlive() && confusedTimer > 0) {
            this.setTarget(null);
            this.setLastHurtByMob(null);
        }

        //Crawling
        if (this.getTarget() != null && this.getTarget().isAlive()) {
            if (shouldCrawl() && !this.isCrawling()) {
                this.setCrawling(true);
                this.setPose(Pose.CROUCHING);
            }
        }

        //Play chasing sounds
        if (this.getTarget() != null && this.getTarget().isAlive() && this.tickCount % 50 == 0) {
            if (!(this.getTarget() instanceof Player player && (player.isCreative() && player.isSpectator()))) {
                this.playSound(SpeciesSoundEvents.GHOUL_ANGRY.get(), 1, this.getVoicePitch());
            }
        }

        //Cooldowns and poses
        if (attackTimer > 0) --attackTimer;
        if (attackTimer == 0 && this.getPose() == SpeciesPose.ATTACK.get()) this.setPose(shouldCrawl() ? Pose.CROUCHING : Pose.STANDING);

        if (confusedTimer > 0) --confusedTimer;
        if (confusedTimer == 0 && (this.getPose() == SpeciesPose.STUN.get() || this.getPose() == SpeciesPose.CROUCHING_STUN.get())) this.setPose(shouldCrawl() ? Pose.CROUCHING : Pose.STANDING);

        if (waveDelay > 0) --waveDelay;
        if ((waveDelay < 0) || (this.getSearchCooldown() > 0 && waveDelay > 0)) waveDelay = 0;

        if (crawlingCooldown > 0) --crawlingCooldown;

        if (searchTimer > 0) --searchTimer;
        if ((searchTimer < 0) || (this.getSearchCooldown() > 0 && searchTimer > 0)) searchTimer = 0;
        if (this.getSearchCooldown() > 0) this.setSearchCooldown(this.getSearchCooldown() - 1);

        //Animations
        if ((this.level()).isClientSide()) {
            this.setupAnimationStates();
        }
    }

    public boolean shouldCrawl() {
        boolean hasBlockAboveWhenCrawling = level().getBlockState(getOnPosLegacy().above(2)).isSolid();
        boolean hasBlockAbove = level().getBlockState(getOnPosLegacy().above(3)).isSolid();

        boolean isFacingWall = level().getBlockState(getOnPosLegacy().above(2).relative(getDirection())).isSolid();
        boolean hasBlockAtFeet = level().getBlockState(getOnPosLegacy().above(1).relative(getDirection())).isSolid();

        return this.onGround() && (hasBlockAboveWhenCrawling || isFacingWall && !hasBlockAtFeet || !isFacingWall && hasBlockAtFeet && hasBlockAbove);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return pose == Pose.CROUCHING ? CRAWLING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(pose);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout == 0) {
            this.idleAnimationTimeout = 180;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
        if (attackTimer == 0 && this.attackAnimationState.isStarted()) this.attackAnimationState.stop();
        if (confusedTimer == 0 && this.confusedAnimationState.isStarted()) this.confusedAnimationState.stop();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == SpeciesPose.SEARCHING.get()) this.searchAnimationState.start(this.tickCount);
            else if (this.getPose() == Pose.STANDING) {
                this.searchAnimationState.stop();
                this.attackAnimationState.stop();
                this.confusedAnimationState.stop();
                this.refreshDimensions();
            }
            else if (this.getPose() == SpeciesPose.ATTACK.get()) {
                this.attackTimer = 10;
                this.attackAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == SpeciesPose.STUN.get()) {
                this.confusedTimer = 20;
                this.confusedAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == SpeciesPose.CROUCHING_STUN.get()) {
                this.confusedTimer = 20;
                this.confusedAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == Pose.CROUCHING) {
                this.searchAnimationState.stop();
                this.refreshDimensions();
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    public DamageSource torn(LivingEntity livingEntity) {
        return this.damageSources().source(SpeciesDamageTypes.TORN, livingEntity);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (searchTimer > 0) searchTimer = 0;
        if (waveDelay > 0) waveDelay = 0;
        return super.hurt(damageSource, amount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SEARCH_COOLDOWN, 100);
        this.entityData.define(IS_CRAWLING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("SearchTimer", searchTimer);
        compoundTag.putInt("WaveDelay", waveDelay);
        compoundTag.putInt("SearchCooldown", this.getSearchCooldown());
        compoundTag.putBoolean("Crawling", this.isCrawling());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        searchTimer = compoundTag.getInt("SearchTimer");
        waveDelay = compoundTag.getInt("WaveDelay");
        this.setSearchCooldown(compoundTag.getInt("SearchCooldown"));
        this.setCrawling(compoundTag.getBoolean("Crawling"));
    }

    public int getSearchCooldown() {
        return this.entityData.get(SEARCH_COOLDOWN);
    }
    public void setSearchCooldown(int cooldown) {
        this.entityData.set(SEARCH_COOLDOWN, cooldown);
    }

    public boolean isCrawling() {
        return this.entityData.get(IS_CRAWLING);
    }
    public void setCrawling(boolean isCrawling) {
        this.entityData.set(IS_CRAWLING, isCrawling);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return (this.searchTimer > 0 || this.getTarget() != null) ? SoundEvents.EMPTY : SpeciesSoundEvents.GHOUL_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SpeciesSoundEvents.GHOUL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.GHOUL_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState state) {
        this.playSound(SpeciesSoundEvents.GHOUL_STEP.get(), 1F, this.getVoicePitch());
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = entity.hurt(this.torn(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.setPose(SpeciesPose.ATTACK.get());
            this.doEnchantDamageEffects(this, entity);
            if (entity instanceof Player player) {
                if (!player.hasEffect(SpeciesStatusEffects.BLOODLUST.get())) {
                    player.addEffect(new MobEffectInstance(SpeciesStatusEffects.BLOODLUST.get(), -1, 0, false, true), this);
                    this.playSound(SpeciesSoundEvents.GHOUL_INFECT.get(), 1.0F, 1.0F);
                } else this.playSound(SpeciesSoundEvents.GHOUL_ATTACK.get(), 1.0F, 1.0F);
            }
            else this.playSound(SpeciesSoundEvents.GHOUL_ATTACK.get(), 1.0F, 1.0F);
            if (entity instanceof LivingEntity livingEntity && this.getAttributeValue(Attributes.ATTACK_DAMAGE) > livingEntity.getHealth()) {
                this.playSound(SpeciesSoundEvents.BLOODLUST_FEED.get(), 1.0F, 1.0F);
                this.heal(10);
            }
        }
        return flag;
    }

    protected void dropCustomDeathLoot(DamageSource p_34697_, int p_34698_, boolean p_34699_) {
        super.dropCustomDeathLoot(p_34697_, p_34698_, p_34699_);
        Entity entity = p_34697_.getEntity();
        if (entity instanceof Creeper creeper) {
            if (creeper.canDropMobsSkull()) {
                ItemStack itemstack = new ItemStack(SpeciesItems.GHOUL_HEAD.get());
                creeper.increaseDroppedSkulls();
                this.spawnAtLocation(itemstack);
            }
        }
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        if (this.level() instanceof ServerLevel serverLevel) {
            biConsumer.accept(this.loudVibrationListener, serverLevel);
        }
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
        private final PositionSource positionSource;

        VibrationUser() {
            this.positionSource = new EntityPositionSource(Ghoul.this, Ghoul.this.getEyeHeight());
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
            if (Ghoul.this.isNoAi()) return false;
            return Ghoul.this.getTarget() != null;
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
            if (Ghoul.isLoudNoise(gameEvent, serverLevel, blockPos) && Ghoul.this.searchTimer == 0 && Ghoul.this.confusedTimer == 0) {
                Ghoul.this.setPose(Ghoul.this.shouldCrawl() ? SpeciesPose.CROUCHING_STUN.get() : SpeciesPose.STUN.get());
                Ghoul.this.playSound(SpeciesSoundEvents.GHOUL_CONFUSED.get(), 1, 1);
                return true;
            }
            return false;
        }
    }
    public static boolean isLoudNoise(GameEvent gameEvent, ServerLevel serverLevel, BlockPos blockPos) {
        return gameEvent == GameEvent.EXPLODE || gameEvent == GameEvent.INSTRUMENT_PLAY || gameEvent == GameEvent.JUKEBOX_PLAY || (gameEvent == GameEvent.BLOCK_CHANGE && serverLevel.getBlockState(blockPos).is(Blocks.BELL));
    }

    class GhoulMoveControl extends MoveControl {
        public GhoulMoveControl() {
            super(Ghoul.this);
        }

        @Override
        public void tick() {
            if (Ghoul.this.searchTimer == 0) {
                super.tick();
            }
        }
    }

    public class GhoulMeleeAttackGhoul extends MeleeAttackGoal {
        public GhoulMeleeAttackGhoul() {
            super(Ghoul.this, 3.25, false);
        }
        @Override
        public boolean canUse() {
            return super.canUse() && !Ghoul.this.isCrawling() && Ghoul.this.confusedTimer == 0;
        }
        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !Ghoul.this.isCrawling() && Ghoul.this.confusedTimer == 0;
        }
    }

    public class GhoulCrawlMeleeAttackGhoul extends MeleeAttackGoal {
        public GhoulCrawlMeleeAttackGhoul() {
            super(Ghoul.this, 1.5, false);
        }
        @Override
        public boolean canUse() {
            return super.canUse() && Ghoul.this.isCrawling() && Ghoul.this.confusedTimer == 0;
        }
        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && Ghoul.this.isCrawling() && Ghoul.this.confusedTimer == 0;
        }
    }

    public class SetCrawling extends Goal {
        public Ghoul mob;

        SetCrawling() {
            mob = Ghoul.this;
        }

        @Override
        public boolean canUse() {
            return !mob.shouldCrawl() && mob.isCrawling();
        }

        @Override
        public boolean canContinueToUse() {
            return crawlingCooldown > 0;
        }

        @Override
        public void start() {
            crawlingCooldown = 10;
        }

        @Override
        public void stop() {
            setCrawling(false);
            mob.setPose(Pose.STANDING);
        }
    }

    public class SearchGoal extends Goal {
        private double preyXOld;
        private double preyZOld;
        private double SecondPreyXOld;
        private double SecondPreyZOld;
        private double ThirdPreyXOld;
        private double ThirdPreyZOld;
        public static final Predicate<LivingEntity> PREY = (entity) -> !(entity.getType().is(SpeciesTags.CANT_BE_TARGETED_BY_GHOUL)) && (!(entity instanceof Player player) || (!player.isSpectator() && !player.isCreative()));
        private boolean endedEarly;

        @Override
        public boolean canUse() {
            return Ghoul.this.getTarget() == null && !Ghoul.this.isCrawling() && Ghoul.this.getRandom().nextFloat() < 0.02F && Ghoul.this.getNavigation().isDone() && Ghoul.this.getSearchCooldown() == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return Ghoul.this.getTarget() == null && Ghoul.this.searchTimer > 0 && !Ghoul.this.isCrawling();
        }

        @Override
        public void start() {
            searchTimer = 200;
            Ghoul.this.setPose(SpeciesPose.SEARCHING.get());
            Ghoul.this.playSound(SpeciesSoundEvents.GHOUL_SEARCHING.get(), 3, 1);
        }

        @Override
        public void tick() {
            if (Ghoul.this.level() instanceof ServerLevel serverLevel) {
                List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, Ghoul.this.getBoundingBox().inflate(5D), PREY);

                if (searchTimer % 50 == 0) {
                    serverLevel.sendParticles(SpeciesParticles.GHOUL_SEARCHING.get(), Ghoul.this.position().x, Ghoul.this.position().y + 0.01, Ghoul.this.position().z, 1, 0, 0, 0, 0.5F);
                    waveDelay = 50;
                }

                if (waveDelay == 26) {
                    serverLevel.sendParticles(SpeciesParticles.GHOUL_SEARCHING2.get(), Ghoul.this.position().x, Ghoul.this.position().y + 0.01, Ghoul.this.position().z, 1, 0, 0, 0, 0.5F);
                }

                if (!list.isEmpty() && searchTimer % 3 == 0) {
                    if (list.get(0) != null) {
                        preyXOld = list.get(0).getX();
                        preyZOld = list.get(0).getZ();
                    }
                    if (list.size() > 1 && list.get(1) != null) {
                        SecondPreyXOld = list.get(1).getX();
                        SecondPreyZOld = list.get(1).getZ();
                    }
                    if (list.size() > 2 && list.get(2) != null) {
                        ThirdPreyXOld = list.get(2).getX();
                        ThirdPreyZOld = list.get(2).getZ();
                    }
                }

                if (waveDelay <= 26 && waveDelay > 0) {

                    if (!list.isEmpty()) {
                        aggro(list, 0, preyXOld, preyZOld);
                        aggro(list, 1, SecondPreyXOld, SecondPreyZOld);
                        aggro(list, 2, ThirdPreyXOld, ThirdPreyZOld);
                    }
                }

            }
        }

        private void aggro(List<LivingEntity> list, int index, double x, double z) {
            if (list.size() > index && list.get(index) != null) {
                Vec3 movement3 = new Vec3(list.get(index).getX() - x, 0, list.get(index).getZ() - z);

                if (movement3.horizontalDistanceSqr() > 0.01) {
                    endedEarly = true;
                    Ghoul.this.setTarget(list.get(index));
                    Ghoul.this.getLookControl().setLookAt(list.get(index));
                    Ghoul.this.getMoveControl().setWantedPosition(list.get(index).getX(),list.get(index).getY(),list.get(index).getZ(), 1);
                    Ghoul.this.searchTimer = 0;
                    Ghoul.this.playSound(SpeciesSoundEvents.GHOUL_AGGRO.get());
                    if (list.get(index) instanceof ServerPlayer serverPlayer) SpeciesCriterion.AGGRO_GHOUL.trigger(serverPlayer);
                }
            }
        }

        @Override
        public void stop() {
            List<ServerPlayer> list = level().getEntitiesOfClass(ServerPlayer.class, Ghoul.this.getBoundingBox().inflate(5D), PREY);
            if (!endedEarly) for (ServerPlayer serverPlayer : list) SpeciesCriterion.SURVIVE_GHOUL.trigger(serverPlayer);

            Ghoul.this.setPose(Ghoul.this.shouldCrawl() ? Pose.CROUCHING : Pose.STANDING);
            Ghoul.this.setSearchCooldown(800);
        }
    }

    class GhoulAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

        public GhoulAvoidEntityGoal(Ghoul ghoul, Class<T> tClass, float maxDistance, double walkSpeed, double sprintSpeed) {
            super(ghoul, tClass, maxDistance, walkSpeed, sprintSpeed);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !Ghoul.this.isCrawling();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !Ghoul.this.isCrawling();
        }
    }

    public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> p_219014_, ServerLevelAccessor p_219015_, MobSpawnType p_219016_, BlockPos p_219017_, RandomSource p_219018_) {
        return p_219017_.getY() <= 0 && p_219015_.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(p_219015_, p_219017_, p_219018_) && checkMobSpawnRules(p_219014_, p_219015_, p_219016_, p_219017_, p_219018_);
    }
}