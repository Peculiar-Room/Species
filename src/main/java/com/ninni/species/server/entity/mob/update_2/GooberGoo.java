package com.ninni.species.server.entity.mob.update_2;

import com.google.common.collect.Maps;
import com.ninni.species.server.data.GooberGooManager;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Map;

public class GooberGoo extends ThrowableItemProjectile {

    public GooberGoo(EntityType<? extends GooberGoo> entityType, Level world) {
        super(entityType, world);
    }

    public GooberGoo(Level world, double x, double y, double z) {
        super(SpeciesEntities.GOOBER_GOO.get(), x, y, z, world);
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
        Map<BlockPos, BlockState> posBlockStateMap = Maps.newHashMap();

        int yRange = 2;
        int xRadius = UniformInt.of(4, 7).sample(this.random);
        int zRadius = UniformInt.of(4, 7).sample(this.random);

        if (!this.level().isClientSide) {
            for (GooberGooManager.GooberGooData data : GooberGooManager.DATA) {
                for (int y = -yRange; y <= yRange; y++) {
                    for (int x = -xRadius; x <= xRadius; x++) {
                        for (int z = -zRadius; z <= zRadius; z++) {

                            BlockPos placePos = BlockPos.containing(blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z);
                            BlockState state = world.getBlockState(placePos);
                            BlockState aboveState = world.getBlockState(placePos.above());

                            int distance = Math.max(1, Math.round(Mth.sqrt((float) blockPos.distSqr(placePos))));

                            if (x * x + z * z > xRadius * zRadius && this.random.nextInt(distance) != 0) continue;

                            Block input = data.input();
                            Block output = data.output();

                            boolean aboveStateFlag = aboveState.isAir() || aboveState.canBeReplaced();

                            if (state.is(input) && aboveStateFlag) {

                                if (state.getBlock() instanceof DoublePlantBlock && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) continue;

                                posBlockStateMap.put(placePos, output.defaultBlockState());
                            }
                        }
                    }
                }
            }

            for (BlockPos position : posBlockStateMap.keySet()) {
                BlockState state = this.level().getBlockState(position);
                BlockState output = posBlockStateMap.get(position);
                if (this.random.nextFloat() < 0.35F) {
                    this.level().levelEvent(2005, position, 0);
                }
                if (state.getBlock() instanceof DoublePlantBlock && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                    BlockState aboveState = world.getBlockState(position.above());
                    if (aboveState.getBlock() instanceof DoublePlantBlock && aboveState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
                        world.removeBlock(position.above(), false);
                    }
                    DoublePlantBlock.placeAt(this.level(), output, position, 2);
                    continue;
                }
                world.setBlock(position, output, 2);
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
        return SpeciesItems.PETRIFIED_EGG.get();
    }
}
