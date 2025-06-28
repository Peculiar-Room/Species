package com.ninni.species.server.entity.mob.update_2;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;

public class SpringlingHead extends PartEntity<Springling> {
    public final Springling parent;
    public final String name;
    private final EntityDimensions size;

    public SpringlingHead(Springling parent, String head, float width, float height) {
        super(parent);
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.parent = parent;
        this.name = head;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        return this.parent.interact(player, interactionHand);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.parent.isVehicle() && this.parent.getExtendedAmount() > 0;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        return this.parent.getPickResult();
    }

    public boolean hurt(DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && this.parent.hurt(source, amount);
    }

    public boolean is(Entity entity) {
        return this == entity || this.parent == entity;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    public boolean shouldBeSaved() {
        return false;
    }

    protected void defineSynchedData() {}
    protected void readAdditionalSaveData(CompoundTag compoundTag) {}
    protected void addAdditionalSaveData(CompoundTag compoundTag) {}
}
