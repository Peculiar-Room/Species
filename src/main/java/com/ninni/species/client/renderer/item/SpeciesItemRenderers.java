package com.ninni.species.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.Species;
import com.ninni.species.client.model.mob_heads.MobHeadModelBase;
import com.ninni.species.client.renderer.block.MobHeadBlockEntityRenderer;
import com.ninni.species.server.block.MobHeadBlock;
import com.ninni.species.server.block.WallMobHeadBlock;
import com.ninni.species.server.item.RicoshieldItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT, modid = Species.MOD_ID)
public class SpeciesItemRenderers extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation RICOSHIELD_TEXTURE = ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "textures/entity/quake/ricoshield/ricoshield.png");
    private static final ResourceLocation RICOSHIELD_TEXTURE_CHARGING = ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "textures/entity/quake/ricoshield/ricoshield_charging.png");
    private static final ResourceLocation RICOSHIELD_TEXTURE_CHARGED = ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "textures/entity/quake/ricoshield/ricoshield_charged.png");
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
                if (modelBase == null) return;
                RenderType rendertype = MobHeadBlockEntityRenderer.getRenderType(type);
                MobHeadBlockEntityRenderer.renderMobHead(null, 180.0F, 0.0F, poseStack, bufferSource, p_108834_, modelBase, rendertype, itemDisplayContext, type, false);
            }
        } else if (item instanceof RicoshieldItem) {
            poseStack.pushPose();
            poseStack.scale(1.0F, -1.0F, -1.0F);

            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(RICOSHIELD_TEXTURE), itemStack.hasFoil());
            this.shieldModel.handle().render(poseStack, vertexconsumer, p_108834_, p_108835_, FastColor.ARGB32.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F));
            this.shieldModel.plate().render(poseStack, vertexconsumer, p_108834_, p_108835_, FastColor.ARGB32.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F));

            CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (tag.contains("StoredDamage") && tag.getFloat("StoredDamage") > 0) {
                float storedDamage = tag.getFloat("StoredDamage");

                float opacityCharging = storedDamage >= 25 ? 1 : 0;
                float opacityCharged = storedDamage >= 35 ? 1 : 0;

                VertexConsumer vertexconsumerCharging = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(RICOSHIELD_TEXTURE_CHARGING), false);
                this.shieldModel.plate().render(poseStack, vertexconsumerCharging, p_108834_, p_108835_, FastColor.ARGB32.colorFromFloat(opacityCharging, 1.0F, 1.0F, 1.0F));

                VertexConsumer vertexconsumerCharged = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(RICOSHIELD_TEXTURE_CHARGED), false);
                this.shieldModel.plate().render(poseStack, vertexconsumerCharged, p_108834_, p_108835_, FastColor.ARGB32.colorFromFloat(opacityCharged, 1.0F, 1.0F, 1.0F));
            }
            poseStack.popPose();
        }

    }
}
