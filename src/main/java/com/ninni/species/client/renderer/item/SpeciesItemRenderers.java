package com.ninni.species.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.Species;
import com.ninni.species.server.block.MobHeadBlock;
import com.ninni.species.server.block.WallMobHeadBlock;
import com.ninni.species.client.model.mob_heads.MobHeadModelBase;
import com.ninni.species.client.renderer.block.MobHeadBlockEntityRenderer;
import com.ninni.species.server.item.RicoshieldItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesItemRenderers extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation RICOSHIELD_TEXTURE = new ResourceLocation(Species.MOD_ID, "textures/entity/quake/ricoshield/ricoshield.png");
    private static final ResourceLocation RICOSHIELD_TEXTURE_CHARGING = new ResourceLocation(Species.MOD_ID, "textures/entity/quake/ricoshield/ricoshield_charging.png");
    private static final ResourceLocation RICOSHIELD_TEXTURE_CHARGED = new ResourceLocation(Species.MOD_ID, "textures/entity/quake/ricoshield/ricoshield_charged.png");
    public static SpeciesItemRenderers instance;
    private Map<MobHeadBlock.Type, MobHeadModelBase> headModelBaseMap;

    public SpeciesItemRenderers(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @SubscribeEvent
    public static void onRegisterReloadListener(RegisterClientReloadListenersEvent event) {
        instance = new SpeciesItemRenderers(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        event.registerReloadListener(instance);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        super.onResourceManagerReload(resourceManager);
        this.headModelBaseMap = MobHeadBlockEntityRenderer.createMobHeadRenderers(this.entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource bufferSource, int p_108834_, int p_108835_) {
        Item item = itemStack.getItem();

        if (item instanceof BlockItem) {
            Block block = ((BlockItem)item).getBlock();
            if (block instanceof MobHeadBlock || block instanceof WallMobHeadBlock) {
                MobHeadBlock.Type type = block instanceof MobHeadBlock ? ((MobHeadBlock)block).getType() : ((WallMobHeadBlock)block).getType();
                MobHeadModelBase modelBase = this.headModelBaseMap.get(type);
                RenderType rendertype = MobHeadBlockEntityRenderer.getRenderType(type);

                MobHeadBlockEntityRenderer.renderMobHead(null, 180.0F, 0.0F, poseStack, bufferSource, p_108834_, modelBase, rendertype, itemDisplayContext, type, false);
            }
        } else if (item instanceof RicoshieldItem) {
            poseStack.pushPose();
            poseStack.scale(1.0F, -1.0F, -1.0F);

            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(RICOSHIELD_TEXTURE), false, itemStack.hasFoil());
            this.shieldModel.handle().render(poseStack, vertexconsumer, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
            this.shieldModel.plate().render(poseStack, vertexconsumer, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);

            if (itemStack.getTag().contains("StoredDamage") && itemStack.getTag().getFloat("StoredDamage") > 0) {
                float storedDamage = itemStack.getTag().getFloat("StoredDamage");

                float opacityCharging = storedDamage >= 25 ? 1 : 0;
                float opacityCharged = storedDamage >= 35 ? 1 : 0;

                VertexConsumer vertexconsumerCharging = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(RICOSHIELD_TEXTURE_CHARGING), false, false);
                this.shieldModel.plate().render(poseStack, vertexconsumerCharging, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, opacityCharging);

                VertexConsumer vertexconsumerCharged = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(RICOSHIELD_TEXTURE_CHARGED), false, false);
                this.shieldModel.plate().render(poseStack, vertexconsumerCharged, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, opacityCharged);
            }
            poseStack.popPose();
        }

    }
}
