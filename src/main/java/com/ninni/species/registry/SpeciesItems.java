package com.ninni.species.registry;

import com.ninni.species.SpeciesDevelopers;
import com.ninni.species.item.BirtEggItem;
import com.ninni.species.item.CrakedWraptorEggItem;
import com.ninni.species.item.SpeciesSpawnEgg;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.Fluids;

import static com.ninni.species.Species.MOD_ID;

@SuppressWarnings("unused")
public class SpeciesItems {

    //UPDATE 1
    public static final Item WRAPTOR_SPAWN_EGG = register("wraptor_spawn_egg", new SpeciesSpawnEgg(SpeciesEntities.WRAPTOR, 0xAF3A5F, 0x55C1A9, SpeciesDevelopers.NOON, new Item.Properties().stacksTo(64)));
    public static final Item WRAPTOR_EGG = register("wraptor_egg", new BlockItem(SpeciesBlocks.WRAPTOR_EGG, new FabricItemSettings()));
    public static final Item CRACKED_WRAPTOR_EGG = register("cracked_wraptor_egg", new CrakedWraptorEggItem(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.7f).effect(new MobEffectInstance(SpeciesStatusEffects.WITHER_RESISTANCE, 20 * 90, 0), 1).build())));

    public static final Item DEEPFISH_SPAWN_EGG = register("deepfish_spawn_egg", new SpeciesSpawnEgg(SpeciesEntities.DEEPFISH, 0x5A5A5A, 0xED98BD, SpeciesDevelopers.BORNHULU, new Item.Properties().stacksTo(64)));
    public static final Item DEEPFISH_BUCKET = register("deepfish_bucket", new MobBucketItem(SpeciesEntities.DEEPFISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, new FabricItemSettings().stacksTo(1)));

    public static final Item ROOMBUG_SPAWN_EGG = register("roombug_spawn_egg", new SpeciesSpawnEgg(SpeciesEntities.ROOMBUG, 0x5A3C29, 0xB4584D, SpeciesDevelopers.NINNI, new Item.Properties().stacksTo(64)));

    public static final Item BIRT_SPAWN_EGG = register("birt_spawn_egg", new SpeciesSpawnEgg(SpeciesEntities.BIRT, 0x53C7BE, 0xD95B4D, SpeciesDevelopers.REDA, new Item.Properties().stacksTo(64)));
    public static final Item BIRT_EGG = register("birt_egg", new BirtEggItem(new Item.Properties().stacksTo(16)));
    public static final Item BIRT_DWELLING = register("birt_dwelling", new BlockItem(SpeciesBlocks.BIRT_DWELLING, new FabricItemSettings()));
    public static final Item MUSIC_DISC_DIAL = register("music_disc_dial", new RecordItem(11, SpeciesSoundEvents.MUSIC_DISC_DIAL, new FabricItemSettings().rarity(Rarity.RARE).stacksTo(1), 193));

    public static final Item LIMPET_SPAWN_EGG = register("limpet_spawn_egg", new SpeciesSpawnEgg(SpeciesEntities.LIMPET, 0xA5C1D2, 0xFBF236, SpeciesDevelopers.GLADOS, new Item.Properties().stacksTo(64)));

    //UPDATE 2
    public static final Item RED_SUSPICIOUS_SAND = register("red_suspicious_sand", new BlockItem(SpeciesBlocks.RED_SUSPICIOUS_SAND, new FabricItemSettings()));

    public static final Item BONE_BARK = register("bone_bark", new BlockItem(SpeciesBlocks.BONE_BARK, new FabricItemSettings()));
    public static final Item BONE_VERTEBRA = register("bone_vertebra", new BlockItem(SpeciesBlocks.BONE_VERTEBRA, new FabricItemSettings()));
    public static final Item BONE_SPIKE = register("bone_spike", new BlockItem(SpeciesBlocks.BONE_SPIKE, new FabricItemSettings()));

    public static final Item TREEPER_SPAWN_EGG = register("treeper_spawn_egg", new SpeciesSpawnEgg(SpeciesEntities.TREEPER, 0x402E1B, 0x32992D, SpeciesDevelopers.NINNI, new Item.Properties().stacksTo(64)));
    public static final Item ANCIENT_PINECONE = register("ancient_pinecone", new ItemNameBlockItem(SpeciesBlocks.TROOPER, new FabricItemSettings()));
    public static final Item TROOPER_SPAWN_EGG = register("trooper_spawn_egg", new SpawnEggItem(SpeciesEntities.TROOPER, 0x6f5535, 0x32992D, new Item.Properties().stacksTo(64)));

    public static final Item GOOBER_SPAWN_EGG = register("goober_spawn_egg", new SpeciesSpawnEgg(SpeciesEntities.GOOBER, 0x49674E, 0x49674E, SpeciesDevelopers.BORNHULU, new Item.Properties().stacksTo(64)));
    public static final Item PETRIFIED_EGG = register("petrified_egg", new BlockItem(SpeciesBlocks.PETRIFIED_EGG, new FabricItemSettings()));

    public static final Item CRUNCHER_SPAWN_EGG = register("cruncher_spawn_egg", new SpeciesSpawnEgg(SpeciesEntities.CRUNCHER, 0x5522B6, 0x99032B, SpeciesDevelopers.NOON, new Item.Properties().stacksTo(64)));
    public static final Item CRUNCHER_EGG = register("cruncher_egg", new DoubleHighBlockItem(SpeciesBlocks.CRUNCHER_EGG, new FabricItemSettings()));
    public static final Item CRUNCHER_PELLET = register("cruncher_pellet", new BlockItem(SpeciesBlocks.CRUNCHER_PELLET, new FabricItemSettings()));

    public static final Item MAMMUTILATION_SPAWN_EGG = register("mammutilation_spawn_egg", new SpeciesSpawnEgg(SpeciesEntities.MAMMUTILATION, 0x472418, 0xDE5D34, SpeciesDevelopers.REDA, new Item.Properties().stacksTo(64)));
    public static final Item FROZEN_MEAT = register("frozen_meat", new BlockItem(SpeciesBlocks.FROZEN_MEAT, new FabricItemSettings()));
    public static final Item FROZEN_HAIR = register("frozen_hair", new BlockItem(SpeciesBlocks.FROZEN_HAIR, new FabricItemSettings()));

    public static final Item SPRINGLING_SPAWN_EGG = register("springling_spawn_egg", new SpeciesSpawnEgg(SpeciesEntities.SPRINGLING, 0xFF7600, 0x3D3FAD, SpeciesDevelopers.GLADOS, new Item.Properties().stacksTo(64)));
    public static final Item SPRINGLING_EGG = register("springling_egg", new DoubleHighBlockItem(SpeciesBlocks.SPRINGLING_EGG, new FabricItemSettings()));

    private static Item register(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, id), item);
    }
}
