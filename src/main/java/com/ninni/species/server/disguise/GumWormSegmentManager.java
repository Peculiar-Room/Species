package com.ninni.species.server.disguise;

import com.ninni.species.registry.SpeciesConfig;
import com.ninni.species.server.disguise.panacea.SegmentChainManager;
import com.ninni.species.server.disguise.panacea.spec.ChainSpec;
import com.ninni.species.server.disguise.panacea.spec.IdealApproachPositioner;
import com.ninni.species.server.disguise.panacea.spec.IdealApproachRotator;
import com.ninni.species.server.disguise.panacea.spec.NoOpServerGuard;
import com.ninni.species.server.disguise.panacea.spec.ReflectionPlan;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Gum Worm disguise. Uses the default Ideal-Approach physics + ground clamp;
 * {@code linkSegment} writes connector IDs onto both this segment and the previous one.
 */
public final class GumWormSegmentManager extends SegmentChainManager {

    private static final int SEGMENT_COUNT = 17;
    private static final ResourceLocation GUM_WORM_ID = new ResourceLocation("alexscaves", "gum_worm");
    private static final ResourceLocation GUM_WORM_SEGMENT_ID = new ResourceLocation("alexscaves", "gum_worm_segment");

    public static final GumWormSegmentManager INSTANCE = new GumWormSegmentManager();

    private GumWormSegmentManager() {
        super(buildSpec());
    }

    private static ChainSpec buildSpec() {
        ReflectionPlan refl = ReflectionPlan.builder(GUM_WORM_ID, GUM_WORM_SEGMENT_ID)
                .accessor("HEAD_ENTITY_ID", ReflectionPlan.Target.PART, "HEAD_ENTITY_ID")
                .accessor("FRONT_ENTITY_ID", ReflectionPlan.Target.PART, "FRONT_ENTITY_ID")
                .accessor("BACK_ENTITY_ID", ReflectionPlan.Target.PART, "BACK_ENTITY_ID")
                .publicMethod("setIndex", ReflectionPlan.Target.PART, "setIndex", "int")
                .publicMethod("setHeadUUID", ReflectionPlan.Target.PART, "setHeadUUID", "java.util.UUID")
                .publicMethod("setFrontEntityUUID", ReflectionPlan.Target.PART, "setFrontEntityUUID", "java.util.UUID")
                .publicMethod("setBackEntityUUID", ReflectionPlan.Target.PART, "setBackEntityUUID", "java.util.UUID")
                .build();

        return ChainSpec.builder(GUM_WORM_ID, GUM_WORM_SEGMENT_ID)
                .segments(SEGMENT_COUNT)
                .backStretch(-2.5F)
                .clampToSurface(clampFromConfig())
                // 17 segments × 2.5 backStretch ≈ 42 blocks straight-line; the chain bunches in play.
                .cameraSizeMinimum(30.0)
                .reflection(refl)
                // Index-0 stretches further so head's tail-end and seg 0 don't overlap.
                .positioner(IdealApproachPositioner.withDefaults(
                        index -> index == 0 ? -3.7 : -2.5,
                        clampFromConfig()))
                .rotator(IdealApproachRotator.withDefaults())
                .serverGuard(NoOpServerGuard.INSTANCE)
                .linker((seg, prev, index, wearer, disguise, chain, r) -> {
                    @SuppressWarnings("unchecked")
                    EntityDataAccessor<Integer> headIdAcc = (EntityDataAccessor<Integer>) r.accessor("HEAD_ENTITY_ID");
                    @SuppressWarnings("unchecked")
                    EntityDataAccessor<Integer> frontIdAcc = (EntityDataAccessor<Integer>) r.accessor("FRONT_ENTITY_ID");
                    @SuppressWarnings("unchecked")
                    EntityDataAccessor<Integer> backIdAcc = (EntityDataAccessor<Integer>) r.accessor("BACK_ENTITY_ID");
                    Method setIndex = r.method("setIndex");
                    Method setHeadUuid = r.method("setHeadUUID");
                    Method setFrontUuid = r.method("setFrontEntityUUID");
                    Method setBackUuid = r.method("setBackEntityUUID");
                    try {
                        if (headIdAcc != null) seg.getEntityData().set(headIdAcc, wearer.getId());
                        if (frontIdAcc != null) seg.getEntityData().set(frontIdAcc, prev.getId());
                        if (setHeadUuid != null) setHeadUuid.invoke(seg, wearer.getUUID());
                        if (setFrontUuid != null) setFrontUuid.invoke(seg, prev.getUUID());
                        if (setIndex != null) setIndex.invoke(seg, index);
                        if (index > 0) {
                            // Back-mutation: write connector IDs onto the PREVIOUS segment so its
                            // renderer can resolve the chain link from front to back.
                            var prevSeg = chain.get(index - 1);
                            if (backIdAcc != null) prevSeg.getEntityData().set(backIdAcc, seg.getId());
                            if (setBackUuid != null) setBackUuid.invoke(prevSeg, seg.getUUID());
                        }
                    } catch (ReflectiveOperationException ignored) {}
                })
                .build();
    }

    private static boolean clampFromConfig() {
        try { return SpeciesConfig.SEGMENT_CHAIN_GROUND_CLAMP.get(); }
        catch (Throwable t) { return true; }
    }
}
