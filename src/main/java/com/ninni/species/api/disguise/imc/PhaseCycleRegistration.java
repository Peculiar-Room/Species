package com.ninni.species.api.disguise.imc;

import com.ninni.species.api.disguise.PhaseCycleHandler;
import net.minecraft.world.entity.EntityType;

/** IMC payload for {@link SpeciesIMCKeys#REGISTER_PHASE_CYCLE}. */
public record PhaseCycleRegistration(EntityType<?> type, PhaseCycleHandler handler) {}
