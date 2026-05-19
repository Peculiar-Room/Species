package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.client.events.InventoryRenderCheck;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Hullback (Whaleborne) disguise. xRot only in inventory; pre-conditions parts to a rigid
 *  layout and re-runs {@code updatePartPositions} inside a full snapshot/restore so the
 *  world tick state isn't polluted. Soft-dep, reflective. */
public final class HullbackBehavior implements DisguiseBehavior {

    public static final HullbackBehavior INSTANCE = new HullbackBehavior();

    private static volatile boolean reflectionInited;
    private static Method updatePartPositionsMethod;
    private static Field partPositionField;
    private static Field partYRotField;
    private static Field partXRotField;
    private static Field prevPartPositionsField;
    private static Field oldPartPositionField;
    private static Field oldPartYRotField;
    private static Field oldPartXRotField;
    private static Field stationaryTicksField;

    /** Per-render-thread snapshot — INSTANCE is a singleton, so concurrent renders (multiple
     *  GUI viewports of different wearers in the same frame) would otherwise trample each other. */
    private static final class Snapshot {
        Vec3[] partPosition;
        float[] partYRot;
        float[] partXRot;
        Vec3[] prevPartPositions;
        Vec3[] oldPartPosition;
        float[] oldPartYRot;
        float[] oldPartXRot;
        Vec3[] subEntityPos;
        float[] subEntityYRot;
        float[] subEntityXRot;
        float[] subEntityYRotO;
        float[] subEntityXRotO;
        int stationaryTicks;
        boolean inventoryNeutralized;
        boolean valid;
    }

    private static final ThreadLocal<Snapshot> SNAPSHOT = ThreadLocal.withInitial(Snapshot::new);

    private HullbackBehavior() {}

    @Override
    public boolean shouldApplyXRot(LivingEntity wearer, LivingEntity disguise, boolean inInventory) {
        // Tilt allowed in inventory (mouse-driven xRot); suppressed in world — whales don't bend.
        return inInventory;
    }

    @Override
    public void preRender(LivingEntity wearer, LivingEntity disguise, float partialTick, boolean inInventory) {
        if (!inInventory) return;
        if (!isInventoryRender()) return;
        initReflection(disguise.getClass());

        snapshot(disguise);

        // Seed prevPart to the rigid target (lerp identity) and pin stationaryTicks > 0
        // (kills swimCycle), then re-run updatePartPositions for one consistent pass.
        rigidLayoutViaUpdatePartPositions(disguise);
    }

    /** Pre-conditions part arrays to rigid layout, then calls {@code updatePartPositions} for one consistent pass. */
    private void rigidLayoutViaUpdatePartPositions(LivingEntity disguise) {
        // Base offsets from HullbackEntity.updatePartPositions:3389-3395.
        Vec3[] baseOffsets = {
                new Vec3(0, 0, 6),     // nose
                new Vec3(0, 0, 2.5),   // head
                new Vec3(0, 0, -2.25), // body
                new Vec3(0, 0, -7),    // tail
                new Vec3(0, 0, -11)    // fluke
        };
        float yaw = disguise.getYRot();
        float pitch = disguise.getXRot();
        // Match sign convention from updatePartPositions:3426-3427.
        float yawRad = -yaw * Mth.DEG_TO_RAD;
        float pitchRad = pitch * Mth.DEG_TO_RAD;
        Vec3 entityPos = disguise.position();

        Vec3[] rigidPositions = new Vec3[baseOffsets.length];
        for (int i = 0; i < baseOffsets.length; i++) {
            Vec3 rotated = baseOffsets[i].yRot(yawRad).xRot(pitchRad);
            rigidPositions[i] = entityPos.add(rotated);
        }

        // Pre-condition prevPart to the rigid target so lerp drag (HullbackEntity:3440-3445)
        // is identity. Pre-fill oldPartPosition for consistent first-frame lerp.
        try {
            if (prevPartPositionsField != null) {
                Vec3[] arr = (Vec3[]) prevPartPositionsField.get(disguise);
                if (arr != null) System.arraycopy(rigidPositions, 0, arr, 0, Math.min(arr.length, rigidPositions.length));
            }
            if (oldPartPositionField != null) {
                Vec3[] arr = (Vec3[]) oldPartPositionField.get(disguise);
                if (arr != null) System.arraycopy(rigidPositions, 0, arr, 0, Math.min(arr.length, rigidPositions.length));
            }
            // stationaryTicks > 0 → swimCycle = 0 (HullbackEntity:3407).
            if (stationaryTicksField != null) {
                stationaryTicksField.setInt(disguise, 60);
            }
        } catch (ReflectiveOperationException ignored) {}

        // Pre-position sub-entities to the rigid layout so any read path matches.
        if (disguise.isMultipartEntity()) {
            PartEntity<?>[] parts = disguise.getParts();
            if (parts != null) {
                for (int i = 0; i < parts.length && i < rigidPositions.length; i++) {
                    parts[i].setPos(rigidPositions[i].x, rigidPositions[i].y, rigidPositions[i].z);
                    parts[i].setYRot(yaw);
                    parts[i].setXRot(pitch);
                    parts[i].yRotO = yaw;
                    parts[i].xRotO = pitch;
                }
            }
        }

        // updatePartPositions() sees prev=rigid (no drag), writes partPosition[i]=rigid
        // and calls subEntities[i].moveTo — every renderer input in one consistent pass.
        try {
            if (updatePartPositionsMethod != null) {
                updatePartPositionsMethod.invoke(disguise);
            }
        } catch (ReflectiveOperationException ignored) {}
    }

