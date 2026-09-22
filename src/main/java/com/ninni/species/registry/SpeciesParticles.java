package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Species.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SNORING = PARTICLE_TYPES.register("snoring", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BIRTD = PARTICLE_TYPES.register("birtd", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DRIPPING_PELLET_DRIP = PARTICLE_TYPES.register("dripping_pellet_drip", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FALLING_PELLET_DRIP = PARTICLE_TYPES.register("falling_pellet_drip", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LANDING_PELLET_DRIP = PARTICLE_TYPES.register("landing_pellet_drip", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FOOD = PARTICLE_TYPES.register("food", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ASCENDING_DUST = PARTICLE_TYPES.register("ascending_dust", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TREEPER_LEAF = PARTICLE_TYPES.register("treeper_leaf", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ICHOR = PARTICLE_TYPES.register("ichor", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> YOUTH_POTION = PARTICLE_TYPES.register("youth_potion", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GHOUL_SEARCHING = PARTICLE_TYPES.register("ghoul_searching", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GHOUL_SEARCHING2 = PARTICLE_TYPES.register("ghoul_searching2", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> KINETIC_ENERGY = PARTICLE_TYPES.register("kinetic_energy", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMALL_KINETIC_ENERGY = PARTICLE_TYPES.register("small_kinetic_energy", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WICKED_FLAME = PARTICLE_TYPES.register("wicked_flame", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WICKED_EMBER = PARTICLE_TYPES.register("wicked_ember", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> POOF = PARTICLE_TYPES.register("poof", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPECTRALIBUR = PARTICLE_TYPES.register("spectralibur", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPECTRALIBUR_INVERTED = PARTICLE_TYPES.register("spectralibur_inverted", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPECTRALIBUR_RELEASED = PARTICLE_TYPES.register("spectralibur_released", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPECTRE_SMOKE = PARTICLE_TYPES.register("spectre_smoke", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ASCENDING_SPECTRE_SMOKE = PARTICLE_TYPES.register("ascending_spectre_smoke", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPECTRE_POP = PARTICLE_TYPES.register("spectre_pop", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BROKEN_LINK = PARTICLE_TYPES.register("broken_link", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> COLLECTED_SOUL = PARTICLE_TYPES.register("collected_soul", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BEWEREAGER_HOWL = PARTICLE_TYPES.register("bewereager_howl", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BEWEREAGER_SPEED = PARTICLE_TYPES.register("bewereager_speed", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BEWEREAGER_SLOW = PARTICLE_TYPES.register("bewereager_slow", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DRIPPING_HANGER_SALIVA = PARTICLE_TYPES.register("dripping_hanger_saliva", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FALLING_HANGER_SALIVA = PARTICLE_TYPES.register("falling_hanger_saliva", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LANDING_HANGER_SALIVA = PARTICLE_TYPES.register("landing_hanger_saliva", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HANGER_CRIT = PARTICLE_TYPES.register("hanger_crit", () -> new SimpleParticleType(true));

}