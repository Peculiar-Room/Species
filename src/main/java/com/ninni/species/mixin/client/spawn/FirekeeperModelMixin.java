package com.ninni.species.mixin.client.spawn;

import com.ninni.species.client.animation.SpeciesFirekeeperAnimations;
import com.ninni.species.client.events.WickedMaskDisguiseEvents;
import com.ninni.species.server.disguise.behaviors.SpawnFirekeeperSpecialActionBehavior;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Soft-dep: while the Firekeeper disguise runs the non-shifted spin chain, swaps Spawn's AWAKENING
 *  (the 4th of 5 chained {@code animate} calls) for the active phase clip; AWAKENING/NONE pass through. */
@Pseudo
@Mixin(targets = "com.ninni.spawn.client.model.FirekeeperModel", remap = false)
public abstract class FirekeeperModelMixin {

    /** Per-{@code setupAnim} entity capture (HEAD set, RETURN clear), read by {@code @ModifyArg}
     *  since {@code @Local} won't bind a LivingEntity param to the Firekeeper-typed arg. */
    private static final ThreadLocal<LivingEntity> SPECIES$CURRENT_ENTITY = new ThreadLocal<>();

    /** Captures the disguise entity into {@link #SPECIES$CURRENT_ENTITY}; {@code @Coerce} takes the
     *  Firekeeper-typed arg, with all 5 floats declared since Mixin needs full capture or none. */
    @Inject(method = "setupAnim", at = @At("HEAD"), remap = false, require = 0)
    private void species$captureEntity(@Coerce LivingEntity entity,
                                       float limbSwing, float limbSwingAmount, float ageInTicks,
                                       float netHeadYaw, float headPitch,
                                       CallbackInfo ci) {
        SPECIES$CURRENT_ENTITY.set(entity);
    }

    /** Pairs with {@link #species$captureEntity} so the ThreadLocal never leaks past one
     *  {@code setupAnim} invocation. */
    @Inject(method = "setupAnim", at = @At("RETURN"), remap = false, require = 0)
    private void species$clearEntity(CallbackInfo ci) {
        SPECIES$CURRENT_ENTITY.remove();
    }

    /** TAIL: directly writes {@code frontPlane} scale (bypassing the additive SCALE channel Spawn's
     *  IDLE drives every tick) to hide the plate in every mode but NONE/HELD_DORMANT. */
    @Inject(method = "setupAnim", at = @At("TAIL"), remap = false, require = 0)
    private void species$forceHidePartsForDisguise(@Coerce LivingEntity entity,
                                                   float p1, float p2, float p3, float p4, float p5,
                                                   CallbackInfo ci) {
        if (entity == null) return;
        if (!entity.getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) return;

        // The behavior owns the plate state: <0 = leave it (NONE/HELD_DORMANT show it via the held
        // animation); otherwise force the scale — 0 hides it during the spin, and the dormant
        // entry/exit lerp it (1→0 / 0→1) for a smooth plate fade.
        float plate = SpawnFirekeeperSpecialActionBehavior.getDesiredFrontPlaneScale(entity);
        if (plate < 0F) return;

        net.minecraft.client.model.geom.ModelPart frontPlane;
        try {
            // root() via the vanilla HierarchicalModel supertype so the call reobf-maps at runtime
            // (reflective getMethod("root") would miss the SRG-mapped name).
            net.minecraft.client.model.geom.ModelPart root =
                    ((net.minecraft.client.model.HierarchicalModel<?>) (Object) this).root();
            net.minecraft.client.model.geom.ModelPart torso = root
                    .getChild("all").getChild("allRot").getChild("body").getChild("torso");
            frontPlane = torso.getChild("frontPlane");
        } catch (Throwable ignored) {
            return;
        }

        frontPlane.xScale = plate;
        frontPlane.yScale = plate;
        frontPlane.zScale = plate;
    }

    @ModifyArg(
            // ordinal=3 = the 4th of 5 chained animate calls (idle/laser/attack/awakening/slam);
            // SRG name m_233385_ hard-coded (Spawn off the Forge classpath, no refmap), entity via the ThreadLocal.
            method = "setupAnim",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/ninni/spawn/client/model/FirekeeperModel;m_233385_(Lnet/minecraft/world/entity/AnimationState;Lnet/minecraft/client/animation/AnimationDefinition;FF)V",
                    ordinal = 3,
                    remap = false
            ),
            index = 1,
            remap = false,
            require = 0
    )
    private AnimationDefinition species$swapAwakeningWithSpinLoop(AnimationDefinition original) {
        LivingEntity disguise = SPECIES$CURRENT_ENTITY.get();
        if (disguise == null) return original;
        if (!disguise.getTags().contains(WickedMaskDisguiseEvents.DISGUISE_TAG)) return original;
        SpawnFirekeeperSpecialActionBehavior.Mode mode =
                SpawnFirekeeperSpecialActionBehavior.getCurrentMode(disguise);
        return switch (mode) {
            case ANCHOR_DOWN -> SpeciesFirekeeperAnimations.ANCHOR_DOWN;
            case SPIN_LOOP -> SpeciesFirekeeperAnimations.ANCHORED_SPIN_LOOP;
            case BRAKE -> SpeciesFirekeeperAnimations.BRAKE;
            case RECOVER -> SpeciesFirekeeperAnimations.RECOVER;
            // The equip fold and the dormant hold both clamp AWAKENING_REVERSE to its final folded
            // pose (HELD_DORMANT runs it past its length so KeyframeAnimations holds it).
            case INVERSE_AWAKENING, HELD_DORMANT -> SpeciesFirekeeperAnimations.AWAKENING_REVERSE;
            // HELD_AWAKENED keeps original AWAKENING running past length, clamping to its t=2.0
            // awakened pose values (frontPlane hidden, limbs out, body settled).
            case AWAKENING, HELD_AWAKENED, NONE -> original;
        };
    }
}
