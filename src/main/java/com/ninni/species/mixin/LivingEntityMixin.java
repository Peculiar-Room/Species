package com.ninni.species.mixin;

import com.ninni.species.server.entity.util.CustomDeathParticles;
import com.ninni.species.server.entity.util.LivingEntityAccess;
import com.ninni.species.server.packet.SnatchedPacket;
import com.ninni.species.server.packet.TankedPacket;
import com.ninni.species.registry.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccess {
    @Shadow public abstract boolean hasEffect(MobEffect effect);
    @Shadow @Nullable public abstract MobEffectInstance getEffect(MobEffect p_21125_);
    @Shadow public abstract RandomSource getRandom();
    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot p_21127_);
    @OnlyIn(Dist.CLIENT)
    private @Unique boolean snatched;
    @OnlyIn(Dist.CLIENT)
    private @Unique boolean tanked;
    private @Unique EntityType disguisedEntityType;
    private @Unique LivingEntity disguisedEntity;

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    @Inject(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;updateGlowingStatus()V", ordinal = 0))
    private void onStatusEffectChange(CallbackInfo ci) {
        SpeciesNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new SnatchedPacket(this.getId(), this.hasEffect(SpeciesStatusEffects.SNATCHED.get())));
        SpeciesNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new TankedPacket(this.getId(), this.hasEffect(SpeciesStatusEffects.TANKED.get())));
    }

    @Inject(method = "tickEffects", at = @At("HEAD"))
    public void applySpeciesEffects(CallbackInfo ci) {
        Level level = this.level();

        if (this.hasEffect(SpeciesStatusEffects.GUT_FEELING.get()) ) {
            if (this.getEffect(SpeciesStatusEffects.GUT_FEELING.get()).getDuration() < 20 * 60 * 5) {
                if (this.getRandom().nextInt(200) == 0) this.playSound(SpeciesSoundEvents.GUT_FEELING_ROAR.get(), 0.2f, 0);
            }
            else {
                if (this.getRandom().nextInt(800) == 0) this.playSound(SpeciesSoundEvents.GUT_FEELING_ROAR.get(), 0.2f, 0);
            }
        }

        if (this.hasEffect(SpeciesStatusEffects.BIRTD.get()) && level instanceof ServerLevel world) {
            if (this.tickCount % 10 == 1) {
                world.sendParticles(SpeciesParticles.BIRTD.get(), this.getX(), this.getEyeY() + 0.5F, this.getZ() - 0.5, 1,0, 0, 0, 0);
            }
        }

    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        ItemStack headItem = this.getItemBySlot(EquipmentSlot.HEAD);
        CompoundTag tag = headItem.getTag();

        if (headItem.is(SpeciesItems.WICKED_MASK.get())) {
            if ((this.getDisguisedEntity() == null || this.getDisguisedEntityType() == null) ||
                    tag == null ||
                    !tag.contains("id") ||
                    !this.getDisguisedEntityType().toShortString().equals(tag.getString("id"))) {

                if (tag != null && tag.contains("id")) {
                    Optional<EntityType<?>> entityType = EntityType.byString(tag.getString("id"));
                    if (entityType.isPresent()) {
                        Entity rawEntity = entityType.get().create(this.level());
                        if (rawEntity instanceof LivingEntity living) {
                            living.load(tag);
                            this.setDisguisedEntity(living);
                            this.setDisguisedEntityType(entityType.get());
                        } else {
                            this.setDisguisedEntity(null);
                            this.setDisguisedEntityType(null);
                        }
                    }
                } else {
                    this.setDisguisedEntity(null);
                    this.setDisguisedEntityType(null);
                }
            }
        } else if (this.getDisguisedEntity() != null || this.getDisguisedEntityType() != null) {
            this.setDisguisedEntity(null);
            this.setDisguisedEntityType(null);
        }

        if (this.getDisguisedEntity() != null && this.getDisguisedEntity().getPose() != this.getPose()) {
            this.getDisguisedEntity().setPose(this.getPose());
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void applyBirtd(CallbackInfo ci) {
        if (this.hasEffect(SpeciesStatusEffects.BIRTD.get()) || this.hasEffect(SpeciesStatusEffects.STUCK.get())) ci.cancel();
    }

    @Inject(method = "makePoofParticles", at = @At("HEAD"), cancellable = true)
    public void S$makePoofParticles(CallbackInfo ci) {
        if (this instanceof CustomDeathParticles customDeathParticles) {
            ci.cancel();
            customDeathParticles.makeDeathParticles();
        }
    }


    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void S$isPushable(CallbackInfoReturnable<Boolean> cir) {
        if (this.hasEffect(SpeciesStatusEffects.BIRTD.get()) || this.hasEffect(SpeciesStatusEffects.STUCK.get())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getVisibilityPercent", at = @At("RETURN"), cancellable = true)
    private void onGetVisibilityPercent(@Nullable Entity observer, CallbackInfoReturnable<Double> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        double visibility = cir.getReturnValue();

        if (observer == null) return;

        ItemStack headItem = self.getItemBySlot(EquipmentSlot.HEAD);
        EntityType<?> observerType = observer.getType();

        if (observerType == SpeciesEntities.GHOUL.get() && headItem.is(SpeciesItems.GHOUL_HEAD.get()) ||
                observerType == SpeciesEntities.WICKED.get() && headItem.is(SpeciesItems.WICKED_CANDLE.get()) ||
                observerType == SpeciesEntities.BEWEREAGER.get() && headItem.is(SpeciesItems.BEWEREAGER_HEAD.get()) ||
                observerType == SpeciesEntities.QUAKE.get() && headItem.is(SpeciesItems.QUAKE_HEAD.get())) {
            visibility *= 0.5D;
            cir.setReturnValue(visibility);
        }
    }

    @Override
    public @Unique boolean hasSnatched() {
        return this.snatched;
    }
    @Override
    public @Unique void setSnatched(boolean snatched) {
        this.snatched = snatched;
    }

    @Override
    public @Unique boolean hasTanked() {
        return this.tanked;
    }
    @Override
    public @Unique void setTanked(boolean tanked) {
        this.tanked = tanked;
    }

    @Override
    public @Unique EntityType getDisguisedEntityType() {
        return disguisedEntityType;
    }
    @Override
    public @Unique void setDisguisedEntityType(EntityType disguisedEntityType) {
        this.disguisedEntityType = disguisedEntityType;
    }

    @Override
    public @Unique LivingEntity getDisguisedEntity() {
        return disguisedEntity;
    }
    @Override
    public @Unique void setDisguisedEntity(LivingEntity disguisedEntity) {
        this.disguisedEntity = disguisedEntity;
    }
}
