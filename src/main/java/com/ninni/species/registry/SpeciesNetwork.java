package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.SpeciesClient;
import com.ninni.species.client.inventory.CruncherInventoryMenu;
import com.ninni.species.client.inventory.CruncherInventoryScreen;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.Springling;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SpeciesNetwork {

    public static final ResourceLocation SEND_SPRINGLING_MESSAGE = new ResourceLocation(Species.MOD_ID, "send_springling_message");
    public static final ResourceLocation UPDATE_SPRINGLING_EXTENDED_DATA = new ResourceLocation(Species.MOD_ID, "update_springling_extended_data");
    public static final ResourceLocation OPEN_CRUNCHER_SCREEN = new ResourceLocation(Species.MOD_ID, "open_cruncher_screen");

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        ClientPlayNetworking.registerGlobalReceiver(SEND_SPRINGLING_MESSAGE, (client, handler, buf, responseSender) -> {
            LocalPlayer localPlayer = client.player;
            if (localPlayer != null && localPlayer.getVehicle() instanceof Springling) {
                localPlayer.displayClientMessage(Component.translatable("springling.keybinds", SpeciesClient.extendKey.getTranslatedKeyMessage(), SpeciesClient.retractKey.getTranslatedKeyMessage()), true);
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SpeciesNetwork.OPEN_CRUNCHER_SCREEN, (client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            Level level = client.level;
            Optional.ofNullable(level).ifPresent(world -> {
                Entity entity = world.getEntity(id);
                if (entity instanceof Cruncher cruncher) {
                    int slotCount = buf.readInt();
                    int syncId = buf.readInt();
                    LocalPlayer clientPlayerEntity = client.player;
                    SimpleContainer simpleInventory = new SimpleContainer(slotCount);
                    assert clientPlayerEntity != null;
                    CruncherInventoryMenu cruncherInventoryMenu = new CruncherInventoryMenu(syncId, clientPlayerEntity.getInventory(), simpleInventory, cruncher);
                    clientPlayerEntity.containerMenu = cruncherInventoryMenu;
                    client.execute(() -> client.setScreen(new CruncherInventoryScreen(cruncherInventoryMenu, clientPlayerEntity.getInventory(), cruncher)));
                }
            });
        });
    }

    public static void commonInit() {
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SPRINGLING_EXTENDED_DATA, (server, player, handler, buf, responseSender) -> {
            float change = buf.readFloat();
            boolean flag = buf.readBoolean();
            if (flag && player.getVehicle() instanceof Springling springling) {
                springling.setExtendedAmount(springling.getExtendedAmount() + change);
                springling.level().broadcastEntityEvent(springling, (byte) 4);
            }
        });
    }

}
