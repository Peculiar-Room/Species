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
 * Soft-dep mixin: cancels {@code MineGuardianEntity.tick} on disguise-tagged bodies (its anchor-spawn block
 * would create a ghost anchor every tick) and replicates only the animation-progress lines.
 */
@Pseudo
@Mixin(targets = "com.github.alexmodguy.alexscaves.server.entity.living.MineGuardianEntity", remap = false)
public abstract class MineGuardianEntityMixin {

    @Shadow private float prevExplodeProgress;
    @Shadow private float prevScanProgress;
    @Shadow private float scanProgress;
    @Shadow private float explodeProgress;

    @Shadow public abstract boolean isScanning();
    @Shadow public abstract boolean isExploding();

    @Inject(
        method = {"tick", "m_8119_"},
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void species$skipTickForDisguise(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) {
            // Replicate only animation-state lines from MineGuardianEntity.tick; anchor
            // spawning, explosion logic, scan-target tracking intentionally skipped.
            this.prevExplodeProgress = this.explodeProgress;
            this.prevScanProgress = this.scanProgress;
            try {
                if (this.isScanning() && this.scanProgress < 5F) this.scanProgress++;
                if (!this.isScanning() && this.scanProgress > 0F) this.scanProgress--;
                if (this.isExploding() && this.explodeProgress < 10F) this.explodeProgress += 0.5F;
                if (!this.isExploding() && this.explodeProgress > 0F) this.explodeProgress -= 0.5F;
            } catch (Throwable ignored) {
                // isScanning/isExploding are abstract AC calls; let progress
                // freeze rather than crash if data isn't initialised as AC expects.
            }
            ci.cancel();
        }
    }
}
