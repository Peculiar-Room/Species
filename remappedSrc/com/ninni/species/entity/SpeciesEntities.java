package com.ninni.species.entity;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

import static com.ninni.species.Species.*;

public class SpeciesEntities {

    public static final EntityType<WraptorEntity> WRAPTOR = register(
        "wraptor",
        FabricEntityTypeBuilder.createMob()
                               .entityFactory(WraptorEntity::new)
                               .defaultAttributes(WraptorEntity::createWraptorAttributes)
                               .spawnGroup(SpawnGroup.MONSTER)
                               .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WraptorEntity::canSpawn)
                               .dimensions(EntityDimensions.changing(1.2F, 2F))
                               .trackRangeBlocks(8),
        new int[]{ 0xAF3A5F, 0x55C1A9 }
    );

    static {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.WARPED_FOREST), SpawnGroup.MONSTER, SpeciesEntities.WRAPTOR, 120, 4, 6);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityType<T> register(String id, EntityType<T> entityType, int[] spawnEggColors) {
        if (spawnEggColors != null) Registry.register(Registry.ITEM, new Identifier(MOD_ID, id + "_spawn_egg"), new SpawnEggItem((EntityType<? extends MobEntity>) entityType, spawnEggColors[0], spawnEggColors[1], new Item.Settings().maxCount(64).group(ITEM_GROUP)));
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, id), entityType);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType, int[] spawnEggColors) { return register(id, entityType.build(), spawnEggColors); }
}
