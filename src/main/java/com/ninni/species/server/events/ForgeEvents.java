package com.ninni.species.server.events;

import com.ninni.species.Species;
import com.ninni.species.server.entity.mob.update_2.Springling;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
