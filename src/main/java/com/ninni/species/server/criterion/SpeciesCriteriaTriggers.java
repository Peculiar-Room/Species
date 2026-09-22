package com.ninni.species.server.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SpeciesCriteriaTriggers extends SimpleCriterionTrigger<SpeciesCriteriaTriggers.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return SpeciesCriteriaTriggers.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, conditions -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static Codec<TriggerInstance> CODEC = ContextAwarePredicate.CODEC.optionalFieldOf("player")
                .xmap(TriggerInstance::new, TriggerInstance::player).codec();

        public static TriggerInstance create() {
            return new TriggerInstance(Optional.empty());
        }
    }
}