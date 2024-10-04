package com.ninni.species.client;

import com.ninni.species.client.inventory.CruncherInventoryMenu;
import com.ninni.species.client.inventory.CruncherInventoryScreen;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.network.OpenCruncherScreenPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ClientEventsHandler {

    public static void openCruncherScreen(OpenCruncherScreenPacket packet) {
        Minecraft client = Minecraft.getInstance();
        Level level = client.level;
        Optional.ofNullable(level).ifPresent(world -> {
            Entity entity = world.getEntity(packet.getId());
            if (entity instanceof Cruncher cruncher) {
                int slotCount = packet.getSlotCount();
                int syncId = packet.getSyncId();
                LocalPlayer clientPlayerEntity = client.player;
                SimpleContainer simpleInventory = new SimpleContainer(slotCount);
                assert clientPlayerEntity != null;
                CruncherInventoryMenu cruncherInventoryMenu = new CruncherInventoryMenu(syncId, clientPlayerEntity.getInventory(), simpleInventory, cruncher);
                clientPlayerEntity.containerMenu = cruncherInventoryMenu;
                client.execute(() -> client.setScreen(new CruncherInventoryScreen(cruncherInventoryMenu, clientPlayerEntity.getInventory(), cruncher)));
            }
        });
    }

}
