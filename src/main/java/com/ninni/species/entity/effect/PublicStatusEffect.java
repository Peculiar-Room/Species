package com.ninni.species.entity.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PublicStatusEffect extends MobEffect {
    public PublicStatusEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }
}
