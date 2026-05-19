package com.ninni.species.mixin.client;

import com.ninni.species.server.disguise.DisguiseBodyRegistry;
import com.ninni.species.server.disguise.DisguiseTickContext;
import com.ninni.species.server.disguise.panacea.SegmentChainManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Resolves disguise bodies + segments via {@code getEntity(int)} fallback, and suppresses
 *  {@code disguise.tick()} particles for the first-person wearer. */
@OnlyIn(Dist.CLIENT)
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "getEntity(I)Lnet/minecraft/world/entity/Entity;", at = @At("RETURN"), cancellable = true)
    private void species$disguiseBodyLookupFallback(int id, CallbackInfoReturnable<Entity> cir) {
        if (cir.getReturnValue() != null) return;
        LivingEntity disguise = DisguiseBodyRegistry.findById(id);
        if (disguise != null) { cir.setReturnValue(disguise); return; }
        // Chain segments aren't in the ClientLevel; resolve via the chain manager.
        Entity segment = SegmentChainManager.findById(id);
        if (segment != null) cir.setReturnValue(segment);
    }

    @Inject(
            method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void species$suppressDisguiseParticleInFirstPerson(
            ParticleOptions options, boolean force, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed,
            CallbackInfo ci) {
        if (DisguiseTickContext.isSuppressing()) ci.cancel();
    }

    @Inject(
            method = "addAlwaysVisibleParticle(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void species$suppressDisguiseAlwaysVisibleParticleInFirstPerson(
            ParticleOptions options, boolean force, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed,
            CallbackInfo ci) {
        if (DisguiseTickContext.isSuppressing()) ci.cancel();
    }
}
