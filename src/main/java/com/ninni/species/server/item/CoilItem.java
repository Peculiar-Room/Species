package com.ninni.species.server.item;

import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.mob.update_3.Coil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class CoilItem extends Item {
    public CoilItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(blockPos);
        Block block = state.getBlock();

        boolean knot = block instanceof FenceBlock
                || (block instanceof LightningRodBlock && state.getValue(LightningRodBlock.FACING).getAxis().isVertical())
                || (block instanceof EndRodBlock && state.getValue(EndRodBlock.FACING).getAxis().isVertical())
                || (block instanceof ChainBlock && state.getValue(ChainBlock.AXIS).isVertical());

        Vec3 pos;
        if (knot) {
            pos = Vec3.atBottomCenterOf(blockPos).add(0, 0.25, 0);
        } else {
            Vec3 blockCenter = Vec3.atCenterOf(blockPos);
            Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal());
            Vec3 offset = normal.scale(0.5);
            pos = blockCenter.add(offset).add(0, -0.125, 0);
        }

        int rot = direction == Direction.UP ? -90 : direction == Direction.DOWN ? 90 : 0;
        if (!stack.hasTag()) {
            Coil coil = new Coil(level, true, pos, null, null);
            coil.setKnot(knot);
            coil.setYRot(direction.toYRot());
            coil.setXRot(rot);
            coil.setIsBeingPlaced(true);
            level.addFreshEntity(coil);

            CompoundTag tag = new CompoundTag();
            tag.putUUID("EndPointUUID", coil.getUUID());
            tag.put("EndPointPos", NbtUtils.writeBlockPos(blockPos));
            tag.putInt("CooldownTicks", 400);
            stack.setTag(tag);
        } else {
            UUID endPointUUID = stack.getTag().getUUID("EndPointUUID");
            BlockPos endPointPos = NbtUtils.readBlockPos(stack.getTag().getCompound("EndPointPos"));

            Coil coil = new Coil(level, false, pos, endPointUUID, endPointPos);
            coil.setKnot(knot);
            coil.setYRot(direction.toYRot());
            coil.setXRot(rot);

            Coil startCoil = coil.getEndPoint();
            if (startCoil != null) {
                startCoil.setIsBeingPlaced(false);
                startCoil.setEndPointUUID(coil.getUUID());
                startCoil.setEndPointPos(BlockPos.containing(coil.position()));
                level.addFreshEntity(coil);
                stack.setTag(null);
                stack.shrink(1);
            } else {
                setStart(stack, level, blockPos, knot, direction);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private static void setStart(ItemStack stack, Level level, BlockPos blockPos, boolean isKnot, Direction direction) {
        if (stack.getTag() != null) stack.setTag(null);

        Vec3 pos;
        if (isKnot) {
            pos = Vec3.atBottomCenterOf(blockPos).add(0, 0.25, 0);
        } else {
            Vec3 blockCenter = Vec3.atCenterOf(blockPos);
            Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal());
            Vec3 offset = normal.scale(0.5);
            pos = blockCenter.add(offset).add(0, -0.125, 0);
        }

        Coil coil = new Coil(level, true, pos, null, null);
        coil.setKnot(isKnot);
        coil.setYRot(direction.toYRot());
        coil.setXRot(direction == Direction.UP ? -90 : direction == Direction.DOWN ? 90 : 0);
        coil.setIsBeingPlaced(true);

        CompoundTag tag = new CompoundTag();
        tag.putUUID("EndPointUUID", coil.getUUID());
        tag.put("EndPointPos", NbtUtils.writeBlockPos(blockPos));
        tag.putInt("CooldownTicks", 400);

        stack.setTag(tag);
        level.addFreshEntity(coil);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && stack.hasTag() && stack.getTag().contains("CooldownTicks")) {
            CompoundTag tag = stack.getTag();
            int ticks = tag.getInt("CooldownTicks");
            if (ticks > 0) tag.putInt("CooldownTicks", ticks - 1);
            else {
                level.playSound(null, entity.blockPosition(), SpeciesSoundEvents.COIL_REMOVE.get(), SoundSource.BLOCKS);
                stack.setTag(null);
            }
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("CooldownTicks");
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (!isBarVisible(stack)) return 0;

        int ticks = stack.getTag().getInt("CooldownTicks");
        float progress = Math.min(1.0f, ticks / 400.0f);
        return Math.round(13 * progress);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x1457d9;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
