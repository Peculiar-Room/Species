package com.ninni.species.server.disguise;

import com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Spawns a client-side {@code MineGuardianAnchorEntity} companion for MineGuardian disguises.
 *  AC's chain renders via {@code MineGuardianAnchorRenderer}, so the anchor is required for
 *  the chain visual. Soft-dep, reflective. */
public final class MineGuardianAnchorManager {

    private static final ResourceLocation MINE_GUARDIAN_ID = new ResourceLocation("alexscaves", "mine_guardian");
    private static final ResourceLocation MINE_GUARDIAN_ANCHOR_ID = new ResourceLocation("alexscaves", "mine_guardian_anchor");

    /** Fixed chain length for the disguise pair. Natural Guardian uses 7-12 randomly. */
    private static final int CHAIN_LENGTH = 5;

    private static final Map<UUID, Entity> ANCHORS = new ConcurrentHashMap<>();

    static {
        // Self-register as a Panacea sub-entity provider (same pattern as BoundroidPairManager).
        DisguiseTopologyRegistry.registerGlobalSubEntityProvider(
                (wearer, disguise) -> {
                    if (!isMineGuardianDisguise(disguise)) return java.util.Collections.emptyList();
                    Entity anchor = ANCHORS.get(wearer.getUUID());
                    if (anchor == null || anchor.isRemoved()) return java.util.Collections.emptyList();
                    return java.util.Collections.singletonList(anchor);
                });
    }

    private static volatile boolean reflectionInited;
    private static EntityType<?> anchorType;
    private static Method anchorLinkWithGuardianMethod;
    private static Method guardianSetAnchorUuidMethod;
    private static Method guardianSetMaxChainLengthMethod;
    private static EntityDataAccessor<Integer> guardianAnchorIdAccessor;
    private static EntityDataAccessor<Integer> anchorGuardianIdAccessor;

    private MineGuardianAnchorManager() {}

    public static boolean isMineGuardianDisguise(LivingEntity disguise) {
        if (disguise == null || disguise.getType() == null) return false;
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(disguise.getType());
        return key != null && key.equals(MINE_GUARDIAN_ID);
    }

    public static Entity getAnchor(UUID wearerUUID) {
        return ANCHORS.get(wearerUUID);
    }

    /** Called when a disguise is created. No-op unless client-side and the disguise is a MineGuardian. */
    public static void onDisguiseCreated(LivingEntity wearer, LivingEntity disguise) {
        if (wearer == null || disguise == null) return;
        if (!wearer.level().isClientSide) return;
        if (!isMineGuardianDisguise(disguise)) return;
        initReflection(wearer.level());
        if (anchorType == null) return;

        // Defensive cleanup before replacing.
        removeAnchorByUUID(wearer.getUUID());

        Entity anchor = anchorType.create(wearer.level());
        if (anchor == null) return;
        anchor.noPhysics = true;
        anchor.setNoGravity(true);

        // Position anchor below the wearer so the chain renders as a visible
        // vertical span. Will be re-positioned each tick in {@link #tickAnchor}.
        anchor.setPos(wearer.getX(), wearer.getY() - CHAIN_LENGTH, wearer.getZ());

        // Link both directions: anchor→disguise (chain endpoint) and disguise→anchor (getAnchor queries).
        try {
            if (anchorLinkWithGuardianMethod != null) {
                anchorLinkWithGuardianMethod.invoke(anchor, disguise);
            }
            if (anchorGuardianIdAccessor != null) {
                anchor.getEntityData().set(anchorGuardianIdAccessor, disguise.getId());
            }
            if (guardianSetAnchorUuidMethod != null) {
                guardianSetAnchorUuidMethod.invoke(disguise, anchor.getUUID());
            }
            if (guardianAnchorIdAccessor != null) {
                disguise.getEntityData().set(guardianAnchorIdAccessor, anchor.getId());
            }
            if (guardianSetMaxChainLengthMethod != null) {
                guardianSetMaxChainLengthMethod.invoke(disguise, CHAIN_LENGTH);
            }
        } catch (ReflectiveOperationException ignored) {}

        // Anchor extends Entity (not LivingEntity), so it cannot itself be registered in
        // DisguiseBodyRegistry. The guardian disguise body is registered separately by the
        // disguise pipeline; the anchor renderer's getGuardian() lookup resolves via that path.

        ANCHORS.put(wearer.getUUID(), anchor);
    }

