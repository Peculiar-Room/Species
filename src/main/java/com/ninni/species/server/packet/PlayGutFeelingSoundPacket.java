package com.ninni.species.server.packet;

import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class PlayGutFeelingSoundPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayGutFeelingSoundPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "play_gut_feeling"));
    public static final StreamCodec<FriendlyByteBuf, PlayGutFeelingSoundPacket> STREAM_CODEC = StreamCodec.of(PlayGutFeelingSoundPacket::write, PlayGutFeelingSoundPacket::read);
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public PlayGutFeelingSoundPacket() {
    }

    public static PlayGutFeelingSoundPacket read(FriendlyByteBuf buf) {
        return new PlayGutFeelingSoundPacket();
    }

    public static void write(FriendlyByteBuf buf, PlayGutFeelingSoundPacket packet) {
    }

    public static void handle(PlayGutFeelingSoundPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            Camera camera = client.gameRenderer.getMainCamera();
            ClientLevel level = client.level;
            double h = camera.getPosition().x;
            double k = camera.getPosition().y;
            double l = camera.getPosition().z;
            if (camera.isInitialized() && level != null) {
                level.playLocalSound(h, k, l, SpeciesSoundEvents.GUT_FEELING_APPLIED.get(), SoundSource.HOSTILE, 2.0f, 1.0f, false);
            }
        });
        //ctx.setPacketHandled(true);
    }
}
