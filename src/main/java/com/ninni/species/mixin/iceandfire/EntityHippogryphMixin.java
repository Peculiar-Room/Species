package com.ninni.species.mixin.iceandfire;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.ninni.species.client.events.WickedMaskDisguiseEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

/** Suppresses the wing-flap sound for disguise-tagged Hippogryph bodies. Soft-dep, fail silent. */
@Pseudo
@Mixin(targets = "com.github.alexthe666.iceandfire.entity.EntityHippogryph", remap = false)
public abstract class EntityHippogryphMixin {

    @WrapWithCondition(
            method = "m_8107_",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/alexthe666/iceandfire/entity/EntityHippogryph;m_5496_(Lnet/minecraft/sounds/SoundEvent;FF)V",
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
