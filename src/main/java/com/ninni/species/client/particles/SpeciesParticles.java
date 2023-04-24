package com.ninni.species.client.particles;

import com.ninni.species.Species;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID)
public class SpeciesParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Species.MOD_ID);

    public static final RegistryObject<SimpleParticleType> SNORING = PARTICLE_TYPES.register("snoring", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BIRTD = PARTICLE_TYPES.register("birtd", () -> new SimpleParticleType(false));
}
