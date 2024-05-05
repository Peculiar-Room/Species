package com.ninni.species.mixin;

import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesStatusEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean hasEffect(MobEffect effect);

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    public void applyWitherResistance(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasEffect(SpeciesStatusEffects.WITHER_RESISTANCE) && effect.getEffect() == MobEffects.WITHER) cir.setReturnValue(false);
    }

    @Inject(method = "tickEffects", at = @At("HEAD"))
    public void applyBirtdParticles(CallbackInfo ci) {
        LivingEntity that = (LivingEntity) (Object) this;
        if (this.hasEffect(SpeciesStatusEffects.GUT_FEELING) ) {
            if (that.getEffect(SpeciesStatusEffects.GUT_FEELING).getDuration() < 20 * 60 * 5) {
                if (that.getRandom().nextInt(200) == 0) that.playSound(SpeciesSoundEvents.GUT_FEELING_ROAR, 0.2f, 0);
            }
            else {
                if (that.getRandom().nextInt(800) == 0) that.playSound(SpeciesSoundEvents.GUT_FEELING_ROAR, 0.2f, 0);
            }
        }
        if (this.hasEffect(SpeciesStatusEffects.BIRTD) && that.level() instanceof ServerLevel world) {
            if (that.tickCount % 10 == 1) {
                world.sendParticles(SpeciesParticles.BIRTD, that.getX(), that.getEyeY() + 0.5F, that.getZ() - 0.5, 1,0, 0, 0, 0);
            }
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void applyBirtd(CallbackInfo ci) {
        if (this.hasEffect(SpeciesStatusEffects.BIRTD)) ci.cancel();
    }
}
