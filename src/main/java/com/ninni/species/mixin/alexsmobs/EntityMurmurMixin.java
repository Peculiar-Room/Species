package com.ninni.species.mixin.alexsmobs;

import com.ninni.species.client.events.WickedMaskDisguiseEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Murmur disguise: suppress {@code EntityMurmur.tick()}'s real-head respawn (it runs in tick(), not a
 *  goal, so goal-stripping can't stop it) and force the body's cosmetic head+neck so it stays attached. */
@Pseudo
@Mixin(targets = "com.github.alexthe666.alexsmobs.entity.EntityMurmur", remap = false)
public abstract class EntityMurmurMixin {

    @Inject(method = "createHead", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void species$suppressDisguiseHead(CallbackInfoReturnable<LivingEntity> cir) {
        if (((Entity) (Object) this).getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) {
            // Non-null (the body itself) so the tick() caller's created.getUUID()/getId() don't NPE.
            // It never resolves as a head (the disguise body isn't in the world; isAngry() is
            // instanceof-guarded), so no EntityMurmurHead is ever spawned for the disguise.
            cir.setReturnValue((LivingEntity) (Object) this);
        }
    }

    @Inject(method = "shouldRenderFakeHead", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void species$alwaysRenderFakeHead(CallbackInfoReturnable<Boolean> cir) {
        if (((Entity) (Object) this).getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) {
            cir.setReturnValue(true);
        }
    }
}
