package com.ninni.species.init;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class SpeciesDamageTypes {
    public static final ResourceKey<DamageType> CRUNCH = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Species.MOD_ID, "crunch"));

}