package com.ninni.species.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.Species;
import com.ninni.species.server.entity.mob.update_2.Springling;
import com.ninni.species.server.entity.util.LivingEntityAccess;
import com.ninni.species.registry.SpeciesItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords;
import static net.minecraft.client.renderer.entity.LivingEntityRenderer.isEntityUpsideDown;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {

    @SubscribeEvent
    public static void livingEntityRenderer(RenderLivingEvent<LivingEntity, EntityModel<LivingEntity>> event) {
        LivingEntity entity = event.getEntity();
        ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
        LivingEntity disguise = ((LivingEntityAccess) entity).getDisguisedEntity();

        if (headItem.is(SpeciesItems.WICKED_MASK.get()) && headItem.hasTag() && headItem.getTag().contains("id") && disguise != null && !(entity instanceof Player player && player.isSpectator())) {
            event.setCanceled(true);
            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<?> baseRenderer = dispatcher.getRenderer(disguise);

            if (baseRenderer instanceof LivingEntityRenderer renderer && disguise != null && !disguise.isRemoved() && disguise.getType() != null && !entity.hasEffect(MobEffects.INVISIBILITY)) {
                EntityModel model = renderer.getModel();
                if (model == null) return;

                PoseStack poseStack = event.getPoseStack();
                MultiBufferSource buffer = event.getMultiBufferSource();
                int light = event.getPackedLight();
                float partialTicks = event.getPartialTick();

                poseStack.pushPose();

                model.attackTime = renderer.getAttackAnim(entity, partialTicks);
                disguise.hurtTime = entity.hurtTime;
                disguise.swingingArm = entity.swingingArm;
                disguise.tickCount = entity.tickCount;
                disguise.setPose(entity.getPose());
                if (disguise instanceof Mob mobDisguise && entity instanceof Mob mobEntity) {
                    mobDisguise.setLeftHanded(mobEntity.isLeftHanded());
                } else if (disguise instanceof Mob mobDisguise && entity instanceof Player player) {
                    mobDisguise.setLeftHanded(player.getMainArm() == HumanoidArm.LEFT);
                }
                if (disguise instanceof EnderMan enderMan) {
                    ItemStack mainHand = entity.getMainHandItem();
                    if (!mainHand.isEmpty() && mainHand.getItem() instanceof BlockItem blockItem) {
                        enderMan.setCarriedBlock(blockItem.getBlock().defaultBlockState());
                    } else {
                        enderMan.setCarriedBlock(Blocks.AIR.defaultBlockState());
                    }
                    if (model instanceof EndermanModel<?> endermanModel) {
                        endermanModel.carrying = !enderMan.getCarriedBlock().is(Blocks.AIR);
                    }
                }

                boolean sitting = entity.isPassenger() && entity.getVehicle() != null && entity.getVehicle().shouldRiderSit();
                model.riding = sitting;
                model.young = disguise.isBaby();

                float bodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
                float headRot = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
                float netHeadYaw = headRot - bodyRot;
                float headPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

                if (isEntityUpsideDown(disguise) || isEntityUpsideDown(entity)) {
                    headPitch *= -1.0F;
                    netHeadYaw *= -1.0F;
                }

                if (entity.hasPose(Pose.SLEEPING)) {
                    Direction bedDir = entity.getBedOrientation();
                    if (bedDir != null) {
                        float offset = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                        poseStack.translate(-bedDir.getStepX() * offset, 0.0F, -bedDir.getStepZ() * offset);
                    }
                }

                float animProgress = renderer.getBob(disguise, partialTicks);
                renderer.setupRotations(disguise, poseStack, animProgress, bodyRot, partialTicks);
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                renderer.scale(disguise, poseStack, partialTicks);
                poseStack.translate(0.0F, -1.501F, 0.0F);

                float walkSpeed = entity.walkAnimation.speed(partialTicks);
                float walkPos = entity.walkAnimation.position(partialTicks);

                if (entity instanceof ArmorStand) {
                    headPitch = 0;
                    netHeadYaw = 0;
                    animProgress = 0;
                }

                model.prepareMobModel(disguise, walkPos, walkSpeed, partialTicks);
                model.setupAnim(disguise, walkPos, walkSpeed, animProgress, netHeadYaw, headPitch);

                Minecraft mc = Minecraft.getInstance();
                boolean bodyVisible = renderer.isBodyVisible(entity);
                boolean invisible = !bodyVisible && !entity.isInvisibleTo(mc.player);
                boolean glowing = mc.shouldEntityAppearGlowing(entity);
                RenderType type = renderer.getRenderType(disguise, bodyVisible, invisible, glowing);

                if (type != null) {
                    VertexConsumer consumer = buffer.getBuffer(type);
                    int overlay = getOverlayCoords(entity, event.getRenderer().getWhiteOverlayProgress(entity, partialTicks));
                    try {
                        model.renderToBuffer(poseStack, consumer, light, overlay, 1.0F, 1.0F, 1.0F, invisible ? 0.15F : 1.0F);
                    } catch (Exception ignored) {}
                }

                if (!entity.isSpectator()) {
                    for (Object layer : renderer.layers) {
                        try {
                            ((RenderLayer)layer).render(poseStack, buffer, light, disguise, walkPos, walkSpeed, partialTicks, animProgress, netHeadYaw, headPitch);
                        } catch (Exception ignored) {}
                    }
                }

                poseStack.popPose();
            }
        }

        if (((LivingEntityAccess) entity).hasTanked()) event.getPoseStack().scale(1.35F, 1.125F, 1.35F);
        if (((LivingEntityAccess) entity).hasSnatched()) event.getPoseStack().scale(0.85F, 1.125F, 0.85F);
    }
}
