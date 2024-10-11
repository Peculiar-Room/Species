package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.entity.effect.GutFeelingEffect;
import com.ninni.species.entity.effect.PublicStatusEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesStatusEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Species.MOD_ID);

    public static final RegistryObject<MobEffect> WITHER_RESISTANCE = MOB_EFFECTS.register("wither_resistance", () -> new PublicStatusEffect(MobEffectCategory.BENEFICIAL, 0x71747B));
    public static final RegistryObject<MobEffect> BIRTD = MOB_EFFECTS.register("birtd", () -> new PublicStatusEffect(MobEffectCategory.HARMFUL, 0x53C7BE).addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -100f, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "7107DE5E-7CE8-4030-940E-514C1F160890", 1f, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> GUT_FEELING = MOB_EFFECTS.register("gut_feeling", () -> new GutFeelingEffect(MobEffectCategory.HARMFUL, 0x5F2FCF));

}
