package com.ninni.species.entity;

import com.ninni.species.registry.*;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class GooberGoo extends ThrowableItemProjectile {

    public GooberGoo(EntityType<? extends GooberGoo> entityType, Level world) {
        super(entityType, world);
    }

    public GooberGoo(Level world, double x, double y, double z) {
        super(SpeciesEntities.GOOBER_GOO, x, y, z, world);
    }


    @Override
    public void handleEntityEvent(byte status) {
        if (status == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getDefaultItem().getDefaultInstance()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        HitResult.Type type = hitResult.getType();
        this.level().broadcastEntityEvent(this, (byte)3);
        this.discard();
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        super.recreateFromPacket(clientboundAddEntityPacket);
        double d = clientboundAddEntityPacket.getXa();
        double e = clientboundAddEntityPacket.getYa();
        double f = clientboundAddEntityPacket.getZa();
        for (int i = 0; i < 7; ++i) {
            double g = 0.4 + 0.1 * (double)i;
            this.level().addParticle(ParticleTypes.SPIT, this.getX(), this.getY(), this.getZ(), d * g, e, f * g);
        }
        this.setDeltaMovement(d, e, f);
    }

    @Override
    protected Item getDefaultItem() {
        return SpeciesItems.PETRIFIED_EGG;
    }
}

