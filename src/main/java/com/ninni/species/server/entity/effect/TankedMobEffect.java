package com.ninni.species.server.entity.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class TankedMobEffect extends MobEffect {
    public TankedMobEffect(MobEffectCategory p_19433_, int p_19434_) {
        super(p_19433_, p_19434_);
    }

    //public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
    //    super.addAttributeModifiers(attributeMap, amplifier);
    //    entity.heal(Math.min(entity.getMaxHealth(), (amplifier * 4) + 4));
    //}

    public void onEffectStarted(LivingEntity entity, int amplifier) {
        entity.heal(Math.min(entity.getMaxHealth(), (amplifier * 4) + 4));
    }
}
