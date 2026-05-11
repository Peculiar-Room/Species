package com.ninni.species.mixin.cataclysm;

import com.ninni.species.server.disguise.DisguiseBodyRegistry;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Forces progressive {@code Crackiness} for Ignis disguise bodies — natural {@code getCrackiness}
 *  reads from HP fraction, but {@code LivingEntityMixin} resets disguise HP to max every tick so
 *  cracks would never appear. Maps BOSS_PHASE → {@code LOW}/{@code MEDIUM}/{@code HIGH}. */
@Pseudo
@Mixin(targets = "com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignis_Entity", remap = false)
public abstract class IgnisCrackinessMixin {

    @Inject(method = "getCrackiness", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void species$forceCracksForDisguise(CallbackInfoReturnable<Object> cir) {
        Entity self = (Entity) (Object) this;
        if (!DisguiseBodyRegistry.isDisguiseBody(self)) return;
        // Map phase → crackiness enum via reflection (no compile dep on Cataclysm's enum).
        try {
            int phase = (int) self.getClass().getMethod("getBossPhase").invoke(self);
            if (phase <= 0) return; // gate matches the Crack_Layer's `getBossPhase() > 0` check
            String constant = phase >= 2 ? "HIGH" : "LOW";
            Class<?> crackinessCls = Class.forName(
                    "com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignis_Entity$Crackiness",
                    false, self.getClass().getClassLoader());
            Object value = crackinessCls.getField(constant).get(null);
            cir.setReturnValue(value);
        } catch (ReflectiveOperationException ignored) {}
    }
}
