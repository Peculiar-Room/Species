package com.ninni.species.world.gen.features;

import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesPlacedFeatures;
import com.ninni.species.registry.SpeciesTags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpeciesBiomeModifier {
    private static final ResourceKey<BiomeModifier> ADD_BIRTED_BIRCH_TREES = createKey("add_birted_birch_trees");
    private static final ResourceKey<BiomeModifier> ADD_MAMMUTILATION_REMNANT = createKey("add_mammutilation_remnant");
    private static final ResourceKey<BiomeModifier> ADD_DEEPFISH = createKey("add_deepfish");
    private static final ResourceKey<BiomeModifier> ADD_LIMPET = createKey("add_limpet");
    private static final ResourceKey<BiomeModifier> ADD_ROOMBUG = createKey("add_roombug");
    private static final ResourceKey<BiomeModifier> ADD_WRAPTOR = createKey("add_wraptor");
    private static final ResourceKey<BiomeModifier> ADD_TREEPER = createKey("add_treeper");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        context.register(ADD_BIRTED_BIRCH_TREES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomeTag(context, SpeciesTags.BIRT_TREE_SPAWNS_IN), getPlacedFeature(context, SpeciesPlacedFeatures.BIRTED_BIRCH_TREES), GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_MAMMUTILATION_REMNANT, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomeTag(context, SpeciesTags.MAMMUTILATION_REMNANT_SPAWNS_IN), getPlacedFeature(context, SpeciesPlacedFeatures.MAMMUTILATION_REMNANT), GenerationStep.Decoration.UNDERGROUND_DECORATION));
        context.register(ADD_DEEPFISH, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomeTag(context, BiomeTags.IS_OVERWORLD), List.of(new MobSpawnSettings.SpawnerData(SpeciesEntities.DEEPFISH.get(), 80, 4, 6))));
        context.register(ADD_LIMPET, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomeTag(context, BiomeTags.IS_OVERWORLD), List.of(new MobSpawnSettings.SpawnerData(SpeciesEntities.LIMPET.get(), 80, 1, 1))));
        context.register(ADD_ROOMBUG, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomeTag(context, SpeciesTags.ROOMBUG_SPAWNS), List.of(new MobSpawnSettings.SpawnerData(SpeciesEntities.ROOMBUG.get(), 10, 1, 3))));
        context.register(ADD_WRAPTOR, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(HolderSet.direct(context.lookup(Registries.BIOME).getOrThrow(Biomes.WARPED_FOREST)), List.of(new MobSpawnSettings.SpawnerData(SpeciesEntities.WRAPTOR.get(), 100, 4, 6))));
        context.register(ADD_TREEPER, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomeTag(context, SpeciesTags.TREEPER_SPAWNS), List.of(new MobSpawnSettings.SpawnerData(SpeciesEntities.TREEPER.get(), 80, 1, 1))));
    }

    @SafeVarargs
    @NotNull
    private static HolderSet.Direct<PlacedFeature> getPlacedFeature(BootstapContext<BiomeModifier> context, ResourceKey<PlacedFeature>... placedFeature) {
        return HolderSet.direct(Stream.of(placedFeature).map(resourceKey -> context.lookup(Registries.PLACED_FEATURE).getOrThrow(resourceKey)).collect(Collectors.toList()));
    }

    @NotNull
    private static HolderSet.Direct<PlacedFeature> getPlacedFeature(BootstapContext<BiomeModifier> context, ResourceKey<PlacedFeature> placedFeature) {
        return HolderSet.direct(context.lookup(Registries.PLACED_FEATURE).getOrThrow(placedFeature));
    }

    @NotNull
    private static HolderSet.Named<Biome> biomeTag(BootstapContext<BiomeModifier> context, TagKey<Biome> tag) {
        return context.lookup(Registries.BIOME).getOrThrow(tag);
    }

    public static ResourceKey<BiomeModifier> createKey(String string) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(Species.MOD_ID, string));
    }

}
