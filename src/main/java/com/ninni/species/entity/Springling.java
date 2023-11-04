package com.ninni.species.entity;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class Springling extends AbstractHorse {
    public static final EntityDataAccessor<Float> EXTENDED_AMOUNT = SynchedEntityData.defineId(Springling.class, EntityDataSerializers.FLOAT);

    public Springling(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    public void tick() {
        super.tick();


        if (this.getFirstPassenger() instanceof LocalPlayer player) {
            if (player.input.down && this.getExtendedAmount() > 0) {
                this.setExtendedAmount(this.getExtendedAmount() - 0.25f);
            }
            if (player.input.jumping && this.getExtendedAmount() < 20f) {
                this.setExtendedAmount(this.getExtendedAmount() + 0.25f);
            }
        }

        if (this.getExtendedAmount() < 0) this.setExtendedAmount(0);

    }

    @Override
    public void dismountTo(double d, double e, double f) {
        this.setExtendedAmount(0);
        super.dismountTo(d, e, f);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {


        if (player.getItemInHand(interactionHand).isEmpty()) {
            return player.startRiding(this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        return super.mobInteract(player, interactionHand);
    }


    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(1F, 1.2f + this.getExtendedAmount()/2);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + this.getExtendedAmount();
    }

    @Override
    public double getMyRidingOffset() {
        return super.getMyRidingOffset() + this.getExtendedAmount();
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Springling> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EXTENDED_AMOUNT, 0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putFloat("ExtendedAmount", this.getExtendedAmount());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        this.setExtendedAmount(compoundTag.getFloat("ExtendedAmount"));
    }

    public float getExtendedAmount() {
        return this.entityData.get(EXTENDED_AMOUNT);
    }
    public void setExtendedAmount(float amount) {
        this.entityData.set(EXTENDED_AMOUNT, amount);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }
}
