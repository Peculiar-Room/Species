package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class SpeciesDamageTypes {
    public static final ResourceKey<DamageType> CRUNCH = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Species.MOD_ID, "crunch"));
    public static final ResourceKey<DamageType> TORN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Species.MOD_ID, "torn"));
    public static final ResourceKey<DamageType> KINETIC = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Species.MOD_ID, "kinetic"));
    public static final ResourceKey<DamageType> CRANKTRAP = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Species.MOD_ID, "cranktrap"));

}