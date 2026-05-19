package com.ninni.species.mixin.client.spawn;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.client.events.WickedMaskDisguiseEvents;
import com.ninni.species.server.disguise.behaviors.SpawnFirekeeperSpecialActionBehavior;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Soft-dep: overrides Spawn's molten-overlay alpha with the disguise's global glow (Spawn zeroes it each
 *  tick when dormant, so a multiply can't recover it). Gated by the disguise tag; real Firekeepers untouched. */
@Pseudo
@Mixin(targets = "com.ninni.spawn.client.renderer.entity.layer.FirekeeperMoltenLayer", remap = false)
public abstract class FirekeeperMoltenLayerMixin {

    /** Captured at render HEAD for the {@code @ModifyArg} below ({@code @Local} won't bind the
     *  Firekeeper-typed arg to a LivingEntity param). */
    private static final ThreadLocal<LivingEntity> SPECIES$CURRENT_ENTITY = new ThreadLocal<>();

    /** {@code @Coerce LivingEntity} takes the Firekeeper-typed arg with no compile-time Spawn reference;
     *  all render() args declared since Mixin needs full capture or none. */
    @Inject(method = "render", at = @At("HEAD"), remap = false, require = 0)
    private void species$captureEntity(PoseStack pose, MultiBufferSource buf, int packedLight,
                                       @Coerce LivingEntity entity,
                                       float limbSwing, float limbSwingAmount, float partialTick,
                                       float ageInTicks, float netHeadYaw, float headPitch,
                                       CallbackInfo ci) {
        SPECIES$CURRENT_ENTITY.set(entity);
    }

    @Inject(method = "render", at = @At("RETURN"), remap = false, require = 0)
    private void species$clearEntity(CallbackInfo ci) {
        SPECIES$CURRENT_ENTITY.remove();
    }

    @ModifyArg(
            method = "render",
            // Spawn is off the Forge compile classpath, so the AP can't refmap this reference and
            // the Mojang name never resolves on the SRG runtime; hard-code the SRG name m_7695_.
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/ninni/spawn/client/model/FirekeeperModel;m_7695_(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
                    remap = false
            ),
            index = 7,
            remap = false,
            require = 0
    )
    private float species$scaleMoltenAlpha(float originalAlpha) {
        LivingEntity disguise = SPECIES$CURRENT_ENTITY.get();
        if (disguise == null) return originalAlpha;
        if (!disguise.getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) return originalAlpha;
        // Global glow in every mode: the alpha is driven by glow, not the awakening/dormancy clips;
        // only spin spam raises it, and it decays over time (faster in water).
        return SpawnFirekeeperSpecialActionBehavior.getDisguiseGlowLevel(disguise);
    }
}
