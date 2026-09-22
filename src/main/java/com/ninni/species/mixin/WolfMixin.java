package com.ninni.species.mixin;

import com.ninni.species.mixin_util.WolfAccess;
import com.ninni.species.server.entity.ai.goal.TransformDuringFullMoonGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal implements NeutralMob, WolfAccess {
    @Shadow
    @Final
    private static EntityDataAccessor<Integer> DATA_COLLAR_COLOR;

    private @Unique static final EntityDataAccessor<Boolean> DATA_IS_BEWEREAGER = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
    private @Unique static final EntityDataAccessor<Boolean> DATA_IS_CURED_BEWEREAGER = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);

    protected WolfMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    public void finalizeSpawn(ServerLevelAccessor l, DifficultyInstance d, MobSpawnType s, SpawnGroupData g, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (this.getRandom().nextInt(10) == 0) this.setIsBewereager(true);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void onInitGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(1, new TransformDuringFullMoonGoal((Wolf)(Object)this));
    }

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    private void S$defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_IS_BEWEREAGER, false);
        builder.define(DATA_IS_CURED_BEWEREAGER, false);
    }


    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void S$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putBoolean("IsBewereager", this.getIsBewereager());
        compoundTag.putBoolean("IsCuredBewereager", this.getIsCuredBewereager());
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void S$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.setIsBewereager(compoundTag.getBoolean("IsBewereager"));
        this.setIsCuredBewereager(compoundTag.getBoolean("IsCuredBewereager"));
    }

    @Override
    public @Unique boolean getIsBewereager() {
        return this.entityData.get(DATA_IS_BEWEREAGER);
    }
    @Override
    public @Unique void setIsBewereager(boolean isBewereager) {
        this.entityData.set(DATA_IS_BEWEREAGER, isBewereager);
    }

    @Override
    public @Unique boolean getIsCuredBewereager() {
        return this.entityData.get(DATA_IS_CURED_BEWEREAGER);
    }
    @Override
    public @Unique void setIsCuredBewereager(boolean isCuredBewereager) {
        this.entityData.set(DATA_IS_CURED_BEWEREAGER, isCuredBewereager);
    }

    @Override
    public @Unique void setNewCollarColor(DyeColor color) {
        this.entityData.set(DATA_COLLAR_COLOR, color.getId());
    }
}
