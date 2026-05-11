package com.ninni.species.server.disguise;

import com.ninni.species.api.disguise.CompanionStackSpec;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.server.entity.mob.update_1.Stackatick;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

/** Stackatick-specific config for {@link GenericCompanionStackManager}: sitting pose, body-yaw
 *  mirror, dye inheritance. Bridge methods delegate to the generic manager. */
public final class StackatickStackManager {

    public static final int MAX_LEVEL = 3;
    private static final double SIT_HEIGHT = 0.5625;
    /** Standing shell-box top (derived from model math). bbHeight 1.05 includes the back-ridge
     *  decoration; anchoring to the shell box top brings stacked companions flush. */
    private static final double STANDING_SHELL_TOP = 0.875;

    static {
        GenericCompanionStackManager.register(
                SpeciesEntities.STACKATICK.get(),
                new CompanionStackSpec<>(
                        SpeciesEntities.STACKATICK.get(),
                        MAX_LEVEL,
                        SIT_HEIGHT,
                        StackatickStackManager::initCompanion,
                        StackatickStackManager::tickCompanion,
                        StackatickStackManager::resolveHeight));
    }

    private static double resolveHeight(net.minecraft.world.entity.LivingEntity wearer,
                                        net.minecraft.world.entity.LivingEntity disguise) {
        // Crouched bbHeight matches visual (SITTING anim drops model into bbox). Standing
        // bbHeight overshoots the shell top, so anchor to the shell box top directly.
        return wearer.getPose() == net.minecraft.world.entity.Pose.CROUCHING
                ? disguise.getDimensions(wearer.getPose()).height
                : STANDING_SHELL_TOP;
    }

    private StackatickStackManager() {}

    public static void cycle(LivingEntity wearer, LivingEntity disguise) {
        GenericCompanionStackManager.cycle(wearer, disguise);
    }

    public static void tickStack(LivingEntity wearer, LivingEntity disguise) {
        GenericCompanionStackManager.tickStack(wearer, disguise);
    }

    public static void clear(LivingEntity wearer) {
        GenericCompanionStackManager.clear(wearer);
    }

    private static void initCompanion(Stackatick c, LivingEntity wearer, LivingEntity disguise, int index) {
        c.setPose(Pose.CROUCHING);
        c.setInSittingPose(true);
        c.setOrderedToSit(true);
        c.setPos(disguise.getX(), disguise.getY(), disguise.getZ());
        c.xo = c.getX(); c.yo = c.getY(); c.zo = c.getZ();
        c.xOld = c.xo; c.yOld = c.yo; c.zOld = c.zo;
        float yaw = wearer.yBodyRot;
        c.setYRot(yaw); c.setYBodyRot(yaw); c.setYHeadRot(yaw);
        c.yRotO = yaw; c.yBodyRotO = yaw; c.yHeadRotO = yaw;
    }

    private static void tickCompanion(Stackatick c, LivingEntity wearer, LivingEntity disguise, int index) {
        float yaw = wearer.yBodyRot;
        c.setYRot(yaw); c.setYBodyRot(yaw); c.setYHeadRot(yaw);
        if (disguise instanceof Stackatick d) {
            if (d.isDyed() && !c.isDyed()) c.setDyed(true);
            c.setColor(d.getColor());
        }
    }
}
