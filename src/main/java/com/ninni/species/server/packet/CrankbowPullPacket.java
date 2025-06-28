package com.ninni.species.server.packet;

import com.ninni.species.client.events.ClientEventsHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CrankbowPullPacket {
    private final UUID playerId;
    private final float progress;

    public CrankbowPullPacket(UUID playerId, float progress) {
        this.playerId = playerId;
        this.progress = progress;
    }

    public static void write(CrankbowPullPacket object, FriendlyByteBuf buffer) {
        buffer.writeUUID(object.playerId);
        buffer.writeFloat(object.progress);
    }

    public static CrankbowPullPacket read(FriendlyByteBuf buffer) {
        return new CrankbowPullPacket(buffer.readUUID(), buffer.readFloat());
    }


    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> ClientEventsHandler.PULL_PROGRESS.put(playerId, progress));
        contextSupplier.get().setPacketHandled(true);
    }
}
