package com.ninni.species.server.disguise;

import com.ninni.species.api.disguise.DisguiseBehavior;
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
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Spawns a client-side {@code EntityMurmurHead} companion for a Murmur disguise, replicating
 *  the body↔head link so {@code RenderMurmurHead} resolves the body via its synced ID.
 *  Soft-dep AM; no-op when absent. */
public final class MurmurPairManager implements DisguiseBehavior {

    public static final MurmurPairManager INSTANCE = new MurmurPairManager();

    private static final ResourceLocation BODY_ID = new ResourceLocation("alexsmobs", "murmur");
    private static final ResourceLocation HEAD_ID = new ResourceLocation("alexsmobs", "murmur_head");

    /** Per-wearer companion head (client-side). */
    private final java.util.Map<UUID, Entity> COMPANIONS = new ConcurrentHashMap<>();

    private volatile boolean reflectionInited;
    private EntityType<?> headType;
    private Method bodySetHeadUuid;
    private Method headSetBodyId;
    private EntityDataAccessor<Integer> headBodyIdAccessor;

    private MurmurPairManager() {}

    public void register() {
        DisguiseBehaviorRegistry.registerGlobal(this);
        DisguiseTopologyRegistry.registerGlobalSubEntityProvider((wearer, disguise) -> {
            if (!isMurmur(disguise)) return Collections.emptyList();
            Entity head = COMPANIONS.get(wearer.getUUID());
            return head != null && !head.isRemoved() ? Collections.singletonList(head) : Collections.emptyList();
        });
    }

    private boolean isMurmur(LivingEntity disguise) {
        return disguise != null && BuiltInRegistries.ENTITY_TYPE.getKey(disguise.getType()).equals(BODY_ID);
    }

    @Override
    public void onCreated(LivingEntity wearer, LivingEntity disguise) {
        // No client companion head: a head bound to the wearer can't render a connected neck —
        // EntityMurmurHead#getNeckTop/getNeckBottom are guarded by `body instanceof EntityMurmur`
        // and degrade to a ~0.5-block stub for a Player body. The disguise instead forces the
        // body's own cosmetic head+neck via the EntityMurmur shouldRenderFakeHead mixin.
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        if (!wearer.level().isClientSide) return;
        Entity head = COMPANIONS.remove(wearer.getUUID());
        if (head != null && !head.isRemoved()) head.discard();
    }

    @Override
    public void preTick(LivingEntity wearer, LivingEntity disguise) {
        if (!isMurmur(disguise) || !wearer.level().isClientSide) return;
        Entity head = COMPANIONS.get(wearer.getUUID());
        if (head == null || head.isRemoved()) return;
        // Track wearer position with a fixed Y offset so the head floats above the body's silhouette.
        Vec3 target = wearer.position().add(0, wearer.getBbHeight() + 1.0, 0);
        head.xo = head.getX(); head.yo = head.getY(); head.zo = head.getZ();
        head.xOld = head.getX(); head.yOld = head.getY(); head.zOld = head.getZ();
        head.yRotO = head.getYRot();
        head.setPos(target);
        head.setYRot(wearer.getYRot());
    }

    private void initReflection(Level level) {
        if (reflectionInited) return;
        synchronized (this) {
            if (reflectionInited) return;
            headType = BuiltInRegistries.ENTITY_TYPE.getOptional(HEAD_ID).orElse(null);
            EntityType<?> bodyType = BuiltInRegistries.ENTITY_TYPE.getOptional(BODY_ID).orElse(null);
            if (bodyType != null) {
                Entity probe = bodyType.create(level);
                if (probe != null) {
                    bodySetHeadUuid = ReflectionHelper.publicMethod(probe.getClass(), "setHeadUUID", UUID.class);
                    probe.discard();
                }
            }
            if (headType != null) {
                Entity probe = headType.create(level);
                if (probe != null) {
                    headSetBodyId = ReflectionHelper.publicMethod(probe.getClass(), "setBodyId", UUID.class);
                    headBodyIdAccessor = ReflectionHelper.accessor(probe.getClass(), "BODY_ID");
                    probe.discard();
                }
            }
            reflectionInited = true;
        }
    }

}
