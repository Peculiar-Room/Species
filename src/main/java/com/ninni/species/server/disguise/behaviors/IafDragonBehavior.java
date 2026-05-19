package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.client.events.InventoryRenderCheck;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** IaF dragon disguise: syncs flying/hovering flags, zeroes pitch/roll buffers when grounded,
 *  locks rotation in inventory render. Soft-dep, reflective with first-call caching. */
public final class IafDragonBehavior implements DisguiseBehavior {

    public static final IafDragonBehavior INSTANCE = new IafDragonBehavior();

    private static volatile boolean reflectionInited;
    private static Method setFlyingMethod;
    private static Method setHoveringMethod;
    private static Method getDragonPitchMethod;
    private static Method setDragonPitchMethod;
    private static Field prevDragonPitchField;
    private static Field turnBufferField;
    private static Field tailBufferField;
    private static Field rollBufferField;
    private static Field pitchBufferField;
    private static Field pitchBufferBodyField;
    private static Field yawVariationField;
    private static Field prevYawVariationField;
    private static Field pitchVariationField;
    private static Field prevPitchVariationField;
    private static Field yawTimerField;
    private static Field pitchTimerField;

    /** Per-render snapshot; {@code postRender} always calls {@link ThreadLocal#remove}. */
    private static final ThreadLocal<Snapshot> SNAPSHOT = new ThreadLocal<>();

    private static final class Snapshot {
        boolean savedDragonPitchValid;
        float savedDragonPitch;
        float savedPrevDragonPitch;
        // Rotation fields pinned to yBodyRot/0 so faceTarget adds no neck bend in inventory.
        float savedYHeadRot;
        float savedYHeadRotO;
        float savedYBodyRotO;
        float savedYRot;
        float savedYRotO;
        float savedXRot;
        float savedXRotO;
        float[] savedBufferYaw;
        float[] savedBufferPrevYaw;
        float[] savedBufferPitch;
        float[] savedBufferPrevPitch;
        int[] savedBufferYawTimer;
        int[] savedBufferPitchTimer;
    }

    private IafDragonBehavior() {}

    @Override
    public void preTick(LivingEntity wearer, LivingEntity disguise) {
        initReflection(disguise.getClass());
        if (setFlyingMethod == null) return;

        boolean wearerFlying = wearer.isFallFlying()
                || wearer.isSwimming()
                || (wearer instanceof Player p && p.getAbilities().flying);

        try {
            setFlyingMethod.invoke(disguise, wearerFlying);
            if (setHoveringMethod != null) {
                boolean wearerHovering = (wearer instanceof Player p && p.getAbilities().flying)
                        && wearer.getDeltaMovement().horizontalDistanceSqr() < 0.005;
                setHoveringMethod.invoke(disguise, wearerHovering);
            }
        } catch (ReflectiveOperationException ignored) {}
    }

    @Override
    public void preRender(LivingEntity wearer, LivingEntity disguise, float partialTick, boolean inInventory) {
        if (!inInventory) return;
        if (!isInventoryRender()) return;
        initReflection(disguise.getClass());

        Snapshot snap = new Snapshot();
        SNAPSHOT.set(snap);

        // (1) Chain buffer snapshot + zero. turn/tail buffers aren't IFChainBuffer; skip via instance check.
        Field[] bufferFields = {turnBufferField, tailBufferField, rollBufferField, pitchBufferField, pitchBufferBodyField};
        snap.savedBufferYaw = new float[bufferFields.length];
        snap.savedBufferPrevYaw = new float[bufferFields.length];
        snap.savedBufferPitch = new float[bufferFields.length];
        snap.savedBufferPrevPitch = new float[bufferFields.length];
        snap.savedBufferYawTimer = new int[bufferFields.length];
        snap.savedBufferPitchTimer = new int[bufferFields.length];
        for (int i = 0; i < bufferFields.length; i++) {
            try {
                if (bufferFields[i] == null) continue;
                Object buf = bufferFields[i].get(disguise);
                if (buf == null) continue;
                if (yawVariationField == null || !yawVariationField.getDeclaringClass().isInstance(buf)) continue;
                snap.savedBufferYaw[i] = yawVariationField.getFloat(buf);
                if (prevYawVariationField != null) snap.savedBufferPrevYaw[i] = prevYawVariationField.getFloat(buf);
                if (pitchVariationField != null) snap.savedBufferPitch[i] = pitchVariationField.getFloat(buf);
                if (prevPitchVariationField != null) snap.savedBufferPrevPitch[i] = prevPitchVariationField.getFloat(buf);
                if (yawTimerField != null) snap.savedBufferYawTimer[i] = yawTimerField.getInt(buf);
                if (pitchTimerField != null) snap.savedBufferPitchTimer[i] = pitchTimerField.getInt(buf);
                zeroBuffer(disguise, bufferFields[i]);
            } catch (ReflectiveOperationException | IllegalArgumentException ignored) {}
        }

        // (2) Full rotation lock — vanilla InventoryScreen leaves stale *RotO that the
        // neck chain amplifies into a visible twist; pinning yBody/0 kills it.
        snap.savedYHeadRot = disguise.yHeadRot;
        snap.savedYHeadRotO = disguise.yHeadRotO;
        snap.savedYBodyRotO = disguise.yBodyRotO;
        snap.savedYRot = disguise.getYRot();
        snap.savedYRotO = disguise.yRotO;
        snap.savedXRot = disguise.getXRot();
        snap.savedXRotO = disguise.xRotO;

        float lockedYaw = disguise.yBodyRot;
        disguise.yBodyRotO = lockedYaw;
        disguise.yHeadRot = lockedYaw;
        disguise.yHeadRotO = lockedYaw;
        disguise.setYRot(lockedYaw);
        disguise.yRotO = lockedYaw;
        disguise.setXRot(0F);
        disguise.xRotO = 0F;

        // (3) Force dragonPitch=0 — the renderer applies it before setupAnim and stale world
        // values tilt the inventory preview.
        if (setDragonPitchMethod != null && prevDragonPitchField != null) {
            try {
                snap.savedDragonPitch = (Float) getDragonPitchMethod.invoke(disguise);
                snap.savedPrevDragonPitch = prevDragonPitchField.getFloat(disguise);
                snap.savedDragonPitchValid = true;
                setDragonPitchMethod.invoke(disguise, 0F);
                prevDragonPitchField.setFloat(disguise, 0F);
            } catch (ReflectiveOperationException ignored) {
                snap.savedDragonPitchValid = false;
            }
        }
    }

