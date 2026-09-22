package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class SpeciesEnchantments {

    public static final ResourceKey<Enchantment> CAPACITY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "capacity"));
    public static final ResourceKey<Enchantment> QUICK_CRANK = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "quick_crank"));
    public static final ResourceKey<Enchantment> SCATTERSHOT = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "scattershot"));
    public static final ResourceKey<Enchantment> SPARING = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "sparing"));
}
