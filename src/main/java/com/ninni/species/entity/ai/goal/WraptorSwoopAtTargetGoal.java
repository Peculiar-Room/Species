package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.WraptorEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WraptorSwoopAtTargetGoal extends Goal {
    private final WraptorEntity mob;
    private LivingEntity target;
    private final float velocity;

    public WraptorSwoopAtTargetGoal(WraptorEntity mob, float velocity) {
        this.mob = mob;
        this.velocity = velocity;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        if (this.target == null) return false;
        double distance = this.mob.distanceToSqr(this.target);

        if (this.mob.getFeatherStage() > 0) return false;

        if (distance < 10.0 || distance > 40.0) return false;
        if (!this.mob.onGround()) return false;

        return this.mob.getRandom().nextInt(LeapAtTargetGoal.reducedTickDelay(5)) == 0;
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.target, 10.0f, this.mob.getMaxHeadXRot());
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.onGround();
    }

    @Override
    public void start() {
        Vec3 vec3d = new Vec3(this.target.getX() - this.mob.getX(), 0.0, this.target.getZ() - this.mob.getZ());
        if (vec3d.lengthSqr() > 1.0E-7) vec3d = vec3d.normalize().scale(0.4).add(this.mob.getDeltaMovement().scale(0.2));
        this.mob.setDeltaMovement(vec3d.x * 1.5, this.velocity, vec3d.z * 1.5);
    }
}


