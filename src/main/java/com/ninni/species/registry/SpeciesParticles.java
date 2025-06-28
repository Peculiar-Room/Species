package com.ninni.species.registry;

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

    public static final RegistryObject<SimpleParticleType> DRIPPING_PELLET_DRIP = PARTICLE_TYPES.register("dripping_pellet_drip", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FALLING_PELLET_DRIP = PARTICLE_TYPES.register("falling_pellet_drip", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> LANDING_PELLET_DRIP = PARTICLE_TYPES.register("landing_pellet_drip", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FOOD = PARTICLE_TYPES.register("food", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ASCENDING_DUST = PARTICLE_TYPES.register("ascending_dust", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> TREEPER_LEAF = PARTICLE_TYPES.register("treeper_leaf", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ICHOR = PARTICLE_TYPES.register("ichor", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> YOUTH_POTION = PARTICLE_TYPES.register("youth_potion", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> GHOUL_SEARCHING = PARTICLE_TYPES.register("ghoul_searching", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> GHOUL_SEARCHING2 = PARTICLE_TYPES.register("ghoul_searching2", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> KINETIC_ENERGY = PARTICLE_TYPES.register("kinetic_energy", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SMALL_KINETIC_ENERGY = PARTICLE_TYPES.register("small_kinetic_energy", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> WICKED_FLAME = PARTICLE_TYPES.register("wicked_flame", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> WICKED_EMBER = PARTICLE_TYPES.register("wicked_ember", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> POOF = PARTICLE_TYPES.register("poof", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPECTRALIBUR = PARTICLE_TYPES.register("spectralibur", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPECTRALIBUR_INVERTED = PARTICLE_TYPES.register("spectralibur_inverted", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPECTRALIBUR_RELEASED = PARTICLE_TYPES.register("spectralibur_released", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPECTRE_SMOKE = PARTICLE_TYPES.register("spectre_smoke", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> ASCENDING_SPECTRE_SMOKE = PARTICLE_TYPES.register("ascending_spectre_smoke", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPECTRE_POP = PARTICLE_TYPES.register("spectre_pop", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BROKEN_LINK = PARTICLE_TYPES.register("broken_link", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> COLLECTED_SOUL = PARTICLE_TYPES.register("collected_soul", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BEWEREAGER_HOWL = PARTICLE_TYPES.register("bewereager_howl", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BEWEREAGER_SPEED = PARTICLE_TYPES.register("bewereager_speed", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BEWEREAGER_SLOW = PARTICLE_TYPES.register("bewereager_slow", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DRIPPING_HANGER_SALIVA = PARTICLE_TYPES.register("dripping_hanger_saliva", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FALLING_HANGER_SALIVA = PARTICLE_TYPES.register("falling_hanger_saliva", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> LANDING_HANGER_SALIVA = PARTICLE_TYPES.register("landing_hanger_saliva", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> HANGER_CRIT = PARTICLE_TYPES.register("hanger_crit", () -> new SimpleParticleType(true));

}