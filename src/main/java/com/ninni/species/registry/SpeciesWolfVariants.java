package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.WolfVariant;

public class SpeciesWolfVariants {

    public static final ResourceKey<WolfVariant> CURED_BEWEREAGER = ResourceKey.create(Registries.WOLF_VARIANT, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "cured_bewereager"));

}