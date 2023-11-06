package com.ninni.species.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class Mammutilation extends PathfinderMob {

    boolean isSitting = false;
    int howlCooldown = 0;

    public Mammutilation(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new MoveControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new HowAtMoonGoal(this));
        //this.goalSelector.addGoal(5, new SitAndFollowMoonGoal(this));
        //this.goalSelector.addGoal(5, new RiseGoal(this));
        //this.goalSelector.addGoal(3, new TeleportIfCullingGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.KNOCKBACK_RESISTANCE, 0.6).add(Attributes.ATTACK_DAMAGE, 0.0);
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Mammutilation> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(howlCooldown>0)
            this.howlCooldown--;
    }

    public class HowAtMoonGoal extends Goal{
        protected final Mammutilation mammutilation;

        public HowAtMoonGoal(Mammutilation mammutilation) {
            this.mammutilation = mammutilation;
        }

        @Override
        public void start() {
            mammutilation.playSound(SoundEvents.WOLF_HOWL, 1.0F, 0.1F);
            mammutilation.howlCooldown = 1000;
        }

        @Override
        public boolean canUse() {
            return mammutilation.level().isNight() && howlCooldown==0;
        }
    }
}
