package com.ninni.species.server.packet;

import com.ninni.species.server.entity.mob.update_3.Harpoon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HarpoonInputPacket {
    private final int harpoonId;
    private final float xInput, yInput, zInput;

    public HarpoonInputPacket(int harpoonId, float xInput, float yInput, float zInput) {
        this.harpoonId = harpoonId;
        this.xInput = xInput;
        this.yInput = yInput;
        this.zInput = zInput;
    }

    public static void write(HarpoonInputPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.harpoonId);
        buf.writeFloat(msg.xInput);
        buf.writeFloat(msg.yInput);
        buf.writeFloat(msg.zInput);
    }

    public static HarpoonInputPacket read(FriendlyByteBuf buf) {
        return new HarpoonInputPacket(buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(HarpoonInputPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player == null) return;
            Entity harpoonEntity = player.level().getEntity(msg.harpoonId);

            if (harpoonEntity instanceof Harpoon harpoon && harpoon.getOwner() == player && harpoon.isAnchored()) {
                harpoon.setSwingInput(msg.xInput, msg.yInput, msg.zInput);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}