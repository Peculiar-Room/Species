package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class WickedSwapperProjectile extends ThrowableItemProjectile {

    public WickedSwapperProjectile(EntityType<? extends WickedSwapperProjectile> p_37491_, Level p_37492_) {
        super(p_37491_, p_37492_);
    }

    public WickedSwapperProjectile(Level p_37499_, LivingEntity p_37500_) {
        super(SpeciesEntities.WICKED_SWAPPER.get(), p_37500_, p_37499_);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult)hitResult).getEntity();
            if (!this.level().isClientSide && !entity.isPassenger() && this.getOwner() != null && !this.getOwner().isPassenger()) {
                double entityX = entity.getX();
                double entityY = entity.getY();
                double entityZ = entity.getZ();

                entity.teleportTo(this.getOwner().getX(), this.getOwner().getY(), this.getOwner().getZ());
                this.getOwner().teleportTo(entityX, entityY, entityZ);

                this.level().playSound(null, this.getOwner().getX(), this.getOwner().getY(), this.getOwner().getZ(), SpeciesSoundEvents.WICKED_SWAPPER_TELEPORT.get(), this.getSoundSource(), 1.0F, 1.0F);
                this.level().playSound(null, entityX, entityY, entityZ, SpeciesSoundEvents.WICKED_SWAPPER_TELEPORT.get(), this.getSoundSource(), 1.0F, 1.0F);

                for (int i = 0; i < 5; i++) {
                    this.level().addParticle(ParticleTypes.PORTAL, this.getOwner().getX(),  this.getOwner().getY() + 1.0,  this.getOwner().getZ(), 0.5 - this.random.nextDouble(), 0.5 - this.random.nextDouble(), 0.5 - this.random.nextDouble());
                    this.level().addParticle(ParticleTypes.EXPLOSION, this.getOwner().getX(), this.getOwner().getY() + 1.0, this.getOwner().getZ(), 0.5 - this.random.nextDouble(), 0.5 - this.random.nextDouble(), 0.5 - this.random.nextDouble());

                    this.level().addParticle(ParticleTypes.PORTAL, entityX, entityY + 1.0, entityZ, 0.5 - this.random.nextDouble(), 0.5 - this.random.nextDouble(), 0.5 - this.random.nextDouble());
                    this.level().addParticle(ParticleTypes.EXPLOSION, entityX, entityY + 1.0, entityZ, 0.5 - this.random.nextDouble(), 0.5 - this.random.nextDouble(), 0.5 - this.random.nextDouble());
                }

                this.remove(RemovalReason.DISCARDED);
            }
        } else {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.WICKED_SWAPPER_FAIL.get(), this.getSoundSource(), 1.0F, 1.0F);
            this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem()));
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return SpeciesItems.WICKED_SWAPPER.get();
    }

    @Override
    public void tick() {
        super.tick();

        //if (this.level().isClientSide) {
        //    this.level().addParticle(SpeciesParticles.WICKED_EMBER.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        //}

        if (!this.level().isClientSide && (this.tickCount > (20 * 60) || this.isInWall())) {
            this.remove(RemovalReason.DISCARDED);
        }
    }
}
