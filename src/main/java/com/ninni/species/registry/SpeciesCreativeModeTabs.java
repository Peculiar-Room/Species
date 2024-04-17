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

        output.accept(SpeciesItems.RED_SUSPICIOUS_SAND);

        output.accept(SpeciesItems.BONE_BARK);
        output.accept(SpeciesItems.BONE_VERTEBRA);
        output.accept(SpeciesItems.BONE_SPIKE);

        output.accept(SpeciesItems.TREEPER_SPAWN_EGG);
        output.accept(SpeciesItems.ANCIENT_PINECONE);
        output.accept(SpeciesItems.TROOPER_SPAWN_EGG);

        output.accept(SpeciesItems.GOOBER_SPAWN_EGG);
        output.accept(SpeciesItems.PETRIFIED_EGG);
        output.accept(SpeciesItems.ALPHACENE_MOSS_BLOCK);
        output.accept(SpeciesItems.ALPHACENE_MOSS_CARPET);
        output.accept(SpeciesItems.ALPHACENE_GRASS_BLOCK);
        output.accept(SpeciesItems.ALPHACENE_GRASS);
        output.accept(SpeciesItems.ALPHACENE_TALL_GRASS);
        output.accept(SpeciesItems.ALPHACENE_MUSHROOM);
        output.accept(SpeciesItems.ALPHACENE_MUSHROOM_BLOCK);

        output.accept(SpeciesItems.CRUNCHER_SPAWN_EGG);
        output.accept(SpeciesItems.CRUNCHER_EGG);
        output.accept(SpeciesItems.CRUNCHER_PELLET);

        output.accept(SpeciesItems.MAMMUTILATION_SPAWN_EGG);
        output.accept(SpeciesItems.FROZEN_MEAT);
        output.accept(SpeciesItems.FROZEN_HAIR);

        output.accept(SpeciesItems.SPRINGLING_SPAWN_EGG);
        output.accept(SpeciesItems.SPRINGLING_EGG);

    }).build());

    static {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.addAfter(Items.HAY_BLOCK, SpeciesItems.BIRT_DWELLING);
            entries.addAfter(Items.TURTLE_EGG, SpeciesItems.WRAPTOR_EGG);
            entries.addBefore(Items.WHEAT_SEEDS, SpeciesItems.ANCIENT_PINECONE);
            entries.addAfter(Items.SNIFFER_EGG, SpeciesItems.CRUNCHER_EGG, SpeciesItems.SPRINGLING_EGG, SpeciesItems.PETRIFIED_EGG, SpeciesItems.FROZEN_MEAT, SpeciesItems.FROZEN_HAIR);
            entries.addAfter(Items.BONE_BLOCK, SpeciesItems.BONE_BARK, SpeciesItems.BONE_VERTEBRA, SpeciesItems.BONE_SPIKE);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.addAfter(Items.SUSPICIOUS_GRAVEL, SpeciesItems.CRUNCHER_PELLET);
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
            entries.addAfter(Items.TRADER_LLAMA_SPAWN_EGG, SpeciesItems.TREEPER_SPAWN_EGG, SpeciesItems.TROOPER_SPAWN_EGG);
            entries.addAfter(Items.CREEPER_SPAWN_EGG, SpeciesItems.CRUNCHER_SPAWN_EGG);
            entries.addAfter(Items.MAGMA_CUBE_SPAWN_EGG, SpeciesItems.MAMMUTILATION_SPAWN_EGG);
            entries.addAfter(Items.GOAT_SPAWN_EGG, SpeciesItems.GOOBER_SPAWN_EGG);
            entries.addAfter(Items.SPIDER_SPAWN_EGG, SpeciesItems.SPRINGLING_SPAWN_EGG);
        });
    }

}
