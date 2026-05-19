package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.PerSideStateMap;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

/** Ghoul disguise: keybind fires the 200t SEARCH alert (timed via {@code Level.getGameTime}), with
 *  {@code SearchingGoal}'s ring particles + sound. */
public final class GhoulSpecialActionBehavior implements DisguiseBehavior {

    public static final GhoulSpecialActionBehavior INSTANCE = new GhoulSpecialActionBehavior();

    /** Matches {@code SearchingGoal.start}: {@code searchTimer = 200}. */
    private static final long SEARCH_DURATION_TICKS = 200L;

    private static final class State {
        long firedGameTime;
        boolean active;
    }

    private final PerSideStateMap<State> states = new PerSideStateMap<>();

    private GhoulSpecialActionBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        Pose searching = SpeciesPose.SEARCHING.get();
        // Force STANDING -> SEARCHING so onSyncedDataUpdated dispatches even when a previous
        // press is still mid-window on this side.
        if (disguise.getPose() == searching) {
            disguise.setPose(Pose.STANDING);
        }
        disguise.setPose(searching);
        State st = states.computeIfAbsent(wearer, State::new);
        st.firedGameTime = wearer.level().getGameTime();
        st.active = true;
        // SearchingGoal.start(): play GHOUL_SEARCHING once when the alert begins.
        if (wearer.level() instanceof ServerLevel) {
            wearer.level().playSound(null, wearer.getX(), wearer.getY(), wearer.getZ(),
                    SpeciesSoundEvents.GHOUL_SEARCHING.get(), wearer.getSoundSource(), 3.0F, 1.0F);
        }
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        State st = states.get(wearer);
        if (st == null || !st.active) return;
        long elapsed = wearer.level().getGameTime() - st.firedGameTime;
        if (elapsed < SEARCH_DURATION_TICKS) {
            // Mirror SearchingGoal.tick() cadence at the wearer position: a GHOUL_SEARCHING ring
            // every 50t, with a GHOUL_SEARCHING2 wave 24t later (goal: waveDelay 50 -> 26).
            if (wearer.level() instanceof ServerLevel serverLevel) {
                long phase = elapsed % 50L;
                if (phase == 0L) {
                    serverLevel.sendParticles(SpeciesParticles.GHOUL_SEARCHING.get(),
                            wearer.getX(), wearer.getY() + 0.01, wearer.getZ(), 1, 0, 0, 0, 0.5F);
                } else if (phase == 24L) {
                    serverLevel.sendParticles(SpeciesParticles.GHOUL_SEARCHING2.get(),
                            wearer.getX(), wearer.getY() + 0.01, wearer.getZ(), 1, 0, 0, 0, 0.5F);
                }
            }
            return;
        }
        if (disguise.getPose() == SpeciesPose.SEARCHING.get()) {
            disguise.setPose(Pose.STANDING);
        }
        st.active = false;
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        states.remove(wearer);
    }
}
