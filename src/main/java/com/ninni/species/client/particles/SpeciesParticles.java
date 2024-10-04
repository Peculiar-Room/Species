package com.ninni.species.client.particles;

import com.ninni.species.Species;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID)
public class SpeciesParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Species.MOD_ID);

    public static final RegistryObject<SimpleParticleType> SNORING = PARTICLE_TYPES.register("snoring", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BIRTD = PARTICLE_TYPES.register("birtd", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> DRIPPING_PELLET_DRIP = PARTICLE_TYPES.register("dripping_pellet_drip", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FALLING_PELLET_DRIP = PARTICLE_TYPES.register("falling_pellet_drip", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> LANDING_PELLET_DRIP = PARTICLE_TYPES.register("landing_pellet_drip", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FOOD = PARTICLE_TYPES.register("food", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ASCENDING_DUST = PARTICLE_TYPES.register("ascending_dust", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> TREEPER_LEAF = PARTICLE_TYPES.register("treeper_leaf", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ICHOR = PARTICLE_TYPES.register("ichor", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> YOUTH_POTION = PARTICLE_TYPES.register("youth_potion", () -> new SimpleParticleType(false));
}