    @Override
    public void postRender(LivingEntity wearer, LivingEntity disguise, float partialTick, boolean inInventory) {
        if (!inInventory) return;
        Snapshot snap = SNAPSHOT.get();
        if (snap == null) return;
        SNAPSHOT.remove();

        // Restore chain buffer state.
        if (snap.savedBufferYaw != null) {
            Field[] bufferFields = {turnBufferField, tailBufferField, rollBufferField, pitchBufferField, pitchBufferBodyField};
            for (int i = 0; i < bufferFields.length; i++) {
                try {
                    if (bufferFields[i] == null) continue;
                    Object buf = bufferFields[i].get(disguise);
                    if (buf == null) continue;
                    if (yawVariationField == null || !yawVariationField.getDeclaringClass().isInstance(buf)) continue;
                    yawVariationField.setFloat(buf, snap.savedBufferYaw[i]);
                    if (prevYawVariationField != null) prevYawVariationField.setFloat(buf, snap.savedBufferPrevYaw[i]);
                    if (pitchVariationField != null) pitchVariationField.setFloat(buf, snap.savedBufferPitch[i]);
                    if (prevPitchVariationField != null) prevPitchVariationField.setFloat(buf, snap.savedBufferPrevPitch[i]);
                    if (yawTimerField != null) yawTimerField.setInt(buf, snap.savedBufferYawTimer[i]);
                    if (pitchTimerField != null) pitchTimerField.setInt(buf, snap.savedBufferPitchTimer[i]);
                } catch (ReflectiveOperationException | IllegalArgumentException ignored) {}
            }
        }

        disguise.yHeadRot = snap.savedYHeadRot;
        disguise.yHeadRotO = snap.savedYHeadRotO;
        disguise.yBodyRotO = snap.savedYBodyRotO;
        disguise.setYRot(snap.savedYRot);
        disguise.yRotO = snap.savedYRotO;
        disguise.setXRot(snap.savedXRot);
        disguise.xRotO = snap.savedXRotO;
        if (snap.savedDragonPitchValid) {
            try {
                setDragonPitchMethod.invoke(disguise, snap.savedDragonPitch);
                prevDragonPitchField.setFloat(disguise, snap.savedPrevDragonPitch);
            } catch (ReflectiveOperationException ignored) {}
        }
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        if (yawVariationField == null) return;

        // IFChainBuffer decay bug: |variation| < angleDecrement settles at angleDecrement, not 0.
        try {
            zeroSmallVariation(disguise, turnBufferField);
            zeroSmallVariation(disguise, tailBufferField);
            zeroSmallVariation(disguise, rollBufferField);
            zeroSmallVariation(disguise, pitchBufferField);
            zeroSmallVariation(disguise, pitchBufferBodyField);
        } catch (ReflectiveOperationException ignored) {}

        // Native calculate/decay is gated on !onGround(); zero explicitly for clean inventory preview.
        if (disguise.onGround() && rollBufferField != null) {
            try {
                zeroBuffer(disguise, rollBufferField);
                zeroBuffer(disguise, pitchBufferField);
                zeroBuffer(disguise, pitchBufferBodyField);
            } catch (ReflectiveOperationException ignored) {}
        }
    }

