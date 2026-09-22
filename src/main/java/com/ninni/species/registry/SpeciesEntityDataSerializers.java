package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.entity.mob.update_2.Cruncher;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SpeciesEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Species.MOD_ID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Cruncher.CruncherState>> CRUNCHER_STATE = ENTITY_DATA_SERIALIZERS.register("cruncher_state", () -> EntityDataSerializer.forValueType(Cruncher.CruncherState.STREAM_CODEC));

}