package com.ninni.species.server.packet;

import com.google.common.collect.BiMap;
import com.ninni.species.Species;
import com.ninni.species.server.data.CruncherPelletManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CruncherPelletSyncPacket extends SyncJsonResourcePacket<CruncherPelletManager.CruncherPelletData> {

    public static final CustomPacketPayload.Type<CruncherPelletSyncPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "cruncher_pellet_sync"));
    public static final StreamCodec<FriendlyByteBuf, CruncherPelletSyncPacket> STREAM_CODEC = StreamCodec.of(CruncherPelletSyncPacket::write, CruncherPelletSyncPacket::read);
    @Override
    public @NotNull Type<CruncherPelletSyncPacket> type() {
        return TYPE;
    }

    public CruncherPelletSyncPacket(BiMap<ResourceLocation, CruncherPelletManager.CruncherPelletData> registryMap) {
        super(registryMap);
    }

    public CruncherPelletSyncPacket() {}

    @Override
    protected CruncherPelletManager.CruncherPelletData readJsonObject(FriendlyByteBuf buf) {
        return CruncherPelletManager.CruncherPelletData.fromNetwork(buf);
    }

    @Override
    protected void writeJsonObject(FriendlyByteBuf buf, CruncherPelletManager.CruncherPelletData toWrite) {
        toWrite.toNetwork(buf);
    }

    public static CruncherPelletSyncPacket read(FriendlyByteBuf buf) {
        CruncherPelletSyncPacket message = new CruncherPelletSyncPacket();
        message.readMap(buf);
        return message;
    }

    public static void write(FriendlyByteBuf buf, CruncherPelletSyncPacket message) {
        message.writeMap(buf);
    }

    public static void handle(CruncherPelletSyncPacket message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                Species.PROXY.getCruncherPelletManager().synchronizeRegistryForClient(message.registryMap);
            }
        });
    }
}

