package com.ninni.species.server.packet;

import com.ninni.species.Species;
import com.ninni.species.server.entity.mob.update_2.Springling;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class UpdateSpringlingDataPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateSpringlingDataPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "update_springling_data"));
    public static final StreamCodec<FriendlyByteBuf, UpdateSpringlingDataPacket> STREAM_CODEC = StreamCodec.of(UpdateSpringlingDataPacket::write, UpdateSpringlingDataPacket::read);
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final float change;
    private final boolean max;

    public UpdateSpringlingDataPacket(float change, boolean max) {
        this.change = change;
        this.max = max;
    }

    public static UpdateSpringlingDataPacket read(FriendlyByteBuf buf) {
        float change = buf.readFloat();
        boolean max = buf.readBoolean();
        return new UpdateSpringlingDataPacket(change, max);
    }

    public static void write(FriendlyByteBuf buf, UpdateSpringlingDataPacket packet) {
        buf.writeFloat(packet.change);
        buf.writeBoolean(packet.max);
    }

    public static void handle(UpdateSpringlingDataPacket packet, IPayloadContext ctx) {
        Player player = ctx.player();
        float change = packet.change;
        boolean max = packet.max;
        if (max && player != null && player.getVehicle() instanceof Springling springling) {
            springling.setExtendedAmount(springling.getExtendedAmount() + change);
            springling.level().broadcastEntityEvent(springling, (byte) 4);
        }
        //ctx.get().setPacketHandled(true);
    }

}
