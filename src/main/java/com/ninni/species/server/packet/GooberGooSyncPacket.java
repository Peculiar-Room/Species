package com.ninni.species.server.packet;

import com.google.common.collect.BiMap;
import com.ninni.species.Species;
import com.ninni.species.server.data.GooberGooManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class GooberGooSyncPacket extends SyncJsonResourcePacket<GooberGooManager.GooberGooData> {

    public static final CustomPacketPayload.Type<GooberGooSyncPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "goober_goo"));
    public static final StreamCodec<FriendlyByteBuf, GooberGooSyncPacket> STREAM_CODEC = StreamCodec.of(GooberGooSyncPacket::write, GooberGooSyncPacket::read);
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public GooberGooSyncPacket(BiMap<ResourceLocation, GooberGooManager.GooberGooData> registryMap) {
        super(registryMap);
    }

    public GooberGooSyncPacket() {}

    @Override
    protected GooberGooManager.GooberGooData readJsonObject(FriendlyByteBuf buf) {
        return GooberGooManager.GooberGooData.fromNetwork(buf);
    }

    @Override
    protected void writeJsonObject(FriendlyByteBuf buf, GooberGooManager.GooberGooData toWrite) {
        toWrite.toNetwork(buf);
    }

    public static GooberGooSyncPacket read(FriendlyByteBuf buf) {
        GooberGooSyncPacket message = new GooberGooSyncPacket();
        message.readMap(buf);
        return message;
    }

    public static void write(FriendlyByteBuf buf, GooberGooSyncPacket message) {
        message.writeMap(buf);
    }

    public static void handle(GooberGooSyncPacket message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                Species.PROXY.getGooberGooManager().synchronizeRegistryForClient(message.registryMap);
            }
        });
    }
}

