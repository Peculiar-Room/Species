package com.ninni.species.mixin.client;

import com.ninni.species.client.events.WickedMaskDisguiseEvents;
import com.ninni.species.mixin_util.LivingEntityAccess;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.server.disguise.panacea.DisguiseTopology;
import com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Scales third/second-person camera distance to fit large Wicked Mask disguises. */
@OnlyIn(Dist.CLIENT)
@Mixin(Camera.class)
public abstract class CameraMixin {

    @ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"))
    private double species$adjustZoomForDisguise(double original) {
        Camera self = (Camera) (Object) this;
        Entity entity = self.getEntity();
        if (!(entity instanceof LivingEntity wearer)) return original;
        if (!wearer.getItemBySlot(EquipmentSlot.HEAD).is(SpeciesItems.WICKED_MASK.get())) return original;

        LivingEntity disguise = ((LivingEntityAccess) wearer).getDisguisedEntity();
        if (disguise == null || !disguise.getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) return original;

        // Intrinsic AABB scaled by per-type factor (wings/sweep extending beyond bbox), with a
        // per-type minimum floor for streaming chains. AABB updates dynamically (IaF stage,
        // armor stand, etc.) so size tracks the current disguise state.
        double intrinsic = Math.max(disguise.getBbWidth(), disguise.getBbHeight())
                * DisguiseTopologyRegistry.getCameraSizeFactor(disguise);
        double pinned = DisguiseTopologyRegistry.getCameraSizeMinimum(disguise);
        double visualSize = Math.max(intrinsic, pinned);
        if (visualSize <= DisguiseTopology.PLAYER_BASELINE) return original;

        // Linear scale (+1.5 per block of silhouette), capped at 48.
        double scaled = original + (visualSize - DisguiseTopology.PLAYER_BASELINE) * 1.5;
        return Math.min(scaled, 48.0);
    }
}