    /** Per-tick positioning of the companion anchor. Client-only. */
    public static void tickAnchor(LivingEntity wearer) {
        if (wearer == null) return;
        if (!wearer.level().isClientSide) return;
        Entity anchor = ANCHORS.get(wearer.getUUID());
        if (anchor == null || anchor.isRemoved()) return;

        // Save previous-frame position for the renderer's partialTicks lerp.
        anchor.xo = anchor.getX(); anchor.yo = anchor.getY(); anchor.zo = anchor.getZ();
        anchor.xOld = anchor.getX(); anchor.yOld = anchor.getY(); anchor.zOld = anchor.getZ();
        anchor.yRotO = anchor.getYRot();
        anchor.xRotO = anchor.getXRot();

        Level level = wearer.level();
        double wx = wearer.getX();
        double wz = wearer.getZ();
        double anchorY = wearer.getY() - CHAIN_LENGTH;
        // Probe downward from wearer's feet for a solid block within chain range.
        net.minecraft.core.BlockPos.MutableBlockPos pos = new net.minecraft.core.BlockPos.MutableBlockPos();
        int probeY = (int) Math.floor(wearer.getY());
        int minProbe = (int) Math.floor(wearer.getY() - CHAIN_LENGTH);
        for (int y = probeY; y >= minProbe; y--) {
            pos.set((int) Math.floor(wx), y, (int) Math.floor(wz));
            if (level.getBlockState(pos).isSuffocating(level, pos)) {
                anchorY = y + 1.0;
                break;
            }
        }
        anchor.setPos(wx, anchorY, wz);
        anchor.tickCount = wearer.tickCount;
    }

    /** Public cleanup hook — gates on isClientSide internally. */
    public static void removeAnchor(LivingEntity wearer) {
        if (wearer == null) return;
        if (!wearer.level().isClientSide) return;
        removeAnchorByUUID(wearer.getUUID());
    }

    private static void removeAnchorByUUID(UUID wearerUUID) {
        Entity anchor = ANCHORS.remove(wearerUUID);
        if (anchor != null && !anchor.isRemoved()) anchor.discard();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void initReflection(Level level) {
        if (reflectionInited) return;
        synchronized (MineGuardianAnchorManager.class) {
            if (reflectionInited) return;
            anchorType = BuiltInRegistries.ENTITY_TYPE.getOptional(MINE_GUARDIAN_ANCHOR_ID).orElse(null);
            EntityType<?> guardianType = BuiltInRegistries.ENTITY_TYPE.getOptional(MINE_GUARDIAN_ID).orElse(null);

            if (anchorType != null) {
                Entity probe = anchorType.create(level);
                if (probe != null) {
                    anchorGuardianIdAccessor = ReflectionHelper.accessor(probe.getClass(), "GUARDIAN_ID");
                    anchorLinkWithGuardianMethod = ReflectionHelper.publicMethod(probe.getClass(), "linkWithGuardian", Entity.class);
                    probe.discard();
                }
            }

            if (guardianType != null) {
                Entity probe = guardianType.create(level);
                if (probe != null) {
                    guardianAnchorIdAccessor = ReflectionHelper.accessor(probe.getClass(), "ANCHOR_ID");
                    guardianSetAnchorUuidMethod = ReflectionHelper.publicMethod(probe.getClass(), "setAnchorUUID", UUID.class);
                    guardianSetMaxChainLengthMethod = ReflectionHelper.publicMethod(probe.getClass(), "setMaxChainLength", int.class);
                    probe.discard();
                }
            }

            // Set inited last so a transient failure doesn't lock out retries.
            reflectionInited = true;
        }
    }
}
