package com.ninni.species.network;

import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayGutFeelingSoundPacket {

    public PlayGutFeelingSoundPacket() {
    }

    public static PlayGutFeelingSoundPacket read(FriendlyByteBuf buf) {
        return new PlayGutFeelingSoundPacket();
    }

    public static void write(PlayGutFeelingSoundPacket packet, FriendlyByteBuf buf) {
    }

    public static void handle(PlayGutFeelingSoundPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
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
        ctx.get().setPacketHandled(true);
    }
}
