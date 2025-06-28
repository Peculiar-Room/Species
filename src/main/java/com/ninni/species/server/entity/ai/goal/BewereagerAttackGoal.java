package com.ninni.species.server.entity.ai.goal;

import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.mob.update_3.Bewereager;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class BewereagerAttackGoal extends Goal {
    protected final Bewereager bewereager;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;

    public BewereagerAttackGoal(Bewereager bewereager) {
        this.bewereager = bewereager;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public boolean canUse() {
        LivingEntity livingEntity = this.bewereager.getTarget();
        if (livingEntity == null || !livingEntity.isAlive() || this.bewereager.getPose() == Pose.ROARING || this.bewereager.isSplitting()) return false;
        else {
            this.path = this.bewereager.getNavigation().createPath(livingEntity, 0);
            if (this.path != null) return true;
            else return this.getAttackReachSqr(livingEntity) >= this.bewereager.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        }
    }

    public boolean canContinueToUse() {
        LivingEntity livingentity = this.bewereager.getTarget();
        if (livingentity == null || this.bewereager.isSplitting()) return false;
        else if (!livingentity.isAlive()) return false;
        else if (!this.bewereager.isWithinRestriction(livingentity.blockPosition())) return false;
        else return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player)livingentity).isCreative();
    }

    public void start() {
        this.bewereager.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
    }

    public void stop() {
        LivingEntity livingentity = this.bewereager.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) this.bewereager.setTarget(null);
        this.bewereager.setAggressive(false);
        this.bewereager.getNavigation().stop();
    }


    public void tick() {
        LivingEntity livingEntity = this.bewereager.getTarget();
        if (livingEntity != null) {
            this.bewereager.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            double distanceToTarget = this.bewereager.getPerceivedTargetDistanceSquareForMeleeAttack(livingEntity);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);

            if (this.bewereager.getSensing().hasLineOfSight(livingEntity) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0 || livingEntity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 0.0 || this.bewereager.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = livingEntity.getX();
                this.pathedTargetY = livingEntity.getY();
                this.pathedTargetZ = livingEntity.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.bewereager.getRandom().nextInt(7);

                if (distanceToTarget > 1024.0) this.ticksUntilNextPathRecalculation += 10;
                else if (distanceToTarget > 256.0) this.ticksUntilNextPathRecalculation += 5;

                if (!this.bewereager.getNavigation().moveTo(livingEntity, 1.4)) this.ticksUntilNextPathRecalculation += 15;

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.checkAndPerformAttack(livingEntity, distanceToTarget);
            this.path = this.bewereager.getNavigation().createPath(livingEntity, 0);
            if (this.getAttackReachSqr(livingEntity) > 0) this.bewereager.getNavigation().moveTo(this.path, 1.4);
        }

    }

    protected void checkAndPerformAttack(LivingEntity livingEntity, double distanceToTarget) {

        if (distanceToTarget <= this.getAttackReachSqr(livingEntity) && bewereager.attackCooldown == 0 && bewereager.getPose() == Pose.STANDING) {
            if (bewereager.getAttackSpeed() >= 3) {
                bewereager.setPose(SpeciesPose.ATTACK.get());
                bewereager.playSound(SpeciesSoundEvents.BEWEREAGER_BITE.get());
            }
            else {
                bewereager.setPose(SpeciesPose.SLASH_ATTACK.get());
                bewereager.playSound(SpeciesSoundEvents.BEWEREAGER_SLASH.get());
            }
            bewereager.playSound(SpeciesSoundEvents.BEWEREAGER_SPEED.get(), 1, 0.7F + (bewereager.getAttackSpeed() * 0.2F));
            this.bewereager.swing(InteractionHand.MAIN_HAND);
            this.bewereager.doHurtTarget(livingEntity);
        }
    }

    protected double getAttackReachSqr(LivingEntity livingEntity) {
        return this.bewereager.getBbWidth() * 2 * this.bewereager.getBbWidth() * 2 + livingEntity.getBbWidth();
    }
}