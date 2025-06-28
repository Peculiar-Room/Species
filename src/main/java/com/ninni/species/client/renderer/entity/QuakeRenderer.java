package com.ninni.species.client.renderer.entity;

import com.ninni.species.client.model.mob.update_3.QuakeModel;
import com.ninni.species.client.renderer.entity.feature.QuakeChargingFeatureRenderer;
import com.ninni.species.server.entity.mob.update_3.Quake;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class QuakeRenderer extends MobRenderer<Quake, QuakeModel<Quake>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/quake/quake.png");

    public QuakeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new QuakeModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.QUAKE)), 1F);
        this.addLayer(new QuakeChargingFeatureRenderer<>(this, new QuakeModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.QUAKE))));
    }

    @Override
    public ResourceLocation getTextureLocation(Quake entity) {
        return TEXTURE;
    }
}