package com.ninni.species.init;

import com.ninni.species.Species;
import com.ninni.species.entity.ai.sensors.CruncherAttackEntitySensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesSensorTypes {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, Species.MOD_ID);

    public static final RegistryObject<SensorType<CruncherAttackEntitySensor>> CRUNCHER_ATTACK_ENTITY_SENSOR = SENSOR_TYPES.register("cruncher_attack_entity_sensor", () -> new SensorType<>(CruncherAttackEntitySensor::new));

}