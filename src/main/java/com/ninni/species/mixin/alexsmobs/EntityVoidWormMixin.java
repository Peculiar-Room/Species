package com.ninni.species.mixin.alexsmobs;

import com.ninni.species.client.events.WickedMaskDisguiseEvents;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Suppresses {@code pushEntities} and {@code launch} on disguise-tagged bodies — both apply
 *  {@code entity.push(...)} directly, bypassing Forge events. Soft-dep, fail silent. */
@Pseudo
@Mixin(targets = "com.github.alexthe666.alexsmobs.entity.EntityVoidWorm", remap = false)
public abstract class EntityVoidWormMixin {

    @Inject(method = "m_6138_", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void species$cancelPushForDisguise(CallbackInfo ci) {
        if (((Entity) (Object) this).getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) {
            ci.cancel();
        }
    }

    @Inject(method = "launch", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void species$cancelLaunchForDisguise(Entity e, boolean huge, CallbackInfo ci) {
        if (((Entity) (Object) this).getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) {
            ci.cancel();
        }
    }
}
