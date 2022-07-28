package com.ninni.species.item;

import com.ninni.species.block.SpeciesBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.ninni.species.Species.*;

@SuppressWarnings("unused")
public class SpeciesItems {
    public static final Item WRAPTOR_EGG = register("wraptor_egg", new BlockItem(SpeciesBlocks.WRAPTOR_EGG, new FabricItemSettings().group(ITEM_GROUP)));
    public static final Item CRACKED_WRAPTOR_EGG = register("cracked_wraptor_egg", new Item(new FabricItemSettings().group(ITEM_GROUP)));

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, id), item);
    }
}
