package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.world.gen.features.AlphaceneMushroomFeature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, Species.MOD_ID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> ALPHACENE_MUSHROOM = FEATURES.register("alphacene_mushroom", () -> new AlphaceneMushroomFeature(NoneFeatureConfiguration.CODEC));

}
