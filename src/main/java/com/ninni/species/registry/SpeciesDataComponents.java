package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.block.entity.BirtDwellingBlockEntity.Occupant;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class SpeciesDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Species.MOD_ID);

    public static final Supplier<DataComponentType<List<Occupant>>> BIRTS = DATA_COMPONENTS.registerComponentType("birts", builder -> builder
            .persistent(Occupant.LIST_CODEC)
            .networkSynchronized(Occupant.STREAM_CODEC.apply(ByteBufCodecs.list()))
            .cacheEncoding());
}