package com.ninni.species.entity;

import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Goober extends Animal {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState layDownIdleAnimationState = new AnimationState();
    public final AnimationState layDownAnimationState = new AnimationState();
    public final AnimationState yawnAnimationState = new AnimationState();
    public final AnimationState layDownYawnAnimationState = new AnimationState();
    public final AnimationState rearUpAnimationState = new AnimationState();

    public Goober(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1));

    }

    public static AttributeSupplier.Builder createGooberAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SpeciesSoundEvents.ENTITY_GOOBER_IDLE;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.ENTITY_GOOBER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.ENTITY_GOOBER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SpeciesSoundEvents.ENTITY_GOOBER_STEP, 0.15f, 1.0f);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }


    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Goober> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return false;
    }
}
