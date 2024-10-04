package com.ninni.species.init;

import com.ninni.species.Species;
import com.ninni.species.world.gen.features.AlphaceneMushroomFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Species.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ALPHACENE_MUSHROOM = FEATURES.register("alphacene_mushroom", () -> new AlphaceneMushroomFeature(NoneFeatureConfiguration.CODEC));

}
