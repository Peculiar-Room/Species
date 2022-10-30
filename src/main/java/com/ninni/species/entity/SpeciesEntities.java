package com.ninni.species.entity;

import com.ninni.species.SpeciesTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesEntities {

    public static final EntityType<WraptorEntity> WRAPTOR = register(
            "wraptor",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(WraptorEntity::new)
                    .defaultAttributes(WraptorEntity::createWraptorAttributes)
                    .spawnGroup(SpawnGroup.MONSTER)
                    .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WraptorEntity::canSpawn)
                    .dimensions(EntityDimensions.changing(1.2F, 2F))
                    .trackRangeChunks(8)
    );

    public static final EntityType<DeepfishEntity> DEEPFISH = register(
            "deepfish",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(DeepfishEntity::new)
                    .defaultAttributes(DeepfishEntity::createDeepfishAttributes)
                    .spawnGroup(SpawnGroup.UNDERGROUND_WATER_CREATURE)
                    .spawnRestriction(SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DeepfishEntity::canSpawn)
                    .dimensions(EntityDimensions.changing(0.5F, 0.5F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<RoombugEntity> ROOMBUG = register(
            "roombug",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(RoombugEntity::new)
                    .defaultAttributes(RoombugEntity::createRoombugAttributes)
                    .spawnGroup(SpawnGroup.CREATURE)
                    .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RoombugEntity::canSpawn)
                    .dimensions(EntityDimensions.changing(1.375F, 0.3125F))
                    .trackRangeChunks(10)
    );

    static {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.WARPED_FOREST), SpawnGroup.MONSTER, SpeciesEntities.WRAPTOR, 120, 4, 6);
        BiomeModifications.addSpawn(BiomeSelectors.all(), SpawnGroup.UNDERGROUND_WATER_CREATURE, SpeciesEntities.DEEPFISH, 80, 4, 6);
        BiomeModifications.addSpawn(BiomeSelectors.tag(SpeciesTags.ROOMBUG_SPAWNS), SpawnGroup.CREATURE, SpeciesEntities.ROOMBUG, 40, 1, 1);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, id), entityType.build());
    }
}
