package com.ninni.species.registry;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.ninni.species.Species;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Map;
import java.util.Optional;

public class SpeciesMemoryModuleTypes {

    public static final Map<ResourceLocation, MemoryModuleType<?>> MEMORY_MODULES = Maps.newLinkedHashMap();

    public static final MemoryModuleType<Unit> ROAR_CHARGING = register("roar_charging", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> ROAR_COOLDOWN = register("roar_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> STOMP_CHARGING = register("stomp_charging", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> SPIT_CHARGING = register("spit_charging", Codec.unit(Unit.INSTANCE));

    private static <U> MemoryModuleType<U> register(String string, Codec<U> codec) {
        MemoryModuleType<U> type = new MemoryModuleType<>(Optional.of(codec));
        MEMORY_MODULES.put(new ResourceLocation(Species.MOD_ID, string), type);
        return type;
    }

    private static <U> MemoryModuleType<U> register(String string) {
        MemoryModuleType<U> type = new MemoryModuleType<>(Optional.empty());
        MEMORY_MODULES.put(new ResourceLocation(Species.MOD_ID, string), type);
        return type;
    }

    public static void init() {
        MEMORY_MODULES.forEach((resourceLocation, memoryModuleType) -> Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, resourceLocation, memoryModuleType));
    }

}
