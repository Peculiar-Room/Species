package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.mob.update_1.Birt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

/** Birt disguise: keybind fires the antenna signal — {@code antennaTicks=60} (antenna wobble + emissive
 *  fade ~3s) plus the {@code BIRT_SEARCH} sound, the same surface as {@code BirtCommunicatingGoal}. */
public final class BirtSignalSpecialActionBehavior implements DisguiseBehavior {

    public static final BirtSignalSpecialActionBehavior INSTANCE = new BirtSignalSpecialActionBehavior();

    /** Mirrors {@link Birt#handleEntityEvent}'s byte-10 dispatch. */
    private static final int SIGNAL_TICKS = 60;

    private BirtSignalSpecialActionBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise, ActionContext context) {
        if (disguise instanceof Birt birt) {
            birt.antennaTicks = SIGNAL_TICKS;
        }
        if (wearer.level() instanceof ServerLevel) {
            wearer.level().playSound(null, wearer.getX(), wearer.getY(), wearer.getZ(),
                    SpeciesSoundEvents.BIRT_SEARCH.get(), wearer.getSoundSource(),
                    0.35F, wearer.getVoicePitch());
        }
    }
}
