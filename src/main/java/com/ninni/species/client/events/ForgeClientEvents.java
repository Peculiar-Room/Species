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
import net.minecraftforge.event.entity.player.PlayerEvent;
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
        LivingEntity disguise = ((LivingEntityAccess)entity).getDisguisedEntity();

        if (headItem.is(SpeciesItems.WICKED_MASK.get()) && headItem.hasTag() && headItem.getTag().contains("id") && disguise != null && !(entity instanceof Player player && player.isSpectator())) {
            event.setCanceled(true);
            EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer render = entityrenderdispatcher.getRenderer(disguise);
            if (render instanceof LivingEntityRenderer renderer && !entity.hasEffect(MobEffects.INVISIBILITY)) {
                EntityModel model = renderer.getModel();
                float partialTick = event.getPartialTick();
                PoseStack stack = event.getPoseStack();
                int packedLight = event.getPackedLight();
                MultiBufferSource multiBufferSource = event.getMultiBufferSource();

                stack.pushPose();
                model.attackTime = renderer.getAttackAnim(entity, partialTick);
                disguise.hurtTime = entity.hurtTime;
                disguise.swingingArm = entity.swingingArm;
                if (disguise instanceof Mob mobDisguise && entity instanceof Mob mobEntity) mobDisguise.setLeftHanded(mobEntity.isLeftHanded());
                else if (disguise instanceof Mob mobDisguise && entity instanceof Player player) mobDisguise.setLeftHanded(player.getMainArm() == HumanoidArm.LEFT);
                if (disguise instanceof EnderMan enderMan) {
                    if (!entity.getMainHandItem().isEmpty() && entity.getMainHandItem().getItem() instanceof BlockItem blockItem) enderMan.setCarriedBlock(blockItem.getBlock().defaultBlockState());
                    else enderMan.setCarriedBlock(Blocks.AIR.defaultBlockState());
                    if (model instanceof EndermanModel<?> endermanModel) {
                        endermanModel.carrying = !enderMan.getCarriedBlock().is(Blocks.AIR);
                    }
                }
                disguise.deathTime = entity.deathTime;
                boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
                model.riding = shouldSit;
                model.young = disguise.isBaby();
                float bodyRotLerp = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
                float headRotLerp = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
                float headY = headRotLerp - bodyRotLerp;
                if (shouldSit && entity.getVehicle() instanceof LivingEntity vehicle) {
                    bodyRotLerp = Mth.rotLerp(partialTick, vehicle.yBodyRotO, vehicle.yBodyRot);
                    headY = headRotLerp - bodyRotLerp;
                    float f3 = Mth.wrapDegrees(headY);
                    if (f3 < -85.0F) f3 = -85.0F;
                    if (f3 >= 85.0F) f3 = 85.0F;
                    bodyRotLerp = headRotLerp - f3;
                    if (f3 * f3 > 2500.0F) bodyRotLerp += f3 * 0.2F;
                    headY = headRotLerp - bodyRotLerp;
                }
                for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                    if (equipmentSlot != EquipmentSlot.HEAD)
                        disguise.setItemSlot(equipmentSlot, entity.getItemBySlot(equipmentSlot));
                }

                float headX = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
                if (isEntityUpsideDown(disguise) || isEntityUpsideDown(entity)) {
                    headX *= -1.0F;
                    headY *= -1.0F;
                }
                if (entity.hasPose(Pose.SLEEPING)) {
                    Direction direction = entity.getBedOrientation();
                    if (direction != null) {
                        float f4 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                        stack.translate((float)(-direction.getStepX()) * f4, 0.0F, (float)(-direction.getStepZ()) * f4);
                    }
                }
                disguise.tickCount = entity.tickCount;
                disguise.wasTouchingWater = entity.wasTouchingWater;
                disguise.wasEyeInWater = entity.wasEyeInWater;

                float animationProgress = renderer.getBob(disguise, partialTick);
                renderer.setupRotations(disguise, stack, animationProgress, bodyRotLerp, partialTick);
                stack.scale(-1.0F, -1.0F, 1.0F);
                renderer.scale(disguise, stack, partialTick);
                stack.translate(0.0F, -1.501F, 0.0F);
                float walkSpeed = 0.0F;
                float walkPos = 0.0F;
                if (!shouldSit && disguise.isAlive() && entity.isAlive()) {
                    walkSpeed = entity.walkAnimation.speed(partialTick);
                    walkPos = entity.walkAnimation.position(partialTick);
                    if (disguise.isBaby()) walkPos *= 3.0F;
                    if (walkSpeed > 1.0F) walkSpeed = 1.0F;
                }

                if (entity instanceof ArmorStand) {
                    headX = 0;
                    headY = 0;
                    animationProgress = 0;
                }

                model.prepareMobModel(disguise, walkPos, walkSpeed, partialTick);
                model.setupAnim(disguise, walkPos, walkSpeed, animationProgress, headY, headX);
                Minecraft minecraft = Minecraft.getInstance();
                boolean bodyVisible = renderer.isBodyVisible(entity);
                boolean isVisible = !bodyVisible && !entity.isInvisibleTo(minecraft.player);
                boolean glowing = minecraft.shouldEntityAppearGlowing(entity);
                RenderType rendertype = renderer.getRenderType(disguise, bodyVisible, isVisible, glowing);
                if (rendertype != null) {
                    VertexConsumer vertexconsumer = multiBufferSource.getBuffer(rendertype);
                    int damageOverlay = getOverlayCoords(entity, event.getRenderer().getWhiteOverlayProgress(entity, partialTick));
                    model.renderToBuffer(stack, vertexconsumer, packedLight, damageOverlay, 1.0F, 1.0F, 1.0F, isVisible ? 0.15F : 1.0F);
                }

                if (!entity.isSpectator()) {
                    for(Object renderLayer : renderer.layers) {
                        ((RenderLayer)renderLayer).render(stack, multiBufferSource, packedLight, disguise, walkPos, walkSpeed, partialTick, animationProgress, headY, headX);
                    }
                }
                stack.popPose();
            }
        }

        if (((LivingEntityAccess)entity).hasTanked()) event.getPoseStack().scale(1.35F, 1.125F, 1.35F);
        if (((LivingEntityAccess)entity).hasSnatched()) event.getPoseStack().scale(0.85F, 1.125F, 0.85F);
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntity().getVehicle() instanceof Springling) {
            event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
        }
    }

}
