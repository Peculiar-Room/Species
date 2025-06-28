package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.SpeciesDevelopers;
import com.ninni.species.server.item.*;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Species.MOD_ID);

    public static final RegistryObject<Item> LOGO = ITEMS.register("logo", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant()));
    public static final RegistryObject<Item> TAB = ITEMS.register("tab", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant()));

    //UPDATE 1
    public static final RegistryObject<Item> V1 = ITEMS.register("v1", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> WRAPTOR_SPAWN_EGG = ITEMS.register("wraptor_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.WRAPTOR, 0xBC2765, 0x44A19D, SpeciesDevelopers.SpeciesDeveloperNames.NOON, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> WRAPTOR_EGG = ITEMS.register("wraptor_egg", () -> new BlockItem(SpeciesBlocks.WRAPTOR_EGG.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRACKED_WRAPTOR_EGG = ITEMS.register("cracked_wraptor_egg", () -> new CrakedWraptorEggItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.7f).effect(new MobEffectInstance(SpeciesStatusEffects.WITHER_RESISTANCE.get(), 20 * 90, 0), 1).build())));

    public static final RegistryObject<Item> DEEPFISH_SPAWN_EGG = ITEMS.register("deepfish_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.DEEPFISH, 0x5A5A5A, 0xED98BD, SpeciesDevelopers.SpeciesDeveloperNames.BORNULHU, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> DEEPFISH_BUCKET = ITEMS.register("deepfish_bucket", () -> new MobBucketItem(SpeciesEntities.DEEPFISH, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> STACKATICK_SPAWN_EGG = ITEMS.register("stackatick_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.STACKATICK, 0x83493B, 0x1F1F21, SpeciesDevelopers.SpeciesDeveloperNames.NINNI, new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> BIRT_SPAWN_EGG = ITEMS.register("birt_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.BIRT, 0x4DD1E1, 0xD87247, SpeciesDevelopers.SpeciesDeveloperNames.REDA, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> BIRT_EGG = ITEMS.register("birt_egg", () -> new BirtEggItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BIRT_DWELLING = ITEMS.register("birt_dwelling", () -> new BlockItem(SpeciesBlocks.BIRT_DWELLING.get(), new Item.Properties()));
    public static final RegistryObject<Item> MUSIC_DISC_DIAL = ITEMS.register("music_disc_dial", () -> new RecordItem(1, SpeciesSoundEvents.MUSIC_DISC_DIAL, new Item.Properties().rarity(Rarity.RARE).stacksTo(1), 193 * 20));

    public static final RegistryObject<Item> LIMPET_SPAWN_EGG = ITEMS.register("limpet_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.LIMPET, 0xA5C1D2, 0xFBF236, SpeciesDevelopers.SpeciesDeveloperNames.GLADOS, new Item.Properties().stacksTo(64)));

    //UPDATE 2
    public static final RegistryObject<Item> V2 = ITEMS.register("v2", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RED_SUSPICIOUS_SAND = ITEMS.register("red_suspicious_sand", () -> new BlockItem(SpeciesBlocks.RED_SUSPICIOUS_SAND.get(), new Item.Properties()));
    public static final RegistryObject<Item> MUSIC_DISC_LAPIDARIAN = ITEMS.register("music_disc_lapidarian", () -> new RecordItem(2, SpeciesSoundEvents.MUSIC_DISC_LAPIDARIAN, new Item.Properties().rarity(Rarity.RARE).stacksTo(1), 200 * 20));

    public static final RegistryObject<Item> BONE_BARK = ITEMS.register("bone_bark", () -> new BlockItem(SpeciesBlocks.BONE_BARK.get(), new Item.Properties()));
    public static final RegistryObject<Item> BONE_VERTEBRA = ITEMS.register("bone_vertebra", () -> new BlockItem(SpeciesBlocks.BONE_VERTEBRA.get(), new Item.Properties()));
    public static final RegistryObject<Item> BONE_SPIKE = ITEMS.register("bone_spike", () -> new BlockItem(SpeciesBlocks.BONE_SPIKE.get(), new Item.Properties()));

    public static final RegistryObject<Item> TREEPER_SPAWN_EGG = ITEMS.register("treeper_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.TREEPER, 0x402E1B, 0x32992D, SpeciesDevelopers.SpeciesDeveloperNames.NINNI, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ANCIENT_PINECONE = ITEMS.register("ancient_pinecone", () -> new ItemNameBlockItem(SpeciesBlocks.TROOPER.get(), new Item.Properties()));
    public static final RegistryObject<Item> TROOPER_SPAWN_EGG = ITEMS.register("trooper_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.TROOPER, 0x6f5535, 0x32992D, SpeciesDevelopers.SpeciesDeveloperNames.NINNI, new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> GOOBER_SPAWN_EGG = ITEMS.register("goober_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.GOOBER, 0x49674E, 0x49674E, SpeciesDevelopers.SpeciesDeveloperNames.BORNULHU, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> PETRIFIED_EGG = ITEMS.register("petrified_egg", () -> new BlockItem(SpeciesBlocks.PETRIFIED_EGG.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_MOSS_BLOCK = ITEMS.register("alphacene_moss_block", () -> new BlockItem(SpeciesBlocks.ALPHACENE_MOSS_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_MOSS_CARPET = ITEMS.register("alphacene_moss_carpet", () -> new BlockItem(SpeciesBlocks.ALPHACENE_MOSS_CARPET.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_GRASS_BLOCK = ITEMS.register("alphacene_grass_block", () -> new BlockItem(SpeciesBlocks.ALPHACENE_GRASS_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_GRASS = ITEMS.register("alphacene_grass", () -> new BlockItem(SpeciesBlocks.ALPHACENE_GRASS.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_TALL_GRASS = ITEMS.register("alphacene_tall_grass", () -> new DoubleHighBlockItem(SpeciesBlocks.ALPHACENE_TALL_GRASS.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_MUSHROOM = ITEMS.register("alphacene_mushroom", () -> new BlockItem(SpeciesBlocks.ALPHACENE_MUSHROOM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_MUSHROOM_BLOCK = ITEMS.register("alphacene_mushroom_block", () -> new BlockItem(SpeciesBlocks.ALPHACENE_MUSHROOM_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALPHACENE_MUSHROOM_GROWTH = ITEMS.register("alphacene_mushroom_growth", () -> new BlockItem(SpeciesBlocks.ALPHACENE_MUSHROOM_GROWTH.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRUNCHER_SPAWN_EGG = ITEMS.register("cruncher_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.CRUNCHER, 0x5522B6, 0x99032B, SpeciesDevelopers.SpeciesDeveloperNames.NOON, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> CRUNCHER_EGG = ITEMS.register("cruncher_egg", () -> new DoubleHighBlockItem(SpeciesBlocks.CRUNCHER_EGG.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRUNCHER_PELLET = ITEMS.register("cruncher_pellet", () -> new BlockItem(SpeciesBlocks.CRUNCHER_PELLET.get(), new Item.Properties()));

    public static final RegistryObject<Item> MAMMUTILATION_SPAWN_EGG = ITEMS.register("mammutilation_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.MAMMUTILATION, 0x472418, 0xDE5D34, SpeciesDevelopers.SpeciesDeveloperNames.REDA, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> FROZEN_MEAT = ITEMS.register("frozen_meat", () -> new BlockItem(SpeciesBlocks.FROZEN_MEAT.get(), new Item.Properties()));
    public static final RegistryObject<Item> FROZEN_HAIR = ITEMS.register("frozen_hair", () -> new BlockItem(SpeciesBlocks.FROZEN_HAIR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ICHOR_BOTTLE = ITEMS.register("ichor_bottle", () -> new IchorBottle(SpeciesBlocks.ICHOR.get(), new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> YOUTH_POTION = ITEMS.register("youth_potion", () -> new YouthPotion(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));

    public static final RegistryObject<Item> SPRINGLING_SPAWN_EGG = ITEMS.register("springling_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.SPRINGLING, 0x413D70, 0xE7663A, SpeciesDevelopers.SpeciesDeveloperNames.GLADOS, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> SPRINGLING_EGG = ITEMS.register("springling_egg", () -> new DoubleHighBlockItem(SpeciesBlocks.SPRINGLING_EGG.get(), new Item.Properties()));

    //UPDATE 3
    public static final RegistryObject<Item> V3 = ITEMS.register("v3", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GHOUL_SPAWN_EGG = ITEMS.register("ghoul_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.GHOUL, 0xA3908C, 0xBAA3A0, SpeciesDevelopers.SpeciesDeveloperNames.BORNULHU, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> GHOUL_TONGUE = ITEMS.register("ghoul_tongue", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).effect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 10, 0), 1).effect(new MobEffectInstance(MobEffects.HUNGER, 20 * 10, 1), 1).build())));
    public static final RegistryObject<Item> GHOUL_HEAD = ITEMS.register("ghoul_head", () -> new MobHeadItem(SpeciesBlocks.GHOUL_HEAD.get(), SpeciesBlocks.GHOUL_WALL_HEAD.get(), (new Item.Properties()).rarity(Rarity.UNCOMMON), Direction.DOWN));

    public static final RegistryObject<Item> QUAKE_SPAWN_EGG = ITEMS.register("quake_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.QUAKE, 0x454646, 0xB77541, SpeciesDevelopers.SpeciesDeveloperNames.NINNI, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> KINETIC_CORE = ITEMS.register("kinetic_core", () -> new BlockItem(SpeciesBlocks.KINETIC_CORE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> DEFLECTOR_DUMMY = ITEMS.register("deflector_dummy", () -> new DeflectorDummyItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));
    public static final RegistryObject<Item> RICOSHIELD = ITEMS.register("ricoshield", () -> new RicoshieldItem(new Item.Properties().rarity(Rarity.UNCOMMON).durability(528)));
    public static final RegistryObject<Item> QUAKE_HEAD = ITEMS.register("quake_head", () -> new MobHeadItem(SpeciesBlocks.QUAKE_HEAD.get(), SpeciesBlocks.QUAKE_WALL_HEAD.get(), (new Item.Properties()).rarity(Rarity.UNCOMMON), Direction.DOWN));
    public static final RegistryObject<Item> MUSIC_DISK_SPAWNER = ITEMS.register("music_disk_spawner", () -> new RecordItem(3, SpeciesSoundEvents.MUSIC_DISK_SPAWNER, new Item.Properties().rarity(Rarity.RARE).stacksTo(1), 136 * 20));

    public static final RegistryObject<Item> SPECTRE_SPAWN_EGG = ITEMS.register("spectre_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.SPECTRE, 0x182C39, 0x35f8ff, SpeciesDevelopers.SpeciesDeveloperNames.REDA, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> BROKEN_LINKS = ITEMS.register("broken_links", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPECLIGHT = ITEMS.register("speclight", () -> new SpectreLightBlockItem(SpeciesBlocks.SPECLIGHT.get(), new Item.Properties()));
    public static final RegistryObject<Item> CHAINDELIER = ITEMS.register("chaindelier", () -> new BlockItem(SpeciesBlocks.CHAINDELIER.get(), new Item.Properties()));
    public static final RegistryObject<Item> HOPELIGHT = ITEMS.register("hopelight", () -> new SpectreLightBlockItem(SpeciesBlocks.HOPELIGHT.get(), new Item.Properties()));
    public static final RegistryObject<Item> SPECTRALIBUR = ITEMS.register("spectralibur", () -> new SpectraliburItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SPECTRALIBUR_PEDESTAL = ITEMS.register("spectralibur_pedestal", () -> new BlockItem(SpeciesBlocks.SPECTRALIBUR_PEDESTAL.get(), new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> WICKED_SPAWN_EGG = ITEMS.register("wicked_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.WICKED, 0x435AA3, 0xDF77A0, SpeciesDevelopers.SpeciesDeveloperNames.GLADOS, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> WICKED_WAX = ITEMS.register("wicked_wax", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WICKED_SWAPPER = ITEMS.register("wicked_swapper", () -> new WickedSwapperItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> MONSTER_MEAL = ITEMS.register("monster_meal", MonsterMealitem::new);
    public static final RegistryObject<Item> SMOKE_BOMB = ITEMS.register("smoke_bomb", () -> new SmokeBombItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> WICKED_DOPE = ITEMS.register("wicked_dope", WickedDopeItem::new);
    public static final RegistryObject<Item> WICKED_MASK = ITEMS.register("wicked_mask", WickedMaskItem::new);
    public static final RegistryObject<Item> WICKED_TREAT = ITEMS.register("wicked_treat", WickedTreatItem::new);
    public static final RegistryObject<Item> WICKED_CANDLE = ITEMS.register("wicked_candle", () -> new MobHeadItem(SpeciesBlocks.WICKED_CANDLE.get(), SpeciesBlocks.WICKED_WALL_CANDLE.get(), (new Item.Properties()).rarity(Rarity.UNCOMMON), Direction.DOWN));

    public static final RegistryObject<Item> BEWEREAGER_SPAWN_EGG = ITEMS.register("bewereager_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.BEWEREAGER, 0x8D383F, 0x5D4B4E, SpeciesDevelopers.SpeciesDeveloperNames.NOON, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> WEREFANG = ITEMS.register("werefang", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRANKBOW = ITEMS.register("crankbow", CrankbowItem::new);
    public static final RegistryObject<Item> CRANKTRAP = ITEMS.register("cranktrap", () -> new BlockItem(SpeciesBlocks.CRANKTRAP.get(), new Item.Properties()));
    public static final RegistryObject<Item> BEWEREAGER_HEAD = ITEMS.register("bewereager_head", () -> new MobHeadItem(SpeciesBlocks.BEWEREAGER_HEAD.get(), SpeciesBlocks.BEWEREAGER_WALL_HEAD.get(), (new Item.Properties()).rarity(Rarity.UNCOMMON), Direction.DOWN));

    public static final RegistryObject<Item> LEAF_HANGER_SPAWN_EGG = ITEMS.register("leaf_hanger_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.LEAF_HANGER, 0x43994E, 0x5C4A45, SpeciesDevelopers.SpeciesDeveloperNames.YAPETTO, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> CLIFF_HANGER_SPAWN_EGG = ITEMS.register("cliff_hanger_spawn_egg", () -> new SpeciesSpawnEggItem(SpeciesEntities.CLIFF_HANGER, 0x8B7648, 0x48484B, SpeciesDevelopers.SpeciesDeveloperNames.YAPETTO, new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> COIL = ITEMS.register("coil", () -> new CoilItem(new Item.Properties()));
    public static final RegistryObject<Item> HARPOON = ITEMS.register("harpoon", () -> new HarpoonItem(new Item.Properties().stacksTo(1).durability(128)));

}
