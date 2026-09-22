package com.ninni.species.server.packet;

import com.ninni.species.Species;
import com.ninni.species.server.entity.mob.update_3.Harpoon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class HarpoonInputPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HarpoonInputPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "harpoon_input"));
    public static final StreamCodec<FriendlyByteBuf, HarpoonInputPacket> STREAM_CODEC = StreamCodec.of(HarpoonInputPacket::write, HarpoonInputPacket::read);
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final int harpoonId;
    private final float xInput, yInput, zInput;

    public HarpoonInputPacket(int harpoonId, float xInput, float yInput, float zInput) {
        this.harpoonId = harpoonId;
        this.xInput = xInput;
        this.yInput = yInput;
        this.zInput = zInput;
    }

    public static void write(FriendlyByteBuf buf, HarpoonInputPacket msg) {
        buf.writeInt(msg.harpoonId);
        buf.writeFloat(msg.xInput);
        buf.writeFloat(msg.yInput);
        buf.writeFloat(msg.zInput);
    }

    public static HarpoonInputPacket read(FriendlyByteBuf buf) {
        return new HarpoonInputPacket(buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(HarpoonInputPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player == null) return;
            Entity harpoonEntity = player.level().getEntity(msg.harpoonId);

            if (harpoonEntity instanceof Harpoon harpoon && harpoon.getOwner() == player && harpoon.isAnchored()) {
                harpoon.setSwingInput(msg.xInput, msg.yInput, msg.zInput);
            }
        });
        //ctx.get().setPacketHandled(true);
    }
}