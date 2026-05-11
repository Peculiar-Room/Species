package com.ninni.species.api.disguise;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Wearer state at special-action keybind press. {@code shifted} = sneak held → behaviors
 *  dispatch the alternate action (e.g. phase cycling). SWIMMING is active swim, UNDERWATER
 *  is the broader still-in-water case. */
public record ActionContext(Phase phase, boolean shifted) {

    public enum Phase { GROUND, FLYING, SWIMMING, UNDERWATER }

    /** Resolves the wearer's current phase without modifier. */
    public static ActionContext of(LivingEntity wearer) {
        return of(wearer, false);
    }

    /** Resolves the wearer's current phase plus the sneak-modifier state at press time. */
    public static ActionContext of(LivingEntity wearer, boolean shifted) {
        return new ActionContext(resolvePhase(wearer), shifted);
    }

    private static Phase resolvePhase(LivingEntity wearer) {
        if (wearer == null) return Phase.GROUND;
        if (wearer.isSwimming()) return Phase.SWIMMING;
        if (wearer.isInWater()) return Phase.UNDERWATER;
        if (wearer.isFallFlying()) return Phase.FLYING;
        if (wearer instanceof Player p && p.getAbilities().flying) return Phase.FLYING;
        return Phase.GROUND;
    }
}
