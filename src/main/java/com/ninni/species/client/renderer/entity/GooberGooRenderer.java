package com.ninni.species.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ninni.species.Species;
import com.ninni.species.client.model.entity.GooberGooModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.entity.GooberGoo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GooberGooRenderer extends EntityRenderer<GooberGoo> {
    public static final ResourceLocation LOCATION = new ResourceLocation(Species.MOD_ID,"textures/entity/goober/goober_goo.png");
    private final GooberGooModel model;

    public GooberGooRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new GooberGooModel(context.bakeLayer(SpeciesEntityModelLayers.GOOBER_GOO));
    }

    @Override
    public void render(GooberGoo goo, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, goo.yRotO, goo.getYRot()) - 90.0f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, goo.xRotO, goo.getXRot()) + 90.0f));
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(LOCATION));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
        this.model.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
        poseStack.popPose();
        super.render(goo, f, g, poseStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(GooberGoo goo) {
        return LOCATION;
    }
}
