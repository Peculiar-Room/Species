package com.ninni.species.server.packet;

import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesKeyMappings;
import com.ninni.species.server.entity.mob.update_2.Springling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SendSpringlingPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SendSpringlingPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "send_springling"));
    public static final StreamCodec<FriendlyByteBuf, SendSpringlingPacket> STREAM_CODEC = StreamCodec.of(SendSpringlingPacket::write, SendSpringlingPacket::read);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public SendSpringlingPacket() {
    }

    public static SendSpringlingPacket read(FriendlyByteBuf buf) {
        return new SendSpringlingPacket();
    }

    public static void write(FriendlyByteBuf buf, SendSpringlingPacket packet) {
    }

    public static void handle(SendSpringlingPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localPlayer = minecraft.player;
            if (localPlayer != null && localPlayer.getVehicle() instanceof Springling) {
                localPlayer.displayClientMessage(Component.translatable("springling.keybinds", SpeciesKeyMappings.EXTEND_KEY.getTranslatedKeyMessage(), SpeciesKeyMappings.RETRACT_KEY.getTranslatedKeyMessage()), true);
            }
        });
        //ctx.get().setPacketHandled(true);
    }
}
