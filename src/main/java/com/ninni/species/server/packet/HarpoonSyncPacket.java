package com.ninni.species.server.packet;

import com.ninni.species.Species;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HarpoonSyncPacket {
    private final int harpoonId;

    public HarpoonSyncPacket(int harpoonId) {
        this.harpoonId = harpoonId;
    }

    public static void write(HarpoonSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.harpoonId);
    }

    public static HarpoonSyncPacket read(FriendlyByteBuf buf) {
        return new HarpoonSyncPacket(buf.readInt());
    }

    public static void handle(HarpoonSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Species.PROXY.harpoonSync(msg.harpoonId));
        ctx.get().setPacketHandled(true);
    }
}
