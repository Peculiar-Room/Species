package com.ninni.species.server.packet;

import com.google.common.collect.BiMap;
import com.ninni.species.Species;
import com.ninni.species.server.data.CruncherPelletManager;
import com.ninni.species.server.data.GooberGooManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CruncherPelletSyncPacket extends SyncJsonResourcePacket<CruncherPelletManager.CruncherPelletData> {

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

    public static void write(CruncherPelletSyncPacket message, FriendlyByteBuf buf) {
        message.writeMap(buf);
    }

    public static void handle(CruncherPelletSyncPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                Species.PROXY.getCruncherPelletManager().synchronizeRegistryForClient(message.registryMap);
            }
        });
    }
}

