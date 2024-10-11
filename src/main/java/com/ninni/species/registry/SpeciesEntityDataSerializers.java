package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.entity.Cruncher;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Species.MOD_ID);

    public static final RegistryObject<EntityDataSerializer<Cruncher.CruncherState>> CRUNCHER_STATE = ENTITY_DATA_SERIALIZERS.register("cruncher_state", () -> EntityDataSerializer.simpleEnum(Cruncher.CruncherState.class));

}