package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public interface SpeciesTags {
    //itemTags
    TagKey<Item> WRAPTOR_BREED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Species.MOD_ID, "wraptor_breed_items"));
    TagKey<Item> STACKATICK_BREED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Species.MOD_ID, "stackatick_breed_items"));
    TagKey<Item> STACKATICK_TEMPT_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Species.MOD_ID, "stackatick_tempt_items"));
    TagKey<Item> STACKATICK_TAME_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Species.MOD_ID, "stackatick_tame_items"));
    TagKey<Item> GOOBER_BREED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Species.MOD_ID, "goober_breed_items"));
    TagKey<Item> SPRINGLING_BREED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Species.MOD_ID, "springling_breed_items"));
    TagKey<Item> SPRINGLING_TAMING_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(Species.MOD_ID, "springling_taming_items"));
    TagKey<Item> CRUNCHER_EATS = TagKey.create(Registries.ITEM, new ResourceLocation(Species.MOD_ID, "cruncher_eats"));
    TagKey<Item> BURNS_TREEPER = TagKey.create(Registries.ITEM, new ResourceLocation(Species.MOD_ID, "burns_treeper"));
    TagKey<Item> EXTINGUISHES_TREEPER = TagKey.create(Registries.ITEM, new ResourceLocation(Species.MOD_ID, "extinguishes_treeper"));

    //blockTags
    TagKey<Block> STACKATICK_IS_COMFY_ON = TagKey.create(Registries.BLOCK, new ResourceLocation(Species.MOD_ID, "stackatick_is_comfy_on"));
    TagKey<Block> WRAPTOR_NESTING_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(Species.MOD_ID, "wraptor_nesting_blocks"));
    TagKey<Block> LIMPET_SPAWNABLE_ON = TagKey.create(Registries.BLOCK, new ResourceLocation(Species.MOD_ID, "limpet_spawnable_on"));
    TagKey<Block> TREEPER_SPAWNABLE_ON = TagKey.create(Registries.BLOCK, new ResourceLocation(Species.MOD_ID, "treeper_spawnable_on"));
    TagKey<Block> PETRIFIED_EGG_HATCH = TagKey.create(Registries.BLOCK, new ResourceLocation(Species.MOD_ID, "petrified_egg_hatch"));
    TagKey<Block> PETRIFIED_EGG_HATCH_BOOST = TagKey.create(Registries.BLOCK, new ResourceLocation(Species.MOD_ID, "petrified_egg_hatch_boost"));
    TagKey<Block> MAMMUTILATION_REMNANT_INVALID_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(Species.MOD_ID, "mammutilation_remnant_invalid_blocks"));
    TagKey<Block> MAMMUTILATION_BODY_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(Species.MOD_ID, "mammutilation_body_blocks"));
    TagKey<Block> CLIFF_HANGER_SPAWNABLE_ON = TagKey.create(Registries.BLOCK, new ResourceLocation(Species.MOD_ID, "cliff_hanger_spawnable_on"));

    //biomeTags
    TagKey<Biome> WRAPTOR_COOP_HAS_STRUCTURE = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "wraptor_coop_has_structure"));
    TagKey<Biome> STACKATICK_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "stackatick_spawns"));
    TagKey<Biome> BIRT_TREE_SPAWNS_IN = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "birt_tree_spawns_in"));
    TagKey<Biome> MAMMUTILATION_REMNANT_SPAWNS_IN = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "mammutilation_remnant_spawns_in"));
    TagKey<Biome> LIMPET_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "limpet_spawns"));
    TagKey<Biome> WITHOUT_LIMPET_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "without_limpet_spawns"));
    TagKey<Biome> TREEPER_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "treeper_spawns"));
    TagKey<Biome> LIBRA_HAS_STRUCTURE = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "libra_has_structure"));
    TagKey<Biome> SPECTRALIBUR_CHAMBER_HAS_STRUCTURE = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "spectralibur_chamber_has_structure"));
    TagKey<Biome> LEAF_HANGER_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "leaf_hanger_spawns"));
    TagKey<Biome> LEAF_HANGER_HAS_DRIPLEAF = TagKey.create(Registries.BIOME, new ResourceLocation(Species.MOD_ID, "leaf_hanger_has_dripleaf"));

    //pointOfInterestTags
    TagKey<PoiType> BIRT_HOME = TagKey.create(Registries.POINT_OF_INTEREST_TYPE, new ResourceLocation(Species.MOD_ID, "birt_home"));

    //entityTags
    TagKey<EntityType<?>> ALWAYS_ADULT = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Species.MOD_ID, "always_adult"));
    TagKey<EntityType<?>> CANT_BE_DAMAGED_BY_DUMMY = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Species.MOD_ID, "cant_be_damaged_by_dummy"));
    TagKey<EntityType<?>> CANT_BE_TARGETED_BY_GHOUL = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Species.MOD_ID, "cant_be_targeted_by_ghoul"));
    TagKey<EntityType<?>> ATTACKED_BY_BEWEREAGER = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Species.MOD_ID, "attacked_by_bewereager"));
    TagKey<EntityType<?>> SOULLESS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Species.MOD_ID, "soulless"));
    TagKey<EntityType<?>> CANT_BE_HAUNTED = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Species.MOD_ID, "cant_be_haunted"));
    TagKey<EntityType<?>> CAN_BE_HAUNTED_EXTRAS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Species.MOD_ID, "can_be_haunted_extras"));
    TagKey<EntityType<?>> CLIFF_HANGER_PREY = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Species.MOD_ID, "cliff_hanger_prey"));
    TagKey<EntityType<?>> LEAF_HANGER_PREY = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Species.MOD_ID, "leaf_hanger_prey"));
    TagKey<EntityType<?>> PREHISTORIC = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Species.MOD_ID, "prehistoric"));
}
