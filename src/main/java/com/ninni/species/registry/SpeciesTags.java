package com.ninni.species.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import static com.ninni.species.Species.MOD_ID;

@SuppressWarnings("unused")
public interface SpeciesTags {
    //itemTags
    TagKey<Item> WRAPTOR_BREED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(MOD_ID, "wraptor_breed_items"));
    TagKey<Item> GOOBER_BREED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(MOD_ID, "goober_breed_items"));
    TagKey<Item> SPRINGLING_BREED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(MOD_ID, "springling_breed_items"));
    TagKey<Item> SPRINGLING_TAMING_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(MOD_ID, "springling_taming_items"));

    //blockTags
    TagKey<Block> WRAPTOR_NESTING_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "wraptor_nesting_blocks"));
    TagKey<Block> LIMPET_SPAWNABLE_ON = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "limpet_spawnable_on"));
    TagKey<Block> TREEPER_SPAWNABLE_ON = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "treeper_spawnable_on"));
    TagKey<Block> PETRIFIED_EGG_HATCH = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "petrified_egg_hatch"));
    TagKey<Block> PETRIFIED_EGG_HATCH_BOOST = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "petrified_egg_hatch_boost"));
    TagKey<Block> MAMMUTILATION_REMNANT_INVALID_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "mammutilation_remnant_invalid_blocks"));
    TagKey<Block> MAMMUTILATION_BODY_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "mammutilation_body_blocks"));

    //biomeTags
    TagKey<Biome> WRAPTOR_COOP_HAS_STRUCTURE = TagKey.create(Registries.BIOME, new ResourceLocation(MOD_ID, "wraptor_coop_has_structure"));
    TagKey<Biome> ROOMBUG_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(MOD_ID, "roombug_spawns"));
    TagKey<Biome> BIRT_TREE_SPAWNS_IN = TagKey.create(Registries.BIOME, new ResourceLocation(MOD_ID, "birt_tree_spawns_in"));
    TagKey<Biome> MAMMUTILATION_REMNANT_SPAWNS_IN = TagKey.create(Registries.BIOME, new ResourceLocation(MOD_ID, "mammutilation_remnant_spawns_in"));
    TagKey<Biome> WITHOUT_LIMPET_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(MOD_ID, "without_limpet_spawns"));
    TagKey<Biome> TREEPER_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(MOD_ID, "treeper_spawns"));

    //pointOfInterestTags
    TagKey<PoiType> BIRT_HOME = TagKey.create(Registries.POINT_OF_INTEREST_TYPE, new ResourceLocation(MOD_ID, "birt_home"));
}
