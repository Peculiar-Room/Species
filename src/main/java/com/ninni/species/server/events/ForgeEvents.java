package com.ninni.species.server.events;

import com.ninni.species.Species;
import com.ninni.species.mixin_util.LivingEntityAccess;
import com.ninni.species.server.disguise.BoundroidPairManager;
import com.ninni.species.server.disguise.DisguiseBodyRegistry;
import com.ninni.species.server.disguise.DisguiseLifecycleTracker;
import com.ninni.species.server.disguise.MineGuardianAnchorManager;
import com.ninni.species.server.entity.mob.update_2.Springling;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
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

    /** Cleans up disguise state on level-leave (dimension change, logout, despawn) and
     *  unregisters from {@link DisguiseBodyRegistry}; without this, static maps grow unbounded. */
    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity wearer) {
            try {
                LivingEntity disguise = ((LivingEntityAccess) wearer).getDisguisedEntity();
                if (disguise != null) {
                    // Snapshot the body so a rebuild (dimension change) resumes its runtime state
                    // instead of resetting to the mask's capture-time NBT.
                    DisguiseLifecycleTracker.snapshot(wearer, disguise);
                    DisguiseBodyRegistry.unregister(disguise);
                }
            } catch (Throwable ignored) {}
            // Cleanup all registered chain managers (Gum Worm, Centipede, Anaconda, etc.).
            try {
                com.ninni.species.server.disguise.panacea.SegmentChainManager.cleanupAll(wearer);
            } catch (Throwable ignored) {}
            try {
                BoundroidPairManager.removeCompanion(wearer);
            } catch (Throwable ignored) {}
            try {
                MineGuardianAnchorManager.removeAnchor(wearer);
            } catch (Throwable ignored) {}
        }
    }
}
