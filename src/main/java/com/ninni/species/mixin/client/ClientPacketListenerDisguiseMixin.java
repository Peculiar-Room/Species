package com.ninni.species.mixin.client;

import com.ninni.species.mixin_util.LivingEntityAccess;
import com.ninni.species.server.disguise.DisguiseLifecycleTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Snapshots the disguise body at {@code handleRespawn} HEAD — before the client swaps the level on a
 *  dimension change (when {@code EntityLeaveLevelEvent} never fires) — so the rebuilt body resumes state. */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerDisguiseMixin {

    @Inject(method = "handleRespawn", at = @At("HEAD"), require = 0)
    private void species$snapshotDisguiseBeforeRespawn(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        // Only act on the render thread (handleRespawn first runs on the network thread); there
        // mc.player is still the OLD player holding the live disguise body.
        if (mc == null || !mc.isSameThread()) return;
        LocalPlayer player = mc.player;
        if (player == null) return;
        try {
            LivingEntity body = ((LivingEntityAccess) player).getDisguisedEntity();
            if (body != null) {
                DisguiseLifecycleTracker.snapshot(player, body);
            }
        } catch (Throwable ignored) {}
    }
}
