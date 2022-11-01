package com.ninni.species.client.particles;

import com.ninni.species.Species;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SpeciesParticles {
    public static final DefaultParticleType SNORING = Registry.register(Registry.PARTICLE_TYPE, new Identifier(Species.MOD_ID, "snoring"), FabricParticleTypes.simple());
    public static final DefaultParticleType BIRTD = Registry.register(Registry.PARTICLE_TYPE, new Identifier(Species.MOD_ID, "birtd"), FabricParticleTypes.simple());
}
