package com.ninni.species.server.packet;

import com.ninni.species.Species;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BlockEntitySyncPacket {
    final BlockPos pos;
    final CompoundTag tag;

    public BlockEntitySyncPacket(BlockPos pos, CompoundTag tag) {
        this.pos = pos;
        this.tag = tag;
    }

    public static void write(BlockEntitySyncPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
        buffer.writeNbt(object.tag);
    }

    public static BlockEntitySyncPacket read(FriendlyByteBuf buffer) {
        return new BlockEntitySyncPacket(buffer.readBlockPos(), buffer.readNbt());
    }

    public static void handle(BlockEntitySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            ServerPlayer sender = ctx.get().getSender();
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
                world = Species.PROXY.getWorld();
            else {
                if (sender == null) return;
                world = sender.level();
            }

            BlockEntity t = world.getBlockEntity(packet.pos);
            if (t != null) {
                t.load(packet.tag);
                t.setChanged();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}