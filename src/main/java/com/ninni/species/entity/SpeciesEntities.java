package com.ninni.species.entity;

import com.ninni.species.tag.SpeciesTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesEntities {

    public static final EntityType<WraptorEntity> WRAPTOR = register(
            "wraptor",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(WraptorEntity::new)
                    .defaultAttributes(WraptorEntity::createWraptorAttributes)
                    .spawnGroup(MobCategory.MONSTER)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WraptorEntity::canSpawn)
                    .dimensions(EntityDimensions.scalable(1.2F, 2F))
                    .trackRangeChunks(8)
    );

    public static final EntityType<DeepfishEntity> DEEPFISH = register(
            "deepfish",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(DeepfishEntity::new)
                    .defaultAttributes(DeepfishEntity::createDeepfishAttributes)
                    .spawnGroup(MobCategory.UNDERGROUND_WATER_CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DeepfishEntity::canSpawn)
                    .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<RoombugEntity> ROOMBUG = register(
            "roombug",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(RoombugEntity::new)
                    .defaultAttributes(RoombugEntity::createRoombugAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, RoombugEntity::canSpawn)
                    .dimensions(EntityDimensions.scalable(1.375F, 0.375F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<BirtEntity> BIRT = register(
            "birt",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(BirtEntity::new)
                    .defaultAttributes(BirtEntity::createBirtAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, BirtEntity::canSpawn)
                    .dimensions(EntityDimensions.scalable(0.6F, 0.6F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<BirtEggEntity> BIRT_EGG = register(
            "birt_egg",
            FabricEntityTypeBuilder.create()
                    .<BirtEggEntity>entityFactory(BirtEggEntity::new)
                    .spawnGroup(MobCategory.MISC)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeChunks(4)
    );

    public static final EntityType<LimpetEntity> LIMPET = register(
            "limpet",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(LimpetEntity::new)
                    .defaultAttributes(LimpetEntity::createLimpetAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, LimpetEntity::canSpawn)
                    .dimensions(EntityDimensions.scalable(0.75F, 1.25F))
                    .trackRangeChunks(10)
    );

    static {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.WARPED_FOREST), MobCategory.MONSTER, SpeciesEntities.WRAPTOR, 100, 4, 6);
        BiomeModifications.addSpawn(BiomeSelectors.all(), MobCategory.UNDERGROUND_WATER_CREATURE, SpeciesEntities.DEEPFISH, 80, 4, 6);
        BiomeModifications.addSpawn(BiomeSelectors.tag(SpeciesTags.ROOMBUG_SPAWNS), MobCategory.CREATURE, SpeciesEntities.ROOMBUG, 10, 1, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(SpeciesTags.LIMPET_SPAWNS), MobCategory.MONSTER, SpeciesEntities.LIMPET, 10, 1, 1);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(MOD_ID, id), entityType.build());
    }
}
