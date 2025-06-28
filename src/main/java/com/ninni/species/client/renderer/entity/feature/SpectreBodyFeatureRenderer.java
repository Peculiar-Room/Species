package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.mob.update_3.SpectreModel;
import com.ninni.species.client.renderer.entity.SpectreRenderer;
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
public class SpectreBodyFeatureRenderer<T extends Spectre, M extends SpectreModel<T>> extends RenderLayer<T, M> {
    private final SpectreModel<T> modelSpectre;
    private final SpectreModel<T> modelSableSpectre;
    private final SpectreModel<T> modelJoustingSpectre;

    public SpectreBodyFeatureRenderer(RenderLayerParent<T, M> renderLayerParent, EntityRendererProvider.Context ctx) {
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

            VertexConsumer vertexConsumerCharged = multiBufferSource.getBuffer(RenderType.dragonExplosionAlpha(getTEXTURE(spectre)));
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

    public ResourceLocation getTEXTURE(T spectre) {
        if (spectre != null) return spectre.isFromSword() ? SpectreRenderer.getSpectreVariantTextureLocation(spectre, "spirit_tamed") : SpectreRenderer.getSpectreVariantTextureLocation(spectre, "spirit");
        return new ResourceLocation("");
    }
}