package com.ninni.species.mixin.iceandfire;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.ninni.species.client.events.WickedMaskDisguiseEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

/** Soft-dep: suppresses the wing-flap sound for disguise-tagged Hippogryph bodies on the IaF-CE port
 *  (its {@code aiStep} calls {@code LivingEntity.playSound}, needing a target separate from {@link EntityHippogryphMixin}). */
@Pseudo
@Mixin(targets = "com.iafenvoy.iceandfire.entity.HippogryphEntity", remap = false)
public abstract class HippogryphEntityMixin {

    @WrapWithCondition(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private boolean species$suppressWingFlapForDisguise(@Coerce Object self, SoundEvent sound, float volume, float pitch) {
        if (self instanceof LivingEntity le && le.getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) {
            return false;
        }
        return true;
    }
}
