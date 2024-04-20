package com.ninni.species.entity.ai.sensors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.ninni.species.entity.Cruncher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class CruncherAttackEntitySensor extends NearestLivingEntitySensor<Cruncher> {

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    @Override
    protected void doTick(ServerLevel serverLevel, Cruncher cruncher) {

        if (cruncher.getHunger() == 0) {
            if (cruncher.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE).isPresent()) {
                cruncher.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
            }
            if (cruncher.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
                cruncher.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }
            return;
        }

        super.doTick(serverLevel, cruncher);

        cruncher.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream()
                .flatMap(Collection::stream)
                .filter(EntitySelector.NO_CREATIVE_OR_SPECTATOR)
                .filter(livingEntity -> Sensor.isEntityAttackable(cruncher, livingEntity))
                .filter(Player.class::isInstance)
                .findFirst()
                .ifPresentOrElse(
                        livingEntity -> cruncher.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, livingEntity),
                        () -> cruncher.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE)
                );
    }

    @Override
    protected int radiusXZ() {
        return 24;
    }

    @Override
    protected int radiusY() {
        return 24;
    }
}
