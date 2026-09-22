package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.SpeciesDevelopers;
import com.ninni.species.server.item.*;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SpeciesItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Species.MOD_ID);

    public static final DeferredItem<Item> LOGO = ITEMS.registerItem("logo", Item::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant());
    public static final DeferredItem<Item> TAB = ITEMS.registerItem("tab", Item::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant());

    //UPDATE 1
    public static final DeferredItem<Item> V1 = ITEMS.registerItem("v1", Item::new, new Item.Properties());

    public static final DeferredItem<Item> WRAPTOR_SPAWN_EGG = ITEMS.registerItem("wraptor_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.WRAPTOR.get(), 0xBC2765, 0x44A19D, SpeciesDevelopers.SpeciesDeveloperNames.NOON, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> WRAPTOR_EGG = ITEMS.registerSimpleBlockItem("wraptor_egg", SpeciesBlocks.WRAPTOR_EGG, new Item.Properties());
    public static final DeferredItem<Item> CRACKED_WRAPTOR_EGG = ITEMS.registerItem("cracked_wraptor_egg", CrakedWraptorEggItem::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.7f).effect(() -> new MobEffectInstance(SpeciesStatusEffects.WITHER_RESISTANCE, 20 * 90, 0), 1).build()));

    public static final DeferredItem<Item> DEEPFISH_SPAWN_EGG = ITEMS.registerItem("deepfish_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.DEEPFISH.get(), 0x5A5A5A, 0xED98BD, SpeciesDevelopers.SpeciesDeveloperNames.BORNULHU, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> DEEPFISH_BUCKET = ITEMS.registerItem("deepfish_bucket", (properties) -> new MobBucketItem(SpeciesEntities.DEEPFISH.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> STACKATICK_SPAWN_EGG = ITEMS.registerItem("stackatick_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.STACKATICK.get(), 0x83493B, 0x1F1F21, SpeciesDevelopers.SpeciesDeveloperNames.NINNI, new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> BIRT_SPAWN_EGG = ITEMS.registerItem("birt_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.BIRT.get(), 0x4DD1E1, 0xD87247, SpeciesDevelopers.SpeciesDeveloperNames.REDA, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> BIRT_EGG = ITEMS.registerItem("birt_egg", BirtEggItem::new, new Item.Properties().stacksTo(16));
    public static final DeferredItem<BlockItem> BIRT_DWELLING = ITEMS.registerItem("birt_dwelling", (properties) -> new BlockItem(SpeciesBlocks.BIRT_DWELLING.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> BIRTDAY_CAKE = ITEMS.registerItem("birtday_cake", (properties) -> new BlockItem(SpeciesBlocks.BIRTDAY_CAKE.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> BIRTDAY_CAKE_SLICE = ITEMS.registerItem("birtday_cake_slice", BirtdayCakeSliceItem::new, new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(4).saturationModifier(0.6f).effect(() -> new MobEffectInstance(SpeciesStatusEffects.BIRTD, 20 * 10, 0), 1).build()));
    public static final DeferredItem<Item> MUSIC_DISC_DIAL = ITEMS.registerItem("music_disc_dial", Item::new, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("species", "dial"))));

    public static final DeferredItem<Item> LIMPET_SPAWN_EGG = ITEMS.registerItem("limpet_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.LIMPET.get(), 0xA5C1D2, 0xFBF236, SpeciesDevelopers.SpeciesDeveloperNames.GLADOS, new Item.Properties().stacksTo(64)));

    //UPDATE 2
    public static final DeferredItem<Item> V2 = ITEMS.registerItem("v2", Item::new, new Item.Properties());
    public static final DeferredItem<Item> RED_SUSPICIOUS_SAND = ITEMS.registerItem("red_suspicious_sand", (properties) -> new BlockItem(SpeciesBlocks.RED_SUSPICIOUS_SAND.get(), new Item.Properties()));
    public static final DeferredItem<Item> MUSIC_DISC_LAPIDARIAN = ITEMS.registerItem("music_disc_lapidarian", Item::new, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("species", "lapidarian"))));

    public static final DeferredItem<Item> BONE_BARK = ITEMS.registerItem("bone_bark", (properties) -> new BlockItem(SpeciesBlocks.BONE_BARK.get(), new Item.Properties()));
    public static final DeferredItem<Item> BONE_VERTEBRA = ITEMS.registerItem("bone_vertebra", (properties) -> new BlockItem(SpeciesBlocks.BONE_VERTEBRA.get(), new Item.Properties()));
    public static final DeferredItem<Item> BONE_SPIKE = ITEMS.registerItem("bone_spike", (properties) -> new BlockItem(SpeciesBlocks.BONE_SPIKE.get(), new Item.Properties()));

    public static final DeferredItem<Item> TREEPER_SPAWN_EGG = ITEMS.registerItem("treeper_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.TREEPER.get(), 0x402E1B, 0x32992D, SpeciesDevelopers.SpeciesDeveloperNames.NINNI, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> ANCIENT_PINECONE = ITEMS.registerItem("ancient_pinecone", (properties) -> new ItemNameBlockItem(SpeciesBlocks.TROOPER.get(), new Item.Properties()));
    public static final DeferredItem<Item> TROOPER_SPAWN_EGG = ITEMS.registerItem("trooper_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.TROOPER.get(), 0x6f5535, 0x32992D, SpeciesDevelopers.SpeciesDeveloperNames.NINNI, new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> GOOBER_SPAWN_EGG = ITEMS.registerItem("goober_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.GOOBER.get(), 0x49674E, 0x49674E, SpeciesDevelopers.SpeciesDeveloperNames.BORNULHU, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> PETRIFIED_EGG = ITEMS.registerItem("petrified_egg", (properties) -> new BlockItem(SpeciesBlocks.PETRIFIED_EGG.get(), new Item.Properties()));
    public static final DeferredItem<Item> ALPHACENE_MOSS_BLOCK = ITEMS.registerItem("alphacene_moss_block", (properties) -> new BlockItem(SpeciesBlocks.ALPHACENE_MOSS_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> ALPHACENE_MOSS_CARPET = ITEMS.registerItem("alphacene_moss_carpet", (properties) -> new BlockItem(SpeciesBlocks.ALPHACENE_MOSS_CARPET.get(), new Item.Properties()));
    public static final DeferredItem<Item> ALPHACENE_GRASS_BLOCK = ITEMS.registerItem("alphacene_grass_block", (properties) -> new BlockItem(SpeciesBlocks.ALPHACENE_GRASS_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> ALPHACENE_GRASS = ITEMS.registerItem("alphacene_grass", (properties) -> new BlockItem(SpeciesBlocks.ALPHACENE_GRASS.get(), new Item.Properties()));
    public static final DeferredItem<Item> ALPHACENE_TALL_GRASS = ITEMS.registerItem("alphacene_tall_grass", (properties) -> new DoubleHighBlockItem(SpeciesBlocks.ALPHACENE_TALL_GRASS.get(), new Item.Properties()));
    public static final DeferredItem<Item> ALPHACENE_MUSHROOM = ITEMS.registerItem("alphacene_mushroom", (properties) -> new BlockItem(SpeciesBlocks.ALPHACENE_MUSHROOM.get(), new Item.Properties()));
    public static final DeferredItem<Item> ALPHACENE_MUSHROOM_BLOCK = ITEMS.registerItem("alphacene_mushroom_block", (properties) -> new BlockItem(SpeciesBlocks.ALPHACENE_MUSHROOM_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> ALPHACENE_MUSHROOM_GROWTH = ITEMS.registerItem("alphacene_mushroom_growth", (properties) -> new BlockItem(SpeciesBlocks.ALPHACENE_MUSHROOM_GROWTH.get(), new Item.Properties()));

    public static final DeferredItem<Item> CRUNCHER_SPAWN_EGG = ITEMS.registerItem("cruncher_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.CRUNCHER.get(), 0x5522B6, 0x99032B, SpeciesDevelopers.SpeciesDeveloperNames.NOON, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> CRUNCHER_EGG = ITEMS.registerItem("cruncher_egg", (properties) -> new DoubleHighBlockItem(SpeciesBlocks.CRUNCHER_EGG.get(), new Item.Properties()));
    public static final DeferredItem<Item> CRUNCHER_PELLET = ITEMS.registerItem("cruncher_pellet", (properties) -> new BlockItem(SpeciesBlocks.CRUNCHER_PELLET.get(), new Item.Properties()));

    public static final DeferredItem<Item> MAMMUTILATION_SPAWN_EGG = ITEMS.registerItem("mammutilation_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.MAMMUTILATION.get(), 0x472418, 0xDE5D34, SpeciesDevelopers.SpeciesDeveloperNames.REDA, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> FROZEN_MEAT = ITEMS.registerItem("frozen_meat", (properties) -> new BlockItem(SpeciesBlocks.FROZEN_MEAT.get(), new Item.Properties()));
    public static final DeferredItem<Item> FROZEN_HAIR = ITEMS.registerItem("frozen_hair", (properties) -> new BlockItem(SpeciesBlocks.FROZEN_HAIR.get(), new Item.Properties()));
    public static final DeferredItem<Item> ICHOR_BOTTLE = ITEMS.registerItem("ichor_bottle", (properties) -> new IchorBottle(SpeciesBlocks.ICHOR.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> YOUTH_POTION = ITEMS.registerItem("youth_potion", (properties) -> new YouthPotion(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));

    public static final DeferredItem<Item> SPRINGLING_SPAWN_EGG = ITEMS.registerItem("springling_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.SPRINGLING.get(), 0x413D70, 0xE7663A, SpeciesDevelopers.SpeciesDeveloperNames.GLADOS, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> SPRINGLING_EGG = ITEMS.registerItem("springling_egg", (properties) -> new DoubleHighBlockItem(SpeciesBlocks.SPRINGLING_EGG.get(), new Item.Properties()));

    //UPDATE 3
    public static final DeferredItem<Item> V3 = ITEMS.registerItem("v3", Item::new, new Item.Properties());
    public static final DeferredItem<Item> GHOUL_SPAWN_EGG = ITEMS.registerItem("ghoul_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.GHOUL.get(), 0xA3908C, 0xBAA3A0, SpeciesDevelopers.SpeciesDeveloperNames.BORNULHU, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> GHOUL_TONGUE = ITEMS.registerItem("ghoul_tongue", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.2f).effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 20 * 10, 0), 1).effect(new MobEffectInstance(MobEffects.HUNGER, 20 * 10, 1), 1).build()));
    public static final DeferredItem<Item> GHOUL_HEAD = ITEMS.registerItem("ghoul_head", (properties) -> new MobHeadItem(SpeciesBlocks.GHOUL_HEAD.get(), SpeciesBlocks.GHOUL_WALL_HEAD.get(), (new Item.Properties()).rarity(Rarity.UNCOMMON), Direction.DOWN));

    public static final DeferredItem<Item> QUAKE_SPAWN_EGG = ITEMS.registerItem("quake_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.QUAKE.get(), 0x454646, 0xB77541, SpeciesDevelopers.SpeciesDeveloperNames.NINNI, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> KINETIC_CORE = ITEMS.registerItem("kinetic_core", (properties) -> new BlockItem(SpeciesBlocks.KINETIC_CORE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> DEFLECTOR_DUMMY = ITEMS.registerItem("deflector_dummy", DeflectorDummyItem::new, new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16));
    public static final DeferredItem<Item> RICOSHIELD = ITEMS.registerItem("ricoshield", RicoshieldItem::new, new Item.Properties().rarity(Rarity.UNCOMMON).durability(528));
    public static final DeferredItem<Item> QUAKE_HEAD = ITEMS.registerItem("quake_head", (properties) -> new MobHeadItem(SpeciesBlocks.QUAKE_HEAD.get(), SpeciesBlocks.QUAKE_WALL_HEAD.get(), (new Item.Properties()).rarity(Rarity.UNCOMMON), Direction.DOWN));
    public static final DeferredItem<Item> MUSIC_DISK_SPAWNER = ITEMS.registerItem("music_disk_spawner", Item::new, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("species", "spawner"))));

    public static final DeferredItem<Item> SPECTRE_SPAWN_EGG = ITEMS.registerItem("spectre_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.SPECTRE.get(), 0x182C39, 0x35f8ff, SpeciesDevelopers.SpeciesDeveloperNames.REDA, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> BROKEN_LINKS = ITEMS.registerItem("broken_links", Item::new, new Item.Properties());
    public static final DeferredItem<Item> SPECLIGHT = ITEMS.registerItem("speclight", (properties) -> new SpectreLightBlockItem(SpeciesBlocks.SPECLIGHT.get(), new Item.Properties().component(DataComponents.DYED_COLOR, new DyedItemColor(0x7CF2F5, false))));
    public static final DeferredItem<Item> CHAINDELIER = ITEMS.registerItem("chaindelier", (properties) -> new BlockItem(SpeciesBlocks.CHAINDELIER.get(), new Item.Properties()));
    public static final DeferredItem<Item> HOPELIGHT = ITEMS.registerItem("hopelight", (properties) -> new SpectreLightBlockItem(SpeciesBlocks.HOPELIGHT.get(), new Item.Properties().component(DataComponents.DYED_COLOR, new DyedItemColor(0x7CF2F5, false))));
    public static final DeferredItem<Item> SPECTRALIBUR = ITEMS.registerItem("spectralibur", SpectraliburItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE).attributes(SpectraliburItem.createAttributes()));
    public static final DeferredItem<Item> SPECTRALIBUR_PEDESTAL = ITEMS.registerItem("spectralibur_pedestal", (properties) -> new BlockItem(SpeciesBlocks.SPECTRALIBUR_PEDESTAL.get(), new Item.Properties().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> WICKED_SPAWN_EGG = ITEMS.registerItem("wicked_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.WICKED.get(), 0x435AA3, 0xDF77A0, SpeciesDevelopers.SpeciesDeveloperNames.GLADOS, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> WICKED_WAX = ITEMS.registerItem("wicked_wax", Item::new, new Item.Properties());
    public static final DeferredItem<Item> WICKED_SWAPPER = ITEMS.registerItem("wicked_swapper", WickedSwapperItem::new, new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> MONSTER_MEAL = ITEMS.registerItem("monster_meal", (properties) -> new MonsterMealitem());
    public static final DeferredItem<Item> SMOKE_BOMB = ITEMS.registerItem("smoke_bomb", SmokeBombItem::new, new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> WICKED_DOPE = ITEMS.registerItem("wicked_dope", (properties) -> new WickedDopeItem());
    public static final DeferredItem<Item> WICKED_MASK = ITEMS.registerItem("wicked_mask", (properties) -> new WickedMaskItem());
    public static final DeferredItem<Item> WICKED_TREAT = ITEMS.registerItem("wicked_treat", (properties) -> new WickedTreatItem());
    public static final DeferredItem<Item> WICKED_CANDLE = ITEMS.registerItem("wicked_candle", (properties) -> new MobHeadItem(SpeciesBlocks.WICKED_CANDLE.get(), SpeciesBlocks.WICKED_WALL_CANDLE.get(), (new Item.Properties()).rarity(Rarity.UNCOMMON), Direction.DOWN));

    public static final DeferredItem<Item> BEWEREAGER_SPAWN_EGG = ITEMS.registerItem("bewereager_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.BEWEREAGER.get(), 0x8D383F, 0x5D4B4E, SpeciesDevelopers.SpeciesDeveloperNames.NOON, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> WEREFANG = ITEMS.registerItem("werefang", Item::new, new Item.Properties());
    public static final DeferredItem<Item> CRANKBOW = ITEMS.registerItem("crankbow", (properties) -> new CrankbowItem());
    public static final DeferredItem<Item> CRANKTRAP = ITEMS.registerItem("cranktrap", (properties) -> new BlockItem(SpeciesBlocks.CRANKTRAP.get(), new Item.Properties()));
    public static final DeferredItem<Item> BEWEREAGER_HEAD = ITEMS.registerItem("bewereager_head", (properties) -> new MobHeadItem(SpeciesBlocks.BEWEREAGER_HEAD.get(), SpeciesBlocks.BEWEREAGER_WALL_HEAD.get(), (new Item.Properties()).rarity(Rarity.UNCOMMON), Direction.DOWN));

    public static final DeferredItem<Item> LEAF_HANGER_SPAWN_EGG = ITEMS.registerItem("leaf_hanger_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.LEAF_HANGER.get(), 0x43994E, 0x5C4A45, SpeciesDevelopers.SpeciesDeveloperNames.YAPETTO, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> CLIFF_HANGER_SPAWN_EGG = ITEMS.registerItem("cliff_hanger_spawn_egg", (properties) -> new SpeciesSpawnEggItem(SpeciesEntities.CLIFF_HANGER.value(), 0x8B7648, 0x48484B, SpeciesDevelopers.SpeciesDeveloperNames.YAPETTO, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> COIL = ITEMS.registerItem("coil", CoilItem::new, new Item.Properties());
    public static final DeferredItem<Item> HARPOON = ITEMS.registerItem("harpoon", HarpoonItem::new, new Item.Properties().stacksTo(1).durability(128));

}
