package com.ninni.species.tag;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.poi.PointOfInterestType;

import static com.ninni.species.Species.*;

@SuppressWarnings("unused")
public interface SpeciesTags {
    //itemTags
    TagKey<Item> WRAPTOR_BREED_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier(MOD_ID, "wraptor_breed_items"));

    //blockTags
    TagKey<Block> WRAPTOR_NESTING_BLOCKS = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "wraptor_nesting_blocks"));

    //biomeTags
    TagKey<Biome> WRAPTOR_COOP_HAS_STRUCTURE = TagKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "wraptor_coop_has_structure"));
    TagKey<Biome> ROOMBUG_SPAWNS = TagKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "roombug_spawns"));

    //pointOfInterestTags
    TagKey<PointOfInterestType> BIRT_HOME = TagKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, new Identifier(MOD_ID, "birt_home"));
}
