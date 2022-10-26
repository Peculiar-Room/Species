package com.ninni.species.item;

import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.entity.effect.SpeciesStatusEffects;
import com.ninni.species.entity.SpeciesEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.ninni.species.Species.ITEM_GROUP;
import static com.ninni.species.Species.MOD_ID;

@SuppressWarnings("unused")
public class SpeciesItems {

    public static final Item WRAPTOR_SPAWN_EGG = register("wraptor_spawn_egg", new SpawnEggItem(SpeciesEntities.WRAPTOR, 0xAF3A5F, 0x55C1A9, new Item.Settings().maxCount(64).group(ITEM_GROUP)));
    public static final Item WRAPTOR_EGG = register("wraptor_egg", new BlockItem(SpeciesBlocks.WRAPTOR_EGG, new FabricItemSettings().group(ITEM_GROUP)));
    public static final Item CRACKED_WRAPTOR_EGG = register("cracked_wraptor_egg", new CrakedWraptorEggItem(new FabricItemSettings().group(ITEM_GROUP).food(new FoodComponent.Builder().hunger(5).saturationModifier(0.7f).statusEffect(new StatusEffectInstance(SpeciesStatusEffects.WITHER_RESISTANCE, 20 * 90, 0), 1).build())));

    public static final Item DEEPFISH_SPAWN_EGG = register("deepfish_spawn_egg", new SpawnEggItem(SpeciesEntities.DEEPFISH, 0x5A5A5A, 0xED98BD, new Item.Settings().maxCount(64).group(ITEM_GROUP)));
    public static final Item DEEPFISH_BUCKET = register("deepfish_bucket", new EntityBucketItem(SpeciesEntities.DEEPFISH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, new FabricItemSettings().maxCount(1).group(ITEM_GROUP)));

    public static final Item ROOMBUG_SPAWN_EGG = register("roombug_spawn_egg", new SpawnEggItem(SpeciesEntities.ROOMBUG, 0x5A3C29, 0xB4584D, new Item.Settings().maxCount(64).group(ITEM_GROUP)));

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, id), item);
    }
}
