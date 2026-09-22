package com.ninni.species.server.packet;

import com.ninni.species.Species;
import com.ninni.species.mixin_util.LivingEntityAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class SnatchedPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SnatchedPacket> TYPE = new SnatchedPacket.Type<>(
            ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "snatched"));
    public static final StreamCodec<FriendlyByteBuf, SnatchedPacket> STREAM_CODEC = StreamCodec.of(SnatchedPacket::write, SnatchedPacket::read);
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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

    public static void write(FriendlyByteBuf buf, SnatchedPacket packet) {
        buf.writeInt(packet.entityId);
        buf.writeBoolean(packet.flag);
    }

    public static void handle(SnatchedPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
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
        //ctx.get().setPacketHandled(true);
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
