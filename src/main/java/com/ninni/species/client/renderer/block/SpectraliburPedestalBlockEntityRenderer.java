package com.ninni.species.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesRenderTypes;
import com.ninni.species.server.block.SpectraliburPedestalBlock;
import com.ninni.species.server.block.entity.ChaindelierBlockEntity;
import com.ninni.species.server.block.entity.SpectraliburBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import static com.ninni.species.Species.MOD_ID;

public class SpectraliburPedestalBlockEntityRenderer implements BlockEntityRenderer<SpectraliburBlockEntity> {

    public SpectraliburPedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        // Constructor logic here
    }

    @Override
    public boolean shouldRenderOffScreen(SpectraliburBlockEntity p_112306_) {
        return true;
    }

    @Override
    public boolean shouldRender(SpectraliburBlockEntity p_173568_, Vec3 p_173569_) {
        return true;
    }

    public void render(SpectraliburBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        float swordPosition = blockEntity.getSwordPosition();

        poseStack.pushPose();
        poseStack.translate(1.65, 0.75 + swordPosition, -0.375);
        poseStack.mulPose(Axis.ZN.rotationDegrees(225));

        float f = blockEntity.getLevel().getGameTime() - blockEntity.getShaking();
        if (f < 5.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(f / 1.5F * (float) Math.PI) * 3.0F));
        }

        poseStack.scale(2f, 2f, 2f);
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(SpeciesItems.SPECTRALIBUR.get()), ItemDisplayContext.HEAD, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
        poseStack.popPose();

        poseStack.pushPose();
        VertexConsumer builder = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(this.getTextureLocation()));

        float left = 0.0f;
        float right = 1.0f;

        Vec2[] vertices = new Vec2[]{
                new Vec2(left, 1.15F + swordPosition),
                new Vec2(right, 1.15F + swordPosition),
                new Vec2(right, 0f),
                new Vec2(left, 0f),
                new Vec2(right, 0),
                new Vec2(left, 0)
        };

        //FRONT
        builder.addVertex(poseStack.last().pose(), vertices[0].x, vertices[0].y, 0.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[1].x, vertices[1].y, 0.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[2].x, vertices[2].y, 0.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 1.0F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[3].x, vertices[3].y, 0.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 1.0F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);


        //BACK
        builder.addVertex(poseStack.last().pose(), vertices[0].x, vertices[0].y, 1)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[1].x, vertices[1].y, 1.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[2].x, vertices[2].y, 1.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 1.0F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[3].x, vertices[3].y, 1.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 1.0F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);

        //LEFT
        builder.addVertex(poseStack.last().pose(), 0.0001F, vertices[0].y, 0.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), 0.0001F, vertices[1].y, 1.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), 0.0001F, vertices[2].y, 1.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 1.0F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), 0.0001F, vertices[3].y, 0.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 1.0F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);

        //RIGHT
        builder.addVertex(poseStack.last().pose(), 0.9999f, vertices[0].y, 0.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), 0.9999f, vertices[1].y, 1.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), 0.9999f, vertices[2].y, 1.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 1.0F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), 0.9999f, vertices[3].y, 0.0F)
                .setColor(1.0F, 1 - swordPosition, 1 - swordPosition, 1.0F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);

        poseStack.popPose();
    }

    public ResourceLocation getTextureLocation() {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/spectralibur_glow.png");
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(SpectraliburBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY() - 2, pos.getZ(), pos.getX() + 1.0, pos.getY() + 2, pos.getZ() + 1.0);
    }
}