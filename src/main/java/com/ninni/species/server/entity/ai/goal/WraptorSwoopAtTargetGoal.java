package com.ninni.species.server.entity.ai.goal;

import com.ninni.species.server.entity.mob.update_1.Wraptor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WraptorSwoopAtTargetGoal extends Goal {
    private final Wraptor wraptor;
    private LivingEntity target;
    private final float velocity;

    public WraptorSwoopAtTargetGoal(Wraptor wraptor, float velocity) {
        this.wraptor = wraptor;
        this.velocity = velocity;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.target = this.wraptor.getTarget();
        if (this.target == null || this.wraptor.getPose() == Pose.ROARING) return false;
        double distance = this.wraptor.distanceToSqr(this.target);

        if (this.wraptor.getFeatherStage() > 0) return false;

        if (distance < 10.0 || distance > 40.0) return false;
        if (!this.wraptor.onGround()) return false;

        return this.wraptor.getRandom().nextInt(LeapAtTargetGoal.reducedTickDelay(5)) == 0;
    }

    @Override
    public void tick() {
        this.wraptor.getLookControl().setLookAt(this.target);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.wraptor.onGround() && this.wraptor.getPose() != Pose.ROARING;
    }

    @Override
    public void start() {
        Vec3 vec3d = new Vec3(this.target.getX() - this.wraptor.getX(), 0.0, this.target.getZ() - this.wraptor.getZ());
        if (vec3d.lengthSqr() > 1.0E-7) vec3d = vec3d.normalize().scale(0.4).add(this.wraptor.getDeltaMovement().scale(0.2));
        this.wraptor.setDeltaMovement(vec3d.x * 1.5, this.velocity, vec3d.z * 1.5);
    }
}


