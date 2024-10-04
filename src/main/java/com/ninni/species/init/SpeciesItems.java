package com.ninni.species.init;

import com.ninni.species.Species;
import com.ninni.species.SpeciesDevelopers;
import com.ninni.species.entity.effect.SpeciesStatusEffects;
import com.ninni.species.item.BirtEggItem;
import com.ninni.species.item.CrakedWraptorEggItem;
import com.ninni.species.item.IchorBottle;
import com.ninni.species.item.SpeciesSpawnEggItem;
import com.ninni.species.item.YouthPotion;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Species.MOD_ID);

    public static final RegistryObject<Item> WRAPTOR_SPAWN_EGG = ITEMS.register("wraptor_spawn_egg", () -> new ForgeSpawnEggItem(SpeciesEntities.WRAPTOR, 0xAF3A5F, 0x55C1A9, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> WRAPTOR_EGG = ITEMS.register("wraptor_egg", () -> new BlockItem(SpeciesBlocks.WRAPTOR_EGG.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRACKED_WRAPTOR_EGG = ITEMS.register("cracked_wraptor_egg", () -> new CrakedWraptorEggItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.7f).effect(new MobEffectInstance(SpeciesStatusEffects.WITHER_RESISTANCE.get(), 20 * 90, 0), 1).build())));

    public static final RegistryObject<Item> DEEPFISH_SPAWN_EGG = ITEMS.register("deepfish_spawn_egg", () -> new ForgeSpawnEggItem(SpeciesEntities.DEEPFISH, 0x5A5A5A, 0xED98BD, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> DEEPFISH_BUCKET = ITEMS.register("deepfish_bucket", () -> new MobBucketItem(SpeciesEntities.DEEPFISH, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ROOMBUG_SPAWN_EGG = ITEMS.register("roombug_spawn_egg", () -> new ForgeSpawnEggItem(SpeciesEntities.ROOMBUG, 0x5A3C29, 0xB4584D, new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> BIRT_SPAWN_EGG = ITEMS.register("birt_spawn_egg", () -> new ForgeSpawnEggItem(SpeciesEntities.BIRT, 0x53C7BE, 0xD95B4D, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> BIRT_EGG = ITEMS.register("birt_egg", () -> new BirtEggItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BIRT_DWELLING = ITEMS.register("birt_dwelling", () -> new BlockItem(SpeciesBlocks.BIRT_DWELLING.get(), new Item.Properties()));
    public static final RegistryObject<Item> MUSIC_DISC_DIAL = ITEMS.register("music_disc_dial", () -> new RecordItem(11, SpeciesSoundEvents.MUSIC_DISC_DIAL, new Item.Properties().rarity(Rarity.RARE).stacksTo(1), 193));

    public static final RegistryObject<Item> LIMPET_SPAWN_EGG = ITEMS.register("limpet_spawn_egg", () -> new ForgeSpawnEggItem(SpeciesEntities.LIMPET, 0xA5C1D2, 0xFBF236, new Item.Properties().stacksTo(64)));

    //UPDATE 2
    public static final RegistryObject<Item> RED_SUSPICIOUS_SAND = ITEMS.register("red_suspicious_sand", () -> new BlockItem(SpeciesBlocks.RED_SUSPICIOUS_SAND.get(), new Item.Properties()));
    public static final RegistryObject<Item> MUSIC_DISC_LAPIDARIAN = ITEMS.register("music_disc_lapidarian", () -> new RecordItem(14, SpeciesSoundEvents.MUSIC_DISC_LAPIDARIAN, new Item.Properties().rarity(Rarity.RARE).stacksTo(1), 200));

    public static final RegistryObject<Item> BONE_BARK = ITEMS.register("bone_bark", () -> new BlockItem(SpeciesBlocks.BONE_BARK.get(), new Item.Properties()));
    public static final RegistryObject<Item> BONE_VERTEBRA = ITEMS.register("bone_vertebra", () -> new BlockItem(SpeciesBlocks.BONE_VERTEBRA.get(), new Item.Properties()));
    public static final RegistryObject<Item> BONE_SPIKE = ITEMS.register("bone_spike", () -> new BlockItem(SpeciesBlocks.BONE_SPIKE.get(), new Item.Properties()));

    public static final RegistryObject<Item> TREEPER_SPAWN_EGG = ITEMS.register("treeper_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.TREEPER, 0x402E1B, 0x32992D, SpeciesDevelopers.NINNI, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ANCIENT_PINECONE = ITEMS.register("ancient_pinecone", () -> new ItemNameBlockItem(SpeciesBlocks.TROOPER.get(), new Item.Properties()));
    public static final RegistryObject<Item> TROOPER_SPAWN_EGG = ITEMS.register("trooper_spawn_egg", () -> new ForgeSpawnEggItem(SpeciesEntities.TROOPER, 0x6f5535, 0x32992D, new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> GOOBER_SPAWN_EGG = ITEMS.register("goober_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.GOOBER, 0x49674E, 0x49674E, SpeciesDevelopers.BORNHULU, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> PETRIFIED_EGG = ITEMS.register("petrified_egg", () -> new BlockItem(SpeciesBlocks.PETRIFIED_EGG.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_MOSS_BLOCK = ITEMS.register("alphacene_moss_block", () -> new BlockItem(SpeciesBlocks.ALPHACENE_MOSS_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_MOSS_CARPET = ITEMS.register("alphacene_moss_carpet", () -> new BlockItem(SpeciesBlocks.ALPHACENE_MOSS_CARPET.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_GRASS_BLOCK = ITEMS.register("alphacene_grass_block", () -> new BlockItem(SpeciesBlocks.ALPHACENE_GRASS_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_GRASS = ITEMS.register("alphacene_grass", () -> new BlockItem(SpeciesBlocks.ALPHACENE_GRASS.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_TALL_GRASS = ITEMS.register("alphacene_tall_grass", () -> new DoubleHighBlockItem(SpeciesBlocks.ALPHACENE_TALL_GRASS.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_MUSHROOM = ITEMS.register("alphacene_mushroom", () -> new BlockItem(SpeciesBlocks.ALPHACENE_MUSHROOM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_MUSHROOM_BLOCK = ITEMS.register("alphacene_mushroom_block", () -> new BlockItem(SpeciesBlocks.ALPHACENE_MUSHROOM_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_MUSHROOM_GROWTH = ITEMS.register("alphacene_mushroom_growth", () -> new BlockItem(SpeciesBlocks.ALPHACENE_MUSHROOM_GROWTH.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRUNCHER_SPAWN_EGG = ITEMS.register("cruncher_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.CRUNCHER, 0x5522B6, 0x99032B, SpeciesDevelopers.NOON, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> CRUNCHER_EGG = ITEMS.register("cruncher_egg", () -> new DoubleHighBlockItem(SpeciesBlocks.CRUNCHER_EGG.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRUNCHER_PELLET = ITEMS.register("cruncher_pellet", () -> new BlockItem(SpeciesBlocks.CRUNCHER_PELLET.get(), new Item.Properties()));

    public static final RegistryObject<Item> MAMMUTILATION_SPAWN_EGG = ITEMS.register("mammutilation_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.MAMMUTILATION, 0x472418, 0xDE5D34, SpeciesDevelopers.REDA, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> FROZEN_MEAT = ITEMS.register("frozen_meat", () -> new BlockItem(SpeciesBlocks.FROZEN_MEAT.get(), new Item.Properties()));
    public static final RegistryObject<Item> FROZEN_HAIR = ITEMS.register("frozen_hair", () -> new BlockItem(SpeciesBlocks.FROZEN_HAIR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ICHOR_BOTTLE = ITEMS.register("ichor_bottle", () -> new IchorBottle(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> YOUTH_POTION = ITEMS.register("youth_potion", () -> new YouthPotion(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));

    public static final RegistryObject<Item> SPRINGLING_SPAWN_EGG = ITEMS.register("springling_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.SPRINGLING, 0xFF7600, 0x3D3FAD, SpeciesDevelopers.GLADOS, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> SPRINGLING_EGG = ITEMS.register("springling_egg", () -> new DoubleHighBlockItem(SpeciesBlocks.SPRINGLING_EGG.get(), new Item.Properties()));

}
