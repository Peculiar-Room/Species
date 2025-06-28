package com.ninni.species.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.server.entity.util.LivingEntityAccess;
import com.ninni.species.server.item.CrankbowItem;
import com.ninni.species.registry.SpeciesItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
        super(p_174289_, p_174290_, p_174291_);
    }

    @Inject(at = @At("HEAD"), method = "getArmPose", cancellable = true)
    private static void S$getArmPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.swinging && stack.getItem() instanceof CrankbowItem && player.isUsingItem() && stack.hasTag() && stack.getTag().contains(CrankbowItem.TAG_ITEMS)) {
            cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
        }
    }

    @Inject(at = @At("HEAD"), method = "renderLeftHand", cancellable = true)
    private void renderLeftHand(PoseStack poseStack, MultiBufferSource bufferSource, int i, AbstractClientPlayer player, CallbackInfo ci) {
        ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
        LivingEntity disguise = ((LivingEntityAccess)player).getDisguisedEntity();
        if (disguise != null) {
            EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<? super LivingEntity> renderer = entityrenderdispatcher.getRenderer(disguise);
            if (headItem.is(SpeciesItems.WICKED_MASK.get())) {
                ci.cancel();
                if (renderer instanceof HumanoidMobRenderer humanoidMobRenderer) {
                    HumanoidModel model = (HumanoidModel)humanoidMobRenderer.getModel();
                    this.renderHand(poseStack, bufferSource, i, player, model.leftArm, null, ci);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "renderRightHand", cancellable = true)
    private void renderRightHand(PoseStack poseStack, MultiBufferSource bufferSource, int i, AbstractClientPlayer player, CallbackInfo ci) {
        ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
        LivingEntity disguise = ((LivingEntityAccess)player).getDisguisedEntity();
        if (disguise != null) {
            EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<? super LivingEntity> renderer = entityrenderdispatcher.getRenderer(disguise);
            if (headItem.is(SpeciesItems.WICKED_MASK.get())) {
                ci.cancel();
                if (renderer instanceof HumanoidMobRenderer humanoidMobRenderer) {
                    HumanoidModel model = (HumanoidModel)humanoidMobRenderer.getModel();
                    this.renderHand(poseStack, bufferSource, i, player, model.rightArm, null, ci);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "renderHand", cancellable = true)
    private void renderHand(PoseStack poseStack, MultiBufferSource bufferSource, int i, AbstractClientPlayer player, ModelPart modelPart, ModelPart modelPart1, CallbackInfo ci) {
        ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);

        LivingEntity disguise = ((LivingEntityAccess)player).getDisguisedEntity();
        if (disguise != null) {
            EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<? super LivingEntity> renderer = entityrenderdispatcher.getRenderer(disguise);

            if (headItem.is(SpeciesItems.WICKED_MASK.get())) {
                ci.cancel();

                if (renderer instanceof HumanoidMobRenderer humanoidMobRenderer) {

                    EntityModel model = humanoidMobRenderer.getModel();
                    model.attackTime = 0.0F;
                    model.setupAnim(disguise, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                    modelPart.xRot = 0.0F;
                    modelPart.render(poseStack, bufferSource.getBuffer(RenderType.entitySolid(renderer.getTextureLocation(disguise))), i, OverlayTexture.NO_OVERLAY);
                    if (modelPart1 != null) {
                        modelPart1.xRot = 0.0F;
                        modelPart1.render(poseStack, bufferSource.getBuffer(RenderType.entitySolid(renderer.getTextureLocation(disguise))), i, OverlayTexture.NO_OVERLAY);
                    }
              }
            }
        }
    }
}
