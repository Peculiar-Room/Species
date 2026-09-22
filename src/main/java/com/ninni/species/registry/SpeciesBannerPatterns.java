package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesBannerPatterns {

    //public static final DeferredRegister<BannerPattern> BANNER_PATTERNS = DeferredRegister.create(Registries.BANNER_PATTERN, Species.MOD_ID);

    public static final ResourceKey<BannerPattern> VILLAGER = ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "villager"));
}
