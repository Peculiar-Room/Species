package com.ninni.species.registry;

import com.mojang.serialization.Codec;
import com.ninni.species.Species;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesMemoryModuleTypes {

    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, Species.MOD_ID);

    public static final RegistryObject<MemoryModuleType<Unit>> ROAR_CHARGING = MEMORY_MODULE_TYPES.register("roar_charging", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Unit>> ROAR_COOLDOWN = MEMORY_MODULE_TYPES.register("roar_cooldown", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Unit>> STOMP_CHARGING = MEMORY_MODULE_TYPES.register("stomp_charging", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Unit>> SPIT_CHARGING = MEMORY_MODULE_TYPES.register("spit_charging", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));

}