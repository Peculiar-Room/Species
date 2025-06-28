package com.ninni.species.client.renderer.entity.feature;

import com.ninni.species.client.model.mob.update_3.SpectreModel;
import com.ninni.species.client.renderer.entity.SpectreRenderer;
import com.ninni.species.client.renderer.entity.rendertypes.OffsetRenderLayer;
import com.ninni.species.server.entity.mob.update_3.Spectre;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectreWiggleFeatureRenderer<T extends Spectre, M extends SpectreModel<T>> extends OffsetRenderLayer<T, M> {
    private final SpectreModel<T> modelSpectre;
    private final SpectreModel<T> modelSableSpectre;
    private final SpectreModel<T> modelJoustingSpectre;

    public SpectreWiggleFeatureRenderer(RenderLayerParent<T, M> renderLayerParent, EntityRendererProvider.Context ctx) {
        super(renderLayerParent);
        this.modelSpectre = new SpectreModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SPECTRE));
        this.modelSableSpectre = new SpectreModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SABLE_SPECTRE));
        this.modelJoustingSpectre = new SpectreModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.JOUSTING_SPECTRE));
    }
    @Override
    protected float xOffset(float f) {
        return f * 0.01F;
    }

    @Override
    protected EntityModel<T> model(T entity) {
        return switch (entity.getVariant()) {
            case SPECTRE -> this.modelSpectre;
            case HULKING_SPECTRE -> this.modelSableSpectre;
            case JOUSTING_SPECTRE -> this.modelJoustingSpectre;
        };
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return SpectreRenderer.getSpectreVariantTextureLocation(entity, "spirit_wiggle");
    }
}