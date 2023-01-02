package com.ninni.species.item;

import static com.ninni.species.Species.ITEM_GROUP;
import static com.ninni.species.Species.MOD_ID;

import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.entity.SpeciesEntities;
import com.ninni.species.entity.effect.SpeciesStatusEffects;
import com.ninni.species.sound.SpeciesSoundEvents;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("unused")
public class SpeciesItems {

    public static final Item WRAPTOR_SPAWN_EGG = register("wraptor_spawn_egg", new SpawnEggItem(SpeciesEntities.WRAPTOR, 0xAF3A5F, 0x55C1A9, new Item.Settings().maxCount(64).group(ITEM_GROUP)));
    public static final Item WRAPTOR_EGG = register("wraptor_egg", new BlockItem(SpeciesBlocks.WRAPTOR_EGG, new FabricItemSettings().group(ITEM_GROUP)));
    public static final Item CRACKED_WRAPTOR_EGG = register("cracked_wraptor_egg", new CrakedWraptorEggItem(new FabricItemSettings().group(ITEM_GROUP).food(new FoodComponent.Builder().hunger(5).saturationModifier(0.7f).statusEffect(new StatusEffectInstance(SpeciesStatusEffects.WITHER_RESISTANCE, 20 * 90, 0), 1).build())));

    public static final Item DEEPFISH_SPAWN_EGG = register("deepfish_spawn_egg", new SpawnEggItem(SpeciesEntities.DEEPFISH, 0x5A5A5A, 0xED98BD, new Item.Settings().maxCount(64).group(ITEM_GROUP)));
    public static final Item DEEPFISH_BUCKET = register("deepfish_bucket", new EntityBucketItem(SpeciesEntities.DEEPFISH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, new FabricItemSettings().maxCount(1).group(ITEM_GROUP)));

    public static final Item ROOMBUG_SPAWN_EGG = register("roombug_spawn_egg", new SpawnEggItem(SpeciesEntities.ROOMBUG, 0x5A3C29, 0xB4584D, new Item.Settings().maxCount(64).group(ITEM_GROUP)));

    public static final Item BIRT_SPAWN_EGG = register("birt_spawn_egg", new SpawnEggItem(SpeciesEntities.BIRT, 0x53C7BE, 0xD95B4D, new Item.Settings().maxCount(64).group(ITEM_GROUP)));
    public static final Item BIRT_EGG = register("birt_egg", new BirtEggItem(new Item.Settings().maxCount(16).group(ITEM_GROUP)));
    public static final Item BIRT_DWELLING = register("birt_dwelling", new BlockItem(SpeciesBlocks.BIRT_DWELLING, new FabricItemSettings().group(ITEM_GROUP)));
    public static final Item MUSIC_DISC_DIAL = register("music_disc_dial", new MusicDiscItem(11, SpeciesSoundEvents.MUSIC_DISC_DIAL, new FabricItemSettings().rarity(Rarity.RARE).maxCount(1).group(ITEM_GROUP), 193));

    public static final Item LIMPET_SPAWN_EGG = register("limpet_spawn_egg", new SpawnEggItem(SpeciesEntities.LIMPET, 0xA5C1D2, 0xFBF236, new Item.Settings().maxCount(64).group(ITEM_GROUP)));

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, id), item);
    }
}
