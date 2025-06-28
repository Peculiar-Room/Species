package com.ninni.species.server.entity.effect;

import net.minecraft.world.effect.HealthBoostMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class TankedMobEffect extends HealthBoostMobEffect {
    public TankedMobEffect(MobEffectCategory p_19433_, int p_19434_) {
        super(p_19433_, p_19434_);
    }

    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        entity.heal(Math.min(entity.getMaxHealth(), (amplifier * 4) + 4));
    }
}
