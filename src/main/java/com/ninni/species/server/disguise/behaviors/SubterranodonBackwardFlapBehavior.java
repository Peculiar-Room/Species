package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.disguise.dsl.WearerPredicates;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

/** Subterranodon flap-on-backward-flight. Native ramps {@code flapAmount} only when
 *  {@code deltaMovement.y > 0 || isHovering()}; level backward flight matches neither, so we
 *  pin {@code setHovering(true)} on that edge to reuse the flap branch. */
public final class SubterranodonBackwardFlapBehavior implements DisguiseBehavior {

    public static final SubterranodonBackwardFlapBehavior INSTANCE = new SubterranodonBackwardFlapBehavior();

    private static final double BACKWARD_DOT_THRESHOLD = -0.05;
    private static final double BACKWARD_HORIZONTAL_SQ = 0.001;

    private static volatile boolean reflectionInited;
    private static Method setHoveringMethod;

    private SubterranodonBackwardFlapBehavior() {}

    @Override
    public void preTick(LivingEntity wearer, LivingEntity disguise) {
        // preTick so we override AutoFlightSyncBehavior's setHovering(false) before tick reads it.
        initReflection(disguise.getClass());
        if (setHoveringMethod == null) return;
        if (!WearerPredicates.FLYING.test(wearer)) return;

        Vec3 motion = wearer.getDeltaMovement();
        if (motion.x * motion.x + motion.z * motion.z < BACKWARD_HORIZONTAL_SQ) return;

        float yawRad = wearer.getYRot() * Mth.DEG_TO_RAD;
        // Mojang convention: yaw 0 → +Z, yaw 90 → -X.
        double facingX = -Mth.sin(yawRad);
        double facingZ = Mth.cos(yawRad);
        double dot = facingX * motion.x + facingZ * motion.z;
        if (dot >= BACKWARD_DOT_THRESHOLD) return;

        try { setHoveringMethod.invoke(disguise, true); }
        catch (ReflectiveOperationException ignored) {}
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (SubterranodonBackwardFlapBehavior.class) {
            if (reflectionInited) return;
            setHoveringMethod = ReflectionHelper.publicMethod(entityClass, "setHovering", boolean.class);
            reflectionInited = true;
        }
    }
}
