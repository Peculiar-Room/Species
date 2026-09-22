package com.ninni.species.server.events;

import com.ninni.species.Species;
import com.ninni.species.server.entity.mob.update_2.Springling;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Species.MOD_ID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        Species.PROXY.getCruncherPelletManager().onDatapackSync(event.getPlayer());
        Species.PROXY.getGooberGooManager().onDatapackSync(event.getPlayer());
    }


    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntity().getVehicle() instanceof Springling) {
            event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
        }
    }
}
