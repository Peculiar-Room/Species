package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.PerSideStateMap;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.disguise.dsl.WearerPredicates;
import com.ninni.species.server.entity.mob.update_3.CliffHanger;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Cliff Hanger: native ceiling cling (flying into a ceiling flips the model) plus an action-key harpoon
 *  — down-trap when clung, up-grapple under a ceiling, else up-trap; press again to reel in + bite 3&times;. */
public final class CliffHangerBehavior implements DisguiseBehavior {

    public static final CliffHangerBehavior INSTANCE = new CliffHangerBehavior();

    /** Harpoon reach, same up and down; kept under the native 40-block tongue auto-retract. */
    private static final double MAX_REACH = 32.0;
    private static final float BITE_DISTANCE = 0.6F;
    private static final int BITES = 3;
    private static final int JAW_SNAP_TICKS = 10;

    private enum Phase { IDLE, EXTEND, GRAPPLE, RETRACT, BITE }

    private static final class State {
        Phase phase = Phase.IDLE;
        boolean launchedUp;
        int bitesDone;
    }

    private final PerSideStateMap<State> states = new PerSideStateMap<>();

    private CliffHangerBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise, ActionContext context) {
        if (context.shifted() || !(disguise instanceof CliffHanger cliff)) return;
        State st = states.computeIfAbsent(wearer, State::new);
        switch (st.phase) {
            case IDLE -> {
                if (cliff.isAttached()) {
                    cliff.activateTongue(raycastDown(wearer));
                    st.phase = Phase.EXTEND;
                    st.launchedUp = false;
                    playServer(wearer, SpeciesSoundEvents.CLIFF_HANGER_SHOOT.get(), 1.0F);
                } else {
                    Vector3f ceiling = raycastUp(wearer);
                    if (ceiling != null) {
                        cliff.activateTongue(ceiling);
                        st.phase = Phase.GRAPPLE;
                        st.launchedUp = true;
                        playServer(wearer, SpeciesSoundEvents.CLIFF_HANGER_ATTACH.get(), 1.0F);
                    } else {
                        cliff.activateTongue(new Vector3f((float) wearer.getX(),
                                (float) (wearer.getY() + MAX_REACH), (float) wearer.getZ()));
                        st.phase = Phase.EXTEND;
                        st.launchedUp = true;
                        playServer(wearer, SpeciesSoundEvents.CLIFF_HANGER_SHOOT.get(), 1.0F);
                    }
                }
            }
            case EXTEND -> {
                st.phase = Phase.RETRACT;
                st.bitesDone = 0;
            }
            case GRAPPLE -> {
                cliff.deactivateTongue();
                st.phase = Phase.IDLE;
            }
            default -> { /* RETRACT/BITE: sequence already running */ }
        }
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        if (!(disguise instanceof CliffHanger cliff)) return;
        // Native cling: the wearer's ceiling state drives ATTACHED (model self-flips); keep the
        // upward-climb flag off so the resting tongue/offset branch is used.
        cliff.setAttached(WearerPredicates.CEILING_HANGING.test(wearer));
        cliff.setGoingUpwards(false);

        State st = states.get(wearer);
        if (st == null) return;

        // An up-launch is cancelled the moment the wearer flips upside-down (clings to the ceiling).
        if (st.launchedUp && (st.phase == Phase.EXTEND || st.phase == Phase.GRAPPLE) && cliff.isAttached()) {
            cliff.deactivateTongue();
            st.phase = Phase.IDLE;
            return;
        }

        switch (st.phase) {
            case EXTEND -> {
                // A downward harpoon is moot if the wearer leaves the ceiling.
                if (!st.launchedUp && !cliff.isAttached()) {
                    cliff.deactivateTongue();
                    st.phase = Phase.IDLE;
                }
            }
            case GRAPPLE -> { /* tongue holds at the ceiling target until released */ }
            case RETRACT -> {
                Vector3f mouth = mouthPos(wearer, cliff);
                cliff.setTargetPos(mouth);
                cliff.setTongueTarget(mouth);
                if (tongueNearMouth(cliff)) {
                    st.phase = Phase.BITE;
                    st.bitesDone = 0;
                }
            }
            case BITE -> {
                if (cliff.getJawSnapTicks() == 0) {
                    if (st.bitesDone < BITES) {
                        cliff.setJawSnapTicks(JAW_SNAP_TICKS);
                        st.bitesDone++;
                        playServer(wearer, SpeciesSoundEvents.CLIFF_HANGER_ATTACK.get(), 1.0F);
                    } else {
                        cliff.deactivateTongue();
                        st.phase = Phase.IDLE;
                    }
                }
            }
            default -> { /* IDLE */ }
        }
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        states.remove(wearer);
    }

    private static Vector3f mouthPos(LivingEntity wearer, CliffHanger cliff) {
        return new Vector3f((float) wearer.getX(),
                (float) (wearer.getY() + cliff.getTongueOffset()), (float) wearer.getZ());
    }

    private static boolean tongueNearMouth(CliffHanger cliff) {
        return cliff.getCurrentTonguePos().distance(new Vector3f(0, cliff.getTongueOffset(), 0)) < BITE_DISTANCE;
    }

    /** Raycast up from the wearer's head; the ceiling hit within reach, or {@code null} for open sky. */
    private static Vector3f raycastUp(LivingEntity wearer) {
        Vec3 start = wearer.position().add(0, wearer.getBbHeight(), 0);
        Vec3 end = start.add(0, MAX_REACH, 0);
        BlockHitResult hit = wearer.level().clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, wearer));
        return hit.getType() == HitResult.Type.BLOCK ? hit.getLocation().toVector3f() : null;
    }

    /** Raycast down from the wearer; the floor hit within reach, or the max-reach point if none. */
    private static Vector3f raycastDown(LivingEntity wearer) {
        Vec3 start = wearer.position();
        Vec3 end = start.subtract(0, MAX_REACH, 0);
        BlockHitResult hit = wearer.level().clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, wearer));
        Vec3 target = hit.getType() == HitResult.Type.BLOCK ? hit.getLocation() : end;
        return target.toVector3f();
    }

    private static void playServer(LivingEntity wearer, SoundEvent sound, float volume) {
        if (!wearer.level().isClientSide) {
            wearer.level().playSound(null, wearer.getX(), wearer.getY(), wearer.getZ(),
                    sound, SoundSource.HOSTILE, volume, 1.0F);
        }
    }
}
