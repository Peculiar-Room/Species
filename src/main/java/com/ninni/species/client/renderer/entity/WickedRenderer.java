package com.ninni.species.client.renderer.entity;

import com.ninni.species.client.model.mob.update_3.WickedModel;
import com.ninni.species.client.renderer.entity.feature.WickedFeatureRenderer;
import com.ninni.species.server.entity.mob.update_3.Wicked;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.ninni.species.Species.MOD_ID;

public class WickedRenderer<T extends LivingEntity> extends MobRenderer<Wicked, WickedModel<Wicked>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/wicked/wicked.png");

    public WickedRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WickedModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.WICKED)), 0.0F);
        this.addLayer(new WickedFeatureRenderer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Wicked p_114482_) {
        return TEXTURE;
    }
}
