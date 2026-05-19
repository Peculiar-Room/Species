package com.ninni.species.mixin.spawn;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.ninni.species.client.events.WickedMaskDisguiseEvents;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

/** Skip {@code setDancing(false)} reset for disguise-tagged Stickbugs. Soft-dep. */
@Pseudo
@Mixin(targets = "com.ninni.spawn.server.entity.mob.Stickbug", remap = false)
public abstract class StickbugMixin {

    @WrapWithCondition(
            method = "m_8119_",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/ninni/spawn/server/entity/mob/Stickbug;setDancing(Z)V",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private boolean species$skipDanceResetForDisguise(@Coerce Object self, boolean value) {
        if (self instanceof LivingEntity stickbug && stickbug.getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) {
            return value;
        }
        return true;
    }
}
