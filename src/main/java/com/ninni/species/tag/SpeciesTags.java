package com.ninni.species.tag;

import net.minecraft.core.Registry;
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
    TagKey<Item> WRAPTOR_BREED_ITEMS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MOD_ID, "wraptor_breed_items"));

    //blockTags
    TagKey<Block> WRAPTOR_NESTING_BLOCKS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(MOD_ID, "wraptor_nesting_blocks"));

    //biomeTags
    TagKey<Biome> WRAPTOR_COOP_HAS_STRUCTURE = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "wraptor_coop_has_structure"));
    TagKey<Biome> ROOMBUG_SPAWNS = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "roombug_spawns"));
    TagKey<Biome> BIRT_TREE_SPAWNS_IN = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "birt_tree_spawns_in"));

    //pointOfInterestTags
    TagKey<PoiType> BIRT_HOME = TagKey.create(Registry.POINT_OF_INTEREST_TYPE_REGISTRY, new ResourceLocation(MOD_ID, "birt_home"));
}
