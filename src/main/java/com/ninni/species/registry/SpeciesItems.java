package com.ninni.species.registry;

import com.ninni.species.item.BirtEggItem;
import com.ninni.species.item.CrakedWraptorEggItem;
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

    public static final Item WRAPTOR_SPAWN_EGG = register("wraptor_spawn_egg", new SpawnEggItem(SpeciesEntities.WRAPTOR, 0xAF3A5F, 0x55C1A9, new Item.Properties().stacksTo(64)));
    public static final Item WRAPTOR_EGG = register("wraptor_egg", new BlockItem(SpeciesBlocks.WRAPTOR_EGG, new FabricItemSettings()));
    public static final Item CRACKED_WRAPTOR_EGG = register("cracked_wraptor_egg", new CrakedWraptorEggItem(new FabricItemSettings().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.7f).effect(new MobEffectInstance(SpeciesStatusEffects.WITHER_RESISTANCE, 20 * 90, 0), 1).build())));

    public static final Item DEEPFISH_SPAWN_EGG = register("deepfish_spawn_egg", new SpawnEggItem(SpeciesEntities.DEEPFISH, 0x5A5A5A, 0xED98BD, new Item.Properties().stacksTo(64)));
    public static final Item DEEPFISH_BUCKET = register("deepfish_bucket", new MobBucketItem(SpeciesEntities.DEEPFISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, new FabricItemSettings().stacksTo(1)));

    public static final Item ROOMBUG_SPAWN_EGG = register("roombug_spawn_egg", new SpawnEggItem(SpeciesEntities.ROOMBUG, 0x5A3C29, 0xB4584D, new Item.Properties().stacksTo(64)));

    public static final Item BIRT_SPAWN_EGG = register("birt_spawn_egg", new SpawnEggItem(SpeciesEntities.BIRT, 0x53C7BE, 0xD95B4D, new Item.Properties().stacksTo(64)));
    public static final Item BIRT_EGG = register("birt_egg", new BirtEggItem(new Item.Properties().stacksTo(16)));
    public static final Item BIRT_DWELLING = register("birt_dwelling", new BlockItem(SpeciesBlocks.BIRT_DWELLING, new FabricItemSettings()));
    public static final Item MUSIC_DISC_DIAL = register("music_disc_dial", new RecordItem(11, SpeciesSoundEvents.MUSIC_DISC_DIAL, new FabricItemSettings().rarity(Rarity.RARE).stacksTo(1), 193));

    public static final Item LIMPET_SPAWN_EGG = register("limpet_spawn_egg", new SpawnEggItem(SpeciesEntities.LIMPET, 0xA5C1D2, 0xFBF236, new Item.Properties().stacksTo(64)));

    public static final Item TREEPER_SPAWN_EGG = register("treeper_spawn_egg", new SpawnEggItem(SpeciesEntities.TREEPER, 0x402E1B, 0x32992D, new Item.Properties().stacksTo(64)));
    public static final Item ANCIENT_PINECONE = register("ancient_pinecone", new ItemNameBlockItem(SpeciesBlocks.TREEPER_SAPLING, new FabricItemSettings()));

    private static Item register(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, id), item);
    }
}
