package com.ninni.species.mixin.client.alexscaves;

import com.ninni.species.client.events.WickedMaskDisguiseEvents;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Soft-dep mixin: cancels {@code BoundroidEntity.tick} on disguise-tagged bodies (its Winch-spawn block
 * would duplicate {@link com.ninni.species.server.disguise.BoundroidPairManager}'s) and syncs only animation state.
 */
@Pseudo
@Mixin(targets = "com.github.alexmodguy.alexscaves.server.entity.living.BoundroidEntity", remap = false)
public abstract class BoundroidEntityMixin {

    /** {@code groundProgress} drives the float/stand pose blend; must be updated manually since the full tick is cancelled. */
    @Shadow private float groundProgress;
    @Shadow private float prevGroundProgress;

    // remap=false: AlexsCaves isn't on the compile classpath, so the annotation
    // processor can't resolve tick()'s inheritance; both runtime names are listed
    // so whichever the loaded class uses matches without a refmap.
    @Inject(
        method = {"tick", "m_8119_"},
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void species$skipTickForDisguise(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) {
            // Replicate only animation-state lines from BoundroidEntity.tick. Winch creation,
            // slamming, target tracking skipped: Winch spawn duplicates BoundroidPairManager's
            // Winch; disguise body has no AI targets.
            this.prevGroundProgress = this.groundProgress;
            if (self.onGround() && this.groundProgress < 5F) {
                this.groundProgress++;
            }
            if (!self.onGround() && this.groundProgress > 0F) {
                this.groundProgress--;
            }
            ci.cancel();
        }
    }
}
