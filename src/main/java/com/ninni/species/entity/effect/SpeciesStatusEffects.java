package com.ninni.species.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.ninni.species.Species.*;

public class SpeciesStatusEffects {
    public static final StatusEffect WITHER_RESISTANCE = register("wither_resistance", new PublicStatusEffect(StatusEffectCategory.BENEFICIAL, 0x71747B));
    public static final StatusEffect BIRTD = register("birtd", new PublicStatusEffect(StatusEffectCategory.HARMFUL, 0x53C7BE).addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -100f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

    private static StatusEffect register(String id, StatusEffect effect) { return Registry.register(Registry.STATUS_EFFECT, new Identifier(MOD_ID, id), effect); }
}
