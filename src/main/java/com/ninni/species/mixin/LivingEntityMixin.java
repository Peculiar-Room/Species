package com.ninni.species.mixin;

import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.entity.effect.SpeciesStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Inject(method = "canHaveStatusEffect", at = @At("HEAD"), cancellable = true)
    public void applyWitherResistance(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasStatusEffect(SpeciesStatusEffects.WITHER_RESISTANCE) && effect.getEffectType() == StatusEffects.WITHER) cir.setReturnValue(false);
    }

    @Inject(method = "tickStatusEffects", at = @At("HEAD"))
    public void applyBirtdParticles(CallbackInfo ci) {
        LivingEntity that = (LivingEntity) (Object) this;
        if (this.hasStatusEffect(SpeciesStatusEffects.BIRTD) && that.world instanceof ServerWorld world) {
            if (that.age % 10 == 1) {
                world.spawnParticles(SpeciesParticles.BIRTD, that.getX(), that.getEyeY() + 0.5F, that.getZ() - 0.5, 1,0, 0, 0, 0);
            }
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void applyBirtd(CallbackInfo ci) {
        if (this.hasStatusEffect(SpeciesStatusEffects.BIRTD)) ci.cancel();
    }
}
