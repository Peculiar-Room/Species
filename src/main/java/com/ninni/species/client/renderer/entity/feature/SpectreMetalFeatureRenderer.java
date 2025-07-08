package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.mob.update_3.SpectreModel;
import com.ninni.species.client.renderer.entity.SpectreRenderer;
import com.ninni.species.client.renderer.entity.rendertypes.OffsetRenderLayer;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.server.entity.mob.update_3.Spectre;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectreMetalFeatureRenderer<T extends Spectre, M extends SpectreModel<T>> extends RenderLayer<T, M> {
    private final SpectreModel<T> modelSpectre;
    private final SpectreModel<T> modelSableSpectre;
    private final SpectreModel<T> modelJoustingSpectre;

    public SpectreMetalFeatureRenderer(RenderLayerParent<T, M> renderLayerParent, EntityRendererProvider.Context ctx) {
        super(renderLayerParent);
        this.modelSpectre = new SpectreModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SPECTRE));
        this.modelSableSpectre = new SpectreModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SABLE_SPECTRE));
        this.modelJoustingSpectre = new SpectreModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.JOUSTING_SPECTRE));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T spectre, float f, float g, float h, float j, float k, float l) {
        if (!spectre.isInvisible()) {
            this.getParentModel().copyPropertiesTo(model(spectre));
            model(spectre).prepareMobModel(spectre, f, g, h);
            model(spectre).setupAnim(spectre, f, g, j, k, l);

            VertexConsumer vertexConsumerCharged = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(spectre)));
            model(spectre).renderToBuffer(poseStack, vertexConsumerCharged, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1);
        }
    }

    protected EntityModel<T> model(T entity) {
        return switch (entity.getVariant()) {
            case SPECTRE -> this.modelSpectre;
            case HULKING_SPECTRE -> this.modelSableSpectre;
            case JOUSTING_SPECTRE -> this.modelJoustingSpectre;
        };
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return SpectreRenderer.getSpectreVariantTextureLocation(entity, "metal");
    }
}