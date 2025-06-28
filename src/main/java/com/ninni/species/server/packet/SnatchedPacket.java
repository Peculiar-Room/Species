package com.ninni.species.server.packet;

import com.ninni.species.server.entity.util.LivingEntityAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class SnatchedPacket {
    private final int entityId;
    private final boolean flag;

    public SnatchedPacket(int entityId, boolean flag) {
        this.entityId = entityId;
        this.flag = flag;
    }

    public static SnatchedPacket read(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        boolean flag = buf.readBoolean();
        return new SnatchedPacket(entityId, flag);
    }

    public static void write(SnatchedPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeBoolean(packet.flag);
    }

    public static void handle(SnatchedPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            Optional.ofNullable(minecraft.level).ifPresent(world -> {
                int id = packet.getEntityId();
                Optional.ofNullable(minecraft.level.getEntity(id))
                        .filter(LivingEntity.class::isInstance)
                        .map(LivingEntityAccess.class::cast)
                        .ifPresent(entity -> {
                            boolean snatched = packet.getFlag();
                            entity.setSnatched(snatched);
                        });
            });
        });
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getFlag() {
        return this.flag;
    }

}