    @Override
    public void postRender(LivingEntity wearer, LivingEntity disguise, float partialTick, boolean inInventory) {
        if (!inInventory) return;
        Snapshot s = SNAPSHOT.get();
        if (!s.valid) return;
        restore(disguise, s);
        if (s.inventoryNeutralized) {
            try {
                if (stationaryTicksField != null) {
                    stationaryTicksField.setInt(disguise, s.stationaryTicks);
                }
            } catch (ReflectiveOperationException | IllegalArgumentException ignored) {}
            s.inventoryNeutralized = false;
        }
        clearSnapshot(s);
    }

    private void snapshot(LivingEntity disguise) {
        Snapshot s = SNAPSHOT.get();
        s.valid = false;
        try {
            s.partPosition = cloneVec3Array(readVec3Array(partPositionField, disguise));
            s.partYRot = cloneFloatArray(readFloatArray(partYRotField, disguise));
            s.partXRot = cloneFloatArray(readFloatArray(partXRotField, disguise));
            s.prevPartPositions = cloneVec3Array(readVec3Array(prevPartPositionsField, disguise));
            s.oldPartPosition = cloneVec3Array(readVec3Array(oldPartPositionField, disguise));
            s.oldPartYRot = cloneFloatArray(readFloatArray(oldPartYRotField, disguise));
            s.oldPartXRot = cloneFloatArray(readFloatArray(oldPartXRotField, disguise));
            snapshotSubEntities(disguise, s);
            if (stationaryTicksField != null) {
                s.stationaryTicks = stationaryTicksField.getInt(disguise);
                s.inventoryNeutralized = true;
            }
            s.valid = true;
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) {
            clearSnapshot(s);
        }
    }

