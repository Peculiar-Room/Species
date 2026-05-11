package com.ninni.species.api.disguise.imc;

import com.ninni.species.api.disguise.IntStateCycleSpec;
import net.minecraft.world.entity.EntityType;

/** IMC payload for {@link SpeciesIMCKeys#REGISTER_INT_STATE_CYCLE}. */
public record IntStateCycleRegistration(EntityType<?> type, IntStateCycleSpec spec) {}
