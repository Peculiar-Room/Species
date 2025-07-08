package com.ninni.species.server.packet;

import com.ninni.species.server.block.entity.BirtdayCakeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateBirtdayCakeDataPacket {
    private final BlockPos pos;
    private final String name;
    private final int age;

    public UpdateBirtdayCakeDataPacket(BlockPos pos, String name, int age) {
        this.pos = pos;
        this.name = name;
        this.age = age;
    }

    public static void write(UpdateBirtdayCakeDataPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeUtf(packet.name);
        buf.writeInt(packet.age);
    }

    public static UpdateBirtdayCakeDataPacket read(FriendlyByteBuf buf) {
        return new UpdateBirtdayCakeDataPacket(buf.readBlockPos(), buf.readUtf(), buf.readInt());
    }

    public static void handle(UpdateBirtdayCakeDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;

            BlockEntity blockEntity = sender.level().getBlockEntity(packet.pos);
            if (blockEntity instanceof BirtdayCakeBlockEntity cake) {
                cake.setPlayerName(packet.name);
                cake.setAge(packet.age);

                blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
