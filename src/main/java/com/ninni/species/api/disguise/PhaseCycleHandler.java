package com.ninni.species.api.disguise;

import net.minecraft.world.entity.LivingEntity;

/** Per-disguise-type handler for the shift+keybind alt action. Implement to cycle the
 *  disguise body through visual phases (multi-stage bosses, variant rotation, etc.).
 *  Register via {@link SpeciesAPI#registerPhaseCycle} or IMC. */
@FunctionalInterface
public interface PhaseCycleHandler {
    /** Called when the wearer presses sneak + action keybind. Advance state by one phase. */
    void cycleNext(LivingEntity disguise);
}
