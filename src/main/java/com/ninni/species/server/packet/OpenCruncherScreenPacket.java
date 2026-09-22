package com.ninni.species.server.packet;

import com.ninni.species.Species;
import com.ninni.species.client.events.ClientEventsHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OpenCruncherScreenPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenCruncherScreenPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "open_cruncher_screen"));
    public static final StreamCodec<FriendlyByteBuf, OpenCruncherScreenPacket> STREAM_CODEC = StreamCodec.of(OpenCruncherScreenPacket::write, OpenCruncherScreenPacket::read);
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final int id;
    private final int slotCount;
    private final int syncId;

    public OpenCruncherScreenPacket(int id, int slotCount, int syncId) {
        this.id = id;
        this.slotCount = slotCount;
        this.syncId = syncId;
    }

    public static OpenCruncherScreenPacket read(FriendlyByteBuf buf) {
        int id = buf.readInt();
        int slotCount = buf.readInt();
        int syncId = buf.readInt();
        return new OpenCruncherScreenPacket(id, slotCount, syncId);
    }

    public static void write(FriendlyByteBuf buf, OpenCruncherScreenPacket packet) {
        buf.writeInt(packet.id);
        buf.writeInt(packet.slotCount);
        buf.writeInt(packet.syncId);
    }

    public static void handle(OpenCruncherScreenPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientEventsHandler.openCruncherScreen(packet));
        //ctx.get().setPacketHandled(true);
    }

    public int getId() {
        return this.id;
    }

    public int getSlotCount() {
        return this.slotCount;
    }

    public int getSyncId() {
        return this.syncId;
    }
}
