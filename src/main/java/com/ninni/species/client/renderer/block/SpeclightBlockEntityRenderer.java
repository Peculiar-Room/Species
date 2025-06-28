package com.ninni.species.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.server.block.entity.SpeclightBlockEntity;
import com.ninni.species.registry.SpeciesRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
        VertexConsumer builder = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(new ResourceLocation(MOD_ID, "textures/block/speclight_glow.png")));
        builder.vertex(poseStack.last().pose(), f, 0.375F, 0.375F).color(r, g, b, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        builder.vertex(poseStack.last().pose(), f, 0.375F, 1.375F).color(r, g, b, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        builder.vertex(poseStack.last().pose(), f, 1.375F, 1.375F).color(r, g, b, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        builder.vertex(poseStack.last().pose(), f, 1.375F, 0.375F).color(r, g, b, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();

        VertexConsumer builder2 = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(new ResourceLocation(MOD_ID, "textures/block/spectre_glow_base.png")));
        //FRONT
        builder2.vertex(poseStack.last().pose(), f2, 0.375F, 0.375F)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f2, 0.375F, 0.625F)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f, 0.375F, 0.625F)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f, 0.375F, 0.375F)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0f)
                .endVertex();


        //BACK
        builder2.vertex(poseStack.last().pose(), f2, 0.625F, 0.375F)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f2, 0.625F, 0.625F)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f, 0.625F, 0.625F)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f, 0.625F, 0.375F)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0f)
                .endVertex();

        //LEFT
        builder2.vertex(poseStack.last().pose(), f2, 0.375F, 0.375F)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f2, 0.625F, 0.375F)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f, 0.625F, 0.375F)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f, 0.375F, 0.375F)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0f)
                .endVertex();

        //RIGHT
        builder2.vertex(poseStack.last().pose(), f2, 0.625F, 0.625F)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f2, 0.375F, 0.625F)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f, 0.375F, 0.625F)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), f, 0.625F, 0.625F)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0f)
                .endVertex();
    }


    private static void xAxis(PoseStack poseStack, MultiBufferSource bufferSource, float f, float f2, int r, int g, int b) {
        //TOP
        VertexConsumer builder = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(new ResourceLocation(MOD_ID, "textures/block/speclight_glow.png")));

        builder.vertex(poseStack.last().pose(), 0.375F, 0.375F, f2).color(r, g, b, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        builder.vertex(poseStack.last().pose(), 1.375F, 0.375F, f2).color(r, g, b, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        builder.vertex(poseStack.last().pose(), 1.375F, 1.375F, f2).color(r, g, b, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        builder.vertex(poseStack.last().pose(), 0.375F, 1.375F, f2).color(r, g, b, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();

        VertexConsumer builder2 = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(new ResourceLocation(MOD_ID, "textures/block/spectre_glow_base.png")));
        //FRONT
        builder2.vertex(poseStack.last().pose(), 0.375F, 0.375F, f)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, 0.375F, f)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, 0.375F, f2)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.375F, 0.375F, f2)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0f)
                .endVertex();


        //BACK
        builder2.vertex(poseStack.last().pose(), 0.375F, 0.625F, f)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, 0.625F, f)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, 0.625F, f2)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.375F, 0.625F, f2)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0f)
                .endVertex();

        //LEFT
        builder2.vertex(poseStack.last().pose(), 0.375F, 0.625F, f)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.375F, 0.375F, f)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.375F, 0.375F, f2)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.375F, 0.625F, f2)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0f)
                .endVertex();

        //RIGHT
        builder2.vertex(poseStack.last().pose(), 0.625F, 0.375F, f)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, 0.625F, f)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, 0.625F, f2)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, 0.375F, f2)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0f)
                .endVertex();
    }

    private static void yAxis(PoseStack poseStack, MultiBufferSource bufferSource, float f, float f2, int r, int g, int b) {
        //TOP
        VertexConsumer builder = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(new ResourceLocation(MOD_ID, "textures/block/speclight_glow.png")));

        builder.vertex(poseStack.last().pose(), 0.375F, f2, 0.375F).color(r, g, b, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
        builder.vertex(poseStack.last().pose(), 1.375F, f2, 0.375F).color(r, g, b, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
        builder.vertex(poseStack.last().pose(), 1.375F, f2, 1.375F).color(r, g, b, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
        builder.vertex(poseStack.last().pose(), 0.375F, f2, 1.375F).color(r, g, b, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();

        VertexConsumer builder2 = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(new ResourceLocation(MOD_ID, "textures/block/spectre_glow_base.png")));
        //FRONT
        builder2.vertex(poseStack.last().pose(), 0.375F, f, 0.375F)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, f, 0.375F)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, f2, 0.375F)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.375F, f2, 0.375F)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();


        //BACK
        builder2.vertex(poseStack.last().pose(), 0.375F, f, 0.625F)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, f, 0.625F)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, f2, 0.625F)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.375F, f2, 0.625F)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();

        //LEFT
        builder2.vertex(poseStack.last().pose(), 0.375F, f, 0.375F)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.375F, f, 0.625F)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.375F, f2, 0.625F)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.375F, f2, 0.375F)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();

        //RIGHT
        builder2.vertex(poseStack.last().pose(), 0.625F, f, 0.375F)
                .color(-r,-g,-b, 0.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, f, 0.625F)
                .color(-r,-g,-b, 0.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, f2, 0.625F)
                .color(-r,-g,-b, 1F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder2.vertex(poseStack.last().pose(), 0.625F, f2, 0.375F)
                .color(-r,-g,-b, 1F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
