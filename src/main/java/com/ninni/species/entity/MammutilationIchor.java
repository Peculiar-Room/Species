package com.ninni.species.entity;

import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class MammutilationIchor extends ThrowableItemProjectile {

    public MammutilationIchor(EntityType<? extends MammutilationIchor> entityType, Level world) {
        super(entityType, world);
    }

    public MammutilationIchor(Level world, double x, double y, double z) {
        super(SpeciesEntities.MAMMUTILATION_ICHOR, x, y, z, world);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        HitResult.Type type = hitResult.getType();

        BlockPos pos = new BlockPos((int)hitResult.getLocation().x(),(int)hitResult.getLocation().y(),(int)hitResult.getLocation().z());

        System.out.println(this.level().getBlockState(pos));

        if (this.level().getBlockState(pos).canBeReplaced()) this.level().setBlockAndUpdate(pos, SpeciesBlocks.ICHOR.defaultBlockState());
        else if (this.level().getBlockState(pos.above()).canBeReplaced()) this.level().setBlockAndUpdate(pos.above(), SpeciesBlocks.ICHOR.defaultBlockState());

        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);

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
        return Items.AIR;
    }
}

