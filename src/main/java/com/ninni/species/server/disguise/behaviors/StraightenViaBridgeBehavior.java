package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.disguise.ModelStraightenBridge;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/** Inventory-only fix for AC mobs whose models read entity-internal rotation trackers. Sets
 *  {@link ModelStraightenBridge#FORCE_STRAIGHTEN}, pins yaw/pitch to yBodyRot/0, and zeros
 *  tailYaw/flightPitch/flightRoll (+prev). */
public final class StraightenViaBridgeBehavior implements DisguiseBehavior {

    public static final StraightenViaBridgeBehavior INSTANCE = new StraightenViaBridgeBehavior();

    /** Field names whose values should be zeroed during inventory render. */
    private static final String[] RESET_FIELDS = {
            "tailYaw", "prevTailYaw",
            "flightPitch", "prevFlightPitch",
            "flightRoll", "prevFlightRoll"
    };

    /** Per-class reflection cache. {@code null} entry means "checked and not present". */
    private static final Map<Class<?>, Field[]> RESOLVED_FIELDS = new java.util.concurrent.ConcurrentHashMap<>();

    /** Per-render snapshot. ThreadLocal so concurrent wearers/render passes don't trample. */
    private static final class Snapshot {
        float yHeadRot, yHeadRotO, yBodyRotO, yRot, yRotO, xRot, xRotO;
        float[] resetValues;
    }

    private static final ThreadLocal<Snapshot> SNAPSHOT = new ThreadLocal<>();

    private StraightenViaBridgeBehavior() {}

    @Override
    public void preRender(LivingEntity wearer, LivingEntity disguise, float partialTick, boolean inInventory) {
        if (!inInventory) return;
        if (!ModelStraightenBridge.isInventoryRender()) return;

        // (1) Bridge flag — mixins read this to override model.straighten before render.
        ModelStraightenBridge.FORCE_STRAIGHTEN = true;

        // (2) Rotation lock — pin all yaw/pitch to yBodyRot/0: netHeadYaw=0, headPitchLerp=0.
        Snapshot s = new Snapshot();
        s.yHeadRot = disguise.yHeadRot;
        s.yHeadRotO = disguise.yHeadRotO;
        s.yBodyRotO = disguise.yBodyRotO;
        s.yRot = disguise.getYRot();
        s.yRotO = disguise.yRotO;
        s.xRot = disguise.getXRot();
        s.xRotO = disguise.xRotO;
        SNAPSHOT.set(s);

        float lockedYaw = disguise.yBodyRot;
        disguise.yBodyRotO = lockedYaw;
        disguise.yHeadRot = lockedYaw;
        disguise.yHeadRotO = lockedYaw;
        disguise.setYRot(lockedYaw);
        disguise.yRotO = lockedYaw;
        disguise.setXRot(0F);
        disguise.xRotO = 0F;

        // (3) Reset tracker fields (tailYaw/flightPitch/flightRoll + prev counterparts).
        // The rotation lock above doesn't reach these — the entity tick updates them
        // independently of yBodyRot.
        Field[] fields = resolveFields(disguise.getClass());
        s.resetValues = new float[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (f == null) continue;
            try {
                s.resetValues[i] = f.getFloat(disguise);
                f.setFloat(disguise, 0F);
            } catch (IllegalAccessException ignored) {}
        }
    }

    @Override
    public void postRender(LivingEntity wearer, LivingEntity disguise, float partialTick, boolean inInventory) {
        // Always reset the bridge flag — guards against lingering true state from a
        // prior frame where preRender ran but postRender did not.
        ModelStraightenBridge.FORCE_STRAIGHTEN = false;

        if (!inInventory) return;
        Snapshot s = SNAPSHOT.get();
        if (s == null) return;
        SNAPSHOT.remove();

        disguise.yHeadRot = s.yHeadRot;
        disguise.yHeadRotO = s.yHeadRotO;
        disguise.yBodyRotO = s.yBodyRotO;
        disguise.setYRot(s.yRot);
        disguise.yRotO = s.yRotO;
        disguise.setXRot(s.xRot);
        disguise.xRotO = s.xRotO;

        if (s.resetValues != null) {
            Field[] fields = resolveFields(disguise.getClass());
            for (int i = 0; i < fields.length && i < s.resetValues.length; i++) {
                Field f = fields[i];
                if (f == null) continue;
                try {
                    f.setFloat(disguise, s.resetValues[i]);
                } catch (IllegalAccessException ignored) {}
            }
        }
    }

    private static synchronized Field[] resolveFields(Class<?> entityClass) {
        Field[] cached = RESOLVED_FIELDS.get(entityClass);
        if (cached != null) return cached;
        Field[] resolved = new Field[RESET_FIELDS.length];
        for (int i = 0; i < RESET_FIELDS.length; i++) {
            resolved[i] = ReflectionHelper.declaredFieldOfType(entityClass, RESET_FIELDS[i], float.class);
        }
        RESOLVED_FIELDS.put(entityClass, resolved);
        return resolved;
    }
}
