package com.ninni.species.mixin.iceandfire;

import com.ninni.species.client.events.WickedMaskDisguiseEvents;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Cancels {@code roar()} on disguise-tagged dragons — applies {@code MobEffects.WEAKNESS}
 *  to nearby entities directly via {@code addEffect}, bypassing Forge events. Soft-dep. */
@Pseudo
@Mixin(targets = {
        "com.github.alexthe666.iceandfire.entity.EntityDragonBase", // stock IaF
        "com.iafenvoy.iceandfire.entity.DragonBaseEntity"           // IaF-CE
}, remap = false)
public abstract class EntityDragonBaseMixin {

    @Inject(method = "roar", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void species$cancelRoarForDisguise(CallbackInfo ci) {
        if (((Entity) (Object) this).getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) {
            ci.cancel();
        }
    }
}
