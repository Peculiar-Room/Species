package com.ninni.species.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.mob.update_3.BewereagerModel;
import com.ninni.species.client.renderer.entity.feature.BewereagerCollarLayer;
import com.ninni.species.client.renderer.entity.feature.BewereagerWetLayer;
import com.ninni.species.server.entity.mob.update_3.Bewereager;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class BewereagerRenderer extends MobRenderer<Bewereager, BewereagerModel<Bewereager>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/bewereager/bewereager.png");
    public static final ResourceLocation TEXTURE_TAME = new ResourceLocation(MOD_ID, "textures/entity/bewereager/bewereager_tame.png");
    public static final ResourceLocation TEXTURE_SPLITTING = new ResourceLocation(MOD_ID, "textures/entity/bewereager/bewereager_splitting.png");

    public BewereagerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BewereagerModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.BEWEREAGER)), 0.6F);
        this.addLayer(new BewereagerCollarLayer(this));
        this.addLayer(new BewereagerWetLayer(this));
    }

    @Override
    public void render(Bewereager bewereager, float v, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight) {
        if (bewereager.splitTime > 0 && bewereager.splitTime <= 25) {
            stack.pushPose();
            float opacity = (float)bewereager.splitTime / 25;
            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(RenderType.dragonExplosionAlpha(TEXTURE_SPLITTING));

            float animationProgress = this.getBob(bewereager, partialTick);
            float bodyRotLerp = Mth.rotLerp(partialTick, bewereager.yBodyRotO, bewereager.yBodyRot);
            float headRotLerp = Mth.rotLerp(partialTick, bewereager.yHeadRotO, bewereager.yHeadRot);
            float headY = headRotLerp - bodyRotLerp;
            float headX = Mth.lerp(partialTick, bewereager.xRotO, bewereager.getXRot());

            this.setupRotations(bewereager, stack, animationProgress, bodyRotLerp, partialTick);

            stack.scale(-1.0F, -1.0F, 1.0F);
            stack.translate(0.0F, -1.501F, 0.0F);

            model.prepareMobModel(bewereager, 0, 0, partialTick);
            model.setupAnim(bewereager, 0, 0, animationProgress, headY, headX);

            this.model.renderToBuffer(stack, vertexConsumer1, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, -opacity);
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityDecal(getTextureLocation(bewereager)));
            this.model.renderToBuffer(stack, vertexConsumer, packedLight,  OverlayTexture.pack(0.0F, false), 1, 1, 1, 1.0F);
            stack.popPose();
        } else {
            super.render(bewereager, v, partialTick, stack, bufferSource, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Bewereager entity) {
        return entity.getFromWolf() ? TEXTURE_TAME : TEXTURE;
    }

    protected boolean isShaking(Bewereager bewereager) {
        return super.isShaking(bewereager) || bewereager.isSplitting();
    }
}