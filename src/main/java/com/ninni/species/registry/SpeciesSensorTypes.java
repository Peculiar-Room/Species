package com.ninni.species.registry;

import com.google.common.collect.Maps;
import com.ninni.species.Species;
import com.ninni.species.entity.ai.sensors.CruncherAttackEntitySensor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.Map;
import java.util.function.Supplier;

public class SpeciesSensorTypes {
    private static final Map<ResourceLocation, SensorType<?>> SENSOR_TYPES = Maps.newLinkedHashMap();

    public static final SensorType<CruncherAttackEntitySensor> CRUNCHER_ATTACK_ENTITY_SENSOR = register("cruncher_attack_entity_sensor", CruncherAttackEntitySensor::new);

    private static <U extends Sensor<?>> SensorType<U> register(String string, Supplier<U> supplier) {
        SensorType<U> sensorType = new SensorType<>(supplier);
        SENSOR_TYPES.put(new ResourceLocation(Species.MOD_ID, string), sensorType);
        return sensorType;
    }

    public static void init() {
        SENSOR_TYPES.forEach((resourceLocation, sensorType) -> Registry.register(BuiltInRegistries.SENSOR_TYPE, resourceLocation, sensorType));
    }

}
