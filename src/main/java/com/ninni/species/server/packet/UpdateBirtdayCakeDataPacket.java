package com.ninni.species.server.packet;

import com.ninni.species.Species;
import com.ninni.species.server.block.entity.BirtdayCakeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class UpdateBirtdayCakeDataPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateBirtdayCakeDataPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "update_birtday_cake_data"));
    public static final StreamCodec<FriendlyByteBuf, UpdateBirtdayCakeDataPacket> STREAM_CODEC = StreamCodec.of(UpdateBirtdayCakeDataPacket::write, UpdateBirtdayCakeDataPacket::read);
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final BlockPos pos;
    private final String name;
    private final int age;

    public UpdateBirtdayCakeDataPacket(BlockPos pos, String name, int age) {
        this.pos = pos;
        this.name = name;
        this.age = age;
    }

    public static void write(FriendlyByteBuf buf, UpdateBirtdayCakeDataPacket packet) {
        buf.writeBlockPos(packet.pos);
        buf.writeUtf(packet.name);
        buf.writeInt(packet.age);
    }

    public static UpdateBirtdayCakeDataPacket read(FriendlyByteBuf buf) {
        return new UpdateBirtdayCakeDataPacket(buf.readBlockPos(), buf.readUtf(), buf.readInt());
    }

    public static void handle(UpdateBirtdayCakeDataPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player sender = ctx.player();
            if (sender == null) return;

            BlockEntity blockEntity = sender.level().getBlockEntity(packet.pos);
            if (blockEntity instanceof BirtdayCakeBlockEntity cake) {
                cake.setPlayerName(packet.name);
                cake.setAge(packet.age);

                blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
            }
        });
        //ctx.get().setPacketHandled(true);
    }
}
