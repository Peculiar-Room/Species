package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.entity.effect.GutFeelingEffect;
import com.ninni.species.server.entity.effect.PublicStatusEffect;
import com.ninni.species.server.entity.effect.TankedMobEffect;
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
    public static final RegistryObject<MobEffect> BIRTD = MOB_EFFECTS.register("birtd", () -> new PublicStatusEffect(MobEffectCategory.HARMFUL, 0x4DD1E1).addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -100f, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.FLYING_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -100f, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "7107DE5E-7CE8-4030-940E-514C1F160890", 1f, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> GUT_FEELING = MOB_EFFECTS.register("gut_feeling", () -> new GutFeelingEffect(MobEffectCategory.HARMFUL, 0x5F2FCF));

    public static final RegistryObject<MobEffect> BLOODLUST = MOB_EFFECTS.register("bloodlust", () -> new PublicStatusEffect(MobEffectCategory.HARMFUL, 0x460000));
    public static final RegistryObject<MobEffect> IRON_WILL = MOB_EFFECTS.register("iron_will", () -> new PublicStatusEffect(MobEffectCategory.BENEFICIAL, 0x555B63).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "7107DE5E-7CE8-4030-940E-514C1F160890", 1f, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> TANKED = MOB_EFFECTS.register("tanked", () -> new TankedMobEffect(MobEffectCategory.BENEFICIAL, 0xF93985).addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, AttributeModifier.Operation.ADDITION).addAttributeModifier(Attributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 3.0D, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> SNATCHED = MOB_EFFECTS.register("snatched", () -> new PublicStatusEffect(MobEffectCategory.BENEFICIAL, 0xB22EB8).addAttributeModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.3F, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> COMBUSTION = MOB_EFFECTS.register("combustion", () -> new PublicStatusEffect(MobEffectCategory.NEUTRAL, 0xFFA342));
    public static final RegistryObject<MobEffect> STUCK = MOB_EFFECTS.register("stuck", () -> new PublicStatusEffect(MobEffectCategory.HARMFUL, 0x4DD1E1).addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -100f, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.FLYING_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -100f, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "7107DE5E-7CE8-4030-940E-514C1F160890", 1f, AttributeModifier.Operation.ADDITION));

}
