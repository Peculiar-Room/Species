package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.PerSideStateMap;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.mob.update_3.LeafHanger;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Leaf Hanger trap on the action key: press in water to cast a lily pad on the surface (body turns
 *  translucent), press again to reel in + bite 3&times;. Per-side state; the body lerps the tongue. */
public final class LeafHangerBehavior implements DisguiseBehavior {

    public static final LeafHangerBehavior INSTANCE = new LeafHangerBehavior();

    /** Past this the native tongue self-retracts, so a surface farther than this yields no cast. */
    private static final double MAX_REACH = 40.0;
    /** Tongue-tip distance from the mouth at which the catch bite begins. */
    private static final float BITE_DISTANCE = 0.6F;
    private static final int BITES = 3;
    private static final int JAW_SNAP_TICKS = 10;

    private enum Phase { IDLE, CAST, RETRACT, BITE }

    private static final class State {
        Phase phase = Phase.IDLE;
        int bitesDone;
    }

    private final PerSideStateMap<State> states = new PerSideStateMap<>();

    private LeafHangerBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise, ActionContext context) {
        if (context.shifted() || !(disguise instanceof LeafHanger leaf)) return;
        State st = states.computeIfAbsent(wearer, State::new);
        switch (st.phase) {
            case IDLE -> {
                if (!wearer.isInWater()) return;
                Vector3f surface = findWaterSurface(wearer, leaf);
                if (surface == null) return;
                leaf.setBaitBlockState(Blocks.LILY_PAD.defaultBlockState());
                leaf.setPullingTarget(false);
                leaf.activateTongue(surface);
                st.phase = Phase.CAST;
                playServer(wearer, SpeciesSoundEvents.LEAF_HANGER_SHOOT.get(), 1.0F);
            }
            case CAST -> {
                st.phase = Phase.RETRACT;
                st.bitesDone = 0;
            }
            default -> { /* RETRACT/BITE: sequence already running */ }
        }
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        if (!(disguise instanceof LeafHanger leaf)) return;
        State st = states.get(wearer);
        if (st == null) return;
        switch (st.phase) {
            case CAST -> {
                if (!wearer.isInWater()) {
                    leaf.deactivateTongue();
                    st.phase = Phase.IDLE;
                    return;
                }
                Vector3f surface = findWaterSurface(wearer, leaf);
                if (surface != null) {
                    leaf.setTargetPos(surface);
                    leaf.setTongueTarget(surface);
                }
                leaf.setPullingTarget(false);
            }
            case RETRACT -> {
                Vector3f mouth = mouthPos(wearer, leaf);
                leaf.setTargetPos(mouth);
                leaf.setTongueTarget(mouth);
                leaf.setPullingTarget(false);
                if (tongueNearMouth(leaf)) {
                    st.phase = Phase.BITE;
                    st.bitesDone = 0;
                }
            }
            case BITE -> {
                if (leaf.getJawSnapTicks() == 0) {
                    if (st.bitesDone < BITES) {
                        leaf.setJawSnapTicks(JAW_SNAP_TICKS);
                        st.bitesDone++;
                        playServer(wearer, SpeciesSoundEvents.LEAF_HANGER_ATTACK.get(), 1.0F);
                    } else {
                        leaf.deactivateTongue();
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

    private static Vector3f mouthPos(LivingEntity wearer, LeafHanger leaf) {
        return new Vector3f((float) wearer.getX(),
                (float) (wearer.getY() + leaf.getTongueOffset()), (float) wearer.getZ());
    }

    private static boolean tongueNearMouth(LeafHanger leaf) {
        return leaf.getCurrentTonguePos().distance(new Vector3f(0, leaf.getTongueOffset(), 0)) < BITE_DISTANCE;
    }

    private static void playServer(LivingEntity wearer, SoundEvent sound, float volume) {
        if (!wearer.level().isClientSide) {
            wearer.level().playSound(null, wearer.getX(), wearer.getY(), wearer.getZ(),
                    sound, SoundSource.HOSTILE, volume, 1.0F);
        }
    }

    /** Climbs the water column above the wearer to the first air block and returns its bottom-face
     *  centre (the water line), or {@code null} if no reachable open surface sits overhead. */
    private static Vector3f findWaterSurface(LivingEntity wearer, LeafHanger leaf) {
        Level level = wearer.level();
        Vec3 origin = wearer.position().add(0, leaf.getTongueOffset(), 0);
        BlockPos pos = BlockPos.containing(origin);
        int guard = 0;
        while (level.getFluidState(pos).is(Fluids.WATER) && guard++ < 64) pos = pos.above();
        if (!level.getBlockState(pos).isAir()) return null;
        Vec3 target = Vec3.atBottomCenterOf(pos);
        if (origin.distanceTo(target) >= MAX_REACH) return null;
        return target.toVector3f();
    }
}