    private void snapshotSubEntities(LivingEntity disguise, Snapshot s) {
        if (!disguise.isMultipartEntity()) return;
        PartEntity<?>[] parts = disguise.getParts();
        if (parts == null) return;
        s.subEntityPos = new Vec3[parts.length];
        s.subEntityYRot = new float[parts.length];
        s.subEntityXRot = new float[parts.length];
        s.subEntityYRotO = new float[parts.length];
        s.subEntityXRotO = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            s.subEntityPos[i] = parts[i].position();
            s.subEntityYRot[i] = parts[i].getYRot();
            s.subEntityXRot[i] = parts[i].getXRot();
            s.subEntityYRotO[i] = parts[i].yRotO;
            s.subEntityXRotO[i] = parts[i].xRotO;
        }
    }

    private void restore(LivingEntity disguise, Snapshot s) {
        try {
            writeVec3Array(disguise, partPositionField, s.partPosition);
            writeFloatArray(disguise, partYRotField, s.partYRot);
            writeFloatArray(disguise, partXRotField, s.partXRot);
            writeVec3Array(disguise, prevPartPositionsField, s.prevPartPositions);
            writeVec3Array(disguise, oldPartPositionField, s.oldPartPosition);
            writeFloatArray(disguise, oldPartYRotField, s.oldPartYRot);
            writeFloatArray(disguise, oldPartXRotField, s.oldPartXRot);
            restoreSubEntities(disguise, s);
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) {}
    }

    private void restoreSubEntities(LivingEntity disguise, Snapshot s) {
        if (s.subEntityPos == null) return;
        if (!disguise.isMultipartEntity()) return;
        PartEntity<?>[] parts = disguise.getParts();
        if (parts == null || parts.length != s.subEntityPos.length) return;
        for (int i = 0; i < parts.length; i++) {
            parts[i].setPos(s.subEntityPos[i].x, s.subEntityPos[i].y, s.subEntityPos[i].z);
            parts[i].setYRot(s.subEntityYRot[i]);
            parts[i].setXRot(s.subEntityXRot[i]);
            parts[i].yRotO = s.subEntityYRotO[i];
            parts[i].xRotO = s.subEntityXRotO[i];
        }
    }

    private void clearSnapshot(Snapshot s) {
        s.partPosition = null;
        s.partYRot = null;
        s.partXRot = null;
        s.prevPartPositions = null;
        s.oldPartPosition = null;
        s.oldPartYRot = null;
        s.oldPartXRot = null;
        s.subEntityPos = null;
        s.subEntityYRot = null;
        s.subEntityXRot = null;
        s.subEntityYRotO = null;
        s.subEntityXRotO = null;
        s.valid = false;
    }

    private static Vec3[] readVec3Array(Field field, LivingEntity target) throws ReflectiveOperationException {
        if (field == null) return null;
        return (Vec3[]) field.get(target);
    }

    private static float[] readFloatArray(Field field, LivingEntity target) throws ReflectiveOperationException {
        if (field == null) return null;
        return (float[]) field.get(target);
    }

    private static float[] cloneFloatArray(float[] src) {
        return src == null ? null : src.clone();
    }

    private static Vec3[] cloneVec3Array(Vec3[] src) {
        if (src == null) return null;
        return src.clone(); // Vec3 is immutable, shallow clone is sufficient
    }

    private static void writeVec3Array(LivingEntity target, Field field, Vec3[] value) throws ReflectiveOperationException {
        if (field == null || value == null) return;
        Vec3[] dst = (Vec3[]) field.get(target);
        if (dst == null || dst.length != value.length) field.set(target, value);
        else System.arraycopy(value, 0, dst, 0, value.length);
    }

    private static void writeFloatArray(LivingEntity target, Field field, float[] value) throws ReflectiveOperationException {
        if (field == null || value == null) return;
        float[] dst = (float[]) field.get(target);
        if (dst == null || dst.length != value.length) field.set(target, value);
        else System.arraycopy(value, 0, dst, 0, value.length);
    }

    private static void initReflection(Class<?> hullbackClass) {
        if (reflectionInited) return;
        synchronized (HullbackBehavior.class) {
            if (reflectionInited) return;
            try {
                updatePartPositionsMethod = hullbackClass.getDeclaredMethod("updatePartPositions");
                updatePartPositionsMethod.setAccessible(true);
            } catch (ReflectiveOperationException ignored) {}
            partPositionField = ReflectionHelper.declaredField(hullbackClass, "partPosition");
            partYRotField = ReflectionHelper.declaredField(hullbackClass, "partYRot");
            partXRotField = ReflectionHelper.declaredField(hullbackClass, "partXRot");
            prevPartPositionsField = ReflectionHelper.declaredField(hullbackClass, "prevPartPositions");
            oldPartPositionField = ReflectionHelper.declaredField(hullbackClass, "oldPartPosition");
            oldPartYRotField = ReflectionHelper.declaredField(hullbackClass, "oldPartYRot");
            oldPartXRotField = ReflectionHelper.declaredField(hullbackClass, "oldPartXRot");
            stationaryTicksField = ReflectionHelper.declaredField(hullbackClass, "stationaryTicks");
            reflectionInited = true;
        }
    }

    private static boolean isInventoryRender() {
        return InventoryRenderCheck.isInventoryRender();
    }
}
