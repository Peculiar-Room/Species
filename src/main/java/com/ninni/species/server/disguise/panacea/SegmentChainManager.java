package com.ninni.species.server.disguise.panacea;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.registry.SpeciesConfig;
import com.ninni.species.server.disguise.ChainHeadRegistry;
import com.ninni.species.server.disguise.DisguiseBehaviorRegistry;
import com.ninni.species.server.disguise.panacea.spec.BeforeChainTickHook;
import com.ninni.species.server.disguise.panacea.spec.ChainSpec;
import com.ninni.species.server.disguise.panacea.spec.SegmentContext;
import com.ninni.species.server.disguise.panacea.spec.TickHook;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Spec-driven manager for client-side segmented-chain disguises (head + linked body parts).
 *  Construct with a {@link ChainSpec} and call {@link #register()} once; self-registers as a
 *  global {@link DisguiseBehavior} and as a sub-entity provider for render dispatch. */
public class SegmentChainManager implements DisguiseBehavior {

    @Override
    public boolean bodyYawTracksCamera(LivingEntity disguise) { return applies(disguise); }

    /** Live registry of all managers. Used by {@link #cleanupAll(LivingEntity)} for level-leave hooks. */
    private static final List<SegmentChainManager> ALL = new java.util.concurrent.CopyOnWriteArrayList<>();

    /** Global ID lookup — segments aren't in {@code ClientLevel} (orphan-packet eviction),
     *  so {@code ClientLevelMixin} falls through here for renderer neighbour lookups. */
    private static final Map<Integer, Entity> SEGMENT_BY_ID = new ConcurrentHashMap<>();

    private final ChainSpec spec;
    private final Map<UUID, List<Entity>> chains = new ConcurrentHashMap<>();

    public SegmentChainManager(ChainSpec spec) {
        this.spec = spec;
    }

    public final ChainSpec spec() { return spec; }

    // ----- Wiring & lifecycle (final) -------------------------------------

    /** Read-only view used by render dispatch. Empty when no chain is active for the wearer. */
    public final List<Entity> getSegments(UUID wearerUUID) {
        return chains.getOrDefault(wearerUUID, Collections.emptyList());
    }

    /** Self-register as a global behavior + sub-entity provider. Call once from setup. */
    public final void register() {
        ALL.add(this);
        DisguiseBehaviorRegistry.registerGlobal(this);
        DisguiseTopologyRegistry.registerGlobalSubEntityProvider((wearer, disguise) -> {
            if (!applies(disguise)) return Collections.emptyList();
            List<Entity> segs = chains.get(wearer.getUUID());
            return segs != null ? segs : Collections.emptyList();
        });
        // Apply the camera-size floor declaratively from the spec — chains whose silhouette
        // extends beyond the head's AABB need this so the third-person camera frames the body.
        if (spec.cameraSizeMinimum() > 0.0) {
            EntityType<?> headType = BuiltInRegistries.ENTITY_TYPE.getOptional(spec.headId()).orElse(null);
            if (headType != null) DisguiseTopologyRegistry.setCameraSizeMinimum(headType, spec.cameraSizeMinimum());
        }
        // Map this chain's segment types back to the head so the Wicked Mask imprint can redirect
        // segment captures (centipede body/tail, anaconda part, …) to the head when the config flag is on.
        ChainHeadRegistry.registerByIds(
                spec.headId(), spec.partId(), spec.tailPartId());
    }

    /** Discard all chains owned by the wearer across every registered manager. Wearer-leaves-level hook. */
    public static void cleanupAll(LivingEntity wearer) {
        if (wearer == null) return;
        UUID id = wearer.getUUID();
        for (SegmentChainManager m : ALL) {
            try { m.removeChain(id); } catch (Throwable ignored) {}
        }
    }

    /** Resolve a chain segment by entity ID (used by {@code ClientLevelMixin} fallback for connector renderers). */
    public static Entity findById(int entityId) {
        return SEGMENT_BY_ID.get(entityId);
    }

    @Override
    public final void onCreated(LivingEntity wearer, LivingEntity disguise) {
        if (wearer == null || disguise == null) return;
        if (!applies(disguise)) return;
        if (wearer.level().isClientSide) {
            if (shouldRenderChain()) spawnChain(wearer, disguise);
        } else {
            // serverGuard suppresses head-side tick effects (spawn loops, etc.) regardless of
            // whether the visual chain renders.
            try {
                spec.reflectionPlan().ensureInited(wearer.level());
                spec.serverGuard().apply(wearer, disguise, spec.reflectionPlan());
            } catch (Throwable ignored) {}
        }
    }

    @Override
    public final void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        if (wearer == null || disguise == null) return;
        if (!applies(disguise)) return;
        if (!wearer.level().isClientSide) return;
        removeChain(wearer.getUUID());
    }

    @Override
    public final void preTick(LivingEntity wearer, LivingEntity disguise) {
        if (wearer == null || disguise == null) return;
        if (!applies(disguise)) return;
        if (!wearer.level().isClientSide) return;

        // Reconcile chain presence with the redirect flag every tick so toggling it mid-wear
        // takes effect without re-imprinting.
        boolean shouldRender = shouldRenderChain();
        UUID uuid = wearer.getUUID();
        boolean hasChain = chains.containsKey(uuid);
        if (shouldRender && !hasChain) spawnChain(wearer, disguise);
        else if (!shouldRender && hasChain) removeChain(uuid);

        if (shouldRender) tickChain(wearer, disguise);
    }

    // ----- Spec-driven dispatch ------------------------------------------

    private boolean applies(LivingEntity disguise) {
        if (disguise == null) return false;
        EntityType<?> t = disguise.getType();
        return t != null && spec.headId().equals(BuiltInRegistries.ENTITY_TYPE.getKey(t));
    }

    private static boolean shouldRenderChain() {
        try { return SpeciesConfig.REDIRECT_SEGMENT_TO_HEAD.get(); }
        catch (Throwable ignored) { return true; }
    }

    private Entity createSegment(Level level, int index, int total) {
        spec.reflectionPlan().ensureInited(level);
        EntityType<?> type = (spec.tailPartId() != null && index == total - 1)
                ? spec.reflectionPlan().tailPartType()
                : spec.reflectionPlan().partType();
        return type == null ? null : type.create(level);
    }

    private void tickSegment(Entity seg, Entity prev, int index, LivingEntity wearer,
                             LivingEntity disguise, int tickCount) {
        SegmentContext ctx = new SegmentContext(seg, prev, index, wearer, disguise, tickCount, spec);
        if (spec.tickGuard().isPresent() && !spec.tickGuard().get().shouldTick(ctx)) return;
        for (TickHook h : spec.preTickHooks()) h.run(ctx);
        spec.positioner().position(ctx);
        for (TickHook h : spec.postTickHooks()) h.run(ctx);
        spec.rotator().rotate(ctx);
        spec.propagator().ifPresent(p -> p.propagate(ctx));
        spec.animDriver().ifPresent(a -> a.drive(ctx));
        seg.tickCount = tickCount;
    }

    // ----- Internals ------------------------------------------------------

    private void spawnChain(LivingEntity wearer, LivingEntity disguise) {
        removeChain(wearer.getUUID()); // defensive
        Level level = wearer.level();
        int total = spec.segmentCount();
        if (total <= 0) return;
        List<Entity> chain = new ArrayList<>(total);
        Entity prev = wearer;
        double cumulative = 0;
        for (int i = 0; i < total; i++) {
            Entity seg;
            try {
                seg = createSegment(level, i, total);
            } catch (Throwable t) {
                continue;
            }
            if (seg == null) continue;

            // Disguise segments shouldn't fall; useNoPhysicsFlag also suppresses block probing
            // (override to false when the chain's positioner needs terrain reads).
            seg.setNoGravity(true);
            if (spec.useNoPhysicsFlag()) seg.noPhysics = true;

            cumulative += spec.initialSpacing();
            Vec3 initial = wearer.position().add(
                    new Vec3(0, 0, -cumulative).yRot((float) -Math.toRadians(wearer.getYRot())));
            seg.setPos(initial);
            seg.xo = seg.xOld = initial.x;
            seg.yo = seg.yOld = initial.y;
            seg.zo = seg.zOld = initial.z;
            seg.setYRot(wearer.getYRot());
            seg.yRotO = wearer.getYRot();

            try {
                spec.linker().link(seg, prev, i, wearer, disguise, chain, spec.reflectionPlan());
            } catch (Throwable ignored) {}

            chain.add(seg);
            SEGMENT_BY_ID.put(seg.getId(), seg);
            prev = seg;
        }
        if (!chain.isEmpty()) chains.put(wearer.getUUID(), chain);
    }

    private void tickChain(LivingEntity wearer, LivingEntity disguise) {
        List<Entity> chain = chains.get(wearer.getUUID());
        if (chain == null || chain.isEmpty()) return;
        for (BeforeChainTickHook h : spec.beforeChainTickHooks()) {
            h.run(wearer, disguise, spec.reflectionPlan());
        }
        // First segment's parent is the disguise body, NOT the wearer. Both share world position,
        // but the disguise's xRot/yRot are normalised by LivingEntityMixin (xRot=0 for ground mobs)
        // whereas wearer.xRot is the player's view pitch — would make the chain follow camera angle
        // into the air.
        Entity prev = disguise;
        int tickCount = wearer.tickCount;
        for (int i = 0; i < chain.size(); i++) {
            Entity seg = chain.get(i);
            if (seg == null || seg.isRemoved()) continue;
            tickSegment(seg, prev, i, wearer, disguise, tickCount);
            prev = seg;
        }
    }

    private void removeChain(UUID wearerUUID) {
        List<Entity> chain = chains.remove(wearerUUID);
        if (chain == null) return;
        for (Entity seg : chain) {
            if (seg == null) continue;
            SEGMENT_BY_ID.remove(seg.getId());
            if (!seg.isRemoved()) seg.discard();
        }
    }

    // ----- Public utility statics ----------------------------------------

    /** Snapshot prev-frame transform fields the renderer interpolates against. Call before setPos. */
    public static void savePrevFrame(Entity e) {
        e.xo = e.getX(); e.yo = e.getY(); e.zo = e.getZ();
        e.xOld = e.getX(); e.yOld = e.getY(); e.zOld = e.getZ();
        e.yRotO = e.getYRot();
        e.xRotO = e.getXRot();
    }

    /** Variant that also pins yBodyRotO/yHeadRotO for LivingEntity segments. */
    public static void savePrevFrameLiving(LivingEntity e) {
        savePrevFrame(e);
        e.yBodyRotO = e.yBodyRot;
        e.yHeadRotO = e.yHeadRot;
    }

    /** Scans 6 blocks down from {@code y+2} for the first non-empty collision shape and returns
     *  its top (leaves/slabs/snow/etc.). Returns {@code y} unchanged if none, so segments over
     *  caves don't dive to a far floor. */
    public static double computeSurfaceY(Level level, double x, double y, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int top = (int) Math.round(y) + 2;
        int blockX = (int) Math.round(x);
        int blockZ = (int) Math.round(z);
        int minY = Math.max(level.getMinBuildHeight(), top - 6);
        for (int curY = top; curY >= minY; curY--) {
            pos.set(blockX, curY, blockZ);
            if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
                return 1.0 + curY;
            }
        }
        return y;
    }
}
