package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.WraptorEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class WraptorSwoopAtTargetGoal extends Goal {
    private final WraptorEntity mob;
    private LivingEntity target;
    private final float velocity;

    public WraptorSwoopAtTargetGoal(WraptorEntity mob, float velocity) {
        this.mob = mob;
        this.velocity = velocity;
        this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        this.target = this.mob.getTarget();
        if (this.target == null) return false;
        double distance = this.mob.squaredDistanceTo(this.target);

        if (this.mob.getFeatherStage() > 0) return false;

        if (distance < 10.0 || distance > 40.0) return false;
        if (!this.mob.isOnGround()) return false;

        return this.mob.getRandom().nextInt(PounceAtTargetGoal.toGoalTicks(5)) == 0;
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.isOnGround();
    }

    @Override
    public void start() {
        Vec3d vec3d = new Vec3d(this.target.getX() - this.mob.getX(), 0.0, this.target.getZ() - this.mob.getZ());
        if (vec3d.lengthSquared() > 1.0E-7) vec3d = vec3d.normalize().multiply(0.4).add(this.mob.getVelocity().multiply(0.2));
        this.mob.setVelocity(vec3d.x * 1.5, this.velocity, vec3d.z * 1.5);
    }
}


