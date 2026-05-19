package com.ninni.species.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Exposes {@code LivingEntity}'s protected hurt/death sound getters so a disguised player can
 *  fall back to the disguised mob's own sounds when no data-driven override is configured. */
@Mixin(LivingEntity.class)
public interface LivingEntitySoundAccessor {

    @Invoker("getHurtSound")
    SoundEvent species$invokeGetHurtSound(DamageSource source);

    @Invoker("getDeathSound")
    SoundEvent species$invokeGetDeathSound();
}
