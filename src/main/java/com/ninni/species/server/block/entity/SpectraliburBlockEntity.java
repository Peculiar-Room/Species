package com.ninni.species.server.block.entity;

import com.ninni.species.registry.*;
import com.ninni.species.server.criterion.SpeciesCriterion;
import com.ninni.species.server.packet.BlockEntitySyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class SpectraliburBlockEntity extends BlockEntity implements IForgeBlockEntity {
    public static final float BASE_INCREMENT = 0.1f;
    public float swordPosition;
    public float shaking;
    public int hitCooldown;
    public int downCooldown;
    public int decrementCoolDown;

    public SpectraliburBlockEntity(BlockPos pos, BlockState state) {
        super(SpeciesBlockEntities.SPECTRALIBUR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpectraliburBlockEntity entity) {
        if (entity.swordPosition >= 1f) {
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SpeciesSoundEvents.SPECTRALIBUR_EXTRACT.get(), SoundSource.BLOCKS, 3.0F, 1.0F);
            if (level instanceof ServerLevel serverLevel) serverLevel.sendParticles(SpeciesParticles.SPECTRALIBUR_RELEASED.get(), pos.getX() + 0.5F,pos.getY() + 0.1F, pos.getZ() + 0.5F, 1,0, 0, 0, 0);
            if (level instanceof ServerLevel serverLevel) serverLevel.sendParticles(SpeciesParticles.SPECTRALIBUR.get(), pos.getX() + 0.5F,pos.getY() + 0.25F, pos.getZ() + 0.5F, 1,0, 0, 0, 0);
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, new ItemStack(SpeciesItems.SPECTRALIBUR.get()));
            itemEntity.setDeltaMovement(new Vec3(0,0.5,0));
            level.addFreshEntity(itemEntity);
            if (level instanceof ServerLevel serverLevel) ExperienceOrb.award(serverLevel, pos.getCenter().add(0,0.6,0), 140);
            return;
        }

        if (entity.hitCooldown > 0) entity.hitCooldown--;

        if (entity.downCooldown > 0) {
            if (entity.hitCooldown == 1) {
                SoundEvent soundEvent;
                if (entity.swordPosition <= 0.5F) soundEvent = SpeciesSoundEvents.SPECTRALIBUR_CAN_BE_PULLED1.get();
                else if (entity.swordPosition > 0.5F && entity.swordPosition <= 0.9F) soundEvent = SpeciesSoundEvents.SPECTRALIBUR_CAN_BE_PULLED2.get();
                else soundEvent = SpeciesSoundEvents.SPECTRALIBUR_CAN_BE_PULLED3.get();

                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), soundEvent, SoundSource.BLOCKS, 2.0F, 1.0F);
                if (level instanceof ServerLevel serverLevel) serverLevel.sendParticles(SpeciesParticles.ASCENDING_SPECTRE_SMOKE.get(), pos.getX() + 0.5F,pos.getY() + 0.1F, pos.getZ() + 0.5F, 10,level.random.nextGaussian() * 0.15,0.2,level.random.nextGaussian() * 0.15, 0);
            }
            entity.downCooldown--;
        }

        if (entity.swordPosition > 0) {
            if (entity.downCooldown == 0) {
                if (entity.decrementCoolDown > 0) {
                    entity.decrementCoolDown--;
                    return;
                }

                if (entity.decrementCoolDown == 0) {
                    entity.shaking = 0;

                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(SpeciesParticles.SPECTRALIBUR_INVERTED.get(), pos.getX() + 0.5F, pos.getY() + 0.01F, pos.getZ() + 0.5F, 1, 0, 0, 0, 0);
                    }

                    entity.swordPosition = Math.max(0, entity.swordPosition - BASE_INCREMENT);

                    if (entity.swordPosition == 0) level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SpeciesSoundEvents.SPECTRALIBUR_GO_IN_FULLY.get(), SoundSource.BLOCKS, 2.0F, 0.75F);
                    else level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SpeciesSoundEvents.SPECTRALIBUR_GO_IN.get(), SoundSource.BLOCKS, 2.0F, 0.75F + (entity.swordPosition * 0.5F));

                    entity.decrementCoolDown = 40;
                    entity.sync();
                }

            }
        }

    }


    public void onHit(Player player) {
        if (player != null) {
            if (hitCooldown == 0) {
                if (!player.hasEffect(MobEffects.DAMAGE_BOOST) && swordPosition == 0) {
                    hitCooldown = 5;
                } else {
                    boolean higherStrengthlevel = player.hasEffect(MobEffects.DAMAGE_BOOST) && player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() > 0;
                    swordPosition = Math.min(1.0f, swordPosition + BASE_INCREMENT);
                    if (player instanceof ServerPlayer serverPlayer) SpeciesCriterion.START_SPECTRE_CHALLENGE.trigger(serverPlayer);

                    if (player.level().getDifficulty() == Difficulty.PEACEFUL) {
                        hitCooldown = 20;
                        downCooldown = 60;
                    } else {
                        if (swordPosition <= 0.2) {
                            hitCooldown = higherStrengthlevel ? 50 : 60;
                            downCooldown = higherStrengthlevel ? 90 : 100;
                        }
                        if (swordPosition > 0.2 && swordPosition <= 0.4) {
                            hitCooldown = higherStrengthlevel ? 70 : 80;
                            downCooldown = higherStrengthlevel ? 120 : 130;
                        }
                        if (swordPosition > 0.4 && swordPosition <= 0.8) {
                            hitCooldown = higherStrengthlevel ? 85 : 100;
                            downCooldown = higherStrengthlevel ? 135 : 150;
                        }
                        if (swordPosition > 0.8) {
                            hitCooldown = higherStrengthlevel ? 100 : 120;
                            downCooldown = higherStrengthlevel ? 200 : 220;
                        }
                    }
                }
            }
            shaking = player.level().getGameTime();
            sync();
        }
    }


    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("SwordPosition")) this.swordPosition = nbt.getFloat("SwordPosition");
        if (nbt.contains("Shaking")) this.shaking = nbt.getFloat("Shaking");
        if (nbt.contains("HitCooldown")) this.hitCooldown = nbt.getInt("HitCooldown");
        if (nbt.contains("DownCooldown")) this.downCooldown = nbt.getInt("DownCooldown");
        if (nbt.contains("DecrementCooldown")) this.decrementCoolDown = nbt.getInt("DecrementCooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putFloat("SwordPosition", swordPosition);
        nbt.putFloat("Shaking", shaking);
        nbt.putInt("HitCooldown", hitCooldown);
        nbt.putInt("DownCooldown", downCooldown);
        nbt.putInt("DecrementCooldown", decrementCoolDown);
    }

    public void sync() {
        setChanged();
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        if (level == null) return;
        if (!level.isClientSide()) {
            SpeciesNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new BlockEntitySyncPacket(worldPosition, tag));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (pkt.getTag() != null) handleUpdateTag(pkt.getTag());
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    public float getSwordPosition() {
        return swordPosition;
    }

    public boolean isOnCooldown() {
        return hitCooldown > 0;
    }
    public float getShaking() {
        return shaking;
    }

    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = this.worldPosition;
        return new AABB(pos.getX(), pos.getY() - 2, pos.getZ(), pos.getX() + 1.0, pos.getY() + 2, pos.getZ() + 1.0);
    }
}