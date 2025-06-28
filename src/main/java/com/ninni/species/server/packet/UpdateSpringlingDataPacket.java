package com.ninni.species.server.packet;

import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.server.entity.mob.update_2.Springling;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class UpdateSpringlingDataPacket {
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

    public static void write(UpdateSpringlingDataPacket packet, FriendlyByteBuf buf) {
        buf.writeFloat(packet.change);
        buf.writeBoolean(packet.max);
    }

    public static void handle(UpdateSpringlingDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        float change = packet.change;
        boolean max = packet.max;
        if (max && player != null && player.getVehicle() instanceof Springling springling) {
            springling.setExtendedAmount(springling.getExtendedAmount() + change);
            springling.level().broadcastEntityEvent(springling, (byte) 4);
        }
        ctx.get().setPacketHandled(true);
    }

}
