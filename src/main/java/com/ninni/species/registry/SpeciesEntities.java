package com.ninni.species.registry;

import com.ninni.species.entity.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Predicate;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesEntities {

    public static final EntityType<Wraptor> WRAPTOR = register(
            "wraptor",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Wraptor::new)
                    .defaultAttributes(Wraptor::createAttributes)
                    .spawnGroup(MobCategory.MONSTER)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Wraptor::canSpawn)
                    .dimensions(EntityDimensions.scalable(1.2F, 2F))
                    .trackRangeChunks(8)
    );

    public static final EntityType<Deepfish> DEEPFISH = register(
            "deepfish",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Deepfish::new)
                    .defaultAttributes(Deepfish::createAttributes)
                    .spawnGroup(MobCategory.UNDERGROUND_WATER_CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Deepfish::canSpawn)
                    .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<Roombug> ROOMBUG = register(
            "roombug",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Roombug::new)
                    .defaultAttributes(Roombug::createAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Roombug::canSpawn)
                    .dimensions(EntityDimensions.scalable(1.375F, 0.375F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<Birt> BIRT = register(
            "birt",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Birt::new)
                    .defaultAttributes(Birt::createAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Birt::canSpawn)
                    .dimensions(EntityDimensions.scalable(0.6F, 0.6F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<BirtEgg> BIRT_EGG = register(
            "birt_egg",
            FabricEntityTypeBuilder.create()
                    .<BirtEgg>entityFactory(BirtEgg::new)
                    .spawnGroup(MobCategory.MISC)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeChunks(4)
    );

    public static final EntityType<Limpet> LIMPET = register(
            "limpet",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Limpet::new)
                    .defaultAttributes(Limpet::createAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Limpet::canSpawn)
                    .dimensions(EntityDimensions.scalable(0.75F, 1.25F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<Treeper> TREEPER = register(
            "treeper",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Treeper::new)
                    .defaultAttributes(Treeper::createAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Treeper::canSpawn)
                    .dimensions(EntityDimensions.scalable(1.9F, 7F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<TreeperSapling> TREEPER_SAPLING = register(
            "treeper_sapling",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(TreeperSapling::new)
                    .defaultAttributes(TreeperSapling::createAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, TreeperSapling::canSpawn)
                    .dimensions(EntityDimensions.scalable(0.7F, 1.2F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<Goober> GOOBER = register(
            "goober",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Goober::new)
                    .defaultAttributes(Goober::createAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Goober::canSpawn)
                    .dimensions(EntityDimensions.scalable(1.5F, 2.2F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<Cruncher> CRUNCHER = register(
            "cruncher",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Cruncher::new)
                    .defaultAttributes(Cruncher::createAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Cruncher::canSpawn)
                    .dimensions(EntityDimensions.scalable(2.6F, 4.2F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<Mammutilation> MAMMUTILATION = register(
            "mammutilation",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Mammutilation::new)
                    .defaultAttributes(Mammutilation::createAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Mammutilation::canSpawn)
                    .dimensions(EntityDimensions.scalable(2.6F, 3.8F))
                    .trackRangeChunks(10)
    );

    public static final EntityType<Springling> SPRINGLING = register(
            "springling",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Springling::new)
                    .defaultAttributes(Springling::createAttributes)
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Springling::canSpawn)
                    .dimensions(EntityDimensions.scalable(0.8F, 1.3F))
                    .trackRangeChunks(10)
    );
    public static final EntityType<CruncherPellet> CRUNCHER_PELLET = register(
            "cruncher_pellet",
            FabricEntityTypeBuilder.create()
                    .<CruncherPellet>entityFactory(CruncherPellet::new)
                    .spawnGroup(MobCategory.MISC)
                    .dimensions(EntityDimensions.fixed(0.98F, 0.98F))
                    .trackRangeChunks(10)
                    .trackedUpdateRate(20)
    );

    static {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.WARPED_FOREST), MobCategory.MONSTER, SpeciesEntities.WRAPTOR, 100, 4, 6);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.UNDERGROUND_WATER_CREATURE, SpeciesEntities.DEEPFISH, 80, 4, 6);
        BiomeModifications.addSpawn(BiomeSelectors.tag(SpeciesTags.ROOMBUG_SPAWNS), MobCategory.CREATURE, SpeciesEntities.ROOMBUG, 10, 1, 3);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().and(Predicate.not(BiomeSelectors.tag(SpeciesTags.WITHOUT_LIMPET_SPAWNS))), MobCategory.MONSTER, SpeciesEntities.LIMPET, 80, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.tag(SpeciesTags.TREEPER_SPAWNS), MobCategory.CREATURE, SpeciesEntities.TREEPER, 80, 1, 1);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(MOD_ID, id), entityType.build());
    }
}
