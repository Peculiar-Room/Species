package com.ninni.species.registry;

import com.ninni.species.Species;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class SpeciesParticles {
    public static final SimpleParticleType SNORING = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "snoring"), FabricParticleTypes.simple());
    public static final SimpleParticleType BIRTD = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "birtd"), FabricParticleTypes.simple());
    public static final SimpleParticleType DRIPPING_PELLET_DRIP = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "dripping_pellet_drip"), FabricParticleTypes.simple());
    public static final SimpleParticleType FALLING_PELLET_DRIP = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "falling_pellet_drip"), FabricParticleTypes.simple());
    public static final SimpleParticleType LANDING_PELLET_DRIP = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "landing_pellet_drip"), FabricParticleTypes.simple());
    public static final SimpleParticleType FOOD = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "food"), FabricParticleTypes.simple());
    public static final SimpleParticleType ASCENDING_DUST = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "ascending_dust"), FabricParticleTypes.simple());
    public static final SimpleParticleType TREEPER_LEAF = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "treeper_leaf"), FabricParticleTypes.simple());
    public static final SimpleParticleType ICHOR = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "ichor"), FabricParticleTypes.simple());
    public static final SimpleParticleType YOUTH_POTION = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "youth_potion"), FabricParticleTypes.simple());
}
