package com.ninni.species.network;

import com.ninni.species.client.ClientEventsHandler;
import com.ninni.species.client.inventory.CruncherInventoryMenu;
import com.ninni.species.client.inventory.CruncherInventoryScreen;
import com.ninni.species.entity.Cruncher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class OpenCruncherScreenPacket {
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

    public static void write(OpenCruncherScreenPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.id);
        buf.writeInt(packet.slotCount);
        buf.writeInt(packet.syncId);
    }

    public static void handle(OpenCruncherScreenPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientEventsHandler.openCruncherScreen(packet));
        ctx.get().setPacketHandled(true);
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
