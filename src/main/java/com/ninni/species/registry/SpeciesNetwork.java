package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.SpeciesClient;
import com.ninni.species.entity.Springling;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SpeciesNetwork {

    public static final ResourceLocation SEND_SPRINGLING_MESSAGE = new ResourceLocation(Species.MOD_ID, "send_springling_message");

    @Environment(EnvType.CLIENT)
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SEND_SPRINGLING_MESSAGE, (client, handler, buf, responseSender) -> {
            LocalPlayer localPlayer = client.player;
            if (localPlayer != null && localPlayer.getVehicle() instanceof Springling) {
                localPlayer.displayClientMessage(Component.translatable("springling.keybinds", SpeciesClient.extendKey.getTranslatedKeyMessage(), SpeciesClient.retractKey.getTranslatedKeyMessage()), true);
            }
        });
    }

}
