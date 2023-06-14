package com.ninni.species.item;

import com.ninni.species.Species;
import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.entity.SpeciesEntities;
import com.ninni.species.entity.effect.SpeciesStatusEffects;
import com.ninni.species.sound.SpeciesSoundEvents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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

}
