package com.ninni.species.client.particles;

import com.ninni.species.Species;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

public class SpeciesParticles {
    public static final SimpleParticleType SNORING = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "snoring"), FabricParticleTypes.simple());
    public static final SimpleParticleType BIRTD = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(Species.MOD_ID, "birtd"), FabricParticleTypes.simple());
}
