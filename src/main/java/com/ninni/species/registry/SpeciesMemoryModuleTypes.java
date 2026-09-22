package com.ninni.species.registry;

import com.mojang.serialization.Codec;
import com.ninni.species.Species;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;

public class SpeciesMemoryModuleTypes {

    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(BuiltInRegistries.MEMORY_MODULE_TYPE, Species.MOD_ID);

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> ROAR_CHARGING = MEMORY_MODULE_TYPES.register("roar_charging", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> ROAR_COOLDOWN = MEMORY_MODULE_TYPES.register("roar_cooldown", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> STOMP_CHARGING = MEMORY_MODULE_TYPES.register("stomp_charging", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Unit>> SPIT_CHARGING = MEMORY_MODULE_TYPES.register("spit_charging", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));

}