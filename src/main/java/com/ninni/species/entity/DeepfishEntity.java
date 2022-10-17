package com.ninni.species.entity;

import com.ninni.species.item.SpeciesItems;
import com.ninni.species.sound.SpeciesSoundEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class DeepfishEntity extends SchoolingFishEntity {

    public DeepfishEntity(EntityType<? extends SchoolingFishEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02f, 0.1f, true);
        this.lookControl = new YawAdjustingLookControl(this, 10);
    }

    public static DefaultAttributeContainer.Builder createDeepfishAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 2.0);
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 480;
    }

    @Override
    public int getMaxLookPitchChange() {
        return 1;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SpeciesSoundEvents.ENTITY_DEEPFISH_FLOP;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SpeciesSoundEvents.ENTITY_DEEPFISH_IDLE;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SpeciesSoundEvents.ENTITY_DEEPFISH_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.ENTITY_DEEPFISH_DEATH;
    }

    @Override
    public ItemStack getBucketItem() {
        return SpeciesItems.DEEPFISH_BUCKET.getDefaultStack();
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<? extends WaterCreatureEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        return pos.getY() <= 0 && world.getBaseLightLevel(pos, 0) == 0 && world.getBlockState(pos).isOf(Blocks.WATER);
    }
}