    /** Zeros sub-decay-threshold variations to defeat the IFChainBuffer settle-at-decrement bug. */
    private static void zeroSmallVariation(LivingEntity disguise, Field bufferField) throws ReflectiveOperationException {
        if (bufferField == null) return;
        Object buffer = bufferField.get(disguise);
        if (buffer == null) return;
        if (yawVariationField == null || !yawVariationField.getDeclaringClass().isInstance(buffer)) return;
        // Above IaF's max angleDecrement (5) so an active swing isn't snapped to 0.
        final float threshold = 6.0F;
        if (yawVariationField != null) {
            float v = yawVariationField.getFloat(buffer);
            if (Math.abs(v) < threshold) {
                yawVariationField.setFloat(buffer, 0.0F);
                if (prevYawVariationField != null) prevYawVariationField.setFloat(buffer, 0.0F);
                if (yawTimerField != null) yawTimerField.setInt(buffer, 0);
            }
        }
        if (pitchVariationField != null) {
            float v = pitchVariationField.getFloat(buffer);
            if (Math.abs(v) < threshold) {
                pitchVariationField.setFloat(buffer, 0.0F);
                if (prevPitchVariationField != null) prevPitchVariationField.setFloat(buffer, 0.0F);
                if (pitchTimerField != null) pitchTimerField.setInt(buffer, 0);
            }
        }
    }

    private static void zeroBuffer(LivingEntity disguise, Field bufferField) throws ReflectiveOperationException {
        Object buffer = bufferField.get(disguise);
        if (buffer == null) return;
        if (yawVariationField == null || !yawVariationField.getDeclaringClass().isInstance(buffer)) return;
        yawVariationField.setFloat(buffer, 0.0F);
        if (prevYawVariationField != null) prevYawVariationField.setFloat(buffer, 0.0F);
        if (pitchVariationField != null) pitchVariationField.setFloat(buffer, 0.0F);
        if (prevPitchVariationField != null) prevPitchVariationField.setFloat(buffer, 0.0F);
        if (yawTimerField != null) yawTimerField.setInt(buffer, 0);
        if (pitchTimerField != null) pitchTimerField.setInt(buffer, 0);
    }

    private static void initReflection(Class<?> dragonClass) {
        if (reflectionInited) return;
        synchronized (IafDragonBehavior.class) {
            if (reflectionInited) return;
            setFlyingMethod = ReflectionHelper.publicMethod(dragonClass, "setFlying", boolean.class);
            setHoveringMethod = ReflectionHelper.publicMethod(dragonClass, "setHovering", boolean.class);
            getDragonPitchMethod = ReflectionHelper.publicMethod(dragonClass, "getDragonPitch");
            setDragonPitchMethod = ReflectionHelper.publicMethod(dragonClass, "setDragonPitch", float.class);
            prevDragonPitchField = ReflectionHelper.declaredField(dragonClass, "prevDragonPitch");
            turnBufferField = ReflectionHelper.declaredField(dragonClass, "turn_buffer");
            tailBufferField = ReflectionHelper.declaredField(dragonClass, "tail_buffer");
            rollBufferField = ReflectionHelper.declaredField(dragonClass, "roll_buffer");
            pitchBufferField = ReflectionHelper.declaredField(dragonClass, "pitch_buffer");
            pitchBufferBodyField = ReflectionHelper.declaredField(dragonClass, "pitch_buffer_body");

            Class<?> bufferClass = findBufferClass(dragonClass.getClassLoader());
            if (bufferClass != null) {
                yawVariationField = ReflectionHelper.declaredField(bufferClass, "yawVariation");
                prevYawVariationField = ReflectionHelper.declaredField(bufferClass, "prevYawVariation");
                pitchVariationField = ReflectionHelper.declaredField(bufferClass, "pitchVariation");
                prevPitchVariationField = ReflectionHelper.declaredField(bufferClass, "prevPitchVariation");
                yawTimerField = ReflectionHelper.declaredField(bufferClass, "yawTimer");
                pitchTimerField = ReflectionHelper.declaredField(bufferClass, "pitchTimer");
            }
            reflectionInited = true;
        }
    }

    private static Class<?> findBufferClass(ClassLoader cl) {
        for (String name : new String[]{
                "com.iafenvoy.iceandfire.render.model.IFChainBuffer",          // IaF-CE
                "com.github.alexthe666.iceandfire.client.model.IFChainBuffer", // stock IaF
                "com.iafenvoy.uranus.client.model.tools.ChainBuffer",          // Uranus
                "com.github.alexthe666.citadel.client.model.ChainBuffer"       // stock Citadel
        }) {
            try {
                return Class.forName(name, false, cl);
            } catch (ClassNotFoundException ignored) {}
        }
        return null;
    }

    private static boolean isInventoryRender() {
        return InventoryRenderCheck.isInventoryRender();
    }
}