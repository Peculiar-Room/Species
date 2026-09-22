package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class SpeciesLootTables {
    public static ResourceKey<@NotNull LootTable> WRAPTOR_COOP_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "chests/wraptor_coop_chest"));
    public static ResourceKey<@NotNull LootTable> LIBRA_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "chests/libra_chest"));
    public static ResourceKey<@NotNull LootTable> PALEONTOLOGY_DIG_SITE_COMMON = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "archaeology/paleontology_dig_site/common"));
    public static ResourceKey<@NotNull LootTable> PALEONTOLOGY_DIG_SITE_RARE = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "archaeology/paleontology_dig_site/rare"));
    public static ResourceKey<@NotNull LootTable> PALEONTOLOGY_DIG_SITE_EPIC = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "archaeology/paleontology_dig_site/epic"));
}
