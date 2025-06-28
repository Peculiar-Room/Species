package com.ninni.species.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.client.model.mob.update_3.SpectreModel;
import com.ninni.species.client.renderer.entity.feature.SpectreBodyFeatureRenderer;
import com.ninni.species.client.renderer.entity.feature.SpectreWiggleFeatureRenderer;
import com.ninni.species.server.entity.mob.update_3.Spectre;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class SpectreRenderer extends MobRenderer<Spectre, SpectreModel<Spectre>> {
    private final SpectreModel<Spectre> modelSpectre = this.getModel();
    private final SpectreModel<Spectre> modelSableSpectre;
    private final SpectreModel<Spectre> modelJoustingSpectre;

    public SpectreRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SpectreModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SPECTRE)), 0.0F);
        this.modelSableSpectre = new SpectreModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SABLE_SPECTRE));
        this.modelJoustingSpectre = new SpectreModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.JOUSTING_SPECTRE));
        this.addLayer(new SpectreBodyFeatureRenderer<>(this, ctx));
        this.addLayer(new SpectreWiggleFeatureRenderer<>(this, ctx));
    }

    @Override
    public void render(Spectre p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {
        this.model = switch (p_115455_.getVariant()) {
            case SPECTRE -> this.modelSpectre;
            case HULKING_SPECTRE -> this.modelSableSpectre;
            case JOUSTING_SPECTRE -> this.modelJoustingSpectre;
        };
        super.render(p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
    }

    @Override
    protected float getFlipDegrees(Spectre p_115337_) {
        return 0;
    }


    public static ResourceLocation getSpectreVariantTextureLocation(Spectre spectre, String texture) {
        String string = spectre.getVariant() != Spectre.Type.SPECTRE ? "/" + spectre.getVariant().getSerializedName().replace("_spectre", "") : "";
        return new ResourceLocation(MOD_ID, "textures/entity/spectre" + string + "/spectre_" + texture + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(Spectre entity) {
        return getSpectreVariantTextureLocation(entity, "metal");
    }
}