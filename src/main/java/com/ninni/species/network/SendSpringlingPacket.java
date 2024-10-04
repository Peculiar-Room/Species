package com.ninni.species.network;

import com.ninni.species.entity.Springling;
import com.ninni.species.init.SpeciesKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SendSpringlingPacket {

    public SendSpringlingPacket() {
    }

    public static SendSpringlingPacket read(FriendlyByteBuf buf) {
        return new SendSpringlingPacket();
    }

    public static void write(SendSpringlingPacket packet, FriendlyByteBuf buf) {
    }

    public static void handle(SendSpringlingPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localPlayer = minecraft.player;
            if (localPlayer != null && localPlayer.getVehicle() instanceof Springling) {
                localPlayer.displayClientMessage(Component.translatable("springling.keybinds", SpeciesKeyMappings.EXTEND_KEY.getTranslatedKeyMessage(), SpeciesKeyMappings.RETRACT_KEY.getTranslatedKeyMessage()), true);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
