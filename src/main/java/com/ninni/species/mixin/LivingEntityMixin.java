package com.ninni.species.mixin;

import com.ninni.species.entity.effect.SpeciesStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Inject(method = "canHaveStatusEffect", at = @At("HEAD"), cancellable = true)
    public void applyWitherResistance(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasStatusEffect(SpeciesStatusEffects.WITHER_RESISTANCE) && effect.getEffectType() == StatusEffects.WITHER) cir.setReturnValue(false);
    }
}
