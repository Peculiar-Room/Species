package com.ninni.species.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ninni.species.registry.SpeciesItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WickedSwapperProjectileRenderer<T extends Entity> extends EntityRenderer<T> {

    public WickedSwapperProjectileRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(T entity, float v, float v1, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + v1) * 80));
        poseStack.translate(0.0F, -0.25F, -0.2175F);

        poseStack.scale(0.5F, 0.5F, 0.5F);


        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(new ItemStack(SpeciesItems.WICKED_SWAPPER.get()), ItemDisplayContext.HEAD, 255, 255, poseStack, multiBufferSource, entity.level(), 0);

        poseStack.popPose();
        super.render(entity, v, v1, poseStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return null;
    }
}
