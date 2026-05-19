package com.ninni.species.server.disguise;

import com.ninni.species.client.sound.DisguiseBoundroidChainSound;
import com.ninni.species.mixin_util.LivingEntityAccess;
import com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Symmetric Boundroid+Winch companion manager. Spawns a trailing companion with rope physics;
 *  {@link #getPartnerForWinch} feeds {@code BoundroidWinchEntityMixin}'s {@code getHead} redirect
 *  so the {@code instanceof BoundroidEntity} branch in {@code getChainTo} fires both ways. */
public final class BoundroidPairManager {

    private static final ResourceLocation BOUNDROID_ID = new ResourceLocation("alexscaves", "boundroid");
    private static final ResourceLocation BOUNDROID_WINCH_ID = new ResourceLocation("alexscaves", "boundroid_winch");

    /** Mirrors AC's unlatched {@code distanceGoal}; soft pull activates beyond, hard clamp caps stretch. */
    private static final double MAX_CHAIN_LENGTH = 3.5;

    /** Pull force per unit of overshoot, capped by {@link #PULL_CAP}. Mirrors AC's {@code pullSpeed} (BoundroidWinchEntity:198). */
    private static final double PULL_STRENGTH = 0.10;
    private static final double PULL_CAP = 0.5;

    /** Horizontal velocity decay per tick. Vertical decay handled via gravity + ground clamp. */
    private static final double HORIZONTAL_FRICTION = 0.20;

    /** Vanilla-ish gravity. Companion is its own entity; its own forces govern it. */
    private static final double GRAVITY = -0.08;

    private static final double MAX_VELOCITY = 1.2;

    /** Threshold above which chain physics goes full 3D; below, horizontal-only avoids groundProgress flicker from micro-vertical forces. */
    private static final double VERTICAL_THRESHOLD = 1.5;

    /** Per-wearer companion entity (the one that trails). */
    private static final java.util.Map<UUID, Entity> COMPANIONS = new ConcurrentHashMap<>();

    /** Per-wearer chain-rattle sound instance. Tracked so it can be stopped explicitly on
     *  cleanup — the sound's {@code canPlaySound} otherwise sees the disguise body as alive
     *  (the cleanup unregisters it but doesn't discard the entity). Client-only. */
    private static final java.util.Map<UUID, DisguiseBoundroidChainSound> CHAIN_SOUNDS = new ConcurrentHashMap<>();

    static {
        // Self-register as a Panacea sub-entity provider so the companion is
        // included in render dispatch without per-type registration elsewhere.
        DisguiseTopologyRegistry.registerGlobalSubEntityProvider(
                (wearer, disguise) -> {
                    if (!isPairDisguise(disguise)) return java.util.Collections.emptyList();
                    Entity companion = COMPANIONS.get(wearer.getUUID());
                    if (companion == null || companion.isRemoved()) return java.util.Collections.emptyList();
                    return java.util.Collections.singletonList(companion);
                });
    }

    /** Mixin entry-point map: Winch UUID → partner Boundroid resolver, keyed by wearer for precise cleanup. */
    private static final java.util.Map<UUID, PartnerEntry> WINCH_PARTNER = new ConcurrentHashMap<>();

    private static volatile boolean reflectionInited;
    private static EntityType<?> winchType;
    private static EntityType<?> boundroidType;
    private static EntityDataAccessor<Integer> winchHeadIdAccessor;
    private static Method winchSetHeadUuidMethod;
    /** {@code distanceToCeiling} — read by AC's chain renderer for the top endpoint. */
    private static Field winchDistanceToCeilingField;
    /** {@code LATCHED} accessor — gates the latched-pose visuals (hooks, upward chain). */
    private static EntityDataAccessor<Boolean> winchLatchedAccessor;
    /** Probe range fallback. Resolved from AC's {@code MAX_DIST_TO_CEILING}. */
    private static float winchMaxDistToCeiling = 5.0F;

    /** BoundroidEntity animation-state fields accessed via reflection to drive the
     *  float-vs-ground pose on companion Boundroids whose tick() never runs. */
    private static volatile boolean boundroidAnimFieldsInited;
    private static Field boundroidGroundProgressField;
    private static Field boundroidPrevGroundProgressField;

    private BoundroidPairManager() {}

    public static boolean isBoundroidDisguise(LivingEntity disguise) {
        return matchesType(disguise, BOUNDROID_ID);
    }

    public static boolean isWinchDisguise(LivingEntity disguise) {
        return matchesType(disguise, BOUNDROID_WINCH_ID);
    }

    public static boolean isPairDisguise(LivingEntity disguise) {
        return isBoundroidDisguise(disguise) || isWinchDisguise(disguise);
    }

    private static boolean matchesType(LivingEntity disguise, ResourceLocation id) {
        if (disguise == null || disguise.getType() == null) return false;
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(disguise.getType());
        return key != null && key.equals(id);
    }

    public static Entity getCompanion(UUID wearerUUID) {
        return COMPANIONS.get(wearerUUID);
    }

    /** Mixin entry point — see class docs. */
    public static Entity getPartnerForWinch(UUID winchUUID) {
        PartnerEntry entry = WINCH_PARTNER.get(winchUUID);
        if (entry == null) return null;
        Entity partner = entry.source.resolve();
        if (partner == null || partner.isRemoved()) return null;
        return partner;
    }

    /** Called when a Boundroid OR Winch disguise is freshly created. Client-only. */
    public static void onDisguiseCreated(LivingEntity wearer, LivingEntity disguise) {
        if (wearer == null || disguise == null) return;
        if (!wearer.level().isClientSide) return;
        if (!isPairDisguise(disguise)) return;
        initReflection(wearer.level());

        // Defensive cleanup before replacing.
        removeCompanionByUUID(wearer.getUUID());

        Level level = wearer.level();
        Entity companion;
        if (isBoundroidDisguise(disguise)) {
            // Disguise body = Boundroid. Companion = Winch trailing behind.
            if (winchType == null) return;
            companion = winchType.create(level);
        } else {
            // Disguise body = Winch. Companion = Boundroid trailing behind.
            if (boundroidType == null) return;
            companion = boundroidType.create(level);
        }
        if (companion == null) return;

        companion.noPhysics = true;
        companion.setNoGravity(true);  // gravity applied manually so ground-clamp can override it

        // Spawn at wearer's position; independent physics will settle it naturally.
        Vec3 initial = wearer.position();
        companion.setPos(initial);
        companion.xo = initial.x; companion.yo = initial.y; companion.zo = initial.z;
        companion.xOld = initial.x; companion.yOld = initial.y; companion.zOld = initial.z;
        companion.setYRot(wearer.getYRot());
        companion.yRotO = wearer.getYRot();

        // Wire chain-rendering partners so the renderer's instanceof BoundroidEntity
        // branch fires for both disguise directions (see class javadoc).
        try {
            UUID wearerUUID = wearer.getUUID();
            if (isBoundroidDisguise(disguise)) {
                // Companion is the Winch.
                if (winchHeadIdAccessor != null) companion.getEntityData().set(winchHeadIdAccessor, wearer.getId());
                if (winchSetHeadUuidMethod != null) winchSetHeadUuidMethod.invoke(companion, wearer.getUUID());
                // Mixin resolves companion.getHead() → disguise body.
                final LivingEntity wearerRef = wearer;
                WINCH_PARTNER.put(companion.getUUID(), new PartnerEntry(wearerUUID, () -> {
                    try {
                        return ((LivingEntityAccess) wearerRef).getDisguisedEntity();
                    } catch (Throwable e) { return null; }
                }));
            } else {
                // Disguise body is the Winch; mixin resolves partner = companion Boundroid.
                if (winchHeadIdAccessor != null) disguise.getEntityData().set(winchHeadIdAccessor, wearer.getId());
                if (winchSetHeadUuidMethod != null) winchSetHeadUuidMethod.invoke(disguise, wearer.getUUID());
                final Entity companionRef = companion;
                WINCH_PARTNER.put(disguise.getUUID(), new PartnerEntry(wearerUUID, () -> companionRef));
            }
        } catch (ReflectiveOperationException ignored) {}

        COMPANIONS.put(wearer.getUUID(), companion);

        try {
            DisguiseBoundroidChainSound sound =
                    new DisguiseBoundroidChainSound(disguise, companion);
            CHAIN_SOUNDS.put(wearer.getUUID(), sound);
            net.minecraft.client.Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        } catch (Throwable ignored) {}
    }

    /** Per-tick companion physics: independent gravity/friction/momentum with soft pull + hard clamp at {@link #MAX_CHAIN_LENGTH}. */
    public static void tickCompanion(LivingEntity wearer) {
        if (wearer == null) return;
        if (!wearer.level().isClientSide) return;

        // Sync the Winch disguise body's distanceToCeiling so AC's chain renderer draws
        // the chain top endpoint to the actual ceiling above the wearer.
        LivingEntity disguise = ((LivingEntityAccess) wearer).getDisguisedEntity();
        if (disguise != null && isWinchDisguise(disguise)) {
            syncWinchCeilingDistance(wearer, disguise);
        }

        Entity comp = COMPANIONS.get(wearer.getUUID());
        if (comp == null || comp.isRemoved()) return;

        // Save prev-frame state for the renderer's partialTicks lerp.
        comp.xo = comp.getX(); comp.yo = comp.getY(); comp.zo = comp.getZ();
        comp.xOld = comp.getX(); comp.yOld = comp.getY(); comp.zOld = comp.getZ();
        comp.yRotO = comp.getYRot();
        comp.xRotO = comp.getXRot();

        // ---- (1) Free-body physics: friction + gravity. No spring toward wearer.
        Vec3 currentVel = comp.getDeltaMovement();
        Vec3 newVel = new Vec3(
                currentVel.x * (1.0 - HORIZONTAL_FRICTION),
                currentVel.y + GRAVITY,
                currentVel.z * (1.0 - HORIZONTAL_FRICTION)
        );

        // ---- (2) Soft chain pull — only beyond MAX_CHAIN_LENGTH.
        // Pull direction is 3D when |dy| > VERTICAL_THRESHOLD (flight/tall structures),
        // horizontal-only otherwise (terrain noise); see field javadoc for rationale.
        double dxw = wearer.getX() - comp.getX();
        double dyw = wearer.getY() - comp.getY();
        double dzw = wearer.getZ() - comp.getZ();
        double dist3DToWearer = Math.sqrt(dxw * dxw + dyw * dyw + dzw * dzw);
        if (dist3DToWearer > MAX_CHAIN_LENGTH && dist3DToWearer > 1.0E-4) {
            double overshoot = dist3DToWearer - MAX_CHAIN_LENGTH;
            double mag = Math.min(overshoot * PULL_STRENGTH, PULL_CAP);
            double pullX = (dxw / dist3DToWearer) * mag;
            double pullZ = (dzw / dist3DToWearer) * mag;
            // Y component only outside terrain-noise range (see VERTICAL_THRESHOLD).
            double pullY = Math.abs(dyw) > VERTICAL_THRESHOLD
                    ? (dyw / dist3DToWearer) * mag
                    : 0.0;
            newVel = new Vec3(newVel.x + pullX, newVel.y + pullY, newVel.z + pullZ);
        }

        // Velocity cap.
        if (newVel.length() > MAX_VELOCITY) {
            newVel = newVel.normalize().scale(MAX_VELOCITY);
        }

        // ---- (3) Integrate.
        Vec3 newPos = comp.position().add(newVel);

        // ---- (4) Ground collision. Companion can't fall through terrain.
        double surfaceY = computeSurfaceY(wearer.level(), newPos.x, newPos.y, newPos.z);
        if (newPos.y < surfaceY) {
            newPos = new Vec3(newPos.x, surfaceY, newPos.z);
            // Zero downward velocity on ground impact (don't kill horizontal
            // momentum, that's what friction is for).
            if (newVel.y < 0) {
                newVel = new Vec3(newVel.x, 0, newVel.z);
            }
        }

        // ---- (5) Hard chain constraint — same 3D/horizontal split as the soft pull.
        // 3D clamp for flight; horizontal-only on terrain to avoid Y-jitter.
        double dx = newPos.x - wearer.getX();
        double dy = newPos.y - wearer.getY();
        double dz = newPos.z - wearer.getZ();
        double horizDist = Math.sqrt(dx * dx + dz * dz);

        if (Math.abs(dy) > VERTICAL_THRESHOLD) {
            // 3D clamp: project companion onto sphere of radius MAX_CHAIN_LENGTH around wearer.
            double dist3D = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist3D > MAX_CHAIN_LENGTH && dist3D > 1.0E-4) {
                double scale = MAX_CHAIN_LENGTH / dist3D;
                newPos = new Vec3(
                        wearer.getX() + dx * scale,
                        wearer.getY() + dy * scale,
                        wearer.getZ() + dz * scale);
                // Cancel the outward velocity component so the companion doesn't push against the clamp.
                double awayX = dx / dist3D;
                double awayY = dy / dist3D;
                double awayZ = dz / dist3D;
                double awaySpeed = newVel.x * awayX + newVel.y * awayY + newVel.z * awayZ;
                if (awaySpeed > 0) {
                    newVel = new Vec3(
                            newVel.x - awayX * awaySpeed,
                            newVel.y - awayY * awaySpeed,
                            newVel.z - awayZ * awaySpeed);
                }
            }
        } else if (horizDist > MAX_CHAIN_LENGTH && horizDist > 1.0E-4) {
            // Horizontal-only clamp (terrain-noise regime).
            double scale = MAX_CHAIN_LENGTH / horizDist;
            double clampedX = wearer.getX() + dx * scale;
            double clampedZ = wearer.getZ() + dz * scale;
            newPos = new Vec3(clampedX, newPos.y, clampedZ);
            double horizAwayX = dx / horizDist;
            double horizAwayZ = dz / horizDist;
            double horizAwaySpeed = newVel.x * horizAwayX + newVel.z * horizAwayZ;
            if (horizAwaySpeed > 0) {
                newVel = new Vec3(
                        newVel.x - horizAwayX * horizAwaySpeed,
                        newVel.y,
                        newVel.z - horizAwayZ * horizAwaySpeed);
            }
        }

        // ---- (6) Yaw. Face direction of motion when moving; otherwise
        // preserve current yaw (no auto-rotation toward wearer).
        double horizSpeedSqr = newVel.x * newVel.x + newVel.z * newVel.z;
        if (horizSpeedSqr > 1.0E-3) {
            float targetYaw = Mth.wrapDegrees(
                    (float) (Mth.atan2(newVel.z, newVel.x) * (180.0 / Math.PI)) - 90F);
            float newYaw = Mth.approachDegrees(comp.getYRot(), targetYaw, 7F);
            comp.setYRot(newYaw);
        }

        comp.setPos(newPos);
        comp.setDeltaMovement(newVel);
        comp.tickCount = wearer.tickCount;

        // (7) Companion Boundroid animation sync. The companion's tick() never runs, so we
        // update onGround + groundProgress manually. 0.15-block tolerance avoids micro-jitter
        // from soft-pull vertical force toggling isOnGround every tick.
        boolean isOnGround = (newPos.y - surfaceY) < 0.15;
        comp.setOnGround(isOnGround);
        ResourceLocation typeKey = BuiltInRegistries.ENTITY_TYPE.getKey(comp.getType());
        if (typeKey != null && typeKey.equals(BOUNDROID_ID)) {
            updateBoundroidGroundProgress(comp, isOnGround);
        }
    }

    /** Reflection-driven equivalent of BoundroidEntity.tick's groundProgress block (lines 105-111).
     *  Called only for companion-Boundroid (Winch disguise). */
    private static void updateBoundroidGroundProgress(Entity comp, boolean onGround) {
        initBoundroidAnimFields(comp.getClass());
        if (boundroidGroundProgressField == null || boundroidPrevGroundProgressField == null) return;
        try {
            float current = boundroidGroundProgressField.getFloat(comp);
            boundroidPrevGroundProgressField.setFloat(comp, current);
            if (onGround && current < 5F) current += 1F;
            if (!onGround && current > 0F) current -= 1F;
            boundroidGroundProgressField.setFloat(comp, current);
        } catch (IllegalAccessException ignored) {}
    }

    private static void initBoundroidAnimFields(Class<?> boundroidClass) {
        if (boundroidAnimFieldsInited) return;
        synchronized (BoundroidPairManager.class) {
            if (boundroidAnimFieldsInited) return;
            boundroidAnimFieldsInited = true;
            Class<?> c = boundroidClass;
            while (c != null && c != Object.class) {
                if (boundroidGroundProgressField == null) {
                    try {
                        Field f = c.getDeclaredField("groundProgress");
                        f.setAccessible(true);
                        boundroidGroundProgressField = f;
                    } catch (NoSuchFieldException ignored) {}
                }
                if (boundroidPrevGroundProgressField == null) {
                    try {
                        Field f = c.getDeclaredField("prevGroundProgress");
                        f.setAccessible(true);
                        boundroidPrevGroundProgressField = f;
                    } catch (NoSuchFieldException ignored) {}
                }
                if (boundroidGroundProgressField != null && boundroidPrevGroundProgressField != null) break;
                c = c.getSuperclass();
            }
        }
    }

    /** Public cleanup hook — gates on isClientSide internally. */
    public static void removeCompanion(LivingEntity wearer) {
        if (wearer == null) return;
        if (!wearer.level().isClientSide) return;
        removeCompanionByUUID(wearer.getUUID());
    }

    /** Internal — caller has already verified client-side context. */
    private static void removeCompanionByUUID(UUID wearerUUID) {
        Entity comp = COMPANIONS.remove(wearerUUID);
        if (comp != null) {
            // Remove only entries owned by this wearer (stamped at registration time).
            WINCH_PARTNER.entrySet().removeIf(e -> wearerUUID.equals(e.getValue().wearerUUID));
            if (!comp.isRemoved()) comp.discard();
        }
        DisguiseBoundroidChainSound sound = CHAIN_SOUNDS.remove(wearerUUID);
        if (sound != null) sound.requestStop();
    }

    /** ThreadLocal scratch position for {@code computeSurfaceY} — avoids per-tick allocation.
     *  ThreadLocal because the main tick thread and render thread can both call this. */
    private static final ThreadLocal<net.minecraft.core.BlockPos.MutableBlockPos> SURFACE_PROBE_POS =
            ThreadLocal.withInitial(net.minecraft.core.BlockPos.MutableBlockPos::new);

    private static double computeSurfaceY(Level level, double x, double y, double z) {
        net.minecraft.core.BlockPos.MutableBlockPos pos = SURFACE_PROBE_POS.get();
        int top = (int) Math.round(y) + 2;
        pos.set((int) Math.round(x), top, (int) Math.round(z));
        int min = level.getMinBuildHeight();
        while (pos.getY() > min && !level.getBlockState(pos).isSuffocating(level, pos)) {
            pos.move(0, -1, 0);
        }
        return 1.0 + pos.getY();
    }

    /** Sync the Winch disguise's ceiling state — AC's natural tick is server-only, so without
     *  this {@code LATCHED} stays false and the hooks render retracted. */
    private static void syncWinchCeilingDistance(LivingEntity wearer, LivingEntity disguise) {
        if (winchDistanceToCeilingField == null && winchLatchedAccessor == null) return;
        Level level = wearer.level();
        net.minecraft.core.BlockPos.MutableBlockPos pos = SURFACE_PROBE_POS.get();
        int wx = (int) Math.floor(wearer.getX());
        int wz = (int) Math.floor(wearer.getZ());
        int startY = (int) Math.ceil(wearer.getY() + wearer.getBbHeight());
        int maxDy = (int) Math.ceil(winchMaxDistToCeiling) + 1;
        float distance = winchMaxDistToCeiling;
        boolean foundCeiling = false;
        for (int dy = 0; dy <= maxDy; dy++) {
            int y = startY + dy;
            pos.set(wx, y, wz);
            if (level.getBlockState(pos).isSuffocating(level, pos)) {
                distance = (float) Math.max(0.0, y - wearer.getY() - wearer.getBbHeight());
                foundCeiling = true;
                break;
            }
        }
        if (winchDistanceToCeilingField != null) {
            try {
                winchDistanceToCeilingField.setFloat(disguise, Math.min(distance, winchMaxDistToCeiling));
            } catch (IllegalAccessException ignored) {}
        }
        if (winchLatchedAccessor != null) {
            // Latched = ceiling is within range; deploys hooks and engages the upward chain visual.
            disguise.getEntityData().set(winchLatchedAccessor, foundCeiling);
        }
    }

    private static void initReflection(Level level) {
        if (reflectionInited) return;
        synchronized (BoundroidPairManager.class) {
            if (reflectionInited) return;
            winchType = BuiltInRegistries.ENTITY_TYPE.getOptional(BOUNDROID_WINCH_ID).orElse(null);
            boundroidType = BuiltInRegistries.ENTITY_TYPE.getOptional(BOUNDROID_ID).orElse(null);

            if (winchType != null) {
                Entity probe = winchType.create(level);
                if (probe != null) {
                    Class<?> probeClass = probe.getClass();
                    winchHeadIdAccessor = ReflectionHelper.accessor(probeClass, "HEAD_ID");
                    winchDistanceToCeilingField = ReflectionHelper.declaredField(probeClass, "distanceToCeiling");
                    winchLatchedAccessor = ReflectionHelper.accessor(probeClass, "LATCHED");
                    try {
                        winchSetHeadUuidMethod = probe.getClass().getMethod("setHeadUUID", UUID.class);
                    } catch (ReflectiveOperationException ignored) {}
                    try {
                        Field maxField = probe.getClass().getDeclaredField("MAX_DIST_TO_CEILING");
                        maxField.setAccessible(true);
                        winchMaxDistToCeiling = maxField.getFloat(null);
                    } catch (ReflectiveOperationException ignored) {}
                    probe.discard();
                }
            }
            // No probe for boundroidType: the Boundroid-disguise path wires through
            // winchHeadIdAccessor on the companion Winch only, not any Boundroid field.
            reflectionInited = true;
        }
    }

    /** Functional interface so partner lookups can resolve dynamically. */
    @FunctionalInterface
    private interface PartnerSource {
        Entity resolve();
    }

    /** Entry in {@link #WINCH_PARTNER}: pairs a resolver with its owning wearer UUID for precise cleanup. */
    private static final class PartnerEntry {
        final UUID wearerUUID;
        final PartnerSource source;

        PartnerEntry(UUID wearerUUID, PartnerSource source) {
            this.wearerUUID = wearerUUID;
            this.source = source;
        }
    }
}
