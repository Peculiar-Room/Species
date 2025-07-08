package com.ninni.species.client.renderer.entity;

import com.ninni.species.client.model.mob.update_3.SpectreModel;
import com.ninni.species.client.renderer.entity.feature.SpectreBodyFeatureRenderer;
import com.ninni.species.client.renderer.entity.feature.SpectreMetalFeatureRenderer;
import com.ninni.species.client.renderer.entity.feature.SpectreWiggleFeatureRenderer;
import com.ninni.species.server.entity.mob.update_3.Spectre;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class SpectreRenderer extends MobRenderer<Spectre, SpectreModel<Spectre>> {

    public SpectreRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SpectreModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SPECTRE)), 0.0F);
        this.addLayer(new SpectreMetalFeatureRenderer<>(this, ctx));
        this.addLayer(new SpectreBodyFeatureRenderer<>(this, ctx));
        this.addLayer(new SpectreWiggleFeatureRenderer<>(this, ctx));
    }

    @Override
    public ResourceLocation getTextureLocation(Spectre p_114482_) {
        return new ResourceLocation(MOD_ID, "textures/misc/empty.png");
    }

    @Override
    protected float getFlipDegrees(Spectre p_115337_) {
        return 0;
    }

    public static ResourceLocation getSpectreVariantTextureLocation(Spectre spectre, String texture) {
        String string = spectre.getVariant() != Spectre.Type.SPECTRE ? "/" + spectre.getVariant().getSerializedName().replace("_spectre", "") : "";
        return new ResourceLocation(MOD_ID, "textures/entity/spectre" + string + "/spectre_" + texture + ".png");
    }
}