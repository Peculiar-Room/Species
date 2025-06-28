package com.ninni.species.server.entity.mob.update_2;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;

public class TreeperCanopy extends PartEntity<Treeper> {
    public final Treeper parent;
    public final String name;
    private final EntityDimensions size;
    private float xOffset,yOffset,zOffset;

    public TreeperCanopy(Treeper parent, String head, float width, float height, float xOffset, float yOffset, float zOffset) {
        super(parent);
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.parent = parent;
        this.name = head;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public float getzOffset() {
        return zOffset;
    }


    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.parent.isPlanted() && tryPlaceBlock(player, hand)) {
            return InteractionResult.SUCCESS;
        }
        return this.parent.interact(player, hand);
    }

    private boolean tryPlaceBlock(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 end = eyePos.add(lookVec.scale(5));

        AABB aabb = this.getBoundingBox();
        Vec3 hitVec = aabb.clip(eyePos, end).orElse(null);
        if (hitVec == null) return false;

        Direction face = getHitDirection(hitVec, aabb);
        if (face == null) return false;

        BlockPos pos = BlockPos.containing(hitVec);
        if (face != Direction.NORTH && face != Direction.UP && face != Direction.WEST) pos = pos.relative(face);

        Level level = player.level();

        if (!player.mayUseItemAt(pos, face, stack)) return false;
        if (!level.mayInteract(player, pos)) return false;
        if (!level.getBlockState(pos).canBeReplaced()) return false;

        BlockPlaceContext context = new BlockPlaceContext(player, hand, stack,
                new BlockHitResult(hitVec, face, pos, false));


        if (!level.isClientSide) {
            InteractionResult result = blockItem.place(context);

            if (result.consumesAction()) {
                BlockState placed = level.getBlockState(pos);
                SoundType sound = placed.getSoundType();
                level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

                return true;
            }
        }
        return false;
    }



    private Direction getHitDirection(Vec3 hit, AABB box) {
        double dx = hit.x - box.minX;
        double dy = hit.y - box.minY;
        double dz = hit.z - box.minZ;

        double maxDx = box.maxX - box.minX;
        double maxDy = box.maxY - box.minY;
        double maxDz = box.maxZ - box.minZ;

        double epsilon = 1e-4;

        if (Math.abs(dx) < epsilon) return Direction.WEST;
        if (Math.abs(dx - maxDx) < epsilon) return Direction.EAST;
        if (Math.abs(dy) < epsilon) return Direction.DOWN;
        if (Math.abs(dy - maxDy) < epsilon) return Direction.UP;
        if (Math.abs(dz) < epsilon) return Direction.NORTH;
        if (Math.abs(dz - maxDz) < epsilon) return Direction.SOUTH;

        return null;
    }


    @Override
    public boolean canBeCollidedWith() {
        return this.parent.isPlanted();
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
    protected void readAdditionalSaveData(CompoundTag p_20052_) {}
    protected void addAdditionalSaveData(CompoundTag p_20139_) {}
}
