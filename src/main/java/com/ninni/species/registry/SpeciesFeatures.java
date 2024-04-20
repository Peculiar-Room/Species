package com.ninni.species.registry;

import com.google.common.collect.Maps;
import com.ninni.species.Species;
import com.ninni.species.world.gen.features.AlphaceneMushroomFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Map;

public class SpeciesFeatures {
    public static final Map<ResourceLocation, Feature<?>> FEATURES = Maps.newLinkedHashMap();

    public static final Feature<NoneFeatureConfiguration> ALPHACENE_MUSHROOM = register("alphacene_mushroom", new AlphaceneMushroomFeature(NoneFeatureConfiguration.CODEC));

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> F register(String name, F feature) {
        FEATURES.put(new ResourceLocation(Species.MOD_ID, name), feature);
        return feature;
    }

    public static void init() {
        FEATURES.forEach((resourceLocation, feature) -> Registry.register(BuiltInRegistries.FEATURE, resourceLocation, feature));
    }

}
