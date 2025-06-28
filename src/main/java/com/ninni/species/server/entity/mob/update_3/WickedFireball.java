package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WickedFireball extends AbstractHurtingProjectile {
    public float hurtAmount;

    public WickedFireball(EntityType<? extends WickedFireball> p_37006_, Level p_37007_) {
        super(p_37006_, p_37007_);
    }

    public WickedFireball(LivingEntity p_37000_, double p_37001_, double p_37002_, double p_37003_, Level p_37004_, float hurtAmount) {
        super(SpeciesEntities.WICKED_FIREBALL.get(), p_37000_, p_37001_, p_37002_, p_37003_, p_37004_);
        this.hurtAmount = hurtAmount;
    }

    public void tick() {
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            super.tick();
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            double v = (this.random.nextFloat() * (this.random.nextBoolean() ? -1 : 1)) * 0.25;
            if (this.random.nextInt(3) == 0) {
                this.level().addParticle(SpeciesParticles.WICKED_FLAME.get(), d0 + v, d1 + v, d2 + v, 0.0, 0.0, 0.0);
            }
        } else {
            this.discard();
        }

    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            if (this.getOwner() instanceof  LivingEntity owner) entity.hurt(this.damageSources().mobAttack(owner), hurtAmount);
            entity.setSecondsOnFire(1);
        }
        this.level().gameEvent(GameEvent.PROJECTILE_LAND, hitResult.getLocation(), GameEvent.Context.of(this, null));
        this.discard();
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    @Override
    public boolean hurt(DamageSource p_36839_, float p_36840_) {
        return this.getOwner() == null && super.hurt(p_36839_, p_36840_);
    }
}