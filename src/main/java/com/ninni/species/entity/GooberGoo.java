package com.ninni.species.entity;

import com.ninni.species.data.GooberGooManager;
import com.ninni.species.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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
            for (int i = 0; i < 3; ++i) {
                double h = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getX() + 0.5 + random.nextFloat()*3 - random.nextFloat()*3, this.getY() + random.nextFloat(), this.getZ() + 0.5 + random.nextFloat()*3 - random.nextFloat()*3, h,h,h);
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        HitResult.Type type = hitResult.getType();
        if (type != HitResult.Type.ENTITY) {
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        Level world = this.level();
        BlockPos blockPos = blockHitResult.getBlockPos();
        int yRange = 1;
        int xRadius = UniformInt.of(1, 2).sample(this.random);
        int zRadius = UniformInt.of(1, 2).sample(this.random);
        if (!this.level().isClientSide) {
            for (GooberGooManager.GooberGooData data : GooberGooManager.DATA) {
                for (int y = -yRange; y <= yRange; y++) {
                    for (int x = -xRadius; x <= xRadius; x++) {
                        for (int z = -zRadius; z <= zRadius; z++) {
                            BlockPos placePos = BlockPos.containing(blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z);
                            BlockState state = world.getBlockState(placePos);
                            BlockState aboveState = world.getBlockState(placePos.above());

                            Block input = data.input();
                            Block output = data.output();

                            boolean aboveStateFlag = aboveState.isAir() || aboveState.canBeReplaced();
                            if (state.is(input) && aboveStateFlag) {
                                world.setBlock(placePos, output.defaultBlockState(), 2);
                                if (this.random.nextFloat() < 0.35F) {
                                    this.level().levelEvent(2005, placePos, 0);
                                }
                            }
                        }
                    }
                }
            }
        }
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

