package com.ninni.species.api.disguise.imc;

import com.ninni.species.api.disguise.CompanionStackSpec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/** IMC payload for {@link SpeciesIMCKeys#REGISTER_COMPANION_STACK}. */
public record CompanionStackRegistration(EntityType<?> type, CompanionStackSpec<? extends LivingEntity> spec) {}
