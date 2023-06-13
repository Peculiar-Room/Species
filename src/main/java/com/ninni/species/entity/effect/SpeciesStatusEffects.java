package com.ninni.species.entity.effect;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesStatusEffects {
    public static final MobEffect WITHER_RESISTANCE = register("wither_resistance", new PublicStatusEffect(MobEffectCategory.BENEFICIAL, 0x71747B));
    public static final MobEffect BIRTD = register("birtd", new PublicStatusEffect(MobEffectCategory.HARMFUL, 0x53C7BE).addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -100f, AttributeModifier.Operation.MULTIPLY_TOTAL));

    private static MobEffect register(String id, MobEffect effect) { return Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(MOD_ID, id), effect); }
}
