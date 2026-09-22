package com.ninni.species.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.registry.SpeciesRenderTypes;
import com.ninni.species.server.block.entity.ChaindelierBlockEntity;
import com.ninni.species.server.block.entity.SpeclightBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.ninni.species.Species.MOD_ID;

public class SpeclightBlockEntityRenderer implements BlockEntityRenderer<SpeclightBlockEntity> {

    public SpeclightBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        // Constructor logic here
    }

    @Override
    public boolean shouldRenderOffScreen(SpeclightBlockEntity p_112306_) {
        return true;
    }

    @Override
    public boolean shouldRender(SpeclightBlockEntity p_173568_, Vec3 p_173569_) {
        return true;
    }

    @Override
    public void render(SpeclightBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity.getBlockState().getValue(BlockStateProperties.POWERED)) {

            poseStack.pushPose();
            int r = (blockEntity.getColor() & 0xFF0000) >> 16;
            int g = (blockEntity.getColor() & 0xFF00) >> 8;
            int b = (blockEntity.getColor() & 0xFF);

            switch (blockEntity.getBlockState().getValue(BlockStateProperties.ATTACH_FACE)) {
                case CEILING -> yAxis(poseStack, bufferSource, 0.188f, 0.812f,r,g,b);
                case FLOOR -> yAxis(poseStack, bufferSource, 0.812f, 0.188f,r,g,b);
                default -> {
                    switch (blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                        case NORTH -> xAxis(poseStack, bufferSource, 0.188f, 0.812f,r,g,b);
                        case SOUTH -> xAxis(poseStack, bufferSource, 0.812f, 0.188f,r,g,b);
                        case EAST -> zAxis(poseStack, bufferSource, 0.188f, 0.812f,r,g,b);
                        case WEST -> zAxis(poseStack, bufferSource, 0.812f, 0.188f,r,g,b);
                    }
                }
            }

            poseStack.popPose();
        }
    }


    private static void zAxis(PoseStack poseStack, MultiBufferSource bufferSource, float f, float f2, int r, int g, int b) {
        //TOP
        VertexConsumer builder = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/speclight_glow.png")));
        builder.addVertex(poseStack.last().pose(), f, 0.375F, 0.375F).setColor(r, g, b, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 0, 1);
        builder.addVertex(poseStack.last().pose(), f, 0.375F, 1.375F).setColor(r, g, b, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 0, 1);
        builder.addVertex(poseStack.last().pose(), f, 1.375F, 1.375F).setColor(r, g, b, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 0, 1);
        builder.addVertex(poseStack.last().pose(), f, 1.375F, 0.375F).setColor(r, g, b, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 0, 1);

        VertexConsumer builder2 = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/spectre_glow_base.png")));
        //FRONT
        builder2.addVertex(poseStack.last().pose(), f2, 0.375F, 0.375F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f2, 0.375F, 0.625F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f, 0.375F, 0.625F)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f, 0.375F, 0.375F)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0f);


        //BACK
        builder2.addVertex(poseStack.last().pose(), f2, 0.625F, 0.375F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f2, 0.625F, 0.625F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f, 0.625F, 0.625F)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f, 0.625F, 0.375F)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0f);

        //LEFT
        builder2.addVertex(poseStack.last().pose(), f2, 0.375F, 0.375F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f2, 0.625F, 0.375F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f, 0.625F, 0.375F)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f, 0.375F, 0.375F)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0f);

        //RIGHT
        builder2.addVertex(poseStack.last().pose(), f2, 0.625F, 0.625F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f2, 0.375F, 0.625F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f, 0.375F, 0.625F)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), f, 0.625F, 0.625F)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0f);
    }


    private static void xAxis(PoseStack poseStack, MultiBufferSource bufferSource, float f, float f2, int r, int g, int b) {
        //TOP
        VertexConsumer builder = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/speclight_glow.png")));

        builder.addVertex(poseStack.last().pose(), 0.375F, 0.375F, f2).setColor(r, g, b, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 0, 1);
        builder.addVertex(poseStack.last().pose(), 1.375F, 0.375F, f2).setColor(r, g, b, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 0, 1);
        builder.addVertex(poseStack.last().pose(), 1.375F, 1.375F, f2).setColor(r, g, b, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 0, 1);
        builder.addVertex(poseStack.last().pose(), 0.375F, 1.375F, f2).setColor(r, g, b, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 0, 1);

        VertexConsumer builder2 = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/spectre_glow_base.png")));
        //FRONT
        builder2.addVertex(poseStack.last().pose(), 0.375F, 0.375F, f)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, 0.375F, f)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, 0.375F, f2)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.375F, 0.375F, f2)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0f);


        //BACK
        builder2.addVertex(poseStack.last().pose(), 0.375F, 0.625F, f)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, 0.625F, f)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, 0.625F, f2)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.375F, 0.625F, f2)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0f);

        //LEFT
        builder2.addVertex(poseStack.last().pose(), 0.375F, 0.625F, f)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.375F, 0.375F, f)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.375F, 0.375F, f2)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.375F, 0.625F, f2)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0f);

        //RIGHT
        builder2.addVertex(poseStack.last().pose(), 0.625F, 0.375F, f)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, 0.625F, f)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, 0.625F, f2)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, 0.375F, f2)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0f);
    }

    private static void yAxis(PoseStack poseStack, MultiBufferSource bufferSource, float f, float f2, int r, int g, int b) {
        //TOP
        VertexConsumer builder = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/speclight_glow.png")));

        builder.addVertex(poseStack.last().pose(), 0.375F, f2, 0.375F).setColor(r, g, b, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 1, 0);
        builder.addVertex(poseStack.last().pose(), 1.375F, f2, 0.375F).setColor(r, g, b, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 1, 0);
        builder.addVertex(poseStack.last().pose(), 1.375F, f2, 1.375F).setColor(r, g, b, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 1, 0);
        builder.addVertex(poseStack.last().pose(), 0.375F, f2, 1.375F).setColor(r, g, b, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 1, 0);

        VertexConsumer builder2 = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/spectre_glow_base.png")));
        //FRONT
        builder2.addVertex(poseStack.last().pose(), 0.375F, f, 0.375F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, f, 0.375F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, f2, 0.375F)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.375F, f2, 0.375F)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);


        //BACK
        builder2.addVertex(poseStack.last().pose(), 0.375F, f, 0.625F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, f, 0.625F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, f2, 0.625F)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.375F, f2, 0.625F)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);

        //LEFT
        builder2.addVertex(poseStack.last().pose(), 0.375F, f, 0.375F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.375F, f, 0.625F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.375F, f2, 0.625F)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.375F, f2, 0.375F)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);

        //RIGHT
        builder2.addVertex(poseStack.last().pose(), 0.625F, f, 0.375F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, f, 0.625F)
                .setColor(-r,-g,-b, 0.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, f2, 0.625F)
                .setColor(-r,-g,-b, 1F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder2.addVertex(poseStack.last().pose(), 0.625F, f2, 0.375F)
                .setColor(-r,-g,-b, 1F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(SpeclightBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
    }
}
