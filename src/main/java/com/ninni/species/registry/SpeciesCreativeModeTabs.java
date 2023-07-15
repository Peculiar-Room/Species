package com.ninni.species.registry;

import com.ninni.species.Species;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

public class SpeciesCreativeModeTabs {

    public static final CreativeModeTab SPECIES = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(Species.MOD_ID, "species"), FabricItemGroup.builder().title(Component.translatable("itemGroup.species.species")).icon(SpeciesItems.BIRT_EGG::getDefaultInstance).displayItems((itemDisplayParameters, output) -> {
        output.accept(SpeciesItems.WRAPTOR_SPAWN_EGG);
        output.accept(SpeciesItems.WRAPTOR_EGG);
        output.accept(SpeciesItems.CRACKED_WRAPTOR_EGG);
        output.accept(SpeciesItems.DEEPFISH_SPAWN_EGG);
        output.accept(SpeciesItems.DEEPFISH_BUCKET);
        output.accept(SpeciesItems.ROOMBUG_SPAWN_EGG);
        output.accept(SpeciesItems.BIRT_SPAWN_EGG);
        output.accept(SpeciesItems.BIRT_EGG);
        output.accept(SpeciesItems.BIRT_DWELLING);
        output.accept(SpeciesItems.MUSIC_DISC_DIAL);
        output.accept(SpeciesItems.LIMPET_SPAWN_EGG);
    }).build());

    static {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.addAfter(Items.HAY_BLOCK, SpeciesItems.BIRT_DWELLING);
            entries.addAfter(Items.SNIFFER_EGG, SpeciesItems.WRAPTOR_EGG);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.addAfter(Items.MUSIC_DISC_RELIC, SpeciesItems.MUSIC_DISC_DIAL);
            entries.addAfter(Items.PUFFERFISH_BUCKET, SpeciesItems.DEEPFISH_BUCKET);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.addAfter(Items.EGG, SpeciesItems.BIRT_EGG);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.EGG, SpeciesItems.BIRT_EGG);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
            entries.addAfter(Items.ENCHANTED_GOLDEN_APPLE, SpeciesItems.CRACKED_WRAPTOR_EGG);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.addAfter(Items.WITHER_SKELETON_SPAWN_EGG, SpeciesItems.WRAPTOR_SPAWN_EGG);
            entries.addAfter(Items.LLAMA_SPAWN_EGG, SpeciesItems.LIMPET_SPAWN_EGG);
            entries.addAfter(Items.DROWNED_SPAWN_EGG, SpeciesItems.DEEPFISH_SPAWN_EGG);
            entries.addAfter(Items.BEE_SPAWN_EGG, SpeciesItems.BIRT_SPAWN_EGG);
            entries.addAfter(Items.RABBIT_SPAWN_EGG, SpeciesItems.ROOMBUG_SPAWN_EGG);
            entries.addAfter(Items.TRADER_LLAMA_SPAWN_EGG, SpeciesItems.TREEPER_SPAWN_EGG);
        });
    }

}
