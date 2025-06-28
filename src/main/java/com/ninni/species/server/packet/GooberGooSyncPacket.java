package com.ninni.species.server.packet;

import com.google.common.collect.BiMap;
import com.ninni.species.Species;
import com.ninni.species.server.data.GooberGooManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GooberGooSyncPacket extends SyncJsonResourcePacket<GooberGooManager.GooberGooData> {

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

    public static void write(GooberGooSyncPacket message, FriendlyByteBuf buf) {
        message.writeMap(buf);
    }

    public static void handle(GooberGooSyncPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                Species.PROXY.getGooberGooManager().synchronizeRegistryForClient(message.registryMap);
            }
        });
    }
}

