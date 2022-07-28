package com.ninni.species;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.ninni.species.Species.*;

@SuppressWarnings("unused")
public interface SpeciesTags {
    //itemTags
    TagKey<Item> WRAPTOR_TEMPT_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier(MOD_ID, "wraptor_tempt_items"));

    //blockTags
    TagKey<Block> WRAPTOR_NESTING_BLOCKS = TagKey.of(Registry.BLOCK_KEY, new Identifier(MOD_ID, "wraptor_nesting_blocks"));
}
