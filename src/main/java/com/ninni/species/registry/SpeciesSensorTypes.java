package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.entity.ai.sensors.CruncherAttackEntitySensor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SpeciesSensorTypes {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(BuiltInRegistries.SENSOR_TYPE, Species.MOD_ID);

    public static final DeferredHolder<SensorType<?>, SensorType<CruncherAttackEntitySensor>> CRUNCHER_ATTACK_ENTITY_SENSOR = SENSOR_TYPES.register("cruncher_attack_entity_sensor", () -> new SensorType<>(CruncherAttackEntitySensor::new));

}