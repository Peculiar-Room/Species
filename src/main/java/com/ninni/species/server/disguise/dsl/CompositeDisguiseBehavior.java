package com.ninni.species.server.disguise.dsl;

import com.mojang.logging.LogUtils;
import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

/**
 * Composes multiple {@link DisguiseBehavior}s; action hooks run in registration order,
 * scalar hooks ({@code yawOffset}/{@code shouldApplyXRot}/{@code preserveRotationDeltaInAiStep}/{@code bodyYawTracksCamera}) return first non-default.
 */
public final class CompositeDisguiseBehavior implements DisguiseBehavior {

    private static final Logger LOGGER = LogUtils.getLogger();

    /** Per-(behavior, hook) throw counter; logs occurrence #1 and every #1024 to prevent tick-rate spam. */
    private static final java.util.concurrent.ConcurrentHashMap<Long, Integer> THROW_COUNTERS = new java.util.concurrent.ConcurrentHashMap<>();

    private static void rateLimitedLog(DisguiseBehavior child, int hookId, String hookName, Throwable t) {
        long key = (long) System.identityHashCode(child) * 7L + hookId;
        int count = THROW_COUNTERS.merge(key, 1, Integer::sum);
        if (count == 1 || count % 1024 == 0) {
            LOGGER.error("[Species] Disguise behavior {} threw in {} (occurrence #{})", child.getClass().getName(), hookName, count, t);
        }
    }

    private final DisguiseBehavior[] behaviors;

    private CompositeDisguiseBehavior(DisguiseBehavior[] behaviors) {
        this.behaviors = behaviors;
    }

    /** Builds a composite from the given behaviors. Empty array is invalid. */
    public static DisguiseBehavior of(DisguiseBehavior... behaviors) {
        if (behaviors == null || behaviors.length == 0) {
            throw new IllegalArgumentException("CompositeDisguiseBehavior needs at least one child");
        }
        if (behaviors.length == 1) return behaviors[0];
        return new CompositeDisguiseBehavior(behaviors.clone());
    }

    @Override
    public void onCreated(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseBehavior b : behaviors) {
            try { b.onCreated(wearer, disguise); }
            catch (Throwable t) { rateLimitedLog(b, 0, "onCreated", t); }
        }
    }

    @Override
    public void onBodyRebuilt(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseBehavior b : behaviors) {
            try { b.onBodyRebuilt(wearer, disguise); }
            catch (Throwable t) { rateLimitedLog(b, 10, "onBodyRebuilt", t); }
        }
    }

    @Override
    public void preTick(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseBehavior b : behaviors) {
            try { b.preTick(wearer, disguise); }
            catch (Throwable t) { rateLimitedLog(b, 1, "preTick", t); }
        }
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseBehavior b : behaviors) {
            try { b.postTick(wearer, disguise); }
            catch (Throwable t) { rateLimitedLog(b, 2, "postTick", t); }
        }
    }

    @Override
    public void preRender(LivingEntity wearer, LivingEntity disguise, float partialTick, boolean inInventory) {
        for (DisguiseBehavior b : behaviors) {
            try { b.preRender(wearer, disguise, partialTick, inInventory); }
            catch (Throwable t) { rateLimitedLog(b, 3, "preRender", t); }
        }
    }

    @Override
    public void postRender(LivingEntity wearer, LivingEntity disguise, float partialTick, boolean inInventory) {
        for (DisguiseBehavior b : behaviors) {
            try { b.postRender(wearer, disguise, partialTick, inInventory); }
            catch (Throwable t) { rateLimitedLog(b, 4, "postRender", t); }
        }
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseBehavior b : behaviors) {
            try { b.onDestroyed(wearer, disguise); }
            catch (Throwable t) { rateLimitedLog(b, 5, "onDestroyed", t); }
        }
    }

    @Override
    public void preInventoryPose(LivingEntity wearer, LivingEntity disguise, float partialTick) {
        for (DisguiseBehavior b : behaviors) {
            try { b.preInventoryPose(wearer, disguise, partialTick); }
            catch (Throwable t) { rateLimitedLog(b, 6, "preInventoryPose", t); }
        }
    }

    @Override
    public void postInventoryPose(LivingEntity wearer, LivingEntity disguise, float partialTick) {
        for (DisguiseBehavior b : behaviors) {
            try { b.postInventoryPose(wearer, disguise, partialTick); }
            catch (Throwable t) { rateLimitedLog(b, 7, "postInventoryPose", t); }
        }
    }

    @Override
    public float yawOffset(LivingEntity disguise) {
        // First non-zero wins; zero is the conventional "no offset" sentinel.
        for (DisguiseBehavior b : behaviors) {
            float v = b.yawOffset(disguise);
            if (v != 0.0F) return v;
        }
        return 0.0F;
    }

    @Override
    public boolean shouldApplyXRot(LivingEntity wearer, LivingEntity disguise, boolean inInventory) {
        // First-non-default-wins. Compute the interface default once via DEFAULT.shouldApplyXRot,
        // then return the first child that disagrees; if all agree, return the default.
        boolean def = DEFAULT.shouldApplyXRot(wearer, disguise, inInventory);
        for (DisguiseBehavior b : behaviors) {
            boolean v = b.shouldApplyXRot(wearer, disguise, inInventory);
            if (v != def) return v;
        }
        return def;
    }

    @Override
    public boolean preserveRotationDeltaInAiStep() {
        // Default is true. First child returning false wins.
        for (DisguiseBehavior b : behaviors) {
            if (!b.preserveRotationDeltaInAiStep()) return false;
        }
        return true;
    }

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        for (DisguiseBehavior b : behaviors) {
            try { b.onSpecialAction(wearer, disguise, context); }
            catch (Throwable t) { rateLimitedLog(b, 9, "onSpecialAction", t); }
        }
    }

    @Override
    public float renderYOffset(LivingEntity wearer, LivingEntity disguise, float partialTick) {
        // Sum offsets across children — multiple behaviors can independently nudge Y.
        float total = 0F;
        for (DisguiseBehavior b : behaviors) {
            try { total += b.renderYOffset(wearer, disguise, partialTick); }
            catch (Throwable t) { rateLimitedLog(b, 8, "renderYOffset", t); }
        }
        return total;
    }

    @Override
    public boolean bodyYawTracksCamera(LivingEntity disguise) {
        // Default is false. Any child opting in wins.
        for (DisguiseBehavior b : behaviors) {
            if (b.bodyYawTracksCamera(disguise)) return true;
        }
        return false;
    }

    /** Sentinel used to compute interface defaults for first-non-default-wins dispatch. */
    private static final DisguiseBehavior DEFAULT = new DisguiseBehavior() {};
}
