package com.ninni.species.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.ninni.species.Species.*;

public class SpeciesStatusEffects {
    public static final StatusEffect WITHER_RESISTANCE = register("wither_resistance", new PublicStatusEffect(StatusEffectCategory.BENEFICIAL, 0x71747B));
    public static final StatusEffect BIRTD = register("birtd", new PublicStatusEffect(StatusEffectCategory.HARMFUL, 0x53C7BE));

    private static StatusEffect register(String id, StatusEffect effect) { return Registry.register(Registry.STATUS_EFFECT, new Identifier(MOD_ID, id), effect); }
}
