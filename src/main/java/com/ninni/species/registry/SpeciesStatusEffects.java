package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.entity.effect.GutFeelingEffect;
import com.ninni.species.server.entity.effect.PublicStatusEffect;
import com.ninni.species.server.entity.effect.TankedMobEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesStatusEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Species.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> WITHER_RESISTANCE = MOB_EFFECTS.register("wither_resistance", () -> new PublicStatusEffect(MobEffectCategory.BENEFICIAL, 0x71747B));
    public static final DeferredHolder<MobEffect, MobEffect> BIRTD = MOB_EFFECTS.register("birtd", () -> new PublicStatusEffect(MobEffectCategory.HARMFUL, 0x4DD1E1)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.speed.birtd"), -100f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.FLYING_SPEED, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.flying_speed.birtd"), -100f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.knockback_resist.birtd"), 1f, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, MobEffect> GUT_FEELING = MOB_EFFECTS.register("gut_feeling", () -> new GutFeelingEffect(MobEffectCategory.HARMFUL, 0x5F2FCF));

    public static final DeferredHolder<MobEffect, MobEffect> BLOODLUST = MOB_EFFECTS.register("bloodlust", () -> new PublicStatusEffect(MobEffectCategory.HARMFUL, 0x460000));
    public static final DeferredHolder<MobEffect, MobEffect> IRON_WILL = MOB_EFFECTS.register("iron_will", () -> new PublicStatusEffect(MobEffectCategory.BENEFICIAL, 0x555B63)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.knockback_resist.iron_will"), 1f, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> TANKED = MOB_EFFECTS.register("tanked", () -> new TankedMobEffect(MobEffectCategory.BENEFICIAL, 0xF93985)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.speed.tanked"), -0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.health_boost.tanked"), 4.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.strength.tanked"), 3.0D, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> SNATCHED = MOB_EFFECTS.register("snatched", () -> new PublicStatusEffect(MobEffectCategory.BENEFICIAL, 0xB22EB8)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.speed.snatched"), 0.3F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> COMBUSTION = MOB_EFFECTS.register("combustion", () -> new PublicStatusEffect(MobEffectCategory.NEUTRAL, 0xFFA342));
    public static final DeferredHolder<MobEffect, MobEffect> STUCK = MOB_EFFECTS.register("stuck", () -> new PublicStatusEffect(MobEffectCategory.HARMFUL, 0x4DD1E1)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.speed.stuck"), -100f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.FLYING_SPEED, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.flying_speed.stuck"), -100f,AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "effect.knockback_resist.stuck"), 1f,AttributeModifier.Operation.ADD_VALUE));

}
